package com.fablesfantasyrp.plugin.hacks

import net.milkbowl.vault.permission.Permission

open class DelegatedVaultPermission(val child: Permission) : Permission() {
	override fun getName(): String = child.name
	override fun isEnabled(): Boolean = child.isEnabled
	override fun hasSuperPermsCompat(): Boolean = child.hasSuperPermsCompat()
	override fun playerHas(world: String?, player: String?, permission: String?): Boolean = child.playerHas(world, player, permission)
	override fun playerAdd(world: String?, player: String?, permission: String?): Boolean = child.playerAdd(world, player, permission)
	override fun playerRemove(world: String?, player: String?, permission: String?): Boolean = child.playerRemove(world, player, permission)

	override fun groupHas(world: String?, group: String?, permission: String?): Boolean = child.groupHas(world, group, permission)
	override fun groupAdd(world: String?, group: String?, permission: String?): Boolean = child.groupAdd(world, group, permission)
	override fun groupRemove(world: String?, group: String?, permission: String?): Boolean = child.groupRemove(world, group, permission)
	override fun playerInGroup(world: String?, player: String?, group: String?): Boolean = child.playerInGroup(world, player, group)
	override fun playerAddGroup(world: String?, player: String?, group: String?): Boolean = child.playerAddGroup(world, player, group)
	override fun playerRemoveGroup(world: String?, player: String?, group: String?): Boolean = child.playerRemoveGroup(world, player, group)
	override fun getPlayerGroups(world: String?, player: String?): Array<String> = child.getPlayerGroups(world, player)
	override fun getPrimaryGroup(world: String?, player: String?): String = child.getPrimaryGroup(world, player)
	override fun getGroups(): Array<String> = child.groups
	override fun hasGroupSupport(): Boolean = child.hasGroupSupport()
}
