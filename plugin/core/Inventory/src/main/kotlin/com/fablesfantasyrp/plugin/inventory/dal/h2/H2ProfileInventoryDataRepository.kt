package com.fablesfantasyrp.plugin.inventory.dal.h2

import com.fablesfantasyrp.plugin.database.asSequence
import com.fablesfantasyrp.plugin.inventory.PassthroughInventory
import com.fablesfantasyrp.plugin.inventory.PassthroughPlayerInventory
import com.fablesfantasyrp.plugin.inventory.dal.model.ProfileInventoryData
import com.fablesfantasyrp.plugin.inventory.dal.repository.ProfileInventoryDataRepository
import java.sql.ResultSet
import javax.sql.DataSource

class H2ProfileInventoryDataRepository(private val dataSource: DataSource) : ProfileInventoryDataRepository {
	private val TABLE_NAME = "FABLES_INVENTORY.INVENTORY"

	override fun forOwner(profileId: Int): ProfileInventoryData {
		val inventory = this.forId(profileId) ?: run {
			this.create(ProfileInventoryData(
					id = profileId,
					inventory = PassthroughPlayerInventory.createEmpty(),
					enderChest = PassthroughInventory(arrayOfNulls(27)))
			)
		}
		return inventory
	}

	override fun all(): Collection<ProfileInventoryData> {
		return dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME")
				.executeQuery()
				.asSequence()
				.map { fromRow(it) }
				.toList()
		}
	}

	override fun destroy(v: ProfileInventoryData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun create(v: ProfileInventoryData): ProfileInventoryData {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(id, inventory, ender_chest) " +
					"VALUES (?, ?, ?)")
			stmnt.setInt(1, v.id)
			stmnt.setObject(2, v.inventory)
			stmnt.setObject(3, v.enderChest)
			stmnt.executeUpdate()
			return ProfileInventoryData(
					id = v.id,
					inventory = v.inventory,
					enderChest = v.enderChest
			)
		}
	}

	override fun update(v: ProfileInventoryData) {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
					"inventory = ?, " +
					"ender_chest = ? " +
					"WHERE id = ?")
			stmnt.setObject(1, v.inventory)
			stmnt.setObject(2, v.enderChest)
			stmnt.setInt(3, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun createOrUpdate(v: ProfileInventoryData): ProfileInventoryData {
		throw NotImplementedError()
	}

	override fun forId(id: Int): ProfileInventoryData? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return null
			fromRow(result)
		}
	}

	override fun allIds(): Collection<Int> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT id FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Int>()
			while (result.next()) all.add(result.getInt("id"))
			all
		}
	}

	private fun fromRow(row: ResultSet): ProfileInventoryData {
		val id = row.getInt("id")
		val inventory = row.getObject("inventory", PassthroughPlayerInventory::class.java)
		val enderChest = row.getObject("ender_chest", PassthroughInventory::class.java)

		return ProfileInventoryData(
				id = id,
				inventory = inventory,
				enderChest = enderChest
		)
	}
}
