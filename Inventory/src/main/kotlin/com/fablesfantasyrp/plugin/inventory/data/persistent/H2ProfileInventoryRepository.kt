package com.fablesfantasyrp.plugin.inventory.data.persistent

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.inventory.PassthroughInventory
import com.fablesfantasyrp.plugin.inventory.PassthroughPlayerInventory
import com.fablesfantasyrp.plugin.inventory.data.entity.FablesInventoryRepository
import com.fablesfantasyrp.plugin.inventory.data.entity.ProfileInventory
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import java.sql.ResultSet
import javax.sql.DataSource

class H2ProfileInventoryRepository(private val dataSource: DataSource)
	: FablesInventoryRepository, HasDirtyMarker<ProfileInventory> {
	override var dirtyMarker: DirtyMarker<ProfileInventory>? = null
	private val TABLE_NAME = "FABLES_INVENTORY.INVENTORY"

	override fun forOwner(profile: Profile): ProfileInventory {
		check(!profile.isDestroyed)
		val inventory = this.forId(profile.id) ?: run {
			this.create(ProfileInventory(
					id = profile.id,
					inventory = PassthroughPlayerInventory.createEmpty(),
					enderChest = PassthroughInventory(arrayOfNulls(27)))
			)
		}
		return inventory
	}

	override fun all(): Collection<ProfileInventory> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<ProfileInventory>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: ProfileInventory) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, v.id)
			stmnt.executeUpdate()
			v.isDestroyed = true
		}
	}

	override fun create(v: ProfileInventory): ProfileInventory {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(id, inventory, ender_chest) " +
					"VALUES (?, ?, ?)")
			stmnt.setInt(1, v.id)
			stmnt.setObject(2, v.inventory)
			stmnt.setObject(3, v.enderChest)
			stmnt.executeUpdate()
			return ProfileInventory(
					id = v.id,
					inventory = v.inventory,
					enderChest = v.enderChest,
					dirtyMarker = dirtyMarker
			)
		}
	}

	override fun update(v: ProfileInventory) {
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

	override fun forId(id: Int): ProfileInventory? {
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

	private fun fromRow(row: ResultSet): ProfileInventory {
		val id = row.getInt("id")
		val inventory = row.getObject("inventory", PassthroughPlayerInventory::class.java)
		val enderChest = row.getObject("ender_chest", PassthroughInventory::class.java)

		return ProfileInventory(
				id = id,
				inventory = inventory,
				enderChest = enderChest,
				dirtyMarker = dirtyMarker
		)
	}
}
