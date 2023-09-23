CREATE TABLE character_trait (
	id						VARCHAR PRIMARY KEY NOT NULL,
	display_name			VARCHAR NOT NULL,
	description				VARCHAR
);

CREATE TABLE character_character_trait (
	character_id		INT NOT NULL,
	character_trait_id	VARCHAR NOT NULL,

	UNIQUE(character_id, character_trait_id),
	FOREIGN KEY (character_trait_id) REFERENCES character_trait(id)
);

CREATE TABLE race_character_trait (
	race				ENUM('HUMAN', 'ATTIAN_HUMAN', 'HINTERLANDER_HUMAN', 'KHADAN_HUMAN', 'HIGH_ELF', 'DARK_ELF', 'WOOD_ELF', 'DWARF', 'TIEFLING', 'ORC', 'GOBLIN', 'HALFLING', 'SYLVANI', 'OTHER') NOT NULL,
	character_trait_id 	VARCHAR NOT NULL,

	UNIQUE(race, character_trait_id),
	FOREIGN KEY (character_trait_id) REFERENCES character_trait(id)
);

INSERT INTO character_trait (id, display_name, description) VALUES ('arcane_initiate', 'Arcane Initiate',
	'Players with this trait know the "Flame Arrow" spell without needing to be a spellcaster');

INSERT INTO character_trait (id, display_name, description) VALUES ('nightseer', 'Nightseer',
	'Players with this trait get the night vision effect when entering dark spaces and during the night.');

INSERT INTO character_trait (id, display_name, description) VALUES ('strong', 'Strong',
	'Players with this trait get two extra points in the strength stat.');

INSERT INTO character_trait (id, display_name, description) VALUES ('companion', 'Companion',
	'Players with this trait get to choose a companion pet. It can be used in CRP and events.');

INSERT INTO character_trait (id, display_name, description) VALUES ('knowledgeable', 'Knowledgeable',
	'Players with this trait get two extra lvl 1 spell slots and one extra lvl 2 spell slot.');

INSERT INTO character_trait (id, display_name, description) VALUES ('night_lords', 'Night Lords',
	'Players with this trait have a +1 on all their stat rolls during the night');

INSERT INTO character_trait (id, display_name, description) VALUES ('swift', 'Swift',
	'Players with this trait have +2 movement in CRP. Outside CRP, players with this trait get a permanent speed 1 boost. (Speed 2 on roads)');

INSERT INTO character_trait (id, display_name, description) VALUES ('packmule', 'Packmule',
	'Players with this trait have a maximum weight cap of +20');

INSERT INTO character_trait (id, display_name, description) VALUES ('heightened_senses', 'Heightened Senses',
	'Players can use this ability once per IC day. When using it, every entity in a radius of 20 blocks gets highlighted for a few seconds (glow minecraft effect)');

INSERT INTO character_trait (id, display_name, description) VALUES ('intelligent', 'Intelligent',
	'Players with this trait get two extra points in intelligence.');

INSERT INTO character_trait (id, display_name, description) VALUES ('nomads_stomach', 'Nomad''s Stomach',
	'The food bar of players with this trait lowers significantly slower than usual.');

INSERT INTO character_trait (id, display_name, description) VALUES ('sturdy', 'Sturdy',
	'Players with this trait get two extra points in the defence stat.');

INSERT INTO character_trait (id, display_name, description) VALUES ('enduring_as_rock', 'Enduring As Rock',
	'Whenever damage is done to this player, roll a D6. On a 4+, the attack that hit the player with this trait does -1 damage. (Resilience potion effect outside CRP)');

INSERT INTO character_trait (id, display_name, description) VALUES ('too_angry_to_die', 'Too Angry To Die',
	'When a player with this trait reaches 0HP, they roll a D20. On a 15+ they regenerate 4 HP. (Totem of undying effect outside CRP.)');

INSERT INTO character_trait (id, display_name, description) VALUES ('naturally_stealthy', 'Naturally Stealthy',
	'Players with this trait can go invisible if they stand still holding shift for a few seconds. This stealthy stance is broken once they move.');

