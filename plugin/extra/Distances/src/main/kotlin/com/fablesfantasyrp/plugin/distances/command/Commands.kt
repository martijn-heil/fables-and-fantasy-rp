package com.fablesfantasyrp.plugin.distances.command

import com.fablesfantasyrp.plugin.distances.Permission
import com.fablesfantasyrp.plugin.distances.SYSPREFIX
import com.fablesfantasyrp.plugin.text.join
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isVanished
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Switch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.GameMode
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import kotlin.math.roundToInt

class Commands(private val plugin: Plugin) {
	private val server get() = plugin.server

	@Command(aliases = ["near", "fnear"], desc = "List nearby players")
	@Require(Permission.Command.Near)
	fun near(@Sender sender: Player, @CommandTarget(Permission.Command.Near + ".others") from: Player) {
		val fromName = if (sender == from) "you" else from.name

		val players = Component.text().append(server.onlinePlayers.asSequence()
				.filter { it != from }
				.filter { it.world == from.world }
				.filter { !it.isSneaking && !it.isVanished && it.gameMode != GameMode.SPECTATOR}
				.map { Pair(it, it.location.distance(from.location)) }
				.filter { it.second <= 32 }
				.sortedBy { it.second }
				.map { miniMessage.deserialize("<player_name>(<red><range></red>)",
						Placeholder.unparsed("player_name", it.first.name),
						Placeholder.unparsed("range", "${it.second.roundToInt()}m")) }
				.join(Component.text(", ")).toList()).color(NamedTextColor.GRAY)
		sender.sendMessage(miniMessage.deserialize("<prefix><gray>Players within 32 m of $fromName: <players></gray>",
			Placeholder.component("players", players),
			Placeholder.component("prefix", legacyText(SYSPREFIX))))
	}

	@Command(aliases = ["distance", "dist"], desc = "Show distance to cursor location")
	@Require(Permission.Command.Distance)
	fun distance(@Sender sender: Player, @CommandTarget(Permission.Command.Distance + ".others") from: Player,
				 @Switch('b') blocksOnly: Boolean, @Switch('e') entityOnly: Boolean) {
		if (blocksOnly && entityOnly) {
			sender.sendError("Command flags -b and -e are mutually exclusive.")
			return
		}

		val fromName = if (sender == from) "you" else from.name

		try {
			if (blocksOnly) {
				val blockTarget = getBlockTargetDistance(sender, from) ?: throw IllegalArgumentException("No block found within 100 m")
				sender.sendMessage("${SYSPREFIX}Targeted block is ${blockTarget.second} m away from $fromName")
			} else if (entityOnly) {
				val entityTarget = getEntityTargetDistance(sender, from) ?: throw IllegalArgumentException("No entity found within 100 m")
				sender.sendMessage("${SYSPREFIX}${entityTarget.first.name} is ${entityTarget.second} m away from $fromName")
			} else {
				val entityTarget = getEntityTargetDistance(sender, from)
				if (entityTarget != null) {
					sender.sendMessage("${SYSPREFIX}${entityTarget.first.name} is ${entityTarget.second} m away from $fromName")
				} else {
					val blockTarget = getBlockTargetDistance(sender, from) ?: throw IllegalArgumentException("No block found within 100 m")
					sender.sendMessage("${SYSPREFIX}Targeted block is ${blockTarget.second} m away from $fromName")
				}
			}
		} catch (ex: IllegalArgumentException) {
			sender.sendError(ex.message ?: "Unknown error")
		}
	}

	private fun getEntityTargetDistance(sender: Player, from: Player): Pair<Entity, Int>? {
		if (sender.world != from.world) throw IllegalArgumentException("You are not in the same world as the player specified")

		val entityTarget = sender.getTargetEntity(100, true) ?: return null

		return Pair(entityTarget, entityTarget.location.distance(from.location).roundToInt())
	}

	private fun getBlockTargetDistance(sender: Player, from: Player): Pair<Block, Int>? {
		if (sender.world != from.world) throw IllegalArgumentException("You are not in the same world as the player specified")

		val blockTarget = sender.getTargetBlock(110) ?: return null

		val distance = blockTarget.location.toCenterLocation().add(0.0, 0.5, 0.0).distance(from.location).roundToInt()
		if (distance > 100) return null

		return Pair(blockTarget, distance)
	}
}
