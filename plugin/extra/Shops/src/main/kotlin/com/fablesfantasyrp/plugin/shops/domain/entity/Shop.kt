package com.fablesfantasyrp.plugin.shops.domain.entity

import com.fablesfantasyrp.plugin.characters.shortName
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomy
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.*
import com.fablesfantasyrp.plugin.utils.validation.CommandValidationException
import org.bukkit.entity.Player
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
	var amount: Int					set(value) { require(value in 1..64); if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var buyPrice: Int				set(value) { require(value >= 0); if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var sellPrice: Int				set(value) { require(value >= 0); if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var stock: Int					set(value) { require(value >= 0); if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	val customersCanBuy get() = buyPrice != 0
	val customersCanSell get() = sellPrice != 0
	val isPublic get() = owner == null

	suspend fun displayTitle() =
		if (isPublic) {
			"Public shop #${id}"
		} else {
			"${owner!!.shortName()}'s shop #${id}"
		}

	@Throws(CommandValidationException::class)
	fun sell(customerPlayer: Player,
			 customerEconomy: ProfileEconomy,
			 ownerEconomy: ProfileEconomy?) {
		val inventory = customerPlayer.inventory
		val available = inventory.countSimilar(this.item)
		if (available < this.amount) {
			throw CommandValidationException("You do not have $itemName")
		}

		val price = this.sellPrice

		if (ownerEconomy != null) {
			if (ownerEconomy.money < price) {
				throw CommandValidationException("The vendor does not have enough funds left!")
			}
			ownerEconomy.money -= price
		}

		inventory.withdrawSimilar(this.item, this.amount)
		this.stock += this.amount
		customerEconomy.money += price
		this.lastActive = Instant.now()
	}

	@Throws(CommandValidationException::class)
	fun buy(customerPlayer: Player,
			customerEconomy: ProfileEconomy,
			ownerEconomy: ProfileEconomy?) {
		if (this.stock < this.amount) {
			throw CommandValidationException("The shop is out of stock!")
		}

		val price = this.buyPrice

		if (customerEconomy.money < price) {
			throw CommandValidationException("You do not have enough funds to buy $itemName")
		}

		customerEconomy.money -= price
		this.stock -= this.amount
		val remainder = customerPlayer.inventory.deposit(this.item.asQuantity(this.amount))
		remainder?.splitStacks()?.forEach { customerPlayer.location.world.dropItem(customerPlayer.location, it) }

		if (ownerEconomy != null) {
			ownerEconomy.money += price
		}

		this.lastActive = Instant.now()
	}

	val itemName get() = this.item.formatNameWithAmount(this.amount)

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
		this.item = item.asOne()
		this.lastActive = lastActive
		this.amount = amount
		this.buyPrice = buyPrice
		this.sellPrice = sellPrice
		this.stock = stock

		this.dirtyMarker = dirtyMarker // Must be last
	}
}
