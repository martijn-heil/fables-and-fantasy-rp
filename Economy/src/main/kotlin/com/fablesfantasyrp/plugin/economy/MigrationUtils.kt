package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.plugin.characters.characterRepository
import com.fablesfantasyrp.plugin.economy.data.entity.EntityPlayerInstanceEconomyRepository
import me.dablakbandit.bank.api.BankAPI
import org.bukkit.Server

internal fun migrate(server: Server, bankApi: BankAPI, economyRepository: EntityPlayerInstanceEconomyRepository) {
	for (player in server.offlinePlayers) {
		val characters = characterRepository.forOwner(player).map { Pair(it, economyRepository.forPlayerInstance(it.playerInstance)) }
		val money: Int = bankApi.getMoney(player.uniqueId.toString()).toInt()
		if (money == 0) continue
		val mod = money % characters.size
		val each = money / characters.size
		characters.forEach { it.second.bankMoney = each }
		characters.first().second.bankMoney += mod
	}
}
