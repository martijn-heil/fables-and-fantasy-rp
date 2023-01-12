package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.plugin.characters.MutableDenizenCharacter
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.economy.data.entity.EntityPlayerInstanceEconomyRepository
import me.dablakbandit.bank.api.BankAPI
import org.bukkit.Server

internal fun migrate(server: Server, bankApi: BankAPI,
					 economyRepository: EntityPlayerInstanceEconomyRepository,
					 characterRepository: EntityCharacterRepository) {
	for (player in server.offlinePlayers) {
		val characters = characterRepository.forOwner(player).map { Pair(it, economyRepository.forPlayerInstance(it.playerInstance)) }
		if (characters.isEmpty()) continue
		val money: Int = bankApi.getMoney(player.uniqueId.toString()).toInt()
		if (money == 0) continue
		val mod = money % characters.size
		val each = money / characters.size
		characters.forEach { it.second.bankMoney = each }
		characters.first().second.bankMoney += mod
	}

	for (character in characterRepository.all()) {
		val pocketMoney = MutableDenizenCharacter(character.id, character.playerInstance.owner).money
		val economy = economyRepository.forPlayerInstance(character.playerInstance)
		economy.money = pocketMoney.toInt()
	}
}
