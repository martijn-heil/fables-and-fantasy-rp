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
package com.fablesfantasyrp.plugin.morelogging

import com.fablesfantasyrp.plugin.utils.extensions.bukkit.humanReadable
import org.bukkit.OfflinePlayer
import java.util.logging.Level
import java.util.logging.Logger

fun logPlayerStateChange(logger: Logger, level: Level, p: OfflinePlayer, name: String, oldState: String, newState: String) =
		logger.log(level,"[${p.player?.location?.humanReadable()}] ${p.name}: $name $oldState -> $newState")
