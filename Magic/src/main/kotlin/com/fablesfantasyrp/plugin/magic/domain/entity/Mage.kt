package com.fablesfantasyrp.plugin.magic.domain.entity

import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.chat.awaitEmote
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.form.YesNoChatPrompt
import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.magic.*
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.ability.aeromancy.Cloud
import com.fablesfantasyrp.plugin.magic.ability.pyromancy.FlamingFamiliar
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
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
import com.fablesfantasyrp.plugin.utils.isVanished
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withTimeout
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.GameMode
import org.koin.core.context.GlobalContext


class Mage : DataEntity<Long, Mage> {
	private val characters: EntityCharacterRepository get() = GlobalContext.get().get<EntityCharacterRepository>()
	private val mages: MageRepository get() = GlobalContext.get().get<MageRepository>()
	private val tears: TearRepository get() = GlobalContext.get().get<TearRepository>()

	val character: Character
		get() = characters.forId(id.toInt())!!

	var isDeleted: Boolean = false

	var magicPath: MagicPath
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var magicLevel: Int
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var spells: List<SpellData>
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var activeAbilities: Set<MageAbility> = emptySet()
		set(value) {
			if (field != value) {
				val added = value.minus(field)
				val removed = field.minus(value)
				val players = getPlayersWithinRange(character.profile.location, DISTANCE_TALK).toList()
				added.forEach { ability ->
					players.forEach {
						it.sendMessage("$SYSPREFIX ${character.name} activated ${ability.displayName}")
					}
				}
				removed.forEach { ability ->
					players.forEach {
						it.sendMessage("$SYSPREFIX ${character.name} deactivated ${ability.displayName}")
					}
				}
				field = value;
				dirtyMarker?.markDirty(this)
			}
		}

	val spellCastingBonus: UInt get() = getSpellCastingBonus(this.magicPath, this.magicLevel).toUInt()

	override val id: Long

	private var isCasting = false

	constructor(id: Long, magicPath: MagicPath, magicLevel: Int, spells: List<SpellData>) : super() {
		this.id = id
		this.magicPath = magicPath
		this.magicLevel = magicLevel
		this.spells = spells
	}

	override var dirtyMarker: DirtyMarker<Mage>? = null

	private fun startSpellCastingParticles(spell: SpellData): Job {
		return PLUGIN.launch {

		}
	}

