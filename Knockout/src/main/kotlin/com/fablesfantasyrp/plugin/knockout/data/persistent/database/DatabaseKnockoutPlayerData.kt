package com.fablesfantasyrp.plugin.knockout.data.persistent.database

import com.fablesfantasyrp.plugin.knockout.data.persistent.PersistentKnockoutPlayerData
import java.util.*

data class DatabaseKnockoutPlayerData(override val id: UUID,
									  override var isKnockedOut: Boolean = false) : PersistentKnockoutPlayerData {

}
