package com.fablesfantasyrp.plugin.characters.domain

enum class CharacterTrait(val displayName: String, val description: String?) {
	// Traits with programmatic effects
	ARCANE_INITIATE("Arcane Initiate","Players with this trait know the 'Flame Arrow' spell without needing to be a spellcaster"),
	NIGHTSEER("Nightseer","Players with this trait get the night vision effect when entering dark spaces and during the night."),
	STRONG("Strong","Players with this trait get two extra points in the strength stat."),
	KNOWLEDGEABLE("Knowledgeable","Players with this trait get two extra lvl 1 spell slots and one extra lvl 2 spell slot."),
	NIGHT_LORDS("Night Lords","Players with this trait have a +1 on all their stat rolls during the night"),
	SWIFT("Swift","Players with this trait have +2 movement in CRP. Outside CRP, players with this trait get a permanent speed 1 boost."),
	PACKMULE("Packmule","Players with this trait have a maximum weight cap of +20"),
	INTELLIGENT("Intelligent","Players with this trait get two extra points in intelligence."),
	NOMADS_STOMACH("Nomad\'s Stomach","The food bar of players with this trait lowers significantly slower than usual."),
	STURDY("Sturdy","Players with this trait get two extra points in the defence stat."),
	ENDURING_AS_ROCK("Enduring As Rock","Whenever damage is done to this player, roll a D6. On a 4+, the attack that hit the player with this trait does -1 damage. (Resilience potion effect outside CRP)"),
	TOO_ANGRY_TO_DIE("Too Angry To Die","When a player with this trait reaches 0HP, they roll a D20. On a 15+ they regenerate 4 HP. (Totem of undying effect outside CRP.)"),
	NATURALLY_STEALTHY("Naturally Stealthy","Players with this trait can go invisible if they stand still holding shift for a few seconds. This stealthy stance is broken once they move."),
	ABNORMALLY_TALL("Abnormally Tall","Players with this trait are considered LARGE creatures instead of MEDIUM."),
	SPIDAH_RIDAH("Spidah Ridah","Players with this trait start with a spider mount. (Voras Pet)"),
	ATTIAN_HERITAGE("Attian Heritage","Players with this trait get an additional 2 HP."),
	HINTISH_HERITAGE("Hintish Heritage","Players with this trait harvest double the meat and crops from their farms/animals due to their farmer's background."),
	PROPHET_OF_THE_GREEN_FLAME("Prophet of the Green Flame","Players with this trait know the 'fireball' spell without needing to be a spellcaster. Additionally, this flame is green and POISONS and BURNS any player it hits for D3 turns."),
	ASPECT_OF_LILITHS_VEIL("Aspect of Lilith's Veil","Players with this trait know the \"Ice Maiden's Kiss\" spell without needing to be a spellcaster during the season Lilith's Veil"),

	BLIND("Blind", "Players have permanent blindness and have a -2 debuff on Strength, Defence, and Agility rolls."),

	// This one is not linked to a race at the moment, but implemented
	HULKING_BRUTE("Hulking Brute", "Players with this trait are considered LARGE creatures instead of MEDIUM."),


