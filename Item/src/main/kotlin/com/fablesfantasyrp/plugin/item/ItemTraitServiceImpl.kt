package com.fablesfantasyrp.plugin.item

import com.denizenscript.denizen.objects.ItemTag
import com.denizenscript.denizencore.objects.core.ListTag
import com.denizenscript.denizencore.objects.core.MapTag
import org.bukkit.inventory.ItemStack

class ItemTraitServiceImpl : ItemTraitService {
	override fun getTraits(item: ItemStack): Set<String> {
		val meta = ItemTag(item).flagTracker.getFlagValue("meta") as? MapTag ?: return emptySet()
		val traits = meta.getObject("traits") as? ListTag ?: return emptySet()
		return traits.toHashSet()
	}
}
