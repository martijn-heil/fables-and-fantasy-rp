package com.fablesfantasyrp.plugin.form

import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.FixedSuggestionsModule
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.sk89q.intake.Command
import com.sk89q.intake.Intake
import com.sk89q.intake.Require
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
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
		val injector = Intake.createInjector()
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
				.graph()
				.dispatcher

		dispatcher.commands.forEach { registerCommand(it.callable, plugin, it.allAliases.toList()) }

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