	// Traits without programmatic effects
  	BODILY_RESILIENCE("Bodily Resilience","Players with this trait are immune to diseases and poison"),
  	SEA_LEGS("Sea Legs","Players with this trait have a speed bonus on the ocean when in a boat and can hold their breath for double the amount of time."),
  	DEATHSPEAKER("Deathspeaker","Players with this trait can interact with ghosts and corpses during events, being able to ask them D5 questions before the effect wears off."),
	ASPECT_OF_THE_EMERALD_DUSK("Aspect of the Emerald Dusk","Players with this trait can use the action \"healing burst\" which heals D3 damage in combat during the season The Emerald Dusk"),
	ASPECT_OF_EDENS_SHINE("Aspect of Eden's Shine","Players with this trait deal one additional damage on all melee attacks during the season Eden's Shine"),
	ASPECT_OF_THE_AMBER_DAWN("Aspect of the Amber Dawn","Players with this trait get 2 additional attacks on ranged weapons during the season Amber Dawn"),
	DRAGON_BLOODED("Dragon Blooded","Players with this trait know the \"Breath of the Dragon\" spell without needing to be a spellcaster"),
	PHALANX("Phalanx","Players with this trait may use the action \"form Phalanx\" in combat. When they do, they provide a +1 to defensive rolls for everyone and themself within 1 block of them until their next turn."),
	SWIFT_LEARNER("Swift learner","Players with this trait require one less lesson/rp session per magic level before they may do their next bonding trial."),
	PAINFULLY_AVERAGE("Painfully Average","Players with this trait get two extra skill points which they can freely distribute."),
	WITCH_HUNTER("Witch Hunter","Players with this trait do 2 additional damage with melee attacks against spellcasters with any weapon"),
	VOIDAL_RESILIENCE("Voidal Resilience","Players with this trait can only take a maximum of 4 damage per attack from voidal mages and creatures."),
	HORSEMASTER("Horsemaster","Players with this trait deal 1D2 additional damage when mounted on a horse. Additionally, the horse has 3 additional movement range."),
	ADAPTIVE( "Adaptive","Thrice per battle, players with this trait may choose a stat modifier of their choice when rolling for any action, reaction, movement roll, or block."),
	WARDEN("Warden","Players with this trait may choose to attempt to take damage aimed at their allies within 3 meters of them during CRP. When doing so, roll a D20. On a 8+, the player with the warden trait takes the damage instead of the target player."),
	LIMB_CARVER("Limb Carver","Players with this trait can replace parts of their body with sandstone sculptures when lost. If they do, the body part becomes fully functional again after one IC year. Though the body part is and still looks like sandstone."),
	FOLLOWER_OF_AKHMAT( "Follower of Akhmat","Players with this trait can summon a skeleton which can be used in CRP using the skeleton stat block."),
	DECAPITATING_STRIKE("Decapitating Strike","Once per battle during CRP. The player can choose to instead of making a normal attack it will do a single Decapitating blow. If they do, they may do a single attack with any weapon that does D6 damage."),
	SOUL_REAPER( "Soul Reaper","At the start of combat, players with this trait can select a target who's soul is marked by Akhmat and which they must claim. If they do, the player can re-roll attack rolls against this target for the duration of the battle."),
	MAGICAL_RESILIENCE("Magical Resilience","Players with this trait may ignore spell effects if the effectiveness roll is below 12"),
	NATURAL_SPRINTER("Natural Sprinter","Players with this trait may once per battle add 5 to their dash action range."),
	IMPOSSIBLE_TO_FINISH("Impossible to Finish","When players with this stat reach 1 HP, they get a +4 on all their defensive rolls. Though they get a -2 in all their attack rolls while this is active."),
	MULEMASTER("Mulemaster","Players with this trait have 8 mount HP instead of 5 when riding a mule. Additionally, the mule has a defence modifier of +1. Despite the stats of the rider."),
	KICK_EM_IN_THE_SHINS("Kick 'em in the Shins","Players with this trait have a +1 to melee attacks targeting medium or large targets."),
	SMALL_STOMACH("Small Stomach","Food regenerate double the food bar points when eaten by a player with this trait."),
	SAVAGE_BLOWS("Savage Blows","Players with this trait do 2 additional damage when striking a critical hit. (18+)"),
	FRENZY("Frenzy","Players with this trait get 1 additional attack per 2 combat rounds with one-handed melee weapons during their turn in combat. Ex: Turn 1 = 2 attacks with a sword, turn 3 = 3 attacks, turn 5 = 4 attacks, etc. The Frenzy resets if the user doesn't fight for a turn."),
	BOSS_SKEWER("Boss Skewer","Players with this trait do D2 additional damage to Nation/City-State/Claim leaders & stewards with melee weapons"),
	GREAT_HUNTER("Great Hunter","Players with this trait do D3 additional damage to creatures with the BEAST or ANIMAL tag."),
	SNEAKY_STABBA( "Sneaky Stabba","When initiating combat, the first round of attacks do 3 additional damage when they hit."),
	MUSHROOM_ADDICT("Mushroom Addict","Players with this trait can once per battle consume a mushroom item using their action. If they do, they go in a trance that allows them to take two actions in their next turn. Additionally, they can add a +1 to all their rolls for that turn."),
	ACID_SPITTA("Acid Spitta","Players with this trait can spit acid at other players, giving them the 'Blindness' status effect for 1 turn if they hit. (roll agility) Range: 10 blocks."),
	PIERCING_GAZE("Piercing Gaze","Players with this trait are immune to the 'blindness' status effect."),
	ATTUNED_TO_IGNOS("Attuned to Ignos","Players with this trait can ignite their weapon. Applying the 'Burning' status effect to whoever they hit."),
}