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
package com.fablesfantasyrp.plugin.petpreview
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import fr.nocsy.mcpets.data.Pet
import fr.nocsy.mcpets.data.PetDespawnReason
import org.bukkit.ChatColor
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.util.UUID
import org.bukkit.ChatColor.*

val SYSPREFIX = "${DARK_RED}${BOLD}[${RED}${BOLD} Pet Demo ${DARK_RED}${BOLD}]${GRAY}"

class Commands {
	val cooldowns: HashMap<Pair<UUID, String>, Long> = HashMap()
	var cooldown = 20
	@Command(aliases = ["petpreview", "pp"], desc = "Summon a temporary pet for demos")
	@Require("fables.petpreview.command.petpreview")
	fun pet_preview(@Sender sender: ConsoleCommandSender, pet: String, target: Player) {
    	val petObject = Pet.getFromId(pet)
		if (cooldowns.containsKey(Pair(target.uniqueId,pet))) {
			if (cooldowns[Pair(target.uniqueId,pet)]!! > System.currentTimeMillis()) {
				val time_left = (cooldowns[Pair(target.uniqueId,pet)]!! - System.currentTimeMillis()) / 1000
				target.sendMessage("$SYSPREFIX ${ChatColor.GREEN}Please wait ${ChatColor.RED}${time_left}${ChatColor.RED} seconds before trying again.")
				return
			} else {
				spawnPet(petObject, target)
				cooldowns[Pair(target.uniqueId,pet)] = System.currentTimeMillis() + cooldown * 1000
				return
			}
		} else {
			spawnPet(petObject, target)
			cooldowns[Pair(target.uniqueId,pet)] = System.currentTimeMillis() + cooldown * 1000
		}
	}
}

fun spawnPet(petObject: Pet, target: Player) {
	target.sendMessage("$SYSPREFIX ${ChatColor.GREEN}Spawned your demo pet.")
	petObject.spawn(target, target.location)
	FablesPetPreview.instance.server.scheduler.scheduleSyncDelayedTask(FablesPetPreview.instance, {
		petObject.despawn(PetDespawnReason.REVOKE)
		target.sendMessage("$SYSPREFIX ${RED}Demo pet has been despawned.")
	}, 150) // 150 ticks later
}
