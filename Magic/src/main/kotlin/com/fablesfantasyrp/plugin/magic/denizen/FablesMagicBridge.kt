package com.fablesfantasyrp.plugin.magic.denizen

import com.denizenscript.denizencore.objects.ObjectFetcher
import com.denizenscript.denizencore.tags.Attribute
import com.denizenscript.denizencore.tags.ReplaceableTagEvent
import com.denizenscript.denizencore.tags.TagManager
import com.denizenscript.denizencore.tags.TagRunnable.RootForm


class FablesMagicBridge {
	var instance: FablesMagicBridge? = null

	fun init() {
		instance = this
		ObjectFetcher.registerWithObjectFetcher(MageTag::class.java)
		TagManager.registerTagHandler(object : RootForm() {
			override fun run(event: ReplaceableTagEvent) {
				tagEvent(event)
			}
		}, "mage")
	}

	fun tagEvent(event: ReplaceableTagEvent) {
		val attribute: Attribute = event.attributes

		if (attribute.startsWith("mage") && attribute.hasParam()) {
			val mage: MageTag? = attribute.paramAsType(MageTag::class.java)
			if (mage != null) {
				event.setReplacedObject(mage.getObjectAttribute(attribute.fulfill(1)))
			} else {
				attribute.echoError("Unknown Mage with id '" + attribute.param + "' for mage[] tag.")
			}
		}
	}
}
