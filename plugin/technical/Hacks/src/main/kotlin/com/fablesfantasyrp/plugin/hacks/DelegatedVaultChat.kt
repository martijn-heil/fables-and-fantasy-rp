package com.fablesfantasyrp.plugin.hacks

import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.entity.Player

open class DelegatedVaultChat(val child: Chat, perms: Permission) : Chat(perms) {
	override fun getName(): String = child.name
	override fun isEnabled() = child.isEnabled

	override fun getPlayerPrefix(world: String?, player: OfflinePlayer?): String 												= child.getPlayerPrefix(world, player)
	override fun getPlayerPrefix(player: Player?): String 																		= child.getPlayerPrefix(player)
	override fun setPlayerPrefix(world: String?, player: OfflinePlayer?, prefix: String?) 										= child.setPlayerPrefix(world, player, prefix)
	override fun setPlayerPrefix(player: Player?, prefix: String?) 																= child.setPlayerPrefix(player, prefix)
	override fun getPlayerSuffix(world: String?, player: OfflinePlayer?): String 												= child.getPlayerSuffix(world, player)
	override fun getPlayerSuffix(player: Player?): String 																		= child.getPlayerSuffix(player)
	override fun setPlayerSuffix(world: String?, player: OfflinePlayer?, suffix: String?) 										= child.setPlayerSuffix(world, player, suffix)
	override fun setPlayerSuffix(player: Player?, suffix: String?) 																= child.setPlayerSuffix(player, suffix)
	override fun getGroupPrefix(world: String?, group: String?): String 														= child.getGroupPrefix(world, group)
	override fun getGroupPrefix(world: World?, group: String?): String 															= child.getGroupPrefix(world, group)
	override fun setGroupPrefix(world: String?, group: String?, prefix: String?) 												= child.setGroupPrefix(world, group, prefix)
	override fun setGroupPrefix(world: World?, group: String?, prefix: String?) 												= child.setGroupPrefix(world, group, prefix)
	override fun getGroupSuffix(world: String?, group: String?): String 														= child.getGroupSuffix(world, group)
	override fun getGroupSuffix(world: World?, group: String?): String 															= child.getGroupSuffix(world, group)
	override fun setGroupSuffix(world: String?, group: String?, suffix: String?) 												= child.setGroupSuffix(world, group, suffix)
	override fun setGroupSuffix(world: World?, group: String?, suffix: String?) 												= child.setGroupSuffix(world, group, suffix)
	override fun getPlayerInfoInteger(world: String?, player: OfflinePlayer?, node: String?, defaultValue: Int): Int 			= child.getPlayerInfoInteger(world, player, node, defaultValue)
	override fun getPlayerInfoInteger(player: Player?, node: String?, defaultValue: Int): Int 									= child.getPlayerInfoInteger(player, node, defaultValue)
	override fun setPlayerInfoInteger(world: String?, player: OfflinePlayer?, node: String?, value: Int) 						= child.setPlayerInfoInteger(world, player, node, value)
	override fun setPlayerInfoInteger(player: Player?, node: String?, value: Int) 												= child.setPlayerInfoInteger(player, node, value)
	override fun getGroupInfoInteger(world: String?, group: String?, node: String?, defaultValue: Int) 							= child.getGroupInfoInteger(world, group, node, defaultValue)
	override fun getGroupInfoInteger(world: World?, group: String?, node: String?, defaultValue: Int): Int 						= child.getGroupInfoInteger(world, group, node, defaultValue)
	override fun setGroupInfoInteger(world: String?, group: String?, node: String?, value: Int) 								= child.setGroupInfoInteger(world, group, node, value)
	override fun setGroupInfoInteger(world: World?, group: String?, node: String?, value: Int) 									= child.setGroupInfoInteger(world, group, node, value)
	override fun getPlayerInfoDouble(world: String?, player: OfflinePlayer?, node: String?, defaultValue: Double): Double 		= child.getPlayerInfoDouble(world, player, node, defaultValue)
	override fun getPlayerInfoDouble(player: Player?, node: String?, defaultValue: Double): Double 								= child.getPlayerInfoDouble(player, node, defaultValue)
	override fun setPlayerInfoDouble(world: String?, player: OfflinePlayer?, node: String?, value: Double) 						= child.setPlayerInfoDouble(world, player, node, value)
	override fun setPlayerInfoDouble(player: Player?, node: String?, value: Double) 											= child.setPlayerInfoDouble(player, node, value)
	override fun getGroupInfoDouble(world: String?, group: String?, node: String?, defaultValue: Double) 						= child.getGroupInfoDouble(world, group, node, defaultValue)
	override fun getGroupInfoDouble(world: World?, group: String?, node: String?, defaultValue: Double): Double 				= child.getGroupInfoDouble(world, group, node, defaultValue)
	override fun setGroupInfoDouble(world: String?, group: String?, node: String?, value: Double) 								= child.setGroupInfoDouble(world, group, node, value)
	override fun setGroupInfoDouble(world: World?, group: String?, node: String?, value: Double) 								= child.setGroupInfoDouble(world, group, node, value)
	override fun getPlayerInfoBoolean(world: String?, player: OfflinePlayer?, node: String?, defaultValue: Boolean): Boolean 	= child.getPlayerInfoBoolean(world, player, node, defaultValue)
	override fun getPlayerInfoBoolean(player: Player?, node: String?, defaultValue: Boolean): Boolean 							= child.getPlayerInfoBoolean(player, node, defaultValue)
	override fun setPlayerInfoBoolean(world: String?, player: OfflinePlayer?, node: String?, value: Boolean) 					= child.setPlayerInfoBoolean(world, player, node, value)
	override fun setPlayerInfoBoolean(player: Player?, node: String?, value: Boolean) 											= child.setPlayerInfoBoolean(player, node, value)
	override fun getGroupInfoBoolean(world: String?, group: String?, node: String?, defaultValue: Boolean) 						= child.getGroupInfoBoolean(world, group, node, defaultValue)
	override fun getGroupInfoBoolean(world: World?, group: String?, node: String?, defaultValue: Boolean): Boolean 				= child.getGroupInfoBoolean(world, group, node, defaultValue)
	override fun setGroupInfoBoolean(world: String?, group: String?, node: String?, value: Boolean) 							= child.setGroupInfoBoolean(world, group, node, value)
	override fun setGroupInfoBoolean(world: World?, group: String?, node: String?, value: Boolean) 								= child.setGroupInfoBoolean(world, group, node, value)
	override fun getPlayerInfoString(world: String?, player: OfflinePlayer?, node: String?, defaultValue: String?): String 		= child.getPlayerInfoString(world, player, node, defaultValue)
	override fun getPlayerInfoString(player: Player?, node: String?, defaultValue: String?): String 							= child.getPlayerInfoString(player, node, defaultValue)
	override fun setPlayerInfoString(world: String?, player: OfflinePlayer?, node: String?, value: String?) 					= child.setPlayerInfoString(world, player, node, value)
	override fun setPlayerInfoString(player: Player?, node: String?, value: String?) 											= child.setPlayerInfoString(player, node, value)
	override fun getGroupInfoString(world: String?, group: String?, node: String?, defaultValue: String?): String 				= child.getGroupInfoString(world, group, node, defaultValue)
	override fun getGroupInfoString(world: World?, group: String?, node: String?, defaultValue: String?): String 				= child.getGroupInfoString(world, group, node, defaultValue)
	override fun setGroupInfoString(world: String?, group: String?, node: String?, value: String?) 								= child.setGroupInfoString(world, group, node, value)
	override fun setGroupInfoString(world: World?, group: String?, node: String?, value: String?) 								= child.setGroupInfoString(world, group, node, value)
	override fun playerInGroup(world: String?, player: OfflinePlayer?, group: String?): Boolean 								= child.playerInGroup(world, player, group)
	override fun playerInGroup(player: Player?, group: String?): Boolean 														= child.playerInGroup(player, group)
	override fun getPlayerGroups(world: String?, player: OfflinePlayer?): Array<String> 										= child.getPlayerGroups(world, player)
	override fun getPlayerGroups(player: Player?): Array<String> 																= child.getPlayerGroups(player)
	override fun getPrimaryGroup(world: String?, player: OfflinePlayer?): String 												= child.getPrimaryGroup(world, player)
	override fun getPrimaryGroup(player: Player?): String 																		= child.getPrimaryGroup(player)
	override fun getGroups(): Array<String> 																					= child.groups

