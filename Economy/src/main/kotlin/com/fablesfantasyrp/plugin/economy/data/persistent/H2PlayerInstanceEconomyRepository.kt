package com.fablesfantasyrp.plugin.economy.data.persistent

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.economy.data.entity.PlayerInstanceEconomy
import com.fablesfantasyrp.plugin.economy.data.entity.PlayerInstanceEconomyRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import java.sql.ResultSet
import javax.sql.DataSource

class H2PlayerInstanceEconomyRepository(private val dataSource: DataSource)
	: PlayerInstanceEconomyRepository, HasDirtyMarker<PlayerInstanceEconomy> {
	override var dirtyMarker: DirtyMarker<PlayerInstanceEconomy>? = null
	private val TABLE_NAME = "FABLES_ECONOMY.POCKET_MONEY"

	override fun forPlayerInstance(playerInstance: PlayerInstance): PlayerInstanceEconomy {
		check(!playerInstance.isDestroyed)

		val economy = this.forId(playerInstance.id) ?: run {
			this.create(PlayerInstanceEconomy(playerInstance.id, 0))
		}

		return economy
	}

	override fun all(): Collection<PlayerInstanceEconomy> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<PlayerInstanceEconomy>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: PlayerInstanceEconomy) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, v.id)
			stmnt.executeUpdate()
			v.isDestroyed = true
		}
	}

	override fun create(v: PlayerInstanceEconomy): PlayerInstanceEconomy {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(" +
					"id, " +
					"pocket_money " +
					") " +
					"VALUES (?, ?)")
			stmnt.setInt(1, v.id)
			stmnt.setInt(2, v.money)
			stmnt.executeUpdate()
			v.dirtyMarker = dirtyMarker
			return v
		}
	}

	override fun update(v: PlayerInstanceEconomy) {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
					"pocket_money = ? " +
				"WHERE id = ?")
			stmnt.setInt(1, v.money)
			stmnt.setInt(2, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun forId(id: Int): PlayerInstanceEconomy? {
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

	private fun fromRow(row: ResultSet): PlayerInstanceEconomy {
		val id = row.getInt("id")
		val money = row.getInt("pocket_money")

		return PlayerInstanceEconomy(
				id = id,
				money = money,
				dirtyMarker = dirtyMarker
		)
	}
}
