package com.fablesfantasyrp.plugin.gui

import de.themoep.inventorygui.GuiElement
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.GuiPageElement
import de.themoep.inventorygui.GuiPageElement.PageAction
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin


open class GuiSingleChoice<E>(plugin: JavaPlugin,
						 title: String,
						 values: Sequence<E>,
						 getItemStack: (value: E) -> ItemStack,
						 getText: ((value: E) -> String)? = null
)
	: ResultProducingGui<E>(plugin, title,
		arrayOf("ggggggggg",
				"ggggggggg",
				"ggggggggg",
				"ggggggggg",
				"ggggggggg",
				"fp     nl")) {

	init {
		val group = GuiElementGroup('g')

		for (value in values) {
			val itemStack = getItemStack(value)
			val text = if (getText != null) getText(value) else null

			val action: (GuiElement.Click) -> Boolean = {
				this.result.complete(value)
				true
			}

			if (text != null) {
				group.addElement(StaticGuiElement('e', itemStack, action, text))
			} else {
				group.addElement(StaticGuiElement('e', itemStack, action))
			}
		}

		this.addElement(group)
		this.addElement(GuiPageElement('f', ItemStack(Material.ARROW), PageAction.FIRST, "Go to first page (current: %page%)"))
		this.addElement(GuiPageElement('p', ItemStack(Material.OAK_SIGN), PageAction.PREVIOUS, "Go to previous page (%prevpage%)"))
		this.addElement(GuiPageElement('n', ItemStack(Material.OAK_SIGN), PageAction.NEXT, "Go to next page (%nextpage%)"))
		this.addElement(GuiPageElement('l', ItemStack(Material.ARROW), PageAction.LAST, "Go to last page (%pages%)"))
	}
}
