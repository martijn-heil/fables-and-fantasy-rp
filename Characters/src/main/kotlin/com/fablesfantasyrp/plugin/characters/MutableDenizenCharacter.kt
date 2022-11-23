package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizen.objects.LocationTag
import com.denizenscript.denizencore.objects.core.ElementTag
import com.denizenscript.denizencore.objects.core.MapTag
import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.Gender
import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer

class MutableDenizenCharacter(override val id: ULong, player: OfflinePlayer) : CharacterData {
	private val dataMap: MapTag
		get() = (player.dFlags.getFlagValue("characters") as MapTag).getObject(id.toString()) as MapTag

	val playerUuid = player.uniqueId

	override val player: OfflinePlayer
		get() = Bukkit.getOfflinePlayer(playerUuid)

	val isDeleted: Boolean
		get() = (player.dFlags.getFlagValue("characters") as? MapTag)?.getObject(id.toString()) == null

	override var name: String
		get() = dataMap.getObject("name").asElement().asString()
		set(value) { dataMap.putObject("name", ElementTag(value)) }
	override var age: UInt
		get() = dataMap.getObject("age").asElement().asInt().toUInt()
		set(value) { dataMap.putObject("age", ElementTag(value.toInt())) }
	override var description: String
		get() = dataMap.getObject("description").asElement().asString()
		set(value) { dataMap.putObject("description", ElementTag(value)) }
	override val gender: Gender
		get() = Gender.valueOf(dataMap.getObject("gender").asElement().asString().uppercase())
	override val race: Race
		get() = Race.valueOf(dataMap.getObject("race").asElement().asString().uppercase().replace(' ', '_'))
	override val stats: CharacterStats
		get() {
			val stats = dataMap.getObject("stats") as MapTag
			val strength = stats.getObject("strength").asElement().asInt().toUInt()
			val defense = stats.getObject("defense").asElement().asInt().toUInt()
			val agility = stats.getObject("agility").asElement().asInt().toUInt()
			val intelligence = stats.getObject("intelligence").asElement().asInt().toUInt()
			return CharacterStats(strength, defense, agility, intelligence)
		}
	override val location: Location
		get() = dataMap.getObject("location") as LocationTag
	override val money: ULong
		get() = dataMap.getObject("money").asElement().asLong().toULong()

	override fun toString(): String {
		return "DenizenCharacter(id=$id, player=${player.uniqueId} (${player.name}), name=$name, age=$age, gender=$gender, race=$race)"
	}

	override fun equals(other: Any?): Boolean {
		return if (other is MutableDenizenCharacter) {
			other.id == id
		} else false
	}

	override fun hashCode(): Int {
		return id.hashCode()
	}
}
