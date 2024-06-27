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
package com.fablesfantasyrp.plugin.economy.interop.vault

import com.fablesfantasyrp.plugin.economy.money
import com.fablesfantasyrp.plugin.profile.ProfileManager
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import java.util.logging.Logger
import kotlin.math.roundToInt

class VaultProfileEconomy(private val server: Server,
						  private val profileManager: ProfileManager,
						  private val logger: Logger) : Economy {
	private fun requirePlayedBefore(player: OfflinePlayer?, amount: Double): EconomyResponse? {
		return if (player?.hasPlayedBefore() != true) {
			logger.warning("Tried to interact with player that has never played before: ${player?.name}")
			EconomyResponse(amount, 0.00, EconomyResponse.ResponseType.FAILURE,
					"The target player has never played on this server before!")
		} else null
	}

	override fun isEnabled() = true
	override fun getName() = "ProfileEconomy"
	override fun hasBankSupport() = false
	override fun fractionalDigits() = 0
	override fun format(amount: Double) = "Ⓐ$amount"
	override fun currencyNamePlural() = "Andros"
	override fun currencyNameSingular() = "Andros"
	override fun hasAccount(player: OfflinePlayer?) = player?.hasPlayedBefore() ?: false
	override fun hasAccount(player: OfflinePlayer?, worldName: String?) = this.hasAccount(player)
	override fun createPlayerAccount(player: OfflinePlayer?) = false
	override fun createPlayerAccount(player: OfflinePlayer?, worldName: String?) = this.createPlayerAccount(player)

	override fun getBalance(player: OfflinePlayer?): Double
			= player?.player?.let { profileManager.getCurrentForPlayer(it)?.money?.toDouble() } ?: 0.00
	override fun getBalance(player: OfflinePlayer?, world: String?) = this.getBalance(player)
	override fun has(player: OfflinePlayer?, amount: Double) = this.getBalance(player) >= amount
	override fun has(player: OfflinePlayer?, worldName: String?, amount: Double) = this.has(player, amount)

	override fun withdrawPlayer(player: OfflinePlayer?, amount: Double): EconomyResponse {
		require(amount >= 0.00)
		requirePlayedBefore(player, amount)?.let { return it }
		check(player != null)

		val profile = player.player?.let { profileManager.getCurrentForPlayer(it) }
				?: return EconomyResponse(amount, this.getBalance(player), EconomyResponse.ResponseType.FAILURE,
						"The target player is not online or does not currently have an active profile!")

		val bal = this.getBalance(player)
		return if (bal >= amount) {
			profile.money -= amount.roundToInt()
			EconomyResponse(amount, this.getBalance(player), EconomyResponse.ResponseType.SUCCESS, null)
		} else {
			EconomyResponse(amount, this.getBalance(player), EconomyResponse.ResponseType.FAILURE, "You don't have enough andros!")
		}
	}

	override fun withdrawPlayer(player: OfflinePlayer?, worldName: String?, amount: Double): EconomyResponse
			= this.withdrawPlayer(player, amount)

	override fun depositPlayer(player: OfflinePlayer?, amount: Double): EconomyResponse {
		require(amount >= 0.00)
		requirePlayedBefore(player, amount)?.let { return it }
		check(player != null)

		val profile = player.player?.let { profileManager.getCurrentForPlayer(it) }
				?: return EconomyResponse(amount, this.getBalance(player), EconomyResponse.ResponseType.FAILURE,
						"The target player is not online or does not currently have an active profile!")

		profile.money += amount.roundToInt()
		return EconomyResponse(amount, this.getBalance(player), EconomyResponse.ResponseType.SUCCESS, null)
	}

	override fun depositPlayer(player: OfflinePlayer?, worldName: String?, amount: Double): EconomyResponse
			= this.depositPlayer(player, amount)



	override fun createBank(name: String?, player: OfflinePlayer?): EconomyResponse {
		throw UnsupportedOperationException("No support for bank")
	}

	override fun deleteBank(name: String?): EconomyResponse {
		throw UnsupportedOperationException("No support for bank")
	}

	override fun bankBalance(name: String?): EconomyResponse {
		throw UnsupportedOperationException("No support for bank")
	}

	override fun bankHas(name: String?, amount: Double): EconomyResponse {
		throw UnsupportedOperationException("No support for bank")
	}

	override fun bankWithdraw(name: String?, amount: Double): EconomyResponse {
		throw UnsupportedOperationException("No support for bank")
	}

	override fun bankDeposit(name: String?, amount: Double): EconomyResponse {
		throw UnsupportedOperationException("No support for bank")
	}

	override fun isBankOwner(name: String?, player: OfflinePlayer?): EconomyResponse {
		throw UnsupportedOperationException("No support for bank")
	}

	override fun isBankMember(name: String?, player: OfflinePlayer?): EconomyResponse {
		throw UnsupportedOperationException("No support for bank")
	}

	override fun getBanks(): MutableList<String> {
		throw UnsupportedOperationException("No support for bank")
	}



	@Deprecated("Deprecated in Java")
	override fun getBalance(playerName: String?)
			= playerName?.let { name -> server.getOfflinePlayer(name) }?.let { this.getBalance(it) } ?: 0.00

	@Deprecated("Deprecated in Java")
	override fun hasAccount(playerName: String?)
			= playerName?.let { name -> server.getOfflinePlayer(name) }?.let { this.hasAccount(it) } ?: false

	@Deprecated("Deprecated in Java")
	override fun hasAccount(playerName: String?, worldName: String?) = this.hasAccount(playerName)

	@Deprecated("Deprecated in Java")
	override fun getBalance(playerName: String?, world: String?) = this.getBalance(playerName)

	@Deprecated("Deprecated in Java")
	override fun has(playerName: String?, amount: Double)
			= playerName?.let { name -> server.getOfflinePlayer(name) }?.let { this.has(it, amount) } ?: false

	@Deprecated("Deprecated in Java")
	override fun withdrawPlayer(playerName: String?, amount: Double): EconomyResponse
		= playerName?.let { name -> server.getOfflinePlayer(name) }?.let { this.withdrawPlayer(it, amount) }
				?: EconomyResponse(amount, 0.00, EconomyResponse.ResponseType.FAILURE, "Player not found")

	@Deprecated("Deprecated in Java")
	override fun withdrawPlayer(playerName: String?, worldName: String?, amount: Double) = this.withdrawPlayer(playerName, amount)

	@Deprecated("Deprecated in Java")
	override fun has(playerName: String?, worldName: String?, amount: Double) = this.has(playerName, amount)

	@Deprecated("Deprecated in Java")
	override fun depositPlayer(playerName: String?, amount: Double): EconomyResponse
		= playerName?.let { name -> server.getOfflinePlayer(name) }?.let { this.depositPlayer(it, amount) }
				?: EconomyResponse(amount, 0.00, EconomyResponse.ResponseType.FAILURE, "Player not found")

	@Deprecated("Deprecated in Java")
	override fun depositPlayer(playerName: String?, worldName: String?, amount: Double) = this.depositPlayer(playerName, amount)



	@Deprecated("Deprecated in Java")
	override fun createBank(name: String?, player: String?): EconomyResponse {
		throw UnsupportedOperationException("No support for bank")
	}

	@Deprecated("Deprecated in Java")
	override fun isBankOwner(name: String?, playerName: String?): EconomyResponse {
		throw UnsupportedOperationException("No support for bank")
	}

	@Deprecated("Deprecated in Java")
	override fun isBankMember(name: String?, playerName: String?): EconomyResponse {
		throw UnsupportedOperationException("No support for bank")
	}

	@Deprecated("Deprecated in Java")
	override fun createPlayerAccount(playerName: String?): Boolean {
		throw UnsupportedOperationException("No support for bank")
	}

	@Deprecated("Deprecated in Java")
	override fun createPlayerAccount(playerName: String?, worldName: String?): Boolean {
		throw UnsupportedOperationException("No support for bank")
	}
}
