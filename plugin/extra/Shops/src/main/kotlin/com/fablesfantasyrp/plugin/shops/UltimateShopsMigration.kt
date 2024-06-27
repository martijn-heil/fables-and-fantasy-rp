/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.shops

import com.fablesfantasyrp.plugin.shops.dal.h2.H2ShopDataRepository
import com.fablesfantasyrp.plugin.shops.dal.yaml.UltimateShopsDataRepository
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import javax.sql.DataSource

class UltimateShopsMigration(private val plugin: Plugin,
							 private val dataSource: DataSource) {
	private val server = plugin.server
	private val logger = plugin.logger

	fun run() {
		val dir = plugin.dataFolder
		val shopsFile = dir.resolve("shops.yml")
		if (!shopsFile.exists()) return

		val inputRepository = UltimateShopsDataRepository(server, YamlConfiguration.loadConfiguration(shopsFile))

		val outputRepository = H2ShopDataRepository(plugin, dataSource)

		val allShops = inputRepository.all()

		logger.info("[UltimateShopsMigration] Found ${allShops.size} shops!")

		allShops.forEach { outputRepository.create(it) }
	}
}
