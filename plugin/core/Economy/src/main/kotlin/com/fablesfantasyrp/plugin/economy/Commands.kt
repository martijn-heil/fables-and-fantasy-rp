/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.parametric.annotation.Range
import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.characters.displayName
import com.fablesfantasyrp.plugin.economy.data.entity.EntityProfileEconomyRepository
import com.fablesfantasyrp.plugin.economy.gui.bank.BankGuiMainMenu
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.text.sendError
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Commands(private val plugin: JavaPlugin,
			   private val profileManager: ProfileManager,
			   private val profiles: EntityProfileRepository,
			   private val economyRepository: EntityProfileEconomyRepository) {
	@Command(aliases = ["balance", "bal", "fbalance", "fbal"], desc = "Show your balance")
	@Require(Permission.Command.Balance)
	fun balance(@Sender sender: CommandSender,
				@CommandTarget(Permission.Command.Balance + ".others") @AllowCharacterName target: Profile) {
		flaunch {
			val currentPlayer = profileManager.getCurrentForProfile(target)
			val start = if (currentPlayer == sender) "You have" else "${target.displayName()} has"
			sender.sendMessage("$SYSPREFIX $start ${CURRENCY_SYMBOL}${target.money}")
		}
	}

	@Command(aliases = ["pay", "fpay"], desc = "Pay someone money")
	@Require(Permission.Command.Pay)
	fun pay(@Sender sender: Profile, @AllowCharacterName target: Profile, @Range(min = 1.0) amount: Int) {
		flaunch {
			val targetPlayer = profileManager.getCurrentForProfile(target)

			val senderPlayer = profileManager.getCurrentForProfile(sender)!!
			val senderOwnProfiles = profiles.allForOwner(senderPlayer)

			if (!target.isActive) {
				senderPlayer.sendError("You cannot pay an inactive profile!")
				return@flaunch
			}

			if (sender.money < amount) {
				senderPlayer.sendError("You cannot afford that!")
				return@flaunch
			}

			if (!senderPlayer.hasPermission(Permission.PayOwn) && senderOwnProfiles.contains(target)) {
				senderPlayer.sendError("You cannot pay a profile that you own!")
				return@flaunch
			}

			sender.money -= amount
			target.money += amount
			senderPlayer.sendMessage("$SYSPREFIX Sent $CURRENCY_SYMBOL${amount} to ${target.displayName()}")
			targetPlayer?.sendMessage("$SYSPREFIX ${sender.displayName()} sent you $CURRENCY_SYMBOL${amount}")
		}
	}

	inner class Eco {
		@Command(aliases = ["give"], desc = "Give money")
		@Require(Permission.Command.Eco.Give)
		fun give(@Sender sender: CommandSender,
				 @CommandTarget @AllowCharacterName target: Profile,
				 @Range(min = 1.0) amount: Int) {
			flaunch {
				target.money += amount
				sender.sendMessage("$SYSPREFIX Gave $CURRENCY_SYMBOL$amount to ${target.displayName()}")
			}
		}

		@Command(aliases = ["take"], desc = "Take money")
		@Require(Permission.Command.Eco.Take)
		fun take(@Sender sender: CommandSender,
				 @CommandTarget @AllowCharacterName target: Profile,
				 @Range(min = 1.0) amount: Int) {
			flaunch {
				target.money -= amount
				sender.sendMessage("$SYSPREFIX Took $CURRENCY_SYMBOL$amount from ${target.displayName()}")
			}
		}

		@Command(aliases = ["set"], desc = "Set money")
		@Require(Permission.Command.Eco.Set)
		fun set(@Sender sender: CommandSender,
				@CommandTarget @AllowCharacterName target: Profile,
				@Range(min = 0.0) amount: Int) {
			flaunch {
				target.money = amount
				sender.sendMessage("$SYSPREFIX Set ${target.displayName()}'s balance to $CURRENCY_SYMBOL$amount")
			}
		}
	}

	inner class PlayerEco {
		@Command(aliases = ["give"], desc = "Give money")
		@Require(Permission.Command.Eco.Give)
		fun give(@Sender sender: CommandSender,
				 @CommandTarget player: Player,
				 @Range(min = 1.0) amount: Int) {
			flaunch {
				val target = profileManager.getCurrentForPlayer(player) ?: run {
					sender.sendError("This player is not currently on a profile")
					return@flaunch
				}
				target.money += amount
				sender.sendMessage("$SYSPREFIX Gave $CURRENCY_SYMBOL$amount to ${target.displayName()}")
			}
		}

		@Command(aliases = ["take"], desc = "Take money")
		@Require(Permission.Command.Eco.Take)
		fun take(@Sender sender: CommandSender,
				 @CommandTarget player: Player,
				 @Range(min = 1.0) amount: Int) {
			flaunch {
				val target = profileManager.getCurrentForPlayer(player) ?: run {
					sender.sendError("This player is not currently on a profile")
					return@flaunch
				}
				target.money -= amount
				sender.sendMessage("$SYSPREFIX Took $CURRENCY_SYMBOL$amount from ${target.displayName()}")
			}
		}

		@Command(aliases = ["set"], desc = "Set money")
		@Require(Permission.Command.Eco.Set)
		fun set(@Sender sender: CommandSender,
				@CommandTarget player: Player,
				@Range(min = 0.0) amount: Int) {
			flaunch {
				val target = profileManager.getCurrentForPlayer(player) ?: run {
					sender.sendError("This player is not currently on a profile")
					return@flaunch
				}
				target.money = amount
				sender.sendMessage("$SYSPREFIX Set ${target.displayName()}'s balance to $CURRENCY_SYMBOL$amount")
			}
		}
	}

	inner class Bank {
		@Command(aliases = ["open"], desc = "Open bank")
		@Require(Permission.Command.Bank.Open)
		fun open(@Sender sender: Player, @AllowCharacterName target: Profile) {
			BankGuiMainMenu(plugin, economyRepository.forProfile(target)).show(sender)
		}
	}
}
