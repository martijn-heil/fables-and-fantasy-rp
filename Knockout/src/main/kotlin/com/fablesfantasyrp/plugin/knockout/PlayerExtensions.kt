package com.fablesfantasyrp.plugin.knockout

import com.fablesfantasyrp.plugin.knockout.data.entity.KnockoutPlayerEntity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

val OfflinePlayer.knockout: KnockoutPlayerEntity
	get() = knockoutPlayerDataManager.forId(uniqueId)!!

fun Player.sendPrefixedMessage(msg: String)
	= this.sendMessage(SYSPREFIX.append(Component.text(msg).color(NamedTextColor.GRAY)))
