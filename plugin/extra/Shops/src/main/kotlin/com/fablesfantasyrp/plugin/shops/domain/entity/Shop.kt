package com.fablesfantasyrp.plugin.shops.domain.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import org.bukkit.inventory.ItemStack
import java.time.Instant

class Shop : DataEntity<Int, Shop> {
	override var dirtyMarker: DirtyMarker<Shop>? = null
	override val id: Int

	var isDestroyed: Boolean = false

	var location: BlockIdentifier	set(value) { if (field != value) { val oldValue = field; field = value; dirtyMarker?.markDirty(this, "location", oldValue, value) } }
	var owner: Profile?				set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var item: ItemStack				set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var lastActive: Instant			set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var amount: Int					set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var buyPrice: Int				set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var sellPrice: Int				set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var stock: Int					set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	val isBuying get() = buyPrice != 0
	val isSelling get() = sellPrice != 0

	constructor(location: BlockIdentifier,
				owner: Profile?,
				item: ItemStack,
				lastActive: Instant,
				amount: Int,
				buyPrice: Int,
				sellPrice: Int,
				stock: Int,
				id: Int = 0,
				dirtyMarker: DirtyMarker<Shop>? = null) {
		this.id = id
		this.location = location
		this.owner = owner
		this.item = item
		this.lastActive = lastActive
		this.amount = amount
		this.buyPrice = buyPrice
		this.sellPrice = sellPrice
		this.stock = stock

		this.dirtyMarker = dirtyMarker // Must be last
	}
}
