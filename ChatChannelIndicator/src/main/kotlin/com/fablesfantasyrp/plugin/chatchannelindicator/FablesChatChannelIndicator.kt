package com.fablesfantasyrp.plugin.chatchannelindicator

import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor.DARK_GRAY
import org.bukkit.ChatColor.GREEN
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

lateinit var instance: FablesChatChannelIndicator

class FablesChatChannelIndicator : JavaPlugin() {

	override fun onEnable() {
		instance = this

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val dispatcher = CommandGraph()
				.builder(builder)
				.commands()
				.registerMethods(Commands())
				.graph()
				.dispatcher

		registerCommand(dispatcher, this, dispatcher.aliases.toList())

		server.scheduler.scheduleSyncRepeatingTask(this, {
			server.onlinePlayers.forEach {
				val channel = it.dFlags.getFlagValue("chat").asElement().asString().uppercase()
				val width = it.chatChannelIndicatorWidth
				val text = TextComponent("${GREEN}${channel}${DARK_GRAY}"
										.padEnd(maxOf(0, width.toInt() - channel.length), '\u00A8'))
				it.spigot().sendMessage(ChatMessageType.ACTION_BAR, text)
			}
		}, 0, 20)
	}
}

private val defaultWidth: UInt = 20U
private val widthMap = HashMap<Player, UInt>()

var Player.chatChannelIndicatorWidth: UInt
	get() {
		return widthMap[this] ?: defaultWidth
	}
	set(value) {
		widthMap[this] = value
	}