INSERT INTO character_trait (id, display_name, description) VALUES ('bodily_resilience', 'Bodily Resilience',
	'Players with this trait are immune to diseases and poison');

INSERT INTO character_trait (id, display_name, description) VALUES ('attian_heritage', 'Attian Heritage',
	'Players with this trait get an additional 2 HP.');

INSERT INTO character_trait (id, display_name, description) VALUES ('spidah_ridah', 'Spidah Ridah',
	'Players with this trait start with a spider mount. (Voras Pet)');

INSERT INTO character_trait (id, display_name, description) VALUES ('hulking_brute', 'Hulking Brute',
	'Players with this trait are considered LARGE creatures instead of MEDIUM.');

INSERT INTO character_trait (id, display_name, description) VALUES ('they_wont_even_notice', 'They Won''t Even Notice',
	'Players with this trait have an additional 10% success bonus when using the lockpick item.');

INSERT INTO character_trait (id, display_name, description) VALUES ('prophet_of_the_green_flame', 'Prophet of the Green Flame',
	'Players with this trait know the "fireball" spell without needing to be a spellcaster. Additionally, this flame is green and POISONS and BURNS any player it hits for D3 turns.');

INSERT INTO character_trait (id, display_name, description) VALUES ('big_thinka', 'Big Thinka',
	'Players with this trait can once per battle cast a spell that they have seen another mage cast. Even if they do not have that spell in their spellbook. This only works when the character is a spellcaster.');

INSERT INTO character_trait (id, display_name, description) VALUES ('hintish_heritage', 'Hintish Heritage',
	'Players with this trait harvest double the meat and crops from their farms/animals due to their farmer''s background.');

INSERT INTO character_trait (id, display_name, description) VALUES ('sea_legs', 'Sea Legs',
	'Players with this trait have a speed bonus on the ocean when in a boat and can hold their breath for double the amount of time.');




INSERT INTO character_trait (id, display_name, description) VALUES ('deathspeaker', 'Deathspeaker',
	'Players with this trait can interact with ghosts and corpses during events, being able to ask them D5 questions before the effect wears off. (Once per event)');

INSERT INTO character_trait (id, display_name, description) VALUES ('sylvani_blooded', 'Sylvani blooded',
	'Players with this trait can regrow limbs after losing them.');

INSERT INTO character_trait (id, display_name, description) VALUES ('aspect_of_liliths_veil', 'Aspect of Lilith''s Veil',
	'Players with this trait know the "ICE BASED CANTRIP" spell without needing to be a spellcaster during the season Lilith''s Veil');

INSERT INTO character_trait (id, display_name, description) VALUES ('aspect_of_the_emerald_dusk', 'Aspect of the Emerald Dusk',
	'Players with this trait can use the action "healing burst" which heals D3 damage in combat during the season The Emerald Dusk');

INSERT INTO character_trait (id, display_name, description) VALUES ('aspect_of_edens_shine', 'Aspect of Eden''s Shine',
	'Players with this trait deal one additional damage on all melee attacks during the season Eden''s Shine');

INSERT INTO character_trait (id, display_name, description) VALUES ('aspect_of_the_amber_dawn', 'Aspect of the Amber Dawn',
	'Players with this trait get 2 additional attacks on ranged weapons during the season Amber Dawn');

INSERT INTO character_trait (id, display_name, description) VALUES ('lightning_reactions', 'Lightning Reactions',
	'Players with this traits always get to strike first in in CRP.');

INSERT INTO character_trait (id, display_name, description) VALUES ('dragon_blooded', 'Dragon Blooded',
	'Players with this trait know the "Breath of the Dragon" spell without needing to be a spellcaster');

INSERT INTO character_trait (id, display_name, description) VALUES ('phalanx', 'Phalanx',
	'Players with this trait may use the action "form Phalanx" in combat. When they do, they provide a +1 to defensive rolls for everyone and themself within 1 block of them until their next turn.');

