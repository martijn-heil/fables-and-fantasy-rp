package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizencore.objects.core.ElementTag
import com.denizenscript.denizencore.objects.core.MapTag
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import org.bukkit.Location
import org.bukkit.entity.Player

class DenizenCharacter(val id: UInt, private val p: Player) : Character {
	private val dataMap: MapTag
		get() = (p.dFlags.getFlagValue("characters") as MapTag).getObject(id.toString()) as MapTag

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
		get() = Race.valueOf(dataMap.getObject("race").asElement().asString().uppercase())
	override val stats: CharacterStats
		get() = TODO("Not yet implemented")
	override val location: Location
		get() = TODO("Not yet implemented")
	override val money: Long
		get() = TODO("Not yet implemented")

	override fun toString(): String {
		return "DenizenCharacter(id=$id, name=$name, age=$age, gender=$gender, race=$race)"
	}
}
