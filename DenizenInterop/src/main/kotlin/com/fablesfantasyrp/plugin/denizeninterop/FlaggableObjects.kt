package com.fablesfantasyrp.plugin.denizeninterop

import com.denizenscript.denizen.objects.EntityTag
import com.denizenscript.denizen.objects.ItemTag
import com.denizenscript.denizencore.flags.AbstractFlagTracker
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack

val Entity.dFlags: AbstractFlagTracker
	get() = EntityTag(this).flagTracker

val ItemStack.dFlags: AbstractFlagTracker
	get() = ItemTag(this).flagTracker

val Server.dFlags: AbstractFlagTracker
	get() = TODO()

fun example() {
	val entity = Bukkit.getServer().worlds.first().entities.first()
	val t = EntityTag(entity)
	entity.dFlags.getFlagValue("test").asElement().asString()
}
