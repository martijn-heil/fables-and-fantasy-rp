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
package com.fablesfantasyrp.plugin.weights

import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.ShulkerBox
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta
import org.bukkit.inventory.meta.BundleMeta

data class SimpleWeighingResult(val weight: Int, val isSingular: Boolean = false, val material: Material? = null)

fun calculateWeight(items: Collection<ItemStack>, config: WeightsConfig): Int {
	val allItems = flattenItems(items.asSequence())
	val results = allItems.map { getSimpleWeight(it, config) }.toList()
	val additive = results.asSequence().filter { !it.isSingular }
	val singular = results.asSequence().filter { it.isSingular }.distinctBy { it.material }
	val all = additive.plus(singular)
	return all.sumOf { it.weight }
}

fun getSimpleWeight(item: ItemStack, config: WeightsConfig): SimpleWeighingResult {
	val singularWeight = config.singular[item.type]
	val additiveWeight = config.additive[item.type]
	val isSingular = singularWeight != null
	return SimpleWeighingResult(singularWeight ?: additiveWeight ?: 0, isSingular, if (isSingular) item.type else null)
}

fun flattenItems(items: Sequence<ItemStack>): Sequence<ItemStack> {
	return items.map { flattenItemStack(it) }.flatten()
}

fun flattenItemStack(item: ItemStack): Sequence<ItemStack> {
	return when {
		 Tag.SHULKER_BOXES.isTagged(item.type) -> {
			val shulker = (item.itemMeta as BlockStateMeta).blockState as ShulkerBox
			shulker.inventory.contents.asSequence().filterNotNull().map { flattenItemStack(it) }.flatten().plus(item)
		}

		item.type == Material.BUNDLE -> {
			val bundleMeta = item.itemMeta as BundleMeta
			bundleMeta.items.asSequence().map { flattenItemStack(it) }.flatten().plus(item)
		}

		else -> {
			sequenceOf(item)
		}
	}
}
