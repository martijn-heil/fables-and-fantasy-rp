package com.fablesfantasyrp.plugin.database

import com.dieselpoint.norm.Database
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.h2.jdbcx.JdbcConnectionPool
import javax.sql.DataSource

class FablesDatabase : JavaPlugin() {
	private val DB_DRIVER = "org.h2.Driver"

	override fun onEnable() {
		logger.fine("Saving default config..")
		saveDefaultConfig()

		logger.info("Migrating database if needed..")
		dbUrl = config.getString("db.url")!!
		dbUsername = config.getString("db.username")!!
		dbPassword = config.getString("db.password")!!
		// Storing the password in a char array doesn't improve much..
		// it's stored in plaintext in the "config" object anyway.. :/

		val dataSource = JdbcConnectionPool.create(dbUrl, dbUsername, dbPassword)
		dataSource.maxConnections = 50
		dataSource.loginTimeout = 5

		printDatabaseSettings(dataSource)

		fablesDatabase = dataSource

		try {
			applyMigrations(this, null, this.classLoader)
		} catch (ex: Exception) {
			this.isEnabled = false
			return
		}

		norm = Database()
		norm.sqlMaker = FablesSQLMaker()
		norm.setJdbcUrl(dbUrl)
		norm.setDriverClassName("org.h2.Driver")
		norm.setUser(dbUsername)
		norm.setPassword(dbPassword)
	}

	override fun onDisable() {

	}

	private fun printDatabaseSettings(dataSource: DataSource) {
		val lines = dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.SETTINGS")
			val result = stmnt.executeQuery()
			val lines = ArrayList<String>()
			while (result.next()) {
				lines.add("${result.getString("SETTING_NAME")}: ${result.getString("SETTING_VALUE")}")
			}
			lines
		}
		logger.info("H2 Database settings:\n" + lines.joinToString("\n"))
	}

	companion object {
		lateinit var instance: FablesDatabase
		private lateinit var dbUrl: String
		private lateinit var dbUsername: String
		private lateinit var dbPassword: String

		lateinit var fablesDatabase: DataSource
			private set

		lateinit var norm: Database
	}
}

fun applyMigrations(plugin: Plugin, schema: String?, classLoader: ClassLoader) {
	val flywayConfig = Flyway.configure(classLoader)
			.dataSource(FablesDatabase.fablesDatabase)
			.defaultSchema(schema)
			.baselineOnMigrate(true)
			.baselineVersion("0")

	if (schema != null) flywayConfig.defaultSchema(schema)

	val flyway = flywayConfig.load()

	try {
		plugin.logger.info("Successfully applied ${flyway.migrate()} migrations")
	} catch (ex: FlywayException) {
		plugin.logger.severe(ex.message)
		ex.printStackTrace()
		try {
			flyway.repair()
		} catch (ex: FlywayException) {
			plugin.logger.severe("Error whilst attempting to repair migrations:")
			ex.printStackTrace()
			throw ex
		}
	}
}
