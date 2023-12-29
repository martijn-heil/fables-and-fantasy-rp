package com.fablesfantasyrp.plugin.form

import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.caturix.spigot.common.CommonModule
import com.fablesfantasyrp.caturix.spigot.common.FixedSuggestionsModule
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.spigot.common.bukkit.BukkitAuthorizer
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.BukkitModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.registerCommand
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Caturix
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.fluent.CommandGraph
import com.fablesfantasyrp.caturix.parametric.ParametricBuilder
import com.fablesfantasyrp.caturix.parametric.provider.PrimitivesModule
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.CompletableDeferred
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

private val clickables = HashMap<UUID, Clickable>()

class Clickable(val validUntil: Instant, val execute: (clicker: Player) -> Unit) {
	val uuid = UUID.randomUUID()

	fun register(): Clickable {
		clickables[uuid] = this
		return this
	}

	fun applyTo(component: Component): Component {
		val clickEvent = ClickEvent.runCommand("/fablesclickable $uuid")
		return component.clickEvent(clickEvent)
	}
}

fun Component.clickable(clickable: Clickable) = clickable.applyTo(this)

class ClickableCommands {
	@Command(aliases = ["fablesclickable"], desc = "Execute a clickable")
	@Require("fables.form.command.clickable")
	fun fablesclickable(@Sender sender: Player, id: String) {
		try {
			(clickables[UUID.fromString(id)] ?: run {
				sender.sendError("Invalid clickable")
				return
			}).execute(sender)
		} catch (ex: IllegalArgumentException) {
			sender.sendError("Invalid clickable (not a UUID)")
		}
	}
}

internal class ClickableManager(private val plugin: Plugin) {
	private val server = plugin.server

	fun start() {
		val injector = Caturix.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(FixedSuggestionsModule(injector))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val dispatcher = CommandGraph()
				.builder(builder)
				.commands()
				.registerMethods(ClickableCommands())
				.graph
				.dispatcher

		dispatcher.commands.mapNotNull { command ->
			registerCommand(command.callable, plugin, command.allAliases.toList()) { plugin.launch(block = it) }
		}

		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			val now = Instant.now()
			clickables.values.removeIf { now.isAfter(it.validUntil) }
		},1, 20)
	}
}

class YesNoChatPrompt(private val player: Player, private val prompt: Component) : CompletableDeferred<Boolean> by CompletableDeferred() {
	private val validUntil = Instant.now().plus(5, ChronoUnit.MINUTES)
	private val yesClickable = Clickable(validUntil) { complete(true) }.register()
	private val noClickable = Clickable(validUntil) { complete(false) }.register()

	private val noButton = Component.text("[No]")
			.color(NamedTextColor.RED)
			.decorate(TextDecoration.BOLD)
			.clickable(noClickable)

	private val yesButton = Component.text("[Yes]")
			.color(NamedTextColor.GREEN)
			.decorate(TextDecoration.BOLD)
			.clickable(yesClickable)

	private val chatMessage = miniMessage.deserialize("<prompt><newline>    <yes> <no>",
		Placeholder.component("prompt", prompt),
		Placeholder.component("yes", yesButton),
		Placeholder.component("no", noButton))

	fun send() {
		player.sendMessage(chatMessage)
	}
}
