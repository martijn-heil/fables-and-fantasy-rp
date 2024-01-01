package com.fablesfantasyrp.plugin.shops.dal.yaml

import com.fablesfantasyrp.plugin.database.sync.repository.Repository
import com.fablesfantasyrp.plugin.shops.dal.model.ShopData
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.configuration.file.YamlConfiguration
import java.time.Instant
import kotlin.math.max

/**
 * This is for importing UltimateShops' yaml storage
 */
class UltimateShopsDataRepository(private val server: Server,
								  private val file: YamlConfiguration) : Repository<ShopData> {

	override fun all(): Collection<ShopData> {
		return file.getKeys(false).mapNotNull { fromKey(it) }
	}

	private fun fromKey(key: String): ShopData? {
		val location = keyToBlockIdentifier(key) ?: return null
		val section = file.getConfigurationSection(key)!!

		val isPublic = section.getBoolean("admin")
		val item = section.getItemStack("item")!!.asOne()
		val buyPrice = section.getInt("buyPrice")
		val sellPrice = section.getInt("sellprice")
		val amount = max(0, section.getInt("stack"))
		val stock = if (isPublic) 0 else section.getInt("amount").let { if (it == Int.MAX_VALUE) 0 else max(0, it) }
		val ownerId = if (isPublic) null else section.getInt("host")
		val lastActive = Instant.ofEpochMilli(section.getLong("lastActive"))

		if (item.type == Material.AIR) return null

		return ShopData(
			location = location,
			owner = ownerId,
			item = item,
			lastActive = lastActive,
			amount = amount,
			buyPrice = buyPrice,
			sellPrice = sellPrice,
			stock = stock
		)
	}

	private fun keyToBlockIdentifier(key: String): BlockIdentifier? {
		val split = key.split(",")
		val worldName = split[0]
		val x = split[1].toInt()
		val y = split[2].toInt()
		val z = split[3].toInt()

		val world = server.getWorld(worldName) ?: return null

		return BlockIdentifier(world.uid, x, y, z)
	}
}
