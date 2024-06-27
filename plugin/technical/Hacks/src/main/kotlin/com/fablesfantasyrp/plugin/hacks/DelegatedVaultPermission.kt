/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
