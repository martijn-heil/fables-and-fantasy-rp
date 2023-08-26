package com.fablesfantasyrp.plugin.magic.gui

import com.fablesfantasyrp.plugin.form.promptGui
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import com.fablesfantasyrp.plugin.magic.dal.repository.SpellDataRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.getMaxSpells
import com.fablesfantasyrp.plugin.magic.getRequiredMageLevel
import com.github.shynixn.mccoroutine.bukkit.launch
import de.themoep.inventorygui.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.Function

class SpellbookGui(plugin: JavaPlugin,
				   private val spells: SpellDataRepository,
				   private val mage: Mage,
				   private val readOnly: Boolean = false)
	: InventoryGui(plugin, "${mage.character.name}'s grimoire",
		arrayOf("agggggggg",
				"bhhhhhhhh",
				"cjjjjjjjj",
				"dkkkkkkkk",
				"ellllllll",
				"  n   y  ")) {
	private val spellLevels = HashMap<Int, MutableMap<Int, DynamicSpellSlotElement>>()

	init {
		val spells = mage.spells.groupBy { it.level }
		for (spellLevel in 1..5) {
			val slots = HashMap<Int, DynamicSpellSlotElement>()
			val spellsInLevel = spells[spellLevel] ?: emptyList()
			for (slot in 1..getMaxSpells(10, spellLevel)) {
				val spell = spellsInLevel.getOrNull(slot-1)
				slots[slot] = DynamicSpellSlotElement(spellLevel, slot, spell == null, spell)
			}

			spellLevels[spellLevel] = slots
		}

		val spellLevelCharMap = mapOf(
				Pair(1, 'g'),
				Pair(2, 'h'),
				Pair(3, 'j'),
				Pair(4, 'k'),
				Pair(5, 'l'))

		val groups = spellLevelCharMap.map {
			val group = GuiElementGroup(it.value)
			group.addElements(spellLevels[it.key]!!.values as Collection<GuiElement>)
			group
		}

		this.addElements(groups)
		this.addElement(SpellLevelTitleElement(1, 'a'))
		this.addElement(SpellLevelTitleElement(2, 'b'))
		this.addElement(SpellLevelTitleElement(3, 'c'))
		this.addElement(SpellLevelTitleElement(4, 'd'))
		this.addElement(SpellLevelTitleElement(5, 'e'))

		if (!readOnly) {
			this.addElement(StaticGuiElement('y', ItemStack(Material.GREEN_WOOL), {
				this.confirm()
				true
			}, "${ChatColor.GREEN}Confirm"))

			this.addElement(StaticGuiElement('n', ItemStack(Material.RED_WOOL), {
				this.cancel()
				true
			}, "${ChatColor.RED}Cancel"))
		}
	}

	fun confirm() {
		this.mage.spells = spellLevels.values.map { it.values.mapNotNull { it.content } }.flatten()
		this.close(true)
	}

	fun cancel() {
		this.close(true)
	}

	private class SpellLevelTitleElement(spellLevel: Int, character: Char)
		: StaticGuiElement(character, ItemStack(Material.ENDER_EYE), "${ChatColor.GOLD}Level $spellLevel spells")

	private inner class DynamicSpellSlotElement(val spellLevel: Int,
												val n: Int,
												val isMutable: Boolean,
												var content: SpellData?)
		: DynamicGuiElement('e', { who -> StaticGuiElement('e', ItemStack(Material.GRAY_STAINED_GLASS_PANE)) }) {
		init {
			check(n <= getMaxSpells(10, spellLevel))
		}

		private val spellSlots: Map<Int, DynamicSpellSlotElement>
			get() = (gui as SpellbookGui).spellLevels[spellLevel]!!

		private val requiredMageLevel = getRequiredMageLevel(spellLevel, n)!!
		private val currentMageLevel = mage.magicLevel

		private val chooseSpellAction: GuiElement.Action = GuiElement.Action { click ->
			val player = click.whoClicked as? Player ?: return@Action true
			plugin.launch {
				val mappedSpellSlots = spellSlots.values.map { it.content }
				val gui = SpellSelectorGui(plugin, spells.forLevelAndPath(spellLevel, mage.magicPath)
						.filter { !mage.spells.contains(it) }
						.filter { !mappedSpellSlots.contains(it) })
				val spell = player.promptGui(gui)
				content = spell
				this@DynamicSpellSlotElement.gui.draw()
			}
			true
		}

		private var query: Function<HumanEntity, GuiElement> = Function {
			if (content != null) {
				val spell = content!!
				val itemStack = ItemStack(Material.PAPER)
				if (this.isMutable) {
					return@Function StaticGuiElement('e', itemStack, {
						this.content = null
						gui.draw()
						true
					},"${ChatColor.RESET}" +
							"${spell.displayName}\n" +
							"${spell.description}\n" +
							"\n${ChatColor.RED}Click to remove")
				} else {
					return@Function StaticGuiElement('e', itemStack,
							"${ChatColor.RESET}" +
									"${spell.displayName}\n" +
									spell.description)
				}
			} else if (requiredMageLevel > currentMageLevel) {
				return@Function StaticGuiElement('e', ItemStack(Material.RED_STAINED_GLASS_PANE),
						"Slot available from mage level $requiredMageLevel")
			} else {
				if (!readOnly) {
					return@Function StaticGuiElement('e', ItemStack(Material.GREEN_STAINED_GLASS_PANE), chooseSpellAction,
							"${ChatColor.RESET}Available slot.\n${ChatColor.GREEN}Click to choose a spell!")
				} else {
					return@Function StaticGuiElement('e', ItemStack(Material.GREEN_STAINED_GLASS_PANE),
							"${ChatColor.RESET}Available slot.")
				}
			}
		}

		override fun getQuery(): Function<HumanEntity, GuiElement> = query
		override fun setQuery(query: Function<HumanEntity, GuiElement>?) { if (query != null) this.query = query }
	}
}
