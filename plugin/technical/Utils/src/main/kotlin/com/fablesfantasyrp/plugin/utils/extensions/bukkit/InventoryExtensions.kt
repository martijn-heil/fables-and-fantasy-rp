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
package com.fablesfantasyrp.plugin.utils.extensions.bukkit

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.math.min

fun Inventory.countSimilar(item: ItemStack): Int = contents.asSequence()
	.filterNotNull()
	.filter { it.isSimilar(item) }
	.sumOf { it.amount }

fun Inventory.withdrawSimilar(item: ItemStack, max: Int = Int.MAX_VALUE): ItemStack {
	val result = item.asQuantity(0)

	for ((index, value) in this.contents.withIndex()) {
		if (result.amount >= max) break

		if (value?.isSimilar(item) == true) {
			val take = min(value.amount, max)
			result.amount += take
			value.amount -= take
			if (value.amount == 0) {
				this.setItem(index, null)
			}
		}
	}

	return result
}

fun Inventory.deposit(item: ItemStack): ItemStack? {
	var leftOver = item.splitStacks().toTypedArray()
	var totalAmountLeft = leftOver.sumOf { it.amount }

	while (leftOver.isNotEmpty()) {
		val leftOverAmount = addItem(*leftOver).values.sumOf { it.amount }
		leftOver = item.asQuantity(leftOverAmount).splitStacks().toTypedArray()

		if (leftOverAmount == totalAmountLeft) {
			// No space left
			break
		}

		totalAmountLeft = leftOverAmount
	}

	return if (totalAmountLeft > 0) item.asQuantity(totalAmountLeft) else null
}
