package com.fablesfantasyrp.plugin.activity.dal.model

import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegionIdentifier

data class ActivityRegionData(val region: WorldGuardRegionIdentifier,
							  val displayName: String,
							  override val id: String = "") : Identifiable<String>
