package com.fablesfantasyrp.plugin.magic.gui

import com.fablesfantasyrp.plugin.gui.GuiSingleChoice
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class SpellSelectorGui(plugin: JavaPlugin, spells: Collection<SpellData>)
	: GuiSingleChoice<SpellData>(plugin, "Please select a spell", spells.asSequence(), {
	ItemStack(Material.PAPER)
}, { "${it.displayName}\n${it.description}" }) {
}
