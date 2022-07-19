package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.chat.chat
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
		Pair(ChatStaffEvent.channelName, ChatStaffEvent),
		Pair(ChatStaffTechTeam.channelName, ChatStaffTechTeam),
		Pair(ChatStaffEventTeam.channelName, ChatStaffEventTeam),
		Pair(ChatStaffLoreTeam.channelName, ChatStaffLoreTeam),
		Pair(ChatStaffModTeam.channelName, ChatStaffModTeam),
		Pair(ChatStaffBuildTeam.channelName, ChatStaffBuildTeam),
		Pair(ChatStaffCommunityTeam.channelName, ChatStaffCommunityTeam),
		Pair(ChatStaffFeatureDevelopmentTeam.channelName, ChatStaffFeatureDevelopmentTeam),
), ChatStaffStandard), Serializable {
	fun readResolve(): Any? = ChatStaff
	override fun getRecipients(from: Player): Sequence<Player>
		= Bukkit.getOnlinePlayers().asSequence().filter { it.hasPermission(Permission.Channel.Staff) }
}

abstract class AbstractChatStaff : ChatChannel, RawChatChannel, CommandSenderCompatibleChatChannel, PreviewableChatChannel, Serializable {
	abstract val channelName: String

	override fun getRecipients(from: Player): Sequence<Player> =
			Bukkit.getOnlinePlayers().asSequence()
					.filter { it.hasPermission(Permission.Channel.Staff + ".${channelName.lowercase()}") }

	override fun getRecipients(from: CommandSender): Sequence<Player> =
			Bukkit.getOnlinePlayers().asSequence()
					.filter { it.hasPermission(Permission.Channel.Staff + ".${channelName.lowercase()}") }

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

		val teamName = if (channelName != "") channelName else null
		val channelName = Component.text(listOfNotNull("ST", teamName).joinToString("#"))

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
	override fun toString() = "staff#${channelName.lowercase()}"
}

object ChatStaffStandard : AbstractChatStaff() {
	@Transient
	override val channelName: String = ""

	fun readResolve(): Any? = ChatStaffStandard
	private const val serialVersionUID: Long = 1
}

object ChatStaffEvent : AbstractChatStaff(), ToggleableChatChannel {
	@Transient
	override val channelName: String = "EVENT"

	override fun getRecipients(from: CommandSender): Sequence<Player>
		= super.getRecipients(from).filter { !it.chat.disabledChannels.contains(this) }

	override fun getRecipients(from: Player): Sequence<Player>
		= super.getRecipients(from).filter { !it.chat.disabledChannels.contains(this) }

	fun readResolve(): Any? = ChatStaffEvent
	private const val serialVersionUID: Long = 1
}

object ChatStaffTechTeam : AbstractChatStaff() {
	@Transient
	override val channelName: String = "TT"

	fun readResolve(): Any? = ChatStaffTechTeam
	private const val serialVersionUID: Long = 1
}

object ChatStaffEventTeam : AbstractChatStaff() {
	@Transient
	override val channelName: String = "ET"

	fun readResolve(): Any? = ChatStaffEventTeam
	private const val serialVersionUID: Long = 1
}

object ChatStaffLoreTeam : AbstractChatStaff() {
	@Transient
	override val channelName: String = "LT"

	fun readResolve(): Any? = ChatStaffLoreTeam
	private const val serialVersionUID: Long = 1
}

object ChatStaffModTeam : AbstractChatStaff() {
	@Transient
	override val channelName: String = "MT"

	fun readResolve(): Any? = ChatStaffModTeam
	private const val serialVersionUID: Long = 1
}

object ChatStaffBuildTeam : AbstractChatStaff() {
	@Transient
	override val channelName: String = "BT"

	fun readResolve(): Any? = ChatStaffModTeam
	private const val serialVersionUID: Long = 1
}

object ChatStaffCommunityTeam : AbstractChatStaff() {
	@Transient
	override val channelName: String = "CT"

	fun readResolve(): Any? = ChatStaffModTeam
	private const val serialVersionUID: Long = 1
}

object ChatStaffFeatureDevelopmentTeam : AbstractChatStaff() {
	@Transient
	override val channelName: String = "FD"

	fun readResolve(): Any? = ChatStaffFeatureDevelopmentTeam
	private const val serialVersionUID: Long = 1
}
