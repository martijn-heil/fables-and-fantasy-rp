package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterData
import com.fablesfantasyrp.plugin.chat.awaitEmote
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.magic.data.SpellData
import com.fablesfantasyrp.plugin.magic.data.entity.Mage
import com.fablesfantasyrp.plugin.rolls.roll
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

suspend fun PlayerCharacterData.tryDefendAgainstSpellCast(spell: SpellData, enemy: Mage, castingRoll: Int): Boolean {
	val player = this.player.player!!
	player.awaitEmote(legacyText("$SYSPREFIX Please emote to try and defend against" +
			"${enemy.playerCharacter.name}'s ${spell.displayName} spell"))

	val roll = roll(20U, CharacterStatKind.INTELLIGENCE, this.stats).second.toInt()

	val messageTargets = getPlayersWithinRange(player.location, 15U)
			.plus(getPlayersWithinRange(enemy.playerCharacter.player.player!!.location, 15U)).distinct()

	if (roll > castingRoll) {
		val message = miniMessage.deserialize("<yellow><my_name></yellow> <green>successfully</green> defended against " +
				"<yellow><enemy_name>'s</yellow> <spell> cast.",
				Placeholder.unparsed("my_name", this.name),
				Placeholder.unparsed("enemy_name", enemy.playerCharacter.name),
				Placeholder.component("spell", spellDisplay(spell)))
				.style(player.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))
		messageTargets.forEach { it.sendMessage(message) }
		return true
	} else {
		val message = miniMessage.deserialize("<yellow><my_name></yellow> <red>failed</red> to defend against " +
				"<yellow><enemy_name>'s</yellow> <spell> cast.",
				Placeholder.unparsed("my_name", this.name),
				Placeholder.unparsed("enemy_name", enemy.playerCharacter.name),
				Placeholder.component("spell", spellDisplay(spell)))
				.style(player.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))
		messageTargets.forEach { it.sendMessage(message) }
		return false
	}
}
