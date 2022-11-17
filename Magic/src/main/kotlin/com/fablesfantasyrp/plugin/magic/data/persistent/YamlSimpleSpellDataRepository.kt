package com.fablesfantasyrp.plugin.magic.data.persistent

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.data.SimpleSpellData
import com.fablesfantasyrp.plugin.magic.data.SimpleSpellDataRepository
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.error.YAMLException
import java.io.File

class YamlSimpleSpellDataRepository(private val plugin: Plugin, private val directory: File) : SimpleSpellDataRepository {
	private fun fileForId(id: String): File {
		require(isValidId(id))
		return directory.resolve("${id}.yml")
	}

	override fun forLevelAndPath(level: Int, path: MagicPath): Collection<SimpleSpellData>
		= this.all().filter { it.level == level && (it.magicPath == path || path.basePath == it.magicPath) }

	override fun forId(id: String): SimpleSpellData? {
		if (!isValidId(id)) return null
		val file = fileForId(id)
		if (!file.exists()) return null

		return fromFileMaybe(file)
	}

	override fun allIds(): Collection<String> {
		return this.all().map { it.id }
	}

	override fun all(): Collection<SimpleSpellData> {
		return directory.listFiles()!!.mapNotNull { fromFileMaybe(it) }
	}

	override fun destroy(v: SimpleSpellData) {
		fileForId(v.id).delete()
	}

	override fun create(v: SimpleSpellData): SimpleSpellData {
		val file = fileForId(v.id)
		require(!file.exists())

		file.createNewFile()
		val yaml = YamlConfiguration.loadConfiguration(file)
		updateYaml(v, yaml)
		yaml.save(file)
		return v
	}

	override fun update(v: SimpleSpellData) {
		val file = fileForId(v.id)
		check(file.exists())
		val yaml = YamlConfiguration.loadConfiguration(file)
		updateYaml(v, yaml)
		yaml.save(file)
	}

	private fun updateYaml(v: SimpleSpellData, yaml: YamlConfiguration) {
		yaml.set("display_name", v.displayName)
		yaml.set("magic_path", v.magicPath.name)
		yaml.set("level", v.level)
		yaml.set("casting_value", v.castingValue)
		yaml.set("description", v.description)
	}

	private fun fromFileMaybe(file: File): SimpleSpellData? {
		return try {
			fromFile(file)
		} catch (ex: Exception) {
			plugin.logger.warning("Encountered exception parsing spell data from file: ")
			ex.printStackTrace()
			plugin.logger.warning("Skipping this spell")
			null
		}
	}

	@Throws(YAMLException::class, IllegalStateException::class)
	private fun fromFile(file: File): SimpleSpellData {
		val yaml = YamlConfiguration.loadConfiguration(file)

		fun missingField(fieldName: String): Nothing {
			throw IllegalStateException("$fieldName is missing from file '${file.path}'")
		}

		val displayName = yaml.getString("display_name") ?: missingField("display_name")
		val description = yaml.getString("description") ?: missingField("description")
		val magicPath = try {
			yaml.getString("magic_path")?.let { MagicPath.valueOf(it) } ?: missingField("magic_path")
		} catch (ex: IllegalArgumentException) {
			throw IllegalStateException("Invalid value for magic_path in file '${file.path}'")
		}

		val level = yaml.getInt("level", Int.MAX_VALUE)
		val castingValue = yaml.getInt("casting_value", Int.MAX_VALUE)
		if (level == Int.MAX_VALUE) missingField("level")
		if (castingValue == Int.MAX_VALUE) missingField("casting_value")


		return SimpleSpellData(
				id = file.nameWithoutExtension,
				displayName = displayName,
				description = description,
				magicPath = magicPath,
				level = level,
				castingValue = castingValue
		)
	}

	companion object {
		fun isValidId(id: String) = id.matches(Regex("[a-z_]+"))
	}
}
