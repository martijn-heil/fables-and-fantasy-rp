package com.fablesfantasyrp.plugin.targeting

import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.targeting.data.SimpleTargetingPlayerDataRepository
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.parametric.annotation.Switch
import org.bukkit.entity.Player

class Commands {
	class Target(private val repo: SimpleTargetingPlayerDataRepository) {
		@Command(aliases = ["select"], desc = "Enter or exit selecting mode")
		@Require(Permission.Command.Target.Select)
		fun select(@Sender sender: Player) {
			val data = repo.forOfflinePlayer(sender)
			repo.update(data.copy(isSelecting = !data.isSelecting))
			if (!data.isSelecting) {
				sender.sendMessage("$SYSPREFIX You are now selecting targets. Left click a player to select/deselect.")
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

		@Command(aliases = ["clear"], desc = "Clear your target list")
		@Require(Permission.Command.Target.Clear)
		fun clear(@Sender sender: Player) {
			val data = repo.forOfflinePlayer(sender)
			repo.update(data.copy(targets = emptySet()))
			sender.sendMessage("$SYSPREFIX Cleared your target list.")
		}

		@Command(aliases = ["foreach"], desc = "Run a command on each target")
		@Require(Permission.Command.Target.Foreach)
		fun foreach(@Sender sender: Player, @Switch('i') replaceMe: String?, command: String) {
			val targets = repo.forOfflinePlayer(sender).targets
			targets.asSequence()
					.map { it.name }
					.map { if(replaceMe != null) command.replace(replaceMe, it) else command.plus(" $it") }
					.forEach { sender.performCommand(it) }
		}
	}
}
