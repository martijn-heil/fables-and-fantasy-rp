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
