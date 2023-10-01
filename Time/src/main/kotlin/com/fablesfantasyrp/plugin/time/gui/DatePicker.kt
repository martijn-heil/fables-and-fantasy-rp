package com.fablesfantasyrp.plugin.time.gui

import com.fablesfantasyrp.plugin.gui.Icon
import com.fablesfantasyrp.plugin.gui.ResultProducingInventoryGui
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.time.format.TextStyle

class DatePicker(plugin: JavaPlugin, title: String)
	: ResultProducingInventoryGui<FablesLocalDate>(plugin, title, arrayOf(
		"   321   ",
		"   dmy   ",
		"   654   ",
		"    v    ")) {
	private var date = FablesLocalDate.now()
		set(value) {
			field = value
			draw()
		}

	init {
		this.addElement(StaticGuiElement('1', Icon.UP, {
			date = date.plusYears(1)
			true
		}, "${ChatColor.GREEN}+"))

		this.addElement(StaticGuiElement('2', Icon.UP, {
			date = date.plusMonths(1)
			true
		}, "${ChatColor.GREEN}+"))

		this.addElement(StaticGuiElement('3', Icon.UP, {
			date = date.plusDays(1)
			true
		}, "${ChatColor.GREEN}+"))

		this.addElement(StaticGuiElement('4', Icon.DOWN, {
			date = date.minusYears(1)
			true
		}, "${ChatColor.RED}-"))

		this.addElement(StaticGuiElement('5', Icon.DOWN, {
			date = date.minusMonths(1)
			true
		}, "${ChatColor.RED}-"))

		this.addElement(StaticGuiElement('6', Icon.DOWN, {
			date = date.minusDays(1)
			true
		}, "${ChatColor.RED}-"))

		this.addElement(DynamicGuiElement('v') { _ ->
			StaticGuiElement('v', Icon.CHECKMARK, {
				result.complete(date)
				true
			}, "${ChatColor.GRAY}${formatDate()}\n" +
				"${ChatColor.GRAY}${ChatColor.ITALIC}${formatDateFancy()}${ChatColor.GRAY}\n" +
				"${ChatColor.GREEN}Confirm")
		})

		this.addElement(DynamicGuiElement('d') { _ ->
			StaticGuiElement('d', ItemStack(Material.PURPLE_WOOL),
				ChatColor.GRAY.toString() + String.format("%02d", date.dayOfMonth))
		})

		this.addElement(DynamicGuiElement('m') { _ ->
			StaticGuiElement('m', ItemStack(Material.PURPLE_WOOL),
				ChatColor.GRAY.toString() + String.format("%02d", date.monthValue))
		})

		this.addElement(DynamicGuiElement('y') { _ ->
			StaticGuiElement('y', ItemStack(Material.PURPLE_WOOL),
				ChatColor.GRAY.toString() + String.format("%04d", date.year))
		})
	}

	private fun formatDateFancy(): String {
		val dayOfWeekName = date.dayOfWeek.getDisplayName(TextStyle.FULL)
		val monthName = date.getMonth().getDisplayName(TextStyle.FULL)

		return "$dayOfWeekName ${date.dayOfMonth}, $monthName, ${date.year}"
	}

	private fun formatDate() =
		String.format("%02d", date.dayOfMonth) + "." +
		String.format("%02d", date.monthValue) + "." +
		String.format("%04d", date.year)
}
