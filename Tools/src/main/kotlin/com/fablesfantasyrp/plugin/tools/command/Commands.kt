package com.fablesfantasyrp.plugin.tools.command

import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.inventory.MirroredInventory
import com.fablesfantasyrp.plugin.inventory.MirroredInventoryManager
import com.fablesfantasyrp.plugin.inventory.inventory
import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.profile.command.annotation.AllowPlayerName
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.nameStyle
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.tools.*
import com.fablesfantasyrp.plugin.tools.command.provider.MinecraftTime
import com.fablesfantasyrp.plugin.utils.SPAWN
import com.fablesfantasyrp.plugin.utils.asEnabledDisabledComponent
import com.fablesfantasyrp.plugin.utils.humanReadable
import com.fablesfantasyrp.plugin.utils.isVanished
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.gitlab.martijn_heil.nincommands.common.Toggle
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Optional
import com.sk89q.intake.parametric.annotation.Range
import com.sk89q.intake.parametric.annotation.Switch
import com.sk89q.intake.parametric.annotation.Text
import com.sk89q.intake.util.auth.AuthorizationException
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.WeatherType
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.ocpsoft.prettytime.PrettyTime
import java.time.Instant

class InventoryCommands(private val mirroredInventoryManager: MirroredInventoryManager) {
	@Command(aliases = ["invsee", "finvsee"], desc = "Invsee a character")
	@Require(Permission.Command.Invsee)
	fun invsee(@Sender sender: Player, @AllowCharacterName target: Profile) {
		val inventory = MirroredInventory(
				target.inventory.inventory,
				sender,
				Component.text("${target.id}'s inventory"))
		mirroredInventoryManager.register(inventory)

		sender.openInventory(inventory.bukkitInventory)
	}

	@Command(aliases = ["endersee", "enderchest", "echest", "fendersee", "fechest", "fenderchest"], desc = "Endersee a character")
	@Require(Permission.Command.Endersee)
	fun endersee(@Sender sender: Player, @AllowCharacterName target: Profile) {
		val inventory = MirroredInventory(
				target.inventory.enderChest,
				sender,
				Component.text("${target.id}'s enderchest"))
		mirroredInventoryManager.register(inventory)

		sender.openInventory(inventory.bukkitInventory)
	}
}

