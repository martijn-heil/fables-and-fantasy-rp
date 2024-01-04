package com.fablesfantasyrp.plugin.inventorysearch.command

import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.form.YesNoChatPrompt
import com.fablesfantasyrp.plugin.inventory.domain.repository.ProfileInventoryRepository
import com.fablesfantasyrp.plugin.inventorysearch.Permission
import com.fablesfantasyrp.plugin.inventorysearch.SYSPREFIX
import com.fablesfantasyrp.plugin.inventorysearch.gui.InventorySearchGui
import com.fablesfantasyrp.plugin.item.ItemTraitService
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.DISTANCE_TALK
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.distanceSafe
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.plugin.java.JavaPlugin

class Commands(private val plugin: JavaPlugin,
			   private val profileManager: ProfileManager,
			   private val itemTraitService: ItemTraitService,
			   private val inventories: ProfileInventoryRepository) {

	@Command(aliases = ["inventorysearch", "finventorysearch"], desc = "Search a player's inventory")
	@Require(Permission.Command.InventorySearch)
	suspend fun inventorysearch(@Sender sender: Character, target: Character) {
		val senderPlayer = profileManager.getCurrentForProfile(sender.profile)!!

		val targetPlayer = profileManager.getCurrentForProfile(target.profile) ?: run {
			senderPlayer.sendError("${target.name} is not currently being played by anyone.")
			return
		}

		if (targetPlayer.location.distanceSafe(senderPlayer.location) > DISTANCE_TALK.toInt()) {
			senderPlayer.sendError("${target.name} is too far away.")
			return
		}

		val request = YesNoChatPrompt(targetPlayer, miniMessage.deserialize(
			"<gray><prefix> <searcher_name> wants to search your inventory. Do you accept?</gray>",
			Placeholder.component("prefix", legacyText(SYSPREFIX)),
			Placeholder.unparsed("searcher_name", sender.name)
		))

		senderPlayer.sendMessage("$SYSPREFIX Requesting ${target.name} to search their inventory..")

		request.send()

		if (!request.await()) {
			senderPlayer.sendMessage("$SYSPREFIX ${target.name} declined your request.")
			targetPlayer.sendMessage("$SYSPREFIX Declined ${sender.name}'s request.")
			return
		}

		val profileInventory = inventories.forOwner(target.profile)

		val gui = InventorySearchGui(plugin, target, profileInventory.inventory, itemTraitService)
		gui.setCloseAction {
			targetPlayer.sendMessage("$SYSPREFIX ${sender.name} stopped searching you.")
			true
		}
		targetPlayer.sendMessage("$SYSPREFIX ${sender.name} Is searching you..")
		gui.show(senderPlayer)
	}
}
