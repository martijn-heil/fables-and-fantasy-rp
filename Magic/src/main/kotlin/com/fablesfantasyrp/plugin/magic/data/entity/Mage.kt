package com.fablesfantasyrp.plugin.magic.data.entity

import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterData
import com.fablesfantasyrp.plugin.characters.playerCharacterRepository
import com.fablesfantasyrp.plugin.chat.awaitEmote
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.form.YesNoChatPrompt
import com.fablesfantasyrp.plugin.magic.*
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.ability.aeromancy.Cloud
import com.fablesfantasyrp.plugin.magic.data.MageData
import com.fablesfantasyrp.plugin.magic.data.SpellData
import com.fablesfantasyrp.plugin.magic.exception.NoSpaceForTearException
import com.fablesfantasyrp.plugin.magic.exception.OpenTearException
import com.fablesfantasyrp.plugin.magic.exception.TooManyTearsException
import com.fablesfantasyrp.plugin.rolls.roll
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withTimeout
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder


class Mage : MageData, HasDirtyMarker<Mage> {
	@get:javax.persistence.Transient
	val playerCharacter: PlayerCharacterData
		get() = playerCharacterRepository.forId(id.toULong())!!

	@get:javax.persistence.Transient
	var isDeleted: Boolean = false

	override var magicPath: MagicPath
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var magicLevel: Int
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var spells: List<SpellData>
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var activeAbilities: Set<MageAbility> = emptySet()
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var id: Long

	private var isCasting = false

	constructor(id: Long, magicPath: MagicPath, magicLevel: Int, spells: List<SpellData>) : super() {
		this.id = id
		this.magicPath = magicPath
		this.magicLevel = magicLevel
		this.spells = spells
	}

	@get:javax.persistence.Transient
	override var dirtyMarker: DirtyMarker<Mage>? = null

	private fun startSpellCastingParticles(spell: SpellData): Job {
		return PLUGIN.launch {

		}
	}

