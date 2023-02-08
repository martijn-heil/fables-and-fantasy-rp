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
import com.fablesfantasyrp.plugin.tools.MovementType
import com.fablesfantasyrp.plugin.tools.Permission
import com.fablesfantasyrp.plugin.tools.PowerToolManager
import com.fablesfantasyrp.plugin.tools.SYSPREFIX
import com.fablesfantasyrp.plugin.tools.command.provider.MinecraftTime
import com.fablesfantasyrp.plugin.tools.command.provider.Weather
import com.fablesfantasyrp.plugin.utils.asEnabledDisabledComponent
import com.fablesfantasyrp.plugin.utils.humanReadable
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
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.WeatherType
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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

class Commands(private val powerToolManager: PowerToolManager) {
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
	fun tphere(@Sender sender: Player, @AllowCharacterName who: Profile) {
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
		fun set(@Sender sender: CommandSender, weather: WeatherType, @CommandTarget(Permission.Command.Ptime + ".others") target: Player) {
			target.setPlayerWeather(weather)
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
		powerToolManager.setPowerTool(sender, command)
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
			@CommandTarget(Permission.Command.Fly + ".others") target: Player) {
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
			  @Optional @Toggle type: MovementType?,
			  @CommandTarget(Permission.Command.Speed+ ".others") target: Player) {
		val finalType = type ?: if (target.isFlying) MovementType.FLY else MovementType.WALK

		if (!sender.hasPermission(Permission.Command.Speed + "." + finalType.name.lowercase())) {
			throw AuthorizationException()
		}

		when(finalType) {
			MovementType.WALK -> target.walkSpeed = value
			MovementType.FLY -> target.flySpeed = value
		}

		sender.sendMessage(miniMessage.deserialize("<gray><prefix> Set <type> speed to <value> for <player></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.unparsed("type", finalType.name.lowercase()),
				Placeholder.unparsed("value", value.toString()),
				Placeholder.component("player", Component.text(target.name).style(target.nameStyle))
		))
	}

	@Command(aliases = ["rigcheer"], desc = "Rig the cheer")
	@Require(Permission.Command.Rigcheer)
	fun rigcheer(@Sender sender: CommandSender, target: Player) {}
}
