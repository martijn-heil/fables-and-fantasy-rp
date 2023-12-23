package com.fablesfantasyrp.plugin.magic.domain.entity

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.chat.awaitEmote
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.magic.*
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.magic.domain.repository.TearRepository
import com.fablesfantasyrp.plugin.magic.exception.OpenTearException
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.rolls.roll
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.DISTANCE_TALK
import com.fablesfantasyrp.plugin.utils.Services
import com.github.shynixn.mccoroutine.bukkit.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder


class Mage : DataEntity<Long, Mage> {
	private val mages: MageRepository get() = Services.get<MageRepository>()
	private val tears: TearRepository get() = Services.get<TearRepository>()

	override var dirtyMarker: DirtyMarker<Mage>? = null

	val character: Character

	override val id: Long get() = character.id.toLong()

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
				field = value;
				dirtyMarker?.markDirty(this)

				PLUGIN.launch {
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
				}
			}
		}

	val spellCastingBonus: UInt get() = getSpellCastingBonus(this.magicPath, this.magicLevel).toUInt()

	private var isCasting = false

	constructor(character: Character, magicPath: MagicPath, magicLevel: Int, spells: List<SpellData>, dirtyMarker: DirtyMarker<Mage>? = null) : super() {
		this.character = character
		this.magicPath = magicPath
		this.magicLevel = magicLevel
		this.spells = spells

		this.dirtyMarker = dirtyMarker
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
		val theirPlayer = profileManager.getCurrentForProfile(them.profile) ?: run {
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

			val theirRoll = them.roll(10U, CharacterStatKind.INTELLIGENCE)
			val theirSpellCastingBonus = mages.forCharacter(them)?.spellCastingBonus?.toInt() ?: 0
			val theirResult = theirRoll + theirSpellCastingBonus

			val success = myResult > theirResult
			val resultMessage = if (success) {
				Component.text("succeeds").color(NamedTextColor.GREEN)
			} else {
				Component.text("fails").color(NamedTextColor.RED)
			}

			val message = miniMessage.deserialize(
					"<my_name> attempts to close a tear owned by <green><their_name></green> and <result>",
					Placeholder.unparsed("my_name", character.name),
					Placeholder.unparsed("their_name", them.name),
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

	suspend fun tryUnbindCast(spell: SpellData, enemy: Character, castingRoll: Int): Boolean {
		val profileManager = Services.get<ProfileManager>()
		val player = profileManager.getCurrentForProfile(character.profile) ?: throw IllegalStateException()
		check(player.isOnline)

		if (this.isCasting) {
			player.sendError("Could not unbind, you are busy casting a spell")
			return false
		}

		try {
			val magicType = magicPath.magicType
			character.findTear(magicType) ?: character.openTear(magicType)
		} catch(e: OpenTearException) {
			player.sendMessage("Couldn't open a tear: ${e.message}")
			return false
		}

		player.awaitEmote(legacyText("$SYSPREFIX Please emote to try and unbind " +
				"${enemy.name}'s ${spell.displayName} spell"))

		val roll = character.roll(20U, CharacterStatKind.INTELLIGENCE)
		val enemyPlayer = profileManager.getCurrentForProfile(enemy.profile)!!
		val messageTargets = getPlayersWithinRange(player.location, DISTANCE_TALK)
				.plus(getPlayersWithinRange(enemyPlayer.location, DISTANCE_TALK)).distinct()

		if (roll > castingRoll) {
			val message = miniMessage.deserialize("<yellow><my_name></yellow> <green>successfully</green> unbound " +
					"<yellow><enemy_name>'s</yellow> <spell> cast.",
			Placeholder.unparsed("my_name", character.name),
			Placeholder.unparsed("enemy_name", enemy.name),
			Placeholder.component("spell", spellDisplay(spell)))
					.style(player.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))
			messageTargets.forEach { it.sendMessage(message) }
			return true
		} else {
			val message = miniMessage.deserialize("<yellow><my_name></yellow> <red>failed</red> to unbind " +
					"<yellow><enemy_name>'s</yellow> <spell> cast.",
					Placeholder.unparsed("my_name", character.name),
					Placeholder.unparsed("enemy_name", enemy.name),
					Placeholder.component("spell", spellDisplay(spell)))
					.style(player.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))
			messageTargets.forEach { it.sendMessage(message) }
			return false
		}
	}
}