	suspend fun tryCloseTear(tear: Tear): Boolean {
		val myPlayer = playerCharacter.player.player!!
		val myStats = playerCharacter.stats

		if (myPlayer.location.world != tear.location.world || myPlayer.location.distance(tear.location) > 15) {
			myPlayer.sendError("Targeted tear is out of range, you need to be within 15 blocks of the tear")
			return false
		}

		val prompt = legacyText("$SYSPREFIX Please emote to close the tear:")

		val them = tear.owner
		val theirPlayer = them.playerCharacter.player.player ?: run {
			myPlayer.awaitEmote(prompt)
			tearRepository.destroy(tear)
			return true
		}
		val theirStats = them.playerCharacter.stats

		myPlayer.awaitEmote(prompt)
		if (theirPlayer == myPlayer) {
			tearRepository.destroy(tear)
			return true
		} else {
			val myRoll = roll(10U, CharacterStatKind.INTELLIGENCE, myStats)
			val myResult = myRoll.second + this.spellCastingBonus.toInt()

			val theirRoll = roll(10U, CharacterStatKind.INTELLIGENCE, theirStats)
			val theirResult = theirRoll.second + them.spellCastingBonus.toInt()

			val success = myResult > theirResult
			val resultMessage = if (success) {
				Component.text("succeeds").color(NamedTextColor.GREEN)
			} else {
				Component.text("fails").color(NamedTextColor.RED)
			}

			val message = miniMessage.deserialize(
					"<my_name> attempts to close a tear owned by <green><their_name></green> and <result>",
					Placeholder.unparsed("my_name", playerCharacter.name),
					Placeholder.unparsed("their_name", them.playerCharacter.name),
					Placeholder.component("result", resultMessage)).style(myPlayer.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))

			getPlayersWithinRange(myPlayer.location, 15U).forEach { it.sendMessage(message) }

			return if (success) {
				tearRepository.destroy(tear)
				true
			} else {
				false
			}
		}
	}

	suspend fun tryUnbindCast(spell: SpellData, enemy: Mage, castingRoll: Int): Boolean {
		val player = this.playerCharacter.player.player!!

		try {
			val tear = this.findTear() ?: this.openTear()
		} catch(e: OpenTearException) {
			player.sendMessage("Couldn't open a tear: ${e.message}")
			return false
		}

		player.awaitEmote(legacyText("$SYSPREFIX Please emote to try and unbind " +
				"${enemy.playerCharacter.name}'s ${spell.displayName} spell"))

		val roll = roll(20U, CharacterStatKind.INTELLIGENCE, this.playerCharacter.stats).second.toInt()

		val messageTargets = getPlayersWithinRange(player.location, 15U)
				.plus(getPlayersWithinRange(enemy.playerCharacter.player.player!!.location, 15U)).distinct()

		if (roll > castingRoll) {
			val message = miniMessage.deserialize("<yellow><my_name></yellow> <green>successfully</green> unbound " +
					"<yellow><enemy_name>'s</yellow> <spell> cast.",
			Placeholder.unparsed("my_name", this.playerCharacter.name),
			Placeholder.unparsed("enemy_name", enemy.playerCharacter.name),
			Placeholder.component("spell", spellDisplay(spell)))
					.style(player.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))
			messageTargets.forEach { it.sendMessage(message) }
			return true
		} else {
			val message = miniMessage.deserialize("<yellow><my_name></yellow> <red>failed</red> to unbind " +
					"<yellow><enemy_name>'s</yellow> <spell> cast.",
					Placeholder.unparsed("my_name", this.playerCharacter.name),
					Placeholder.unparsed("enemy_name", enemy.playerCharacter.name),
					Placeholder.component("spell", spellDisplay(spell)))
					.style(player.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))
			messageTargets.forEach { it.sendMessage(message) }
			return false
		}
	}

	suspend fun awaitUnbindAttempts(spell: SpellData, castingRoll: Int): Boolean {
		val player = this.playerCharacter.player.player!!
		val otherMages = getPlayersWithinRange(player.location, 15U)
				.mapNotNull { it.currentPlayerCharacter }
				.mapNotNull { mageRepository.forPlayerCharacter(it) }
				.filter { it != this }

		val prompt = legacyText("$SYSPREFIX Do you want to unbind ${this.playerCharacter.name}'s casting attempt?: ")
		val prompts = otherMages
				.map { Pair(it, YesNoChatPrompt(it.playerCharacter.player.player!!, prompt)) }
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
					}
				}
				return@withTimeout false
			}
		} catch(_: CancellationException) { }
		return false
	}

	suspend fun tryCastSpell(spell: SpellData): Boolean {
		val player = playerCharacter.player.player!!
		if (this.isCasting) {
			player.sendError("You are already busy casting a spell.")
			return false
		}
		this.isCasting = true

		try {
			val stats = playerCharacter.stats

			val tear = this.findTear()
			if (tear == null) {
				player.sendError("No tear found within 15 blocks. Please open a tear with first with /opentear.")
				this.isCasting = false
				return false
			}

			val particlesJob = this.startSpellCastingParticles(spell)
			try {
				player.awaitEmote(legacyText("$SYSPREFIX Please emote to try to cast a spell:"))
				val additionalBonus = if (this.activeAbilities.contains(Cloud)) 1U else 0U
				val castingRoll = roll(20U, CharacterStatKind.INTELLIGENCE, stats).second +
						this.spellCastingBonus.toInt() +
						additionalBonus.toInt()
				val success = castingRoll >= spell.castingValue

				val effectivenessRoll = roll(20U, CharacterStatKind.INTELLIGENCE, stats).second + this.spellCastingBonus.toInt()
				val effectiveness = if (!success) null else SpellEffectiveness.fromRoll(effectivenessRoll)

				val message = getSpellCastingMessage(playerCharacter, spell, success, effectiveness)
				val messageTargets = getPlayersWithinRange(player.location, 15U).toList()

				if (success) {
					val startMessage = miniMessage.deserialize(
							"<yellow><character_name></yellow> starts an attempt to cast <spell_name> ",
							Placeholder.unparsed("character_name", playerCharacter.name),
							Placeholder.component("spell_name", spellDisplay(spell)))
							.style(player.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))
					messageTargets.forEach { it.sendMessage(startMessage) }

					if(awaitUnbindAttempts(spell, castingRoll.toInt())) {
						particlesJob.cancel()
						this.isCasting = false
						return false
					}

					//val spellTargets = messageTargets.mapNotNull { it.currentPlayerCharacter } // TODO
					//awaitSpellDefence(spell, spellTargets)

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
		val player = playerCharacter.player.player
		check(player != null)
		if (tearRepository.forOwner(this).size >= MAX_TEARS_PER_MAGE) throw TooManyTearsException()

		player.awaitEmote(legacyText("$SYSPREFIX Please emote to open a tear:"))

		val location = calculateTearLocation(player.eyeLocation) ?: throw NoSpaceForTearException()
		return tearRepository.create(Tear(0, location, magicPath.magicType, this))
	}

	fun findTear(): Tear? {
		return tearRepository.all().asSequence()
				.filter { it.magicType == this.magicPath.magicType }
				.filter { it.location.world == player.location.world }
				.find { it.location.distance(player.location) <= 15 }
	}

	override fun equals(other: Any?): Boolean = other is MageData && other.id == this.id
	override fun hashCode(): Int = this.id.hashCode()
}
