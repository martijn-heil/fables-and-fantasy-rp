package com.fablesfantasyrp.plugin.basicsystem.data.persistent

import com.fablesfantasyrp.plugin.basicsystem.data.BasicSystemPlayerRepository
import com.fablesfantasyrp.plugin.basicsystem.data.entity.BasicSystemPlayer
import com.fablesfantasyrp.plugin.database.repository.BaseH2PlayerRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import org.bukkit.Server
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

class H2BasicSystemPlayerRepository(private val dataSource: DataSource, private val server: Server)
	: BaseH2PlayerRepository<BasicSystemPlayer>(dataSource, server),
		BasicSystemPlayerRepository, HasDirtyMarker<BasicSystemPlayer> {

	override val TABLE_NAME = "FABLES_BASICSYSTEM.BASICSYSTEM"

	override fun create(v: BasicSystemPlayer): BasicSystemPlayer {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(id, is_epic) " +
					"VALUES (?, ?)")
			stmnt.setObject(1, v.id)
			stmnt.setBoolean(2, v.isEpic)
			stmnt.executeUpdate()
			v.dirtyMarker = dirtyMarker
			return v
		}
	}

	override fun create(id: UUID): BasicSystemPlayer {
		return this.create(BasicSystemPlayer(
				id = id,
				isEpic = false
		))
	}

	override fun update(v: BasicSystemPlayer) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET" +
					"is_epic = ? " +
					"WHERE id = ?")
			stmnt.setBoolean(1, v.isEpic)
			stmnt.setObject(2, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun fromRow(row: ResultSet): BasicSystemPlayer {
		val id = row.getObject("id", UUID::class.java)
		val isEpic = row.getBoolean("is_epic")

		return BasicSystemPlayer(
				id = id,
				isEpic = isEpic,
				dirtyMarker = dirtyMarker)
	}
}
