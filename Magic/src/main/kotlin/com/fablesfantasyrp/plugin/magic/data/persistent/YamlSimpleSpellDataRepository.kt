package com.fablesfantasyrp.plugin.magic.data.persistent

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.data.SimpleSpellData
import com.fablesfantasyrp.plugin.magic.data.SimpleSpellDataRepository
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class YamlSimpleSpellDataRepository(private val directory: File) : SimpleSpellDataRepository {
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

		return fromFile(file)
	}

	override fun allIds(): Collection<String> {
		return this.all().map { it.id }
	}

	override fun all(): Collection<SimpleSpellData> {
		return directory.listFiles()!!.map { fromFile(it) }
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

	private fun fromFile(file: File): SimpleSpellData {
		val yaml = YamlConfiguration.loadConfiguration(file)
		val displayName = yaml.getString("display_name")!!
		val description = yaml.getString("description")!!
		val magicPath = MagicPath.valueOf(yaml.getString("magic_path")!!)
		val level = yaml.getInt("level", Int.MAX_VALUE)
		val castingValue = yaml.getInt("casting_value", Int.MAX_VALUE)
		check(level != Int.MAX_VALUE)
		check(castingValue != Int.MAX_VALUE)
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
