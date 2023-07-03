package com.fablesfantasyrp.plugin.magic.denizen

import com.denizenscript.denizen.utilities.debugging.Debug
import com.denizenscript.denizencore.objects.Fetchable
import com.denizenscript.denizencore.objects.ObjectTag
import com.denizenscript.denizencore.objects.core.ElementTag
import com.denizenscript.denizencore.tags.Attribute
import com.denizenscript.denizencore.tags.TagContext
import com.fablesfantasyrp.plugin.magic.data.entity.Mage
import com.fablesfantasyrp.plugin.magic.mageRepository


class MageTag(private val mage: Mage) : ObjectTag {


	companion object {
		@JvmStatic
		fun valueOf(string: String?): MageTag? {
			return valueOf(string, null)
		}

		@JvmStatic
		@Fetchable("mage")
		fun valueOf(string: String?, context: TagContext?): MageTag? {
			if (string == null) { return null }
			val mage = string.removePrefix("mage@").toLongOrNull()?.let { mageRepository.forId(it) }
			if (mage == null) {
				Debug.echoError("valueOf Mage returning null: Invalid id '$string'")
				return null
			}
			return MageTag(mage)
		}

		@JvmStatic
		fun matches(arg: String): Boolean {
			return true
		}
	}

	private var prefix = "Mage"

	override fun getPrefix(): String = prefix
	override fun setPrefix(prefix: String): ObjectTag {
		this.prefix = prefix
		return this
	}

	override fun isUnique(): Boolean = true
	override fun identify(): String = "mage@${mage.id}"
	override fun identifySimple(): String = identify()
	override fun toString(): String = identify()

	override fun getObjectAttribute(attribute: Attribute): ObjectTag {
		if (attribute.startsWith("level")) {
			return ElementTag(mage.magicLevel).getObjectAttribute(attribute.fulfill(1))
		}

		if (attribute.startsWith("path")) {
			return ElementTag(mage.magicPath.name).getObjectAttribute(attribute.fulfill(1))
		}

		return ElementTag(identify()).getObjectAttribute(attribute)
	}
}
