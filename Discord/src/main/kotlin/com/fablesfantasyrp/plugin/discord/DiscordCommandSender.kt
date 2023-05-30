package com.fablesfantasyrp.plugin.discord

import com.fablesfantasyrp.plugin.utils.EDEN
import com.fablesfantasyrp.plugin.utils.Services
import dev.kord.core.entity.User
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.plugin.Plugin
import java.util.*

class DiscordCommandSender(val user: User,
						   private val sendMessageCallback: (message: String) -> Unit) : CommandSender {
	private val vaultPermission = Services.get<net.milkbowl.vault.permission.Permission>()

	override fun sendMessage(message: String)
		= ChatColor.stripColor(message)!!.chunked(2000).forEach { sendMessageCallback(it) }

	override fun sendMessage(vararg messages: String) = messages.forEach { sendMessage(it) }
	override fun sendMessage(sender: UUID?, message: String) = sendMessage(message)
	override fun sendMessage(sender: UUID?, vararg messages: String) = messages.forEach { sendMessage(it) }

	override fun isOp(): Boolean = false
	override fun setOp(value: Boolean) {}

	override fun isPermissionSet(name: String): Boolean = true
	override fun isPermissionSet(perm: Permission): Boolean = true

	override fun hasPermission(name: String): Boolean = vaultPermission.groupHas(EDEN, "discord", name)
	override fun hasPermission(perm: Permission): Boolean = hasPermission(perm.name)

	override fun recalculatePermissions() {}

	override fun getName(): String = user.username
	override fun name(): Component = Component.text(name)

	override fun getServer(): Server = Bukkit.getServer()

	override fun spigot(): CommandSender.Spigot { throw NotImplementedError() }
	override fun addAttachment(plugin: Plugin, name: String, value: Boolean): PermissionAttachment { throw NotImplementedError() }
	override fun addAttachment(plugin: Plugin): PermissionAttachment { throw NotImplementedError() }
	override fun addAttachment(plugin: Plugin, name: String, value: Boolean, ticks: Int): PermissionAttachment? { throw NotImplementedError() }
	override fun addAttachment(plugin: Plugin, ticks: Int): PermissionAttachment? { throw NotImplementedError() }
	override fun removeAttachment(attachment: PermissionAttachment) { throw NotImplementedError() }
	override fun getEffectivePermissions(): MutableSet<PermissionAttachmentInfo> { throw NotImplementedError() }
}
