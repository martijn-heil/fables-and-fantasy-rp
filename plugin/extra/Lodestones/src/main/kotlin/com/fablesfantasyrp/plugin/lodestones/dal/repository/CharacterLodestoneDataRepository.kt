package com.fablesfantasyrp.plugin.lodestones.dal.repository

interface CharacterLodestoneDataRepository {
	fun forCharacter(characterId: Int): Set<Int>
	fun add(characterId: Int, lodestoneId: Int)
	fun remove(characterId: Int, lodestoneId: Int)
	fun destroy(lodestoneId: Int)
}
