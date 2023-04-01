package com.fablesfantasyrp.plugin.weights

import org.bukkit.Material
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
	return when (item.type) {
		Material.SHULKER_BOX -> {
			val shulker = (item.itemMeta as BlockStateMeta).blockState as ShulkerBox
			shulker.inventory.contents.asSequence().filterNotNull().map { flattenItemStack(it) }.flatten().plus(item)
		}
		Material.BUNDLE -> {
			val bundleMeta = item.itemMeta as BundleMeta
			bundleMeta.items.asSequence().map { flattenItemStack(it) }.flatten().plus(item)
		}
		else -> {
			sequenceOf(item)
		}
	}
}
