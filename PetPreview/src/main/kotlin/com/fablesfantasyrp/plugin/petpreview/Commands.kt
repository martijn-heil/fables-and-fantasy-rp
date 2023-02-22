package com.fablesfantasyrp.plugin.petpreview
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
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