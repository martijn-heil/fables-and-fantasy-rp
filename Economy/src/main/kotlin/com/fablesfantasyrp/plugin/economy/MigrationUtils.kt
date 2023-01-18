package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.plugin.characters.MutableDenizenCharacter
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.economy.data.entity.EntityProfileEconomyRepository
import me.dablakbandit.bank.api.BankAPI
import org.bukkit.plugin.Plugin

internal fun migrate(plugin: Plugin, bankApi: BankAPI,
					 economyRepository: EntityProfileEconomyRepository,
					 characterRepository: EntityCharacterRepository) {
	val server = plugin.server

	for (player in server.offlinePlayers) {
		val characters = characterRepository.forOwner(player).map { Pair(it, economyRepository.forProfile(it.profile)) }
		if (characters.isEmpty()) continue
		val money: Int = bankApi.getMoney(player.uniqueId.toString()).toInt()
		if (money == 0) continue
		val mod = money % characters.size
		val each = money / characters.size
		characters.forEach { it.second.bankMoney = each }
		characters.first().second.bankMoney += mod
	}

	for (character in characterRepository.all()) {
		plugin.logger.info("Migrating #${character.id}")
		val pocketMoney = MutableDenizenCharacter(character.id, character.profile.owner).money
		val economy = economyRepository.forProfile(character.profile)
		economy.money = pocketMoney.toInt()
	}
}