	@Deprecated("Deprecated in Java")
	override fun getPlayerPrefix(world: String?, player: String?): String
		= child.getPlayerPrefix(world, player)

	@Deprecated("Deprecated in Java")
	override fun getPlayerPrefix(world: World?, player: String?): String
		= child.getPlayerPrefix(world, player)

	@Deprecated("Deprecated in Java")
	override fun setPlayerPrefix(world: String?, player: String?, prefix: String?)
		= child.setPlayerPrefix(world, player, prefix)

	@Deprecated("Deprecated in Java")
	override fun setPlayerPrefix(world: World?, player: String?, prefix: String?)
		= child.setPlayerPrefix(world, player, prefix)

	@Deprecated("Deprecated in Java")
	override fun getPlayerSuffix(world: String?, player: String?): String
		= child.getPlayerSuffix(world, player)

	@Deprecated("Deprecated in Java")
	override fun getPlayerSuffix(world: World?, player: String?): String
		= child.getPlayerSuffix(world, player)

	@Deprecated("Deprecated in Java")
	override fun setPlayerSuffix(world: String?, player: String?, suffix: String?)
		= child.setPlayerSuffix(world, player, suffix)

	@Deprecated("Deprecated in Java")
	override fun setPlayerSuffix(world: World?, player: String?, suffix: String?)
		= child.setPlayerSuffix(world, player, suffix)

