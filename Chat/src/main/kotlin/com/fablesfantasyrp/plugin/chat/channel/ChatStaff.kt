package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.text.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.Serializable

object ChatStaff : AbstractSubChanneledChatChannel("staff", mapOf(
		Pair(ChatStaffTechTeam.team, ChatStaffTechTeam),
		Pair(ChatStaffEventTeam.team, ChatStaffEventTeam),
		Pair(ChatStaffLoreTeam.team, ChatStaffLoreTeam),
		Pair(ChatStaffModTeam.team, ChatStaffModTeam),
		Pair(ChatStaffBuildTeam.team, ChatStaffBuildTeam),
		Pair(ChatStaffCommunityTeam.team, ChatStaffCommunityTeam),
		Pair(ChatStaffFeatureDevelopmentTeam.team, ChatStaffFeatureDevelopmentTeam),
), ChatStaffStandard), Serializable {
	fun readResolve(): Any? = ChatStaff
	override fun getRecipients(from: Player): Sequence<Player>
		= Bukkit.getOnlinePlayers().asSequence().filter { it.hasPermission(Permission.Channel.Staff) }
}

abstract class AbstractChatStaff : ChatChannel, RawChatChannel, CommandSenderCompatibleChatChannel, PreviewableChatChannel, Serializable {
	abstract val team: String

	override fun getRecipients(from: Player): Sequence<Player> =
			Bukkit.getOnlinePlayers().asSequence()
					.filter { it.hasPermission(Permission.Channel.Staff + ".${team.lowercase()}") }

	override fun getRecipients(from: CommandSender): Sequence<Player> =
			Bukkit.getOnlinePlayers().asSequence()
					.filter { it.hasPermission(Permission.Channel.Staff + ".${team.lowercase()}") }

	override fun sendMessage(from: Player, message: String) = this.sendMessage(from, parseLinks(message))
	override fun sendMessage(from: CommandSender, message: String) = this.sendMessage(from, parseLinks(message))
	override fun sendMessage(from: Player, message: Component) = sendMessage(from as CommandSender, message)

	fun sendMessage(from: CommandSender, message: Component) {
		if(PlainTextComponentSerializer.plainText().serialize(message).isEmpty()) return

		val final = this.formatMessage(from, message)
		getRecipients(from).forEach { it.sendMessage(final) }
		logChatToConsole(final)
	}

	private fun formatMessage(from: CommandSender, message: Component): Component {
		val chatPrefix = if (from is Player) {
			vaultChat.getPlayerPrefix(from)
					.let { ChatColor.translateAlternateColorCodes('&', it) }
					.let { legacyText(it) }
		} else Component.text()

		val chatSuffix = if (from is Player) {
			vaultChat.getPlayerSuffix(from)
					.let { ChatColor.translateAlternateColorCodes('&', it) }
					.let { legacyText(it) }
		} else Component.text()

		val teamName = if (team != "") team else null
		val channelName = Component.text(listOfNotNull("ST", teamName).joinToString("."))

		val customResolver = TagResolver.builder()
				.tag("prefix", Tag.selfClosingInserting(chatPrefix))
				.tag("suffix", Tag.selfClosingInserting(chatSuffix))
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name).style(from.nameStyle)))
				.tag("message", Tag.selfClosingInserting(message))
				.tag("channel", Tag.selfClosingInserting(channelName))
				.build()
		return miniMessage.deserialize(
				"<dark_red>[<channel>]</dark_red> <gray><prefix><player_name><suffix></gray> <dark_gray>»</dark_gray> <red><message></red>",
				TagResolver.standard(), customResolver)
	}

	override fun getPreview(from: Player, message: String): Component = this.formatMessage(from, parseLinks(message))
	override fun toString() = "staff.${team.lowercase()}"
}

object ChatStaffStandard : AbstractChatStaff() {
	@Transient
	override val team: String = ""

	fun readResolve(): Any? = ChatStaffStandard
}

object ChatStaffTechTeam : AbstractChatStaff() {
	@Transient
	override val team: String = "TT"

	fun readResolve(): Any? = ChatStaffTechTeam
}

object ChatStaffEventTeam : AbstractChatStaff() {
	@Transient
	override val team: String = "ET"

	fun readResolve(): Any? = ChatStaffEventTeam
}

object ChatStaffLoreTeam : AbstractChatStaff() {
	@Transient
	override val team: String = "LT"

	fun readResolve(): Any? = ChatStaffLoreTeam
}

object ChatStaffModTeam : AbstractChatStaff() {
	@Transient
	override val team: String = "MT"

	fun readResolve(): Any? = ChatStaffModTeam
}

object ChatStaffBuildTeam : AbstractChatStaff() {
	@Transient
	override val team: String = "BT"

	fun readResolve(): Any? = ChatStaffModTeam
}

object ChatStaffCommunityTeam : AbstractChatStaff() {
	@Transient
	override val team: String = "CT"

	fun readResolve(): Any? = ChatStaffModTeam
}

object ChatStaffFeatureDevelopmentTeam : AbstractChatStaff() {
	@Transient
	override val team: String = "FD"

	fun readResolve(): Any? = ChatStaffFeatureDevelopmentTeam
}
