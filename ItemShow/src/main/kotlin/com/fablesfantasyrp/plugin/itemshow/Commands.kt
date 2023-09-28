package com.fablesfantasyrp.plugin.itemshow

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.broadcast
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.entity.Player

class Commands(private val server: Server,
			   private val characters: CharacterRepository,
			   private val profileManager: ProfileManager) {
	@Command(aliases = ["itemshow", "showitem"], desc = "")
	@Require("fables.itemshow.command.itemshow")
	fun itemshow(@Sender sender: Player) {
		val item = sender.inventory.itemInMainHand
		if (item.type == Material.AIR) {
			sender.sendError("You must hold the item you want to show in your main hand.")
			return
		}

		val character = profileManager.getCurrentForPlayer(sender)?.let { characters.forProfile(it) }
		val displayName = character?.name ?: sender.name

		val itemDisplay = Component.text("[ HOVER OVER ME ]").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
			.hoverEvent(item.asHoverEvent())

		server.broadcast(sender.location, 15,
			miniMessage.deserialize("<gray><prefix> <display_name> is displaying the item in their hand.<newline>" +
				"<item></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.unparsed("display_name", displayName),
				Placeholder.component("item", itemDisplay)
			)
		)
	}
}