	@Deprecated("Deprecated in Java")
	override fun getPlayerInfoInteger(world: String?, player: String?, node: String?, defaultValue: Int)
		= child.getPlayerInfoInteger(world, player, node, defaultValue)

	@Deprecated("Deprecated in Java")
	override fun getPlayerInfoInteger(world: World?, player: String?, node: String?, defaultValue: Int): Int
		= child.getPlayerInfoInteger(world, player, node, defaultValue)

	@Deprecated("Deprecated in Java")
	override fun setPlayerInfoInteger(world: String?, player: String?, node: String?, value: Int)
		= child.setPlayerInfoInteger(world, player, node, value)
	@Deprecated("Deprecated in Java")
	override fun setPlayerInfoInteger(world: World?, player: String?, node: String?, value: Int)
		= child.setPlayerInfoInteger(world, player, node, value)

	@Deprecated("Deprecated in Java")
	override fun getPlayerInfoDouble(world: String?, player: String?, node: String?, defaultValue: Double)
		= child.getPlayerInfoDouble(world, player, node, defaultValue)

	@Deprecated("Deprecated in Java")
	override fun getPlayerInfoDouble(world: World?, player: String?, node: String?, defaultValue: Double): Double
		= child.getPlayerInfoDouble(world, player, node, defaultValue)

	@Deprecated("Deprecated in Java")
	override fun setPlayerInfoDouble(world: String?, player: String?, node: String?, value: Double)
		= child.setPlayerInfoDouble(world, player, node, value)

	@Deprecated("Deprecated in Java")
	override fun setPlayerInfoDouble(world: World?, player: String?, node: String?, value: Double)
		= child.setPlayerInfoDouble(world, player, node, value)

	@Deprecated("Deprecated in Java")
	override fun getPlayerInfoBoolean(world: String?, player: String?, node: String?, defaultValue: Boolean)
		= child.getPlayerInfoBoolean(world, player, node, defaultValue)

	@Deprecated("Deprecated in Java")
	override fun getPlayerInfoBoolean(world: World?, player: String?, node: String?, defaultValue: Boolean): Boolean
		= child.getPlayerInfoBoolean(world, player, node, defaultValue)

	@Deprecated("Deprecated in Java")
	override fun setPlayerInfoBoolean(world: String?, player: String?, node: String?, value: Boolean)
		= child.setPlayerInfoBoolean(world, player, node, value)

	@Deprecated("Deprecated in Java")
	override fun setPlayerInfoBoolean(world: World?, player: String?, node: String?, value: Boolean)
		= child.setPlayerInfoBoolean(world, player, node, value)

	@Deprecated("Deprecated in Java")
	override fun getPlayerInfoString(world: String?, player: String?, node: String?, defaultValue: String?)
		= child.getPlayerInfoString(world, player, node, defaultValue)

	@Deprecated("Deprecated in Java")
	override fun getPlayerInfoString(world: World?, player: String?, node: String?, defaultValue: String?): String
		= child.getPlayerInfoString(world, player, node, defaultValue)

	@Deprecated("Deprecated in Java")
	override fun setPlayerInfoString(world: String?, player: String?, node: String?, value: String?)
		= child.setPlayerInfoString(world, player, node, value)

	@Deprecated("Deprecated in Java")
	override fun setPlayerInfoString(world: World?, player: String?, node: String?, value: String?)
		= child.setPlayerInfoString(world, player, node, value)

	@Deprecated("Deprecated in Java")
	override fun playerInGroup(world: String?, player: String?, group: String?): Boolean
		= child.playerInGroup(world, player, group)

	@Deprecated("Deprecated in Java")
	override fun playerInGroup(world: World?, player: String?, group: String?): Boolean
		= child.playerInGroup(world, player, group)

	@Deprecated("Deprecated in Java")
	override fun getPlayerGroups(world: String?, player: String?): Array<String>
		= child.getPlayerGroups(world, player)

	@Deprecated("Deprecated in Java")
	override fun getPlayerGroups(world: World?, player: String?): Array<String>
		= child.getPlayerGroups(world, player)

	@Deprecated("Deprecated in Java")
	override fun getPrimaryGroup(world: String?, player: String?): String
		= child.getPrimaryGroup(world, player)

	@Deprecated("Deprecated in Java")
	override fun getPrimaryGroup(world: World?, player: String?): String
		= child.getPrimaryGroup(world, player)
}