INSERT INTO character_trait (id, display_name, description) VALUES ('swift_learner', 'Swift learner',
	'Players with this trait require one less lesson/rp session per magic level before they may do their next bonding trial.');

INSERT INTO character_trait (id, display_name, description) VALUES ('painfully_average', 'Painfully Average',
	'Players with this trait get two extra skill points which they can freely distribute.');

INSERT INTO character_trait (id, display_name, description) VALUES ('witch_hunter', 'Witch Hunter',
	'Players with this trait do 2 additional damage with melee attacks against spellcasters with any weapon');

INSERT INTO character_trait (id, display_name, description) VALUES ('voidal_resilience', 'Voidal Resilience',
	'Players with this trait can only take a maximum of 2 damage per attack from voidal mages and creatures.');

INSERT INTO character_trait (id, display_name, description) VALUES ('horsemaster', 'Horsemaster',
	'Players with this trait deal 1D2 additional damage when mounted on a horse. Additionally, the horse has 3 additional movement range.');

INSERT INTO character_trait (id, display_name, description) VALUES ('adaptive', 'Adaptive',
	'Thrice per battle, players with this trait may choose a stat modifier of their choice when rolling for any action, reaction, movement roll, or block.');

INSERT INTO character_trait (id, display_name, description) VALUES ('warden', 'Warden',
	'Players with this trait may choose to attempt to take damage aimed at their allies within 3 meters of them during CRP. When doing so, roll a D20. On a 8+, the player with the warden trait takes the damage instead of the target player.');

INSERT INTO character_trait (id, display_name, description) VALUES ('limb_carver', 'Limb Carver',
	'Players with this trait can replace parts of their body with sandstone sculptures when lost. If they do, the body part becomes fully functional again after one IC year. Though the body part is and still looks like sandstone.');

INSERT INTO character_trait (id, display_name, description) VALUES ('follower_of_akhmat', 'Follower of Akhmat',
	'Players with this trait can summon a skeleton which can be used in CRP using the skeleton stat block.');

INSERT INTO character_trait (id, display_name, description) VALUES ('decapitating_strike', 'Decapitating Strike',
	'Once per battle during CRP. The player can choose to instead of making a normal attack it will do a single Decapitating blow. If they do, they may do a single attack with any weapon that does D6 damage.');

INSERT INTO character_trait (id, display_name, description) VALUES ('soul_reaper', 'Soul Reaper',
	'At the start of combat, players with this trait can select a target who''s soul is marked by Akhmat and which they must claim. If they do, the player can re-roll attack rolls against this target for the duration of the battle.');

INSERT INTO character_trait (id, display_name, description) VALUES ('magical_resilience', 'Magical Resilience',
	'Players with this trait may ignore spell effects if the effectiveness roll is below 12');

INSERT INTO character_trait (id, display_name, description) VALUES ('natural_sprinter', 'Natural Sprinter',
	'Players with this trait may once per battle add 5 to their dash action range.');

INSERT INTO character_trait (id, display_name, description) VALUES ('impossible_to_finish', 'Impossible to Finish',
	'When players with this stat reach 1 HP, they get a +4 on all their defensive rolls. Though they get a -2 in all their attack rolls while this is active.');

INSERT INTO character_trait (id, display_name, description) VALUES ('mulemaster', 'Mulemaster',
	'Players with this trait have 8 mount HP instead of 5 when riding a mule. Additionally, the mule has a defence modifier of +1. Despite the stats of the rider.');

INSERT INTO character_trait (id, display_name, description) VALUES ('kick_em_in_the_shins', 'Kick ''em in the Shins',
	'Players with this trait have a +1 to melee attacks targeting medium or large targets.');

INSERT INTO character_trait (id, display_name, description) VALUES ('small_stomach', 'Small Stomach',
	'Food regenerate double the food bar points when eaten by a player with this trait.');

INSERT INTO character_trait (id, display_name, description) VALUES ('savage_blows', 'Savage Blows',
	'Players with this trait do 2 additional damage when striking a critical hit. (18+)');

