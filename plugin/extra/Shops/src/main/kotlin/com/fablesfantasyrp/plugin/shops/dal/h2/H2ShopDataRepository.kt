package com.fablesfantasyrp.plugin.shops.dal.h2

import com.fablesfantasyrp.plugin.database.getUuid
import com.fablesfantasyrp.plugin.database.repository.BaseH2KeyedRepository
import com.fablesfantasyrp.plugin.database.setUuid
import com.fablesfantasyrp.plugin.shops.dal.model.ShopData
import com.fablesfantasyrp.plugin.shops.dal.repository.ShopDataRepository
import com.fablesfantasyrp.plugin.utils.SerializableItemStack
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Timestamp
import java.sql.Types
import javax.sql.DataSource

class H2ShopDataRepository(private val dataSource: DataSource)
	: BaseH2KeyedRepository<Int, ShopData>(Int::class.java, dataSource), ShopDataRepository {
	override val TABLE_NAME = "FABLES_SHOPS.SHOP"

	override fun create(v: ShopData): ShopData {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
				"(" +
				"location_x, " +
				"location_y, " +
				"location_z, " +
				"world, " +
				"owner, " +
				"item, " +
				"last_active, " +
				"amount, " +
				"buy_price, " +
				"sell_price, " +
				"stock" +
				") " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
			stmnt.setInt(1, v.location.x)
			stmnt.setInt(2, v.location.y)
			stmnt.setInt(3, v.location.z)
			stmnt.setUuid(4, v.location.world)
			if (v.owner != null) stmnt.setInt(5, v.owner) else stmnt.setNull(5, Types.INTEGER)
			stmnt.setObject(6, SerializableItemStack(v.item))
			stmnt.setTimestamp(7, Timestamp.from(v.lastActive))
			stmnt.setInt(8, v.amount)
			stmnt.setInt(9, v.buyPrice)
			stmnt.setInt(10, v.sellPrice)
			stmnt.setInt(11, v.stock)

			stmnt.executeUpdate()
			val rs = stmnt.generatedKeys
			rs.next()
			val id = rs.getInt(1)
			return v.copy(id = id)
		}
	}

	override fun update(v: ShopData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
				"location_x = ?, " +
				"location_y = ?, " +
				"location_z = ?, " +
				"world = ?, " +
				"owner = ?, " +
				"item = ?, " +
				"last_active = ?, " +
				"amount = ?, " +
				"buy_price = ?, " +
				"sell_price = ?, " +
				"stock = ? " +
				"WHERE id = ?")
			stmnt.setInt(1, v.location.x)
			stmnt.setInt(2, v.location.y)
			stmnt.setInt(3, v.location.z)
			stmnt.setUuid(4, v.location.world)
			if (v.owner != null) stmnt.setInt(5, v.owner) else stmnt.setNull(5, Types.INTEGER)
			stmnt.setObject(6, SerializableItemStack(v.item))
			stmnt.setTimestamp(7, Timestamp.from(v.lastActive))
			stmnt.setInt(8, v.amount)
			stmnt.setInt(9, v.buyPrice)
			stmnt.setInt(10, v.sellPrice)
			stmnt.setInt(11, v.stock)

			stmnt.setInt(12, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun createOrUpdate(v: ShopData): ShopData {
		throw NotImplementedError()
	}

	override fun forId(id: Int): ShopData? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return null
			fromRow(result)
		}
	}

	override fun fromRow(row: ResultSet): ShopData {
		val id = row.getInt("id")
		val locationX = row.getInt("location_x")
		val locationY = row.getInt("location_y")
		val locationZ = row.getInt("location_z")
		val world = row.getUuid("world")!!
		val location = BlockIdentifier(world, locationX, locationY, locationZ)
		val owner = row.getObject("owner") as Int?
		val item = (row.getObject("item") as SerializableItemStack).itemStack
		val lastActive = row.getTimestamp("last_active").toInstant()
		val amount = row.getInt("amount")
		val buyPrice = row.getInt("buy_price")
		val sellPrice = row.getInt("sell_price")
		val stock = row.getInt("stock")

		return ShopData(
			id = id,
			location = location,
			owner = owner,
			item = item,
			lastActive = lastActive,
			amount = amount,
			buyPrice = buyPrice,
			sellPrice = sellPrice,
			stock = stock
		)
	}
}
