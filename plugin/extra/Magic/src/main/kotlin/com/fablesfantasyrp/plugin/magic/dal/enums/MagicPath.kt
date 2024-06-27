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
package com.fablesfantasyrp.plugin.magic.dal.enums

enum class MagicPath {
	PYROMANCY,
	SOLARMANCY,
	MAGMAMANCY,
	AEROMANCY,
	TEMPESTACY,
	LUNARMANCY,
	AQUAMANCY,
	FRIGUMANCY,
	MARINAMANCY,
	GEOMANCY,
	PETROMANCY,
	BIOMANCY,
	NECROMANCY,
	NECROMANCY_SOUL_DESTINED,
	NECROMANCY_DEATH_DESTINED,
	HEMOMANCY,
	HEMOMANCY_HOUSE_OF_NIGHT,
	HEMOMANCY_HOUSE_OF_BEAST,
	HEMOMANCY_HOUSE_OF_BLOOD;

	val magicType: MagicType
		get() = when (this) {
			PYROMANCY, MAGMAMANCY, SOLARMANCY -> MagicType.PYROMANCY
			AEROMANCY, TEMPESTACY, LUNARMANCY -> MagicType.AEROMANCY
			AQUAMANCY, FRIGUMANCY, MARINAMANCY -> MagicType.AQUAMANCY
			GEOMANCY, PETROMANCY, BIOMANCY -> MagicType.GEOMANCY
			NECROMANCY, NECROMANCY_SOUL_DESTINED, NECROMANCY_DEATH_DESTINED -> MagicType.NECROMANCY
			HEMOMANCY, HEMOMANCY_HOUSE_OF_NIGHT, HEMOMANCY_HOUSE_OF_BEAST, HEMOMANCY_HOUSE_OF_BLOOD -> MagicType.HEMOMANCY
		}

	val basePath: MagicPath
		get() = when (this) {
			PYROMANCY, MAGMAMANCY, SOLARMANCY -> PYROMANCY
			AEROMANCY, TEMPESTACY, LUNARMANCY -> AEROMANCY
			AQUAMANCY, FRIGUMANCY, MARINAMANCY -> AQUAMANCY
			GEOMANCY, PETROMANCY, BIOMANCY -> GEOMANCY
			NECROMANCY, NECROMANCY_SOUL_DESTINED, NECROMANCY_DEATH_DESTINED -> NECROMANCY
			HEMOMANCY, HEMOMANCY_HOUSE_OF_NIGHT, HEMOMANCY_HOUSE_OF_BEAST, HEMOMANCY_HOUSE_OF_BLOOD -> HEMOMANCY
		}
}
