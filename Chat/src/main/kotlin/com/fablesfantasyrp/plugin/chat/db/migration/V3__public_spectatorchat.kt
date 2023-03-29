package com.fablesfantasyrp.plugin.chat.db.migration

import com.fablesfantasyrp.plugin.chat.channel.ChatSpectator
import com.fablesfantasyrp.plugin.chat.channel.ToggleableChatChannel
import com.fablesfantasyrp.plugin.database.getList
import com.fablesfantasyrp.plugin.database.getUuid
import com.fablesfantasyrp.plugin.database.setList
import com.fablesfantasyrp.plugin.database.setUuid
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.h2.api.H2Type
import java.util.*

class V3__public_spectatorchat : BaseJavaMigration() {
	override fun migrate(context: Context) {
		val connection = context.connection
		val updates = ArrayList<Pair<UUID, List<ToggleableChatChannel>>>()

		val stmnt = connection.prepareStatement("SELECT id, disabled_channels FROM chat")
		val result = stmnt.executeQuery()
		while (result.next()) {
			val id = result.getUuid("id")!!
			val disabledChannels = result.getList<ToggleableChatChannel>("disabled_channels")
			if (!disabledChannels.contains(ChatSpectator)) {
				val newDisabledChannels = disabledChannels.plus(ChatSpectator)
				updates.add(Pair(id, newDisabledChannels))
			}
		}

		for (update in updates) {
			val stmnt2 = connection.prepareStatement("UPDATE chat SET disabled_channels = ? WHERE id = ?")
			stmnt2.setList(1, H2Type.JAVA_OBJECT, update.second)
			stmnt2.setUuid(2, update.first)
			stmnt2.executeUpdate()
		}
	}
}
