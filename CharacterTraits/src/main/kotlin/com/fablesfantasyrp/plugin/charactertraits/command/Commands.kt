package com.fablesfantasyrp.plugin.charactertraits.command

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.charactertraits.Permission
import com.fablesfantasyrp.plugin.charactertraits.SYSPREFIX
import com.fablesfantasyrp.plugin.charactertraits.domain.entity.CharacterTrait
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender

class Commands(private val traits: CharacterTraitRepository) {
	inner class CharacterTraitCommand {
		@Command(aliases = ["assign"], desc = "Assign a trait to a character")
		@Require(Permission.Command.CharacterTrait.Assign)
		fun assign(@Sender sender: CommandSender, trait: CharacterTrait, @CommandTarget to: Character) {
			traits.linkToCharacter(to, trait)
			sender.sendMessage("$SYSPREFIX Assigned ${trait.id} to ${to.name}")
		}

		@Command(aliases = ["revoke"], desc = "Revoke trait from a character")
		@Require(Permission.Command.CharacterTrait.Revoke)
		fun revoke(@Sender sender: CommandSender, trait: CharacterTrait, @CommandTarget from: Character) {
			traits.unlinkFromCharacter(from, trait)
			sender.sendMessage("$SYSPREFIX Revoked ${trait.id} from ${from.name}")
		}

		@Command(aliases = ["list"], desc = "List traits of a character")
		@Require(Permission.Command.CharacterTrait.List)
		fun list(@Sender sender: CommandSender, @CommandTarget from: Character) {
			val assignedTraits = traits.forCharacter(from)

			sender.sendMessage(miniMessage.deserialize(
				"<prefix> <gray><character_name> has the following traits:<newline><traits></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.unparsed("character_name", from.name),
				Placeholder.component("traits", Component.join(JoinConfiguration.newlines(),
					assignedTraits.map { Component.text(it.id) })))
			)
		}
	}
}