class Commands(private val powerToolManager: PowerToolManager,
			   private val backManager: BackManager) {
	@Command(aliases = ["teleport", "fteleport", "tp", "ftp", "tele", "ftele"], desc = "Teleport characters")
	@Require(Permission.Command.Teleport)
	fun teleport(@Sender sender: CommandSender,
				 @AllowCharacterName @AllowPlayerName one: Profile,
				 @Optional @AllowCharacterName @AllowPlayerName two: Profile?) {
		if (two != null) {
			one.location = two.location
		} else if (sender is Player) {
			sender.teleport(one.location)
		} else {
			sender.sendError("You have to be a player to use this command.")
		}
	}

	@Command(aliases = ["tppos", "ftppos"], desc = "Teleport to a position")
	@Require(Permission.Command.Tppos)
	fun tppos(@Sender sender: CommandSender,
			  to: Location,
			  @CommandTarget(Permission.Command.Tppos + ".others") @AllowCharacterName @AllowPlayerName target: Profile) {
		target.location = to
		sender.sendMessage("$SYSPREFIX Teleported #${target.id} to ${to.humanReadable()}")
	}

	@Command(aliases = ["tphere", "ftphere"], desc = "Teleport characters to you")
	@Require(Permission.Command.Tphere)
	fun tphere(@Sender sender: Player, @AllowCharacterName @AllowPlayerName who: Profile) {
		who.location = sender.location
	}

	inner class Ptime {
		@Command(aliases = ["set"], desc = "")
		@Require(Permission.Command.Ptime)
		fun set(@Sender sender: CommandSender, time: MinecraftTime, @CommandTarget(Permission.Command.Ptime + ".others") target: Player) {
			target.setPlayerTime(time.ticks, false)
			sender.sendMessage("$SYSPREFIX Set ${target.name}'s playertime to ${time.ticks}")
		}

		@Command(aliases = ["reset"], desc = "")
		@Require(Permission.Command.Ptime)
		fun reset(@Sender sender: CommandSender, @CommandTarget(Permission.Command.Ptime + ".others") target: Player) {
			target.resetPlayerTime()
			sender.sendMessage("$SYSPREFIX Reset ${target.name}'s playertime")
		}
	}

	inner class PWeather {
		@Command(aliases = ["set"], desc = "")
		@Require(Permission.Command.PWeather)
		fun set(@Sender sender: CommandSender, weather: PlayerWeather, @CommandTarget(Permission.Command.Ptime + ".others") target: Player) {
			val bukkitWeather = when (weather) {
				PlayerWeather.CLEAR -> WeatherType.CLEAR
				PlayerWeather.RAIN -> WeatherType.DOWNFALL
			}
			target.setPlayerWeather(bukkitWeather)
			sender.sendMessage("$SYSPREFIX Set ${target.name}'s playerweather to $weather")
		}

		@Command(aliases = ["reset"], desc = "")
		@Require(Permission.Command.PWeather)
		fun reset(@Sender sender: CommandSender, @CommandTarget(Permission.Command.Ptime + ".others") target: Player) {
			target.resetPlayerWeather()
			sender.sendMessage("$SYSPREFIX Reset ${target.name}'s playerweather")
		}
	}

	@Command(aliases = ["weather", "fweather"], desc = "")
	@Require(Permission.Command.PWeather)
	fun weather(@Sender sender: Player, weather: Weather, @Switch('s') storm: Boolean) {
		val world = sender.world
		world.setStorm(weather != Weather.CLEAR)
		world.isThundering = weather == Weather.THUNDER
		sender.sendMessage("$SYSPREFIX Set ${world.name}'s weather to $weather")
	}

	@Command(aliases = ["gamemode", "gm", "fgamemode"], desc = "")
	@Require(Permission.Command.GameMode)
	fun gamemode(@Sender sender: CommandSender, gameMode: GameMode, @CommandTarget(Permission.Command.GameMode + ".others") target: Player) {
		if (!sender.hasPermission("${Permission.Command.GameMode}.${gameMode.name.lowercase()}")) {
			throw AuthorizationException()
		}

		target.gameMode = gameMode
	}

	@Command(aliases = ["survival", "gms", "survivalmode", "fsurvival"], desc = "")
	@Require(Permission.Command.GameMode + ".survival")
	fun survival(@Sender sender: CommandSender, @CommandTarget(Permission.Command.GameMode + ".others") target: Player) {
		target.gameMode = GameMode.SURVIVAL
	}

	@Command(aliases = ["creative", "gmc", "creativemode", "fcreative"], desc = "")
	@Require(Permission.Command.GameMode + ".creative")
	fun creative(@Sender sender: CommandSender, @CommandTarget(Permission.Command.GameMode + ".others") target: Player) {
		target.gameMode = GameMode.CREATIVE
	}

	@Command(aliases = ["spectator", "gmsp", "spectatormode", "fspectator"], desc = "")
	@Require(Permission.Command.GameMode + ".spectator")
	fun spectator(@Sender sender: CommandSender, @CommandTarget(Permission.Command.GameMode + ".others") target: Player) {
		target.gameMode = GameMode.SPECTATOR
	}

	@Command(aliases = ["adventure", "gma", "adventuremode", "fadventure"], desc = "")
	@Require(Permission.Command.GameMode + ".adventure")
	fun adventure(@Sender sender: CommandSender, @CommandTarget(Permission.Command.GameMode + ".others") target: Player) {
		target.gameMode = GameMode.ADVENTURE
	}

	@Command(aliases = ["powertool", "pt", "fpt", "fpowertool"], desc = "")
	@Require(Permission.Command.PowerTool)
	fun powertool(@Sender sender: Player, @Optional @Text command: String?) {
		val material = sender.inventory.itemInMainHand.type
		powerToolManager.setPowerTool(sender, material, command)
		if (command != null) {
			sender.sendMessage("$SYSPREFIX Set your powertool to: '$command'")
		} else {
			sender.sendMessage("$SYSPREFIX Cleared your powertool")
		}
	}

	@Command(aliases = ["fly", "ffly"], desc = "Take off, and soar!")
	@Require(Permission.Command.Fly)
	fun fly(@Sender sender: CommandSender,
			@Optional @Toggle value: Boolean?,
			@Optional @CommandTarget(Permission.Command.Fly + ".others") target: Player) {
		val finalValue = value ?: !target.allowFlight
		target.allowFlight = finalValue
		sender.sendMessage(miniMessage.deserialize("<gray><prefix> Set fly mode <value> for <player></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.component("value", finalValue.asEnabledDisabledComponent()),
				Placeholder.component("player", Component.text(target.name).style(target.nameStyle))
		))
	}

	@Command(aliases = ["speed", "fspeed"], desc = "Speedy boi")
	@Require(Permission.Command.Speed)
	fun speed(@Sender sender: CommandSender,
			  @Range(min = 0.0, max = 10.0) value: Float,
			  @Optional type: MovementType?,
			  @Optional @CommandTarget(Permission.Command.Speed+ ".others") target: Player) {
		val finalType = type ?: if (target.isFlying) MovementType.FLY else MovementType.WALK

		if (!sender.hasPermission(Permission.Command.Speed + "." + finalType.name.lowercase())) {
			throw AuthorizationException()
		}

		val speed = getRealMoveSpeed(value, finalType)

		when(finalType) {
			MovementType.WALK -> target.walkSpeed = speed
			MovementType.FLY -> target.flySpeed = speed
		}

		sender.sendMessage(miniMessage.deserialize("<gray><prefix> Set <type> speed to <value> for <player></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.unparsed("type", finalType.name.lowercase()),
				Placeholder.unparsed("value", speed.toString()),
				Placeholder.component("player", Component.text(target.name).style(target.nameStyle))
		))
	}

	@Command(aliases = ["god", "godmode", "fgod", "fgodmode"], desc = "Invulnerability!!")
	@Require(Permission.Command.God)
	fun god(@Sender sender: CommandSender,
			@Optional @Toggle value: Boolean?,
			@Optional @CommandTarget(Permission.Command.God + ".others") target: Player) {
		val finalValue = value ?: !target.isInvulnerable
		target.isInvulnerable = finalValue
		sender.sendMessage(miniMessage.deserialize("<gray><prefix> Set god mode to <value> for <player></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.component("value", finalValue.asEnabledDisabledComponent()),
				Placeholder.component("player", Component.text(target.name).style(target.nameStyle))
		))
	}

	@Command(aliases = ["seen", "fseen"], desc = "Look up when a player was last seen")
	@Require(Permission.Command.Seen)
	fun seen(@Sender sender: CommandSender, target: OfflinePlayer) {
		sender.sendMessage(miniMessage.deserialize("<gray><prefix> <player> was last seen <when></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.unparsed("player", target.name ?: target.uniqueId.toString()),
				Placeholder.unparsed("when", PrettyTime().format(Instant.ofEpochMilli(target.lastSeen)))
		))
	}

	@Command(aliases = ["whois", "fwhois"], desc = "Show information about a player")
	@Require(Permission.Command.Whois)
	fun whois(@Sender sender: CommandSender, target: OfflinePlayer) {
		val player: Player? = target.player

		val playerName: Component = if (player != null) {
			Component.text(player.name).style(player.nameStyle)
		} else if (target.name != null) {
			Component.text(target.name!!)
		} else {
			Component.text("(player name not known)")
		}

		val message = miniMessage.deserialize(
				"<newline>" +
						"<gray>══════════ <white><player_name></white> <status>══════════</gray><newline>" +
						"<gold>" +
						"UUID: <white><id></white><newline>" +
						"Last seen: <white><last_seen></white><newline>" +
						"Gamemode: <white><gamemode></white><newline>" +
						"Invulnerable: <white><invulnerable></white><newline>" +
						"Vanish: <white><vanish></white><newline>" +
						"Fly mode: <white><fly_mode></white><newline>" +
						"Flying speed: <white><fly_speed></white><newline>" +
						"Walking speed: <white><walk_speed></white><newline>" +
						"</gold>",
				Placeholder.component("player_name", playerName),
				Placeholder.unparsed("id", target.uniqueId.toString()),
				Placeholder.unparsed("last_seen", if (target.lastSeen != 0L) PrettyTime().format(Instant.ofEpochMilli(target.lastSeen)) else "unknown"),
				Placeholder.unparsed("gamemode", player?.gameMode?.toString() ?: "unknown" ),
				Placeholder.unparsed("invulnerable", player?.isInvulnerable?.toString() ?: "unknown" ),
				Placeholder.component("vanish", player?.isVanished?.asEnabledDisabledComponent() ?: Component.text("unknown")),
				Placeholder.component("fly_mode", miniMessage.deserialize("<allow_flight> <flight_status>",
						Placeholder.component("allow_flight", player?.allowFlight?.asEnabledDisabledComponent() ?: Component.text("unknown")),
						Placeholder.unparsed("flight_status",
								if (player != null && player.isFlying) "(flying)"
								else if (player != null && !player.isFlying) "(not flying)"
								else "")
				)),
				Placeholder.unparsed("fly_speed", player?.flySpeed?.toString() ?: "unknown" ),
				Placeholder.unparsed("walk_speed", player?.walkSpeed?.toString() ?: "unknown" ),
				Placeholder.component("status", if (target.isBanned) Component.text("(banned)").color(NamedTextColor.RED) else Component.empty())
		)

		sender.sendMessage(message)
	}

	@Command(aliases = ["back", "fback"], desc = "Teleport back to your previous location")
	@Require(Permission.Command.Back)
	fun back(@Sender sender: CommandSender, @CommandTarget(Permission.Command.Back + ".others") target: Player) {
		backManager.getPreviousLocation(target)?.let { target.teleport(it) }
		sender.sendMessage(miniMessage.deserialize("<gray><prefix> Teleported <player> to their previous location.</gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.component("player", Component.text(target.name).style(target.nameStyle))
		))
	}

	@Command(aliases = ["spawn", "fspawn"], desc = "Teleport to spawn")
	@Require(Permission.Command.Spawn)
	fun spawn(@Sender sender: CommandSender, @CommandTarget(Permission.Command.Spawn + ".others") target: Player) {
		target.teleport(SPAWN)
		sender.sendMessage(miniMessage.deserialize("<gray><prefix> Teleported <player> to spawn.</gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.component("player", Component.text(target.name).style(target.nameStyle))
		))
	}

	@Command(aliases = ["rigcheer"], desc = "Rig the cheer")
	@Require(Permission.Command.Rigcheer)
	fun rigcheer(@Sender sender: CommandSender, target: Player) {}
}