INSERT INTO character_trait (id, display_name, description) VALUES ('frenzy', 'Frenzy',
	'Players with this trait get 1 additional attack per 2 combat rounds with one-handed weapons during their turn in combat. Ex: Turn 1 = 2 attacks with a sword, turn 3 = 3 attacks, turn 5 = 4 attacks, etc.');

INSERT INTO character_trait (id, display_name, description) VALUES ('boss_skewer', 'Boss Skewer',
	'Players with this trait do D2 additional damage to Nation/City-State/Claim leaders & stewards with melee weapons');

INSERT INTO character_trait (id, display_name, description) VALUES ('great_hunter', 'Great Hunter',
	'Players with this trait do D3 additional damage to creatures with the BEAST or ANIMAL tag.');

INSERT INTO character_trait (id, display_name, description) VALUES ('sneaky_stabba', 'Sneaky Stabba',
	'When initiating combat, the first round of attacks do 3 additional damage when they hit.');

INSERT INTO character_trait (id, display_name, description) VALUES ('mushroom_addict', 'Mushroom Addict',
	'Players with this trait can once per battle consume a mushroom item using their action. If they do, they go in a trance that allows them to take two actions in their next turn. Additionally, they can add a +1 to all their rolls for that turn.');

INSERT INTO character_trait (id, display_name, description) VALUES ('acid_spitta', 'Acid Spitta',
	'Players with this trait can spit acid at other players, giving them the "Blindness" status effect for 1 turn if they hit. (roll agility) Range: 10 blocks.');

INSERT INTO character_trait (id, display_name, description) VALUES ('voidal_origins', 'Voidal Origins',
	'Players with this trait are immune to direct damage from voidal spells.');

INSERT INTO character_trait (id, display_name, description) VALUES ('bonding_skin', 'Bonding Skin',
	'Players with this trait heal 1 HP per combat round. - Outside CRP, players with this trait can regenerate small wounds naturally without treatment. (Ex: Sword cuts, arrow hits)');

INSERT INTO character_trait (id, display_name, description) VALUES ('piercing_gaze', 'Piercing Gaze',
	'Players with this trait are immune to the "blindness" status effect.');

INSERT INTO character_trait (id, display_name, description) VALUES ('missile_mirror', 'Missile Mirror',
	'Players start with an unique shield item that allows them to deflect missiles back to the one shooting them. If you wish to deflect missiles, roll a D20. On a 1-6, you fail to deflect the missile. On a 7-14, you deflect the missile back to the shooter and do half damage to a minimum of 1. On a 15-20 you deflect all missiles with full damage.');

INSERT INTO character_trait (id, display_name, description) VALUES ('attuned_to_ignos', 'Attuned to Ignos',
	'Players with this trait can ignite their weapon. Applying the "Burning" status effect to whoever they hit.');

INSERT INTO character_trait (id, display_name, description) VALUES ('abnormally_tall', 'Abnormally Tall',
	'Players with this trait are considered LARGE creatures instead of MEDIUM.');


INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DARK_ELF', 'arcane_initiate');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DARK_ELF', 'nightseer');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DARK_ELF', 'strong');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DARK_ELF', 'companion');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DARK_ELF', 'knowledgeable');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DARK_ELF', 'swift');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DARK_ELF', 'attuned_to_ignos');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DARK_ELF', 'deathspeaker');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DARK_ELF', 'heightened_senses');

INSERT INTO race_character_trait (race, character_trait_id) VALUES ('WOOD_ELF', 'swift');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('WOOD_ELF', 'heightened_senses');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('WOOD_ELF', 'nightseer');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('WOOD_ELF', 'strong');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('WOOD_ELF', 'companion');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('WOOD_ELF', 'aspect_of_liliths_veil');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('WOOD_ELF', 'aspect_of_the_emerald_dusk');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('WOOD_ELF', 'aspect_of_edens_shine');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('WOOD_ELF', 'aspect_of_the_amber_dawn');

INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HIGH_ELF', 'swift');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HIGH_ELF', 'arcane_initiate');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HIGH_ELF', 'intelligent');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HIGH_ELF', 'nightseer');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HIGH_ELF', 'companion');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HIGH_ELF', 'dragon_blooded');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HIGH_ELF', 'phalanx');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HIGH_ELF', 'swift_learner');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HIGH_ELF', 'knowledgeable');

INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ATTIAN_HUMAN', 'strong');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ATTIAN_HUMAN', 'companion');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ATTIAN_HUMAN', 'painfully_average');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ATTIAN_HUMAN', 'attian_heritage');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ATTIAN_HUMAN', 'witch_hunter');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ATTIAN_HUMAN', 'horsemaster');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ATTIAN_HUMAN', 'adaptive');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ATTIAN_HUMAN', 'warden');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ATTIAN_HUMAN', 'voidal_resilience');

INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HINTERLANDER_HUMAN', 'companion');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HINTERLANDER_HUMAN', 'adaptive');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HINTERLANDER_HUMAN', 'abnormally_tall');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HINTERLANDER_HUMAN', 'sea_legs');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HINTERLANDER_HUMAN', 'knowledgeable');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HINTERLANDER_HUMAN', 'hintish_heritage');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HINTERLANDER_HUMAN', 'arcane_initiate');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HINTERLANDER_HUMAN', 'packmule');

INSERT INTO race_character_trait (race, character_trait_id) VALUES ('KHADAN_HUMAN', 'arcane_initiate');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('KHADAN_HUMAN', 'strong');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('KHADAN_HUMAN', 'companion');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('KHADAN_HUMAN', 'limb_carver');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('KHADAN_HUMAN', 'nomads_stomach');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('KHADAN_HUMAN', 'follower_of_akhmat');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('KHADAN_HUMAN', 'decapitating_strike');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('KHADAN_HUMAN', 'attuned_to_ignos');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('KHADAN_HUMAN', 'soul_reaper');

INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DWARF', 'nightseer');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DWARF', 'sturdy');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DWARF', 'enduring_as_rock');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DWARF', 'too_angry_to_die');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DWARF', 'companion');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DWARF', 'magical_resilience');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DWARF', 'natural_sprinter');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('DWARF', 'warden');

INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HALFLING', 'naturally_stealthy');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HALFLING', 'companion');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HALFLING', 'bodily_resilience');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HALFLING', 'impossible_to_finish');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HALFLING', 'mulemaster');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HALFLING', 'they_wont_even_notice');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HALFLING', 'kick_em_in_the_shins');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('HALFLING', 'small_stomach');

INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ORC', 'strong');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ORC', 'too_angry_to_die');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ORC', 'companion');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ORC', 'savage_blows');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ORC', 'frenzy');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ORC', 'spidah_ridah');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ORC', 'prophet_of_the_green_flame');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ORC', 'boss_skewer');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('ORC', 'great_hunter');

INSERT INTO race_character_trait (race, character_trait_id) VALUES ('GOBLIN', 'companion');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('GOBLIN', 'naturally_stealthy');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('GOBLIN', 'knowledgeable');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('GOBLIN', 'sneaky_stabba');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('GOBLIN', 'mushroom_addict');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('GOBLIN', 'big_thinka');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('GOBLIN', 'impossible_to_finish');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('GOBLIN', 'acid_spitta');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('GOBLIN', 'spidah_ridah');

INSERT INTO race_character_trait (race, character_trait_id) VALUES ('TIEFLING', 'swift');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('TIEFLING', 'arcane_initiate');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('TIEFLING', 'nightseer');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('TIEFLING', 'companion');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('TIEFLING', 'voidal_origins');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('TIEFLING', 'knowledgeable');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('TIEFLING', 'piercing_gaze');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('TIEFLING', 'bodily_resilience');
INSERT INTO race_character_trait (race, character_trait_id) VALUES ('TIEFLING', 'packmule');
