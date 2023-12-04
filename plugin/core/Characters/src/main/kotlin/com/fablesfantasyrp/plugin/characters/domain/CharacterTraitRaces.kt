package com.fablesfantasyrp.plugin.characters.domain

import com.fablesfantasyrp.plugin.characters.dal.enums.Race

val CharacterTraitRaces = hashMapOf(
	Pair(Race.DARK_ELF, setOf(
		CharacterTrait.ARCANE_INITIATE,
		CharacterTrait.NIGHTSEER,
		CharacterTrait.STRONG,
		CharacterTrait.KNOWLEDGEABLE,
		CharacterTrait.SWIFT,
		CharacterTrait.ATTUNED_TO_IGNOS,
		CharacterTrait.DEATHSPEAKER,
		CharacterTrait.NIGHT_LORDS
	)),
	Pair(Race.WOOD_ELF, setOf(
		CharacterTrait.SWIFT,
		CharacterTrait.NIGHTSEER,
		CharacterTrait.STRONG,
		CharacterTrait.ASPECT_OF_LILITHS_VEIL,
		CharacterTrait.ASPECT_OF_THE_EMERALD_DUSK,
		CharacterTrait.ASPECT_OF_EDENS_SHINE,
		CharacterTrait.ASPECT_OF_THE_AMBER_DAWN
	)),
	Pair(Race.SYLVANI, setOf(
		CharacterTrait.SWIFT,
		CharacterTrait.NIGHTSEER,
		CharacterTrait.STRONG,
		CharacterTrait.ASPECT_OF_LILITHS_VEIL,
		CharacterTrait.ASPECT_OF_THE_EMERALD_DUSK,
		CharacterTrait.ASPECT_OF_EDENS_SHINE,
		CharacterTrait.ASPECT_OF_THE_AMBER_DAWN
	)),
	Pair(Race.HIGH_ELF, setOf(
		CharacterTrait.SWIFT,
		CharacterTrait.ARCANE_INITIATE,
		CharacterTrait.INTELLIGENT,
		CharacterTrait.NIGHTSEER,
		CharacterTrait.DRAGON_BLOODED,
		CharacterTrait.PHALANX,
		CharacterTrait.SWIFT_LEARNER,
		CharacterTrait.KNOWLEDGEABLE,
	)),
	Pair(Race.ATTIAN_HUMAN, setOf(
		CharacterTrait.STRONG,
		CharacterTrait.PAINFULLY_AVERAGE,
		CharacterTrait.ATTIAN_HERITAGE,
		CharacterTrait.WITCH_HUNTER,
		CharacterTrait.HORSEMASTER,
		CharacterTrait.ADAPTIVE,
		CharacterTrait.WARDEN,
		CharacterTrait.VOIDAL_RESILIENCE,
	)),
	Pair(Race.HINTERLANDER_HUMAN, setOf(
		CharacterTrait.ADAPTIVE,
		CharacterTrait.ABNORMALLY_TALL,
		CharacterTrait.SEA_LEGS,
		CharacterTrait.KNOWLEDGEABLE,
		CharacterTrait.HINTISH_HERITAGE,
		CharacterTrait.ARCANE_INITIATE,
		CharacterTrait.PACKMULE
	)),
	Pair(Race.KHADAN_HUMAN, setOf(
		CharacterTrait.ARCANE_INITIATE,
		CharacterTrait.STRONG,
		CharacterTrait.LIMB_CARVER,
		CharacterTrait.NOMADS_STOMACH,
		CharacterTrait.FOLLOWER_OF_AKHMAT,
		CharacterTrait.DECAPITATING_STRIKE,
		CharacterTrait.ATTUNED_TO_IGNOS,
		CharacterTrait.SOUL_REAPER
	)),
	Pair(Race.DWARF, setOf(
		CharacterTrait.NIGHTSEER,
		CharacterTrait.STURDY,
		CharacterTrait.ENDURING_AS_ROCK,
		CharacterTrait.TOO_ANGRY_TO_DIE,
		CharacterTrait.MAGICAL_RESILIENCE,
		CharacterTrait.NATURAL_SPRINTER,
		CharacterTrait.WARDEN
	)),
	Pair(Race.HALFLING, setOf(
		CharacterTrait.NATURALLY_STEALTHY,
		CharacterTrait.BODILY_RESILIENCE,
		CharacterTrait.IMPOSSIBLE_TO_FINISH,
		CharacterTrait.MULEMASTER,
		CharacterTrait.KICK_EM_IN_THE_SHINS,
		CharacterTrait.SMALL_STOMACH
	)),
	Pair(Race.ORC, setOf(
		CharacterTrait.STRONG,
		CharacterTrait.TOO_ANGRY_TO_DIE,
		CharacterTrait.SAVAGE_BLOWS,
		CharacterTrait.FRENZY,
		CharacterTrait.SPIDAH_RIDAH,
		CharacterTrait.PROPHET_OF_THE_GREEN_FLAME,
		CharacterTrait.BOSS_SKEWER,
		CharacterTrait.GREAT_HUNTER
	)),
	Pair(Race.GOBLIN, setOf(
		CharacterTrait.NATURALLY_STEALTHY,
		CharacterTrait.KNOWLEDGEABLE,
		CharacterTrait.SNEAKY_STABBA,
		CharacterTrait.MUSHROOM_ADDICT,
		CharacterTrait.IMPOSSIBLE_TO_FINISH,
		CharacterTrait.ACID_SPITTA,
		CharacterTrait.SPIDAH_RIDAH
	)),
	Pair(Race.TIEFLING, setOf(
		CharacterTrait.SWIFT,
		CharacterTrait.ARCANE_INITIATE,
		CharacterTrait.NIGHTSEER,
		CharacterTrait.VOIDAL_RESILIENCE,
		CharacterTrait.KNOWLEDGEABLE,
		CharacterTrait.PIERCING_GAZE,
		CharacterTrait.BODILY_RESILIENCE,
		CharacterTrait.PACKMULE
	)),
)
