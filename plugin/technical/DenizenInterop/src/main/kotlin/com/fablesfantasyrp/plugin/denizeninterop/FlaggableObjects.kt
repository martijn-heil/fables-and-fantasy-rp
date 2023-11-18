package com.fablesfantasyrp.plugin.denizeninterop

import com.denizenscript.denizen.objects.EntityTag
import com.denizenscript.denizen.objects.ItemTag
import com.denizenscript.denizen.objects.PlayerTag
import com.denizenscript.denizencore.flags.AbstractFlagTracker
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack

val Entity.dFlagsEntity: AbstractFlagTracker
	get() = EntityTag(this).flagTracker

val OfflinePlayer.dFlags: AbstractFlagTracker
	get() = PlayerTag(this).flagTracker

val ItemStack.dFlags: AbstractFlagTracker
	get() = ItemTag(this).flagTracker

val Server.dFlags: AbstractFlagTracker
	get() = TODO()

fun example() {
	val entity = Bukkit.getServer().worlds.first().entities.first()
	val t = EntityTag(entity)
	entity.dFlagsEntity.getFlagValue("test").asElement().asString()
}
