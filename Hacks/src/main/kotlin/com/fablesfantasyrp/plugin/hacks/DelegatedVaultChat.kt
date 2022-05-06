package com.fablesfantasyrp.plugin.hacks

import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission

open class DelegatedVaultChat(val child: Chat, perms: Permission) : Chat(perms) {
	override fun getName() = child.name
	override fun isEnabled() = child.isEnabled

	@Deprecated("Deprecated in Java")
	override fun getPlayerPrefix(world: String?, player: String?)
		= child.getPlayerPrefix(world, player)

	override fun setPlayerPrefix(world: String?, player: String?, prefix: String?)
		= child.setPlayerPrefix(world, player, prefix)

	override fun getPlayerSuffix(world: String?, player: String?)
		= child.getPlayerSuffix(world, player)

	override fun setPlayerSuffix(world: String?, player: String?, suffix: String?)
		= child.setPlayerSuffix(world, player, suffix)

	override fun getGroupPrefix(world: String?, group: String?)
		= child.getGroupPrefix(world, group)

	override fun setGroupPrefix(world: String?, group: String?, prefix: String?)
		= child.setGroupPrefix(world, group, prefix)

	override fun getGroupSuffix(world: String?, group: String?)
		= child.getGroupSuffix(world, group)

	override fun setGroupSuffix(world: String?, group: String?, suffix: String?)
		= child.setGroupSuffix(world, group, suffix)

	override fun getPlayerInfoInteger(world: String?, player: String?, node: String?, defaultValue: Int)
		= child.getPlayerInfoInteger(world, player, node, defaultValue)

	override fun setPlayerInfoInteger(world: String?, player: String?, node: String?, value: Int)
		= child.setPlayerInfoInteger(world, player, node, value)

	override fun getGroupInfoInteger(world: String?, group: String?, node: String?, defaultValue: Int)
		= child.getGroupInfoInteger(world, group, node, defaultValue)

	override fun setGroupInfoInteger(world: String?, group: String?, node: String?, value: Int)
		 = child.setGroupInfoInteger(world, group, node, value)

	override fun getPlayerInfoDouble(world: String?, player: String?, node: String?, defaultValue: Double)
		= child.getPlayerInfoDouble(world, player, node, defaultValue)

	override fun setPlayerInfoDouble(world: String?, player: String?, node: String?, value: Double)
		 = child.setPlayerInfoDouble(world, player, node, value)

	override fun getGroupInfoDouble(world: String?, group: String?, node: String?, defaultValue: Double)
		 = child.getGroupInfoDouble(world, group, node, defaultValue)

	override fun setGroupInfoDouble(world: String?, group: String?, node: String?, value: Double)
		 = child.setGroupInfoDouble(world, group, node, value)

	override fun getPlayerInfoBoolean(world: String?, player: String?, node: String?, defaultValue: Boolean)
		= child.getPlayerInfoBoolean(world, player, node, defaultValue)

	override fun setPlayerInfoBoolean(world: String?, player: String?, node: String?, value: Boolean)
		 = child.setPlayerInfoBoolean(world, player, node, value)

	override fun getGroupInfoBoolean(world: String?, group: String?, node: String?, defaultValue: Boolean)
		 = child.getGroupInfoBoolean(world, group, node, defaultValue)

	override fun setGroupInfoBoolean(world: String?, group: String?, node: String?, value: Boolean)
		= child.setGroupInfoBoolean(world, group, node, value)

	@Deprecated("Deprecated in Java")
	override fun getPlayerInfoString(world: String?, player: String?, node: String?, defaultValue: String?)
		= child.getPlayerInfoString(world, player, node, defaultValue)

	override fun setPlayerInfoString(world: String?, player: String?, node: String?, value: String?)
		= child.setPlayerInfoString(world, player, node, value)

	override fun getGroupInfoString(world: String?, group: String?, node: String?, defaultValue: String?)
		= child.getGroupInfoString(world, group, node, defaultValue)

	override fun setGroupInfoString(world: String?, group: String?, node: String?, value: String?)
		= child.setGroupInfoString(world, group, node, value)
}
