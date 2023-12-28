package com.fablesfantasyrp.plugin.rolls.command

import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.rolls.ROLL_RANGE
import com.fablesfantasyrp.plugin.rolls.rollExpression
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.broadcast
import com.fablesfantasyrp.caturix.spigot.common.FixedSuggestions
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.spigot.common.Suggestions
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.CommandException
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.parametric.annotation.Optional
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Server

class Commands(private val server: Server,
			   private val characters: CharacterRepository,
			   private val profileManager: ProfileManager) {
	@Command(aliases = ["roll", "dice"], desc = "Roll the dice!")
	@Require("fables.rolls.command.roll")
	fun roll(@Sender sender: Profile,
			 @FixedSuggestions @Suggestions(["3", "6", "20", "100"]) @Optional("20") expression: String,
			 @Optional kind: CharacterStatKind?) {
		val senderPlayer = profileManager.getCurrentForProfile(sender) ?: throw IllegalStateException()
		val senderCharacter = characters.forProfile(sender)
		val senderName = senderCharacter?.name ?: senderPlayer.name
		val prefix = legacyText(GLOBAL_SYSPREFIX)
		val chatStyle = senderPlayer.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW)
		val resolver = TagResolver.builder().tag("chat_color", Tag.styling { it.merge(chatStyle) }).build()

		val isComplex = !expression.matches(Regex("[0-9]+"))

		if (isComplex) {
			val result = try {
				rollExpression(expression, senderCharacter, kind)
			} catch (ex: Exception) {
				throw CommandException(ex.message)
			}

			server.broadcast(senderPlayer.location, 15,
				miniMessage.deserialize(
					"<prefix> <yellow><name></yellow> <chat_color>rolls <kind><expression> = <bold><result></bold></chat_color>",
					Placeholder.component("prefix", prefix),
					Placeholder.unparsed("expression", expression),
					Placeholder.unparsed("kind", if (kind != null) "${kind.shortName} " else ""),
					Placeholder.unparsed("result", result.toString()),
					Placeholder.unparsed("name", senderName),
					resolver
				)
			)
			return
		} else {
			if (senderCharacter == null && kind != null) {
				throw CommandException("You cannot roll a specific stat kind while you are out of character")
			}

			val dice = expression.toIntOrNull() ?: throw CommandException("Could not parse '$expression' as an integer")

			val roll = com.fablesfantasyrp.plugin.rolls.roll(dice.toUInt(), senderCharacter, kind)
			val result = roll.second

			val letter = when(kind) {
				CharacterStatKind.AGILITY -> "a"
				CharacterStatKind.DEFENSE -> "d"
				CharacterStatKind.STRENGTH -> "s"
				CharacterStatKind.INTELLIGENCE -> "i"
				null -> ""
			}

			val message = miniMessage.deserialize(
				"<prefix> <yellow><name></yellow> <chat_color>rolls d<dice><stat_letter> = <bold><result></bold></chat_color>",
				Placeholder.component("prefix", prefix),
				Placeholder.unparsed("dice", dice.toString()),
				Placeholder.unparsed("result", result.toString()),
				Placeholder.unparsed("stat_letter", letter),
				Placeholder.unparsed("name", senderName),
				resolver
			)

			server.broadcast(senderPlayer.location, ROLL_RANGE.toInt(), message)
		}
	}
}
