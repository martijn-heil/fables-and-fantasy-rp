package com.fablesfantasyrp.plugin.rolls.command

import com.fablesfantasyrp.plugin.characters.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.chat.chatStyle
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import com.fablesfantasyrp.plugin.rolls.ROLL_RANGE
import com.fablesfantasyrp.plugin.rolls.getRollModifierFor
import com.fablesfantasyrp.plugin.text.miniMessage
import com.gitlab.martijn_heil.nincommands.common.FixedSuggestions
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.gitlab.martijn_heil.nincommands.common.Suggestions
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Optional
import com.sk89q.intake.parametric.annotation.Range
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.random.Random
import kotlin.random.nextInt

class Commands {
	@Command(aliases = ["roll"], desc = "Roll the dice!")
	@Require("fables.rolls.command.roll")
	fun roll(@Sender sender: Player,
			 @FixedSuggestions @Suggestions(["3", "6", "20", "100"]) @Range(min = 2.0, max = 200.0) dice: Int,
			 @Optional kind: CharacterStatKind?) {
		val fPlayer = FablesPlayer.forPlayer(sender)
		val stats = fPlayer.currentPlayerCharacter!!.stats

		val random = Random.nextInt(1..dice)
		val result = if (kind != null) random + kind.getRollModifierFor(stats[kind]) else random

		val prefix = "<bold><gold>[</gold> <blue>ROLL</blue> <gold>]</gold></bold> "

		val messages = if (kind != null) {
			listOf(
					"$prefix <yellow><name></yellow> <chat_color>rolls <roll> out of <dice> for $kind.</chat_color>",
					"$prefix <chat_color>Result after applying $kind modifier: <bold><result></bold></chat_color>"
			)
		} else {
			listOf(
					"$prefix <yellow><name></yellow> <chat_color>rolls <roll> out of <dice>.</chat_color>"
			)
		}

		val chatStyle = fPlayer.chatStyle ?: Style.style(NamedTextColor.YELLOW)
		val resolver = TagResolver.builder().tag("chat_color", Tag.styling { it.merge(chatStyle) }).build()
		val parsed = messages.map {
			miniMessage.deserialize(it,
					Placeholder.unparsed("name", fPlayer.currentPlayerCharacter?.name ?: sender.name),
					Placeholder.unparsed("roll", random.toString()),
					Placeholder.unparsed("dice", dice.toString()),
					Placeholder.unparsed("result", result.toString()),
					resolver
			)
		}

		getPlayersWithinRange(sender.location, ROLL_RANGE).forEach { parsed.forEach { message -> it.sendMessage(message) } }
		parsed.forEach { Bukkit.getConsoleSender().sendMessage(it) }
	}
}