	suspend fun tryCloseTear(tear: Tear): Boolean {
		val profileManager = Services.get<ProfileManager>()
		val myPlayer = profileManager.getCurrentForProfile(character.profile)!!

		if (myPlayer.location.world != tear.location.world || myPlayer.location.distance(tear.location) > 15) {
			myPlayer.sendError("Targeted tear is out of range, you need to be within 15 blocks of the tear")
			return false
		}

		val prompt = legacyText("$SYSPREFIX Please emote to close the tear:")

		val them = tear.owner
		val theirPlayer = profileManager.getCurrentForProfile(them.character.profile) ?: run {
			myPlayer.awaitEmote(prompt)
			tears.destroy(tear)
			return true
		}

		myPlayer.awaitEmote(prompt)
		if (theirPlayer == myPlayer) {
			tears.destroy(tear)
			return true
		} else {
			val myRoll = character.roll(10U, CharacterStatKind.INTELLIGENCE)
			val myResult = myRoll + this.spellCastingBonus.toInt()

			val theirRoll = them.character.roll(10U, CharacterStatKind.INTELLIGENCE)
			val theirResult = theirRoll + them.spellCastingBonus.toInt()

			val success = myResult > theirResult
			val resultMessage = if (success) {
				Component.text("succeeds").color(NamedTextColor.GREEN)
			} else {
				Component.text("fails").color(NamedTextColor.RED)
			}

			val message = miniMessage.deserialize(
					"<my_name> attempts to close a tear owned by <green><their_name></green> and <result>",
					Placeholder.unparsed("my_name", character.name),
					Placeholder.unparsed("their_name", them.character.name),
					Placeholder.component("result", resultMessage)).style(myPlayer.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))

			getPlayersWithinRange(myPlayer.location, DISTANCE_TALK).forEach { it.sendMessage(message) }

			return if (success) {
				tears.destroy(tear)
				true
			} else {
				false
			}
		}
	}

	suspend fun tryUnbindCast(spell: SpellData, enemy: Mage, castingRoll: Int): Boolean {
		val profileManager = Services.get<ProfileManager>()
		val player = profileManager.getCurrentForProfile(this.character.profile) ?: throw IllegalStateException()
		check(player.isOnline)

		if (this.isCasting) {
			player.sendError("Could not unbind, you are busy casting a spell")
			return false
		}

		try {
			val tear = this.findTear() ?: this.openTear()
		} catch(e: OpenTearException) {
			player.sendMessage("Couldn't open a tear: ${e.message}")
			return false
		}

		player.awaitEmote(legacyText("$SYSPREFIX Please emote to try and unbind " +
				"${enemy.character.name}'s ${spell.displayName} spell"))

		val roll = character.roll(20U, CharacterStatKind.INTELLIGENCE)
		val enemyPlayer = profileManager.getCurrentForProfile(enemy.character.profile)!!
		val messageTargets = getPlayersWithinRange(player.location, DISTANCE_TALK)
				.plus(getPlayersWithinRange(enemyPlayer.location, DISTANCE_TALK)).distinct()

		if (roll > castingRoll) {
			val message = miniMessage.deserialize("<yellow><my_name></yellow> <green>successfully</green> unbound " +
					"<yellow><enemy_name>'s</yellow> <spell> cast.",
			Placeholder.unparsed("my_name", this.character.name),
			Placeholder.unparsed("enemy_name", enemy.character.name),
			Placeholder.component("spell", spellDisplay(spell)))
					.style(player.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))
			messageTargets.forEach { it.sendMessage(message) }
			return true
		} else {
			val message = miniMessage.deserialize("<yellow><my_name></yellow> <red>failed</red> to unbind " +
					"<yellow><enemy_name>'s</yellow> <spell> cast.",
					Placeholder.unparsed("my_name", this.character.name),
					Placeholder.unparsed("enemy_name", enemy.character.name),
					Placeholder.component("spell", spellDisplay(spell)))
					.style(player.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))
			messageTargets.forEach { it.sendMessage(message) }
			return false
		}
	}

	private suspend fun awaitUnbindAttempts(spell: SpellData, castingRoll: Int): Boolean {
		val profileManager = GlobalContext.get().get<ProfileManager>()
		val characters = GlobalContext.get().get<EntityCharacterRepository>()
		val player = profileManager.getCurrentForProfile(this.character.profile) ?: throw IllegalStateException()
		check(player.isOnline)
		val otherMages = getPlayersWithinRange(player.location, DISTANCE_TALK)
				.mapNotNull { profileManager.getCurrentForPlayer(it) }
				.mapNotNull { characters.forProfile(it) }
				.mapNotNull { mages.forCharacter(it) }
				.filter { it != this }
				.filter { !profileManager.getCurrentForProfile(it.character.profile)!!.isVanished }
				.filter { profileManager.getCurrentForProfile(it.character.profile)!!.gameMode != GameMode.SPECTATOR }

		val prompt = legacyText("$SYSPREFIX Do you want to unbind ${this.character.name}'s casting attempt?: ")
		val prompts = otherMages
				.map { Pair(it, YesNoChatPrompt(profileManager.getCurrentForProfile(it.character.profile)!!, prompt)) }
				.toMap().toMutableMap()
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
						if (mage.tryUnbindCast(spell, this@Mage, castingRoll)) return@withTimeout true
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

	suspend fun tryCastSpell(spell: SpellData): Boolean {
		val profileManager = Services.get<ProfileManager>()
		val player = profileManager.getCurrentForProfile(character.profile) ?: throw IllegalStateException()
		check(player.isOnline)
		if (this.isCasting) {
			player.sendError("You are already busy casting a spell.")
			return false
		}
		this.isCasting = true

		try {
			val tear = this.findTear()
			if (tear == null) {
				player.sendError("No tear found within 15 blocks. Please open a tear with first with /opentear.")
				this.isCasting = false
				return false
			}

			val particlesJob = this.startSpellCastingParticles(spell)
			try {
				player.awaitEmote(legacyText("$SYSPREFIX Please emote to try to cast a spell:"))
				val additionalBonus = if (this.activeAbilities.any { it == Cloud || it == FlamingFamiliar }) 1U else 0U
				val castingRoll = character.roll(20U, CharacterStatKind.INTELLIGENCE) +
						this.spellCastingBonus.toInt() +
						additionalBonus.toInt()
				val success = castingRoll >= spell.castingValue

				val effectivenessRoll = character.roll(20U, CharacterStatKind.INTELLIGENCE)
				val effectiveness = if (success) {
					if (effectivenessRoll > 16) SpellEffectiveness.CRITICAL_SUCCESS else SpellEffectiveness.SUCCESS
				} else {
					if (effectivenessRoll < 7) SpellEffectiveness.CRITICAL_FAILURE else SpellEffectiveness.FAILURE
				}

				val message = getSpellCastingMessage(character, spell, castingRoll, effectiveness)
				val messageTargets = getPlayersWithinRange(player.location, DISTANCE_TALK).toList()

				val spellTargets = player.targeting.targets

				if (success) {
					val startMessage = miniMessage.deserialize(
							"<yellow><character_name></yellow> starts an attempt to cast <spell_name> " +
									"targeting the following players: <gray><targets></gray>",
							Placeholder.unparsed("character_name", character.name),
							Placeholder.component("spell_name", spellDisplay(spell)),
							Placeholder.unparsed("targets", spellTargets.joinToString(", ") { "\"${it.name}\"" }))
							.style(player.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))
					messageTargets.forEach { it.sendMessage(startMessage) }

					if(awaitUnbindAttempts(spell, castingRoll)) {
						particlesJob.cancel()
						this.isCasting = false
						return false
					}

					messageTargets.forEach { it.sendMessage(message) }
				} else {
					messageTargets.forEach { it.sendMessage(message) }
					particlesJob.cancel()
					this.isCasting = false
					return false
				}

				return effectiveness == SpellEffectiveness.SUCCESS || effectiveness == SpellEffectiveness.CRITICAL_SUCCESS
			} catch(ex: CancellationException) {
				particlesJob.cancel(ex)
			} finally {
				this.isCasting = false
			}
		} catch(_: Exception) {

		} finally {
			this.isCasting = false
		}
		return false
	}

	@Throws(OpenTearException::class)
	suspend fun openTear(): Tear {
		if (this.isCasting) throw CasterBusyException()
		val profileManager = Services.get<ProfileManager>()
		val player = profileManager.getCurrentForProfile(character.profile) ?: throw IllegalStateException()
		check(player.isOnline)
		if (tears.forOwner(this).size >= MAX_TEARS_PER_MAGE) throw TooManyTearsException()

		player.awaitEmote(legacyText("$SYSPREFIX Please emote to open a tear:"))

		val location = calculateTearLocation(player.eyeLocation) ?: throw NoSpaceForTearException()
		return tears.create(Tear(0, location, magicPath.magicType, this))
	}

	fun findTear(): Tear? {
		val profileManager = Services.get<ProfileManager>()
		val player = profileManager.getCurrentForProfile(character.profile) ?: throw IllegalStateException()
		check(player.isOnline)
		return tears.all().asSequence()
				.filter { it.magicType == this.magicPath.magicType }
				.filter { it.location.world == player.location.world }
				.find { it.location.distance(player.location) <= 15 }
	}
}
