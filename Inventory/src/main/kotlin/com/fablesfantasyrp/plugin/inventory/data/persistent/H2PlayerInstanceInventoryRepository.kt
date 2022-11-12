package com.fablesfantasyrp.plugin.inventory.data.persistent

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.inventory.PassthroughPlayerInventory
import com.fablesfantasyrp.plugin.inventory.data.entity.FablesInventoryRepository
import com.fablesfantasyrp.plugin.inventory.data.entity.PlayerInstanceInventory
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import java.sql.ResultSet
import javax.sql.DataSource

class H2PlayerInstanceInventoryRepository(private val dataSource: DataSource)
	: FablesInventoryRepository, HasDirtyMarker<PlayerInstanceInventory> {
	override var dirtyMarker: DirtyMarker<PlayerInstanceInventory>? = null
	private val TABLE_NAME = "FABLES_INVENTORY.INVENTORY"

	override fun forOwner(playerInstance: PlayerInstance): PlayerInstanceInventory {
		check(!playerInstance.isDestroyed)
		val offlinePlayer = playerInstance.owner
		val player = offlinePlayer.player
		val inventory = this.forId(playerInstance.id) ?: run {
			this.create(PlayerInstanceInventory(playerInstance.id, PassthroughPlayerInventory.createEmpty()))
		}
		if (offlinePlayer.isOnline && player != null) {
			inventory.delegate.bukkitInventory = player.inventory
		}
		return inventory
	}

	override fun all(): Collection<PlayerInstanceInventory> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<PlayerInstanceInventory>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: PlayerInstanceInventory) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
			v.isDestroyed = true
		}
	}

	override fun create(v: PlayerInstanceInventory): PlayerInstanceInventory {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(id, inventory) " +
					"VALUES (?, ?)")
			stmnt.setInt(1, v.id)
			stmnt.setObject(2, v.delegate)
			stmnt.executeUpdate()
			val rs = stmnt.generatedKeys
			rs.next()
			val id = rs.getInt(1)
			return PlayerInstanceInventory(
					id = id,
					delegate = v.delegate,
					dirtyMarker = dirtyMarker
			)
		}
	}

	override fun update(v: PlayerInstanceInventory) {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
				"inventory = ? " +
				"WHERE id = ?")
			stmnt.setObject(1, v.delegate)
			stmnt.setInt(1, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun forId(id: Int): PlayerInstanceInventory? {
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

	private fun fromRow(row: ResultSet): PlayerInstanceInventory {
		val id = row.getInt("id")
		val inventory = row.getObject("inventory", PassthroughPlayerInventory::class.java)

		return PlayerInstanceInventory(
				id = id,
				delegate = inventory,
				dirtyMarker = dirtyMarker
		)
	}
}
