package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.chat.awaitEmote
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.form.YesNoChatPrompt
import com.fablesfantasyrp.plugin.magic.ability.aeromancy.Cloud
import com.fablesfantasyrp.plugin.magic.ability.pyromancy.FlamingFamiliar
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicType
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.entity.Tear
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.magic.domain.repository.TearRepository
import com.fablesfantasyrp.plugin.magic.exception.CasterBusyException
import com.fablesfantasyrp.plugin.magic.exception.NoSpaceForTearException
import com.fablesfantasyrp.plugin.magic.exception.OpenTearException
import com.fablesfantasyrp.plugin.magic.exception.TooManyTearsException
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.rolls.roll
import com.fablesfantasyrp.plugin.targeting.targeting
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.DISTANCE_TALK
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.distanceSafe
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isVanished
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withTimeout
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.GameMode


suspend fun Character.awaitUnbindAttempts(spell: SpellData, castingRoll: Int): Boolean {
	val profileManager = Services.get<ProfileManager>()
	val characters = Services.get<CharacterRepository>()
	val mages = Services.get<MageRepository>()
	val player = profileManager.getCurrentForProfile(this.profile) ?: throw IllegalStateException()
	check(player.isOnline)
	val otherMages = getPlayersWithinRange(player.location, DISTANCE_TALK)
		.asFlow()
		.mapNotNull { profileManager.getCurrentForPlayer(it) }
		.mapNotNull { characters.forProfile(it) }
		.mapNotNull { mages.forCharacter(it) }
		.filter { it.character != this }
		.filter { !profileManager.getCurrentForProfile(it.character.profile)!!.isVanished }
		.filter { profileManager.getCurrentForProfile(it.character.profile)!!.gameMode != GameMode.SPECTATOR }

	val prompt = legacyText("$SYSPREFIX Do you want to unbind ${this.name}'s casting attempt?: ")
	val prompts = otherMages
		.map { Pair(it, YesNoChatPrompt(profileManager.getCurrentForProfile(it.character.profile)!!, prompt)) }
		.toList().toMap().toMutableMap()
	prompts.values.forEach { it.send() }

	try {
		return withTimeout(30000) {
			while (prompts.isNotEmpty()) {
				val result = select<Pair<Mage, Boolean>> {
					prompts.forEach {
						it.value.onAwait { answer ->
							Pair(it.key, answer)
						}
					}
				}
				val mage = result.first
				val answer = result.second
				prompts.remove(mage)
				if (answer) {
					if (mage.tryUnbindCast(spell, this@awaitUnbindAttempts, castingRoll)) return@withTimeout true
				} else {
					val answerPlayer = profileManager.getCurrentForProfile(mage.character.profile) ?: continue
					answerPlayer.sendMessage("$SYSPREFIX You will not try to unbind.")
				}
			}
			return@withTimeout false
		}
	} catch(_: CancellationException) { }
	return false
}

suspend fun Character.tryCastSpell(spell: SpellData): Boolean {
	val profileManager = Services.get<ProfileManager>()
	val mages = Services.get<MageRepository>()
	val castingTracker = Services.get<CastingTracker>()

	val player = profileManager.getCurrentForProfile(profile) ?: throw IllegalStateException()
	check(player.isOnline)
	val mage = mages.forCharacter(this)

	if (castingTracker.isCasting(this)) {
		player.sendError("You are already busy casting a spell.")
		return false
	}
	castingTracker.setIsCasting(this, true)

	try {
		val tear = this.findTear(spell.magicPath.magicType)
		if (tear == null) {
			player.sendError("No tear found within 15 blocks. Please open a tear with first with /opentear.")
			castingTracker.setIsCasting(this, false)
			return false
		}

		try {
			player.awaitEmote(legacyText("$SYSPREFIX Please emote to try to cast a spell:"))
			val additionalBonus = if (mage != null && mage.activeAbilities.any { it == Cloud || it == FlamingFamiliar }) 1U else 0U
			val spellCastingBonus = mage?.spellCastingBonus?.toInt() ?: 0
			val castingRoll = this.roll(20U, CharacterStatKind.INTELLIGENCE) +
				spellCastingBonus +
				additionalBonus.toInt()
			val success = castingRoll >= spell.castingValue

			val effectivenessRoll = this.roll(20U, CharacterStatKind.INTELLIGENCE)
			val effectiveness = if (success) {
				if (effectivenessRoll > 16) SpellEffectiveness.CRITICAL_SUCCESS else SpellEffectiveness.SUCCESS
			} else {
				if (effectivenessRoll < 7) SpellEffectiveness.CRITICAL_FAILURE else SpellEffectiveness.FAILURE
			}

			val message = getSpellCastingMessage(this, spell, castingRoll, effectiveness)
			val messageTargets = getPlayersWithinRange(player.location, DISTANCE_TALK).toList()

			val spellTargets = player.targeting.targets

			if (success) {
				val startMessage = miniMessage.deserialize(
					"<yellow><character_name></yellow> starts an attempt to cast <spell_name> " +
						"targeting the following players: <gray><targets></gray>",
					Placeholder.unparsed("character_name", this.name),
					Placeholder.component("spell_name", spellDisplay(spell)),
					Placeholder.unparsed("targets", spellTargets.joinToString(", ") { "\"${it.name}\"" }))
					.style(player.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))
				messageTargets.forEach { it.sendMessage(startMessage) }

				if(this.awaitUnbindAttempts(spell, castingRoll)) {
					castingTracker.setIsCasting(this, false)
					return false
				}

				messageTargets.forEach { it.sendMessage(message) }
			} else {
				messageTargets.forEach { it.sendMessage(message) }
				castingTracker.setIsCasting(this, false)
				return false
			}

			return effectiveness == SpellEffectiveness.SUCCESS || effectiveness == SpellEffectiveness.CRITICAL_SUCCESS
		} catch(_: CancellationException) { } finally {
			castingTracker.setIsCasting(this, false)
		}
	} catch(_: Exception) { } finally {
		castingTracker.setIsCasting(this, false)
	}
	return false
}

@Throws(OpenTearException::class)
suspend fun Character.openTear(magicType: MagicType): Tear {
	val castingTracker = Services.get<CastingTracker>()

	if (castingTracker.isCasting(this)) throw CasterBusyException()

	val profileManager = Services.get<ProfileManager>()
	val tears = Services.get<TearRepository>()

	val player = profileManager.getCurrentForProfile(profile) ?: throw IllegalStateException()
	check(player.isOnline)
	if (tears.forOwner(this).size >= MAX_TEARS_PER_MAGE) throw TooManyTearsException()

	player.awaitEmote(legacyText("$SYSPREFIX Please emote to open a tear:"))

	val location = calculateTearLocation(player.eyeLocation) ?: throw NoSpaceForTearException()
	return tears.create(Tear(0, location, magicType, this))
}

fun Character.findTear(magicType: MagicType): Tear? {
	val profileManager = Services.get<ProfileManager>()
	val tears = Services.get<TearRepository>()

	val player = profileManager.getCurrentForProfile(profile) ?: throw IllegalStateException()
	check(player.isOnline)

	return tears.all().asSequence()
		.filter { it.magicType == magicType }
		.filter { it.location.world == player.location.world }
		.find { it.location.distanceSafe(player.location) <= 15 }
}
