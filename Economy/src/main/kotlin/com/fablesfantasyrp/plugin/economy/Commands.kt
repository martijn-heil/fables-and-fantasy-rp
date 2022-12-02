package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.playerinstance.currentPlayer
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.text.sendError
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Range
import org.bukkit.command.CommandSender

class Commands(private val characters: EntityCharacterRepository) {
	@Command(aliases = ["balance", "bal", "fbalance", "fbal"], desc = "Show your balance")
	@Require(Permission.Command.Balance)
	fun balance(@Sender sender: CommandSender,
				@CommandTarget(Permission.Command.Balance + ".others") @AllowCharacterName target: PlayerInstance) {
		sender.sendMessage("$SYSPREFIX You have ${CURRENCY_SYMBOL}${target.money}")
	}

	@Command(aliases = ["pay", "fpay"], desc = "Pay someone money")
	@Require(Permission.Command.Pay)
	fun pay(@Sender sender: PlayerInstance, @AllowCharacterName target: PlayerInstance, @Range(min = 1.0) amount: Int) {
		val character = characters.forPlayerInstance(target)
		val displayName = character?.name ?: "#${target.id}"
		val currentPlayer = sender.currentPlayer!!

		if (sender.money < amount) {
			currentPlayer.sendError("You cannot afford that!")
			return
		}

		sender.money -= amount
		target.money += amount
		currentPlayer.sendMessage("$SYSPREFIX Sent $CURRENCY_SYMBOL${amount} to $displayName")
	}
}
