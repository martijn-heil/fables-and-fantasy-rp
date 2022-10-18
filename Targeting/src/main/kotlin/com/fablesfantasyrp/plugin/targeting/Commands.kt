package com.fablesfantasyrp.plugin.targeting

import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.targeting.data.SimpleTargetingPlayerDataRepository
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.entity.Player

class Commands {
	class Target(private val repo: SimpleTargetingPlayerDataRepository) {
		@Command(aliases = ["select"], desc = "Enter or exit selecting mode")
		@Require(Permission.Command.Target.Select)
		fun select(@Sender sender: Player) {
			val data = repo.forOfflinePlayer(sender)
			repo.update(data.copy(isSelecting = !data.isSelecting))
			if (!data.isSelecting) {
				sender.sendMessage("$SYSPREFIX You are now selecting targets. Right click a player to select/deselect.")
			} else {
				sender.sendMessage("$SYSPREFIX You are no longer selecting targets")
			}
		}

		@Command(aliases = ["remove"], desc = "Remove a player from target list")
		@Require(Permission.Command.Target.Remove)
		fun remove(@Sender sender: Player, @AllowCharacterName target: Player) {
			val data = repo.forOfflinePlayer(sender)
			repo.update(data.copy(targets = data.targets.minus(target)))
			sender.sendMessage("$SYSPREFIX removed ${target.name} from your target list.")
		}

		@Command(aliases = ["add"], desc = "Add a player to target list")
		@Require(Permission.Command.Target.Add)
		fun add(@Sender sender: Player, @AllowCharacterName target: Player) {
			val data = repo.forOfflinePlayer(sender)
			repo.update(data.copy(targets = data.targets.plus(target)))
			sender.sendMessage("$SYSPREFIX added ${target.name} to your target list.")
		}

		@Command(aliases = ["list"], desc = "List all players in your target list")
		@Require(Permission.Command.Target.List)
		fun list(@Sender sender: Player) {
			val data = repo.forOfflinePlayer(sender)
			sender.sendMessage("$SYSPREFIX Targets:\n${data.targets.joinToString("\n") { "  ${it.name}" }}")
		}
	}
}
