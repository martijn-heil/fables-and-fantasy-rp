package com.fablesfantasyrp.plugin.warp.data.persistent

import com.fablesfantasyrp.plugin.warp.data.SimpleWarp
import com.fablesfantasyrp.plugin.warp.data.SimpleWarpRepository
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.error.YAMLException
import java.io.File
import java.util.*

class YamlSimpleWarpRepository(private val plugin: Plugin,
							   private val directory: File) : SimpleWarpRepository {
	private val server = plugin.server

	private fun fileForId(id: String): File {
		require(isSyntacticallyValidId(id))
		return directory.resolve("${id}.yml")
	}

	override fun forId(id: String): SimpleWarp? {
		if (!isSyntacticallyValidId(id)) return null
		val file = fileForId(id)
		if (!file.exists()) return null

		return fromFileMaybe(file)
	}

	override fun allIds(): Collection<String> {
		return directory.list { _, s -> s.endsWith(".yml") }.map { it.removeSuffix(".yml") }
	}

	override fun all(): Collection<SimpleWarp> {
		return directory.listFiles()!!.mapNotNull { fromFileMaybe(it) }
	}

	override fun destroy(v: SimpleWarp) {
		fileForId(v.id).delete()
	}

	override fun create(v: SimpleWarp): SimpleWarp {
		val file = fileForId(v.id)
		require(!file.exists())

		file.createNewFile()
		val yaml = YamlConfiguration.loadConfiguration(file)
		updateYaml(v, yaml)
		yaml.save(file)
		return v
	}

	override fun update(v: SimpleWarp) {
		val file = fileForId(v.id)
		check(file.exists())
		val yaml = YamlConfiguration.loadConfiguration(file)
		updateYaml(v, yaml)
		yaml.save(file)
	}

	private fun updateYaml(v: SimpleWarp, yaml: YamlConfiguration) {
		yaml.set("world", v.location.world.uid.toString())
		yaml.set("x", v.location.x)
		yaml.set("y", v.location.y)
		yaml.set("z", v.location.z)
		yaml.set("yaw", v.location.pitch)
		yaml.set("pitch", v.location.pitch)
	}

	private fun fromFileMaybe(file: File): SimpleWarp? {
		return try {
			fromFile(file)
		} catch (ex: Exception) {
			plugin.logger.warning("Encountered exception parsing warp data from file '${file.name}': ")
			ex.printStackTrace()
			plugin.logger.warning("Skipping this warp")
			null
		}
	}

	@Throws(YAMLException::class, IllegalStateException::class)
	private fun fromFile(file: File): SimpleWarp {
		val yaml = YamlConfiguration.loadConfiguration(file)

		fun missingField(fieldName: String): Nothing {
			throw IllegalStateException("$fieldName is missing from file '${file.path}'")
		}

		val x = yaml.getDouble("x", Double.NaN)
		val y = yaml.getDouble("y", Double.NaN)
		val z = yaml.getDouble("z", Double.NaN)
		val yaw = yaml.getDouble("pitch", Double.NaN)
		val pitch = yaml.getDouble("pitch", Double.NaN)
		val worldUuid = yaml.getString("world") ?: missingField("world")

		val world = try {
			server.getWorld(UUID.fromString(worldUuid))
		} catch (ex: IllegalArgumentException) {
			server.getWorld(worldUuid)
		} ?: throw IllegalStateException("World with UUID '$worldUuid' is not loaded.")

		if (x.isNaN()) missingField("x")
		if (y.isNaN()) missingField("y")
		if (z.isNaN()) missingField("z")
		if (yaw.isNaN()) missingField("yaw")
		if (pitch.isNaN()) missingField("pitch")

		val location = Location(world, x, y, z, yaw.toFloat(), pitch.toFloat())

		return SimpleWarp(
				id = file.nameWithoutExtension,
				location = location,
		)
	}

	override fun isSyntacticallyValidId(id: String) = id.matches(Regex("[a-z_]+"))
}
