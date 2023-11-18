package com.fablesfantasyrp.plugin.bell

import com.fablesfantasyrp.plugin.bell.domain.entity.Bell
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.chat.awaitEmote
import com.fablesfantasyrp.plugin.chat.channel.ChatInCharacterShout
import com.fablesfantasyrp.plugin.discord.FablesDiscordBot
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.distanceSafe
import com.github.shynixn.mccoroutine.bukkit.launch
import dev.kord.core.entity.channel.TextChannel
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.time.Duration

fun tryRingBell(bell: Bell, who: Character) {
	PLUGIN.launch {
		val profileManager = Services.get<ProfileManager>()
		val bot = Services.get<FablesDiscordBot>()

		val player = profileManager.getCurrentForProfile(who.profile) ?: return@launch
		player.sendMessage("$SYSPREFIX You are attempting to ring the bell!")

		val emote = player.awaitEmote("$SYSPREFIX Please emote to ring the bell:", ChatInCharacterShout)
			.let { ChatInCharacterShout.getPreview(player, it) }
			.let { PlainTextComponentSerializer.plainText().serialize(it) }

		if (emote.length < 64) {
			player.sendError("Your emote is too short. Please try again")
			return@launch
		}

		if (player.location.distanceSafe(bell.location.toLocation()) > 10) {
			player.sendError("You moved too far away from the bell!")
			return@launch
		}

		if (bell.isRinging) {
			player.sendError("The bell is already ringing")
			return@launch
		}

		bell.ringFor(Duration.ofMinutes(1))

		val pings = bell.discordRoleIds.joinToString(" ") { "<@&${it.value}>" }
		val message = "**[!]** *${who.name} is ringing the ${bell.locationName} bell!*\n\n$emote\n\n$pings"
		val channel = bot.kord.getChannel(bell.discordChannelId) as? TextChannel ?: run {
			player.sendError("The designated discord channel does not exist. " +
				"Cannot not send a ping on Discord. " +
				"Contact your nation leader about this problem.")
			return@launch
		}
		channel.createMessage(message)
	}
}
