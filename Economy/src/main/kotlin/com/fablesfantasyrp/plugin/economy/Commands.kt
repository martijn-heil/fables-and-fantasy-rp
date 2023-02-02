package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.economy.data.entity.EntityProfileEconomyRepository
import com.fablesfantasyrp.plugin.economy.gui.bank.BankGuiMainMenu
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.profiles
import com.fablesfantasyrp.plugin.text.sendError
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Range
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Commands(private val characters: EntityCharacterRepository,
			   private val profileManager: ProfileManager) {
	@Command(aliases = ["balance", "bal", "fbalance", "fbal"], desc = "Show your balance")
	@Require(Permission.Command.Balance)
	fun balance(@Sender sender: CommandSender,
				@CommandTarget(Permission.Command.Balance + ".others") @AllowCharacterName target: Profile) {
		val character = characters.forProfile(target)
		val currentPlayer = profileManager.getCurrentForProfile(target)
		val start = if (currentPlayer == sender) "You have" else "${character?.name ?: "#${target.id}"} has"
		sender.sendMessage("$SYSPREFIX $start ${CURRENCY_SYMBOL}${target.money}")
	}

	@Command(aliases = ["pay", "fpay"], desc = "Pay someone money")
	@Require(Permission.Command.Pay)
	fun pay(@Sender sender: Profile, @AllowCharacterName target: Profile, @Range(min = 1.0) amount: Int) {
		val character = characters.forProfile(target)
		val displayName = character?.name ?: "#${target.id}"
		val currentPlayer = profileManager.getCurrentForProfile(sender)!!
		val ownProfiles = profiles.allForOwner(currentPlayer)

		if (!target.isActive) {
			currentPlayer.sendError("You cannot pay an inactive profile!")
			return
		}

		if (sender.money < amount) {
			currentPlayer.sendError("You cannot afford that!")
			return
		}

		if (!currentPlayer.hasPermission(Permission.PayOwn) && ownProfiles.contains(target)) {
			currentPlayer.sendError("You cannot pay a profile that you own!")
			return
		}

		sender.money -= amount
		target.money += amount
		currentPlayer.sendMessage("$SYSPREFIX Sent $CURRENCY_SYMBOL${amount} to $displayName")
	}

	class Eco(private val characters: EntityCharacterRepository) {
		@Command(aliases = ["give"], desc = "Give money")
		@Require(Permission.Command.Eco.Give)
		fun give(@Sender sender: CommandSender,
				 @CommandTarget @AllowCharacterName target: Profile,
				 @Range(min = 1.0) amount: Int) {
			val character = characters.forProfile(target)
			val displayName = character?.name ?: "#${target.id}"
			target.money += amount
			sender.sendMessage("$SYSPREFIX Gave $CURRENCY_SYMBOL$amount to $displayName")
		}

		@Command(aliases = ["take"], desc = "Take money")
		@Require(Permission.Command.Eco.Take)
		fun take(@Sender sender: CommandSender,
				 @CommandTarget @AllowCharacterName target: Profile,
				 @Range(min = 1.0) amount: Int) {
			val character = characters.forProfile(target)
			val displayName = character?.name ?: "#${target.id}"
			target.money -= amount
			sender.sendMessage("$SYSPREFIX Took $CURRENCY_SYMBOL$amount from $displayName")
		}

		@Command(aliases = ["set"], desc = "Set money")
		@Require(Permission.Command.Eco.Set)
		fun set(@Sender sender: CommandSender,
				@CommandTarget @AllowCharacterName target: Profile,
				@Range(min = 0.0) amount: Int) {
			val character = characters.forProfile(target)
			val displayName = character?.name ?: "#${target.id}"
			target.money = amount
			sender.sendMessage("$SYSPREFIX Set $displayName's balance to $CURRENCY_SYMBOL$amount")
		}
	}

	class Bank(private val plugin: JavaPlugin, private val economyRepository: EntityProfileEconomyRepository) {
		@Command(aliases = ["open"], desc = "Open bank")
		@Require(Permission.Command.Bank.Open)
		fun open(@Sender sender: Player, @AllowCharacterName target: Profile) {
			BankGuiMainMenu(plugin, economyRepository.forProfile(target)).show(sender)
		}
	}
}