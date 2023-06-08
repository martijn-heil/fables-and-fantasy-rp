package com.fablesfantasyrp.plugin.party.data.entity

import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.glowing.GlowingManager
import com.fablesfantasyrp.plugin.party.PartyColor
import com.fablesfantasyrp.plugin.party.PartySpectatorManager
import com.fablesfantasyrp.plugin.party.SYSPREFIX
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.utils.humanReadable
import com.fablesfantasyrp.plugin.utils.quoteCommandArgument
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.ChatColor
import org.bukkit.Location
import org.koin.core.context.GlobalContext

class Party : DataEntity<Int, Party> {
	private var isInitialized = false
	override var dirtyMarker: DirtyMarker<Party>? = null
	var isDestroyed = false
		set(value) {
			if (field != value && value) {
				field = value;
				val glowingManager = GlobalContext.get().get<GlowingManager>()
				val profileManager = GlobalContext.get().get<ProfileManager>()
				members
					.mapNotNull { profileManager.getCurrentForProfile(it.profile) }
					.forEach { glowingManager.setGlowColor(it, null) }
				this.broadcast("$SYSPREFIX ${this.name} has been disbanded.")
			}
		}

	override val id: Int
	var name: String
		set(value) {
			if (field != value) {
				field = value;
				dirtyMarker?.markDirty(this)
				if (isInitialized) this.broadcast("$SYSPREFIX The party's name was changed to $value.")
			}
		}

	var respawnLocation: Location? = null
		set(value) {
			if (field != value) {
				field = value;
				dirtyMarker?.markDirty(this)
				if (value != null) {
					this.broadcast("$SYSPREFIX The party's respawn location was set to ${value.humanReadable()}.")
				} else {
					this.broadcast("$SYSPREFIX The party's respawn location was reset.")
				}
			}
		}

	var owner: Character
		set(value) {
			if (field != value) {
				field = value;
				if (isInitialized) {
					if (!members.contains(value)) members = members.plus(value)
					members
						.mapNotNull { GlobalContext.get().get<ProfileManager>().getCurrentForProfile(it.profile) }
						.forEach { it.sendMessage("$SYSPREFIX The party's owner was changed to ${value.name}.") }
				}
				dirtyMarker?.markDirty(this)
			}
		}

	var members: Set<Character> = emptySet()
		set(value) {
			if (field != value) {
				if (isInitialized) {
					check(value.contains(owner))
					val glowingManager = GlobalContext.get().get<GlowingManager>()
					val added = value.minus(field)
					val removed = field.minus(value)
					removed.forEach { this.broadcast("$SYSPREFIX ${it.name} left the party.") }
					added.forEach { this.broadcast("$SYSPREFIX ${it.name} joined the party.") }
					val profileManager = GlobalContext.get().get<ProfileManager>()
					added.mapNotNull { profileManager.getCurrentForProfile(it.profile) }
						.forEach {
							if (this.color != null) glowingManager.setGlowColor(it, this.color?.chatColor)
							it.sendMessage("$SYSPREFIX Welcome in ${this.name}")
						}
					removed.mapNotNull { profileManager.getCurrentForProfile(it.profile) }
						.forEach { glowingManager.setGlowColor(it, null) }
				}
				field = value;
				if (isInitialized) invites = invites.minus(value)
				dirtyMarker?.markDirty(this, "members")
			}
		}

	var invites: Set<Character> = emptySet()
		set(value) {
			if (field != value) {
				if (isInitialized) {
					val added = value.minus(field)
					val removed = field.minus(value)
					val profileManager = GlobalContext.get().get<ProfileManager>()
					added.forEach {
						this.broadcast("$SYSPREFIX ${it.name} was invited to the party.")
						profileManager.getCurrentForProfile(it.profile)?.sendMessage(miniMessage.deserialize(
							"<prefix> You are invited to join <party> " +
								"<clickhere> to join or run <command>",
							Placeholder.component("prefix", legacyText(SYSPREFIX)),
							Placeholder.unparsed("party", this.name),
							Placeholder.component("clickhere", Component.text("[ CLICK HERE ]")
								.color(NamedTextColor.GREEN)
								.decorate(TextDecoration.BOLD)
								.clickEvent(ClickEvent.runCommand("/party join #${this.id}"))),
							Placeholder.component("command", Component.text("/party join ${quoteCommandArgument(this.name)}")
								.color(NamedTextColor.YELLOW)
								.clickEvent(ClickEvent.suggestCommand("/party join ${quoteCommandArgument(this.name)}")))
						).color(NamedTextColor.GRAY))
					}
					removed.filter { !members.contains(it) }.forEach {
						this.broadcast("$SYSPREFIX ${it.name}'s invite was retracted.")
						profileManager.getCurrentForProfile(it.profile)
							?.sendMessage("$SYSPREFIX Your invite to ${this.name} was retracted.")
					}
				}
				field = value;
				dirtyMarker?.markDirty(this)
			}
		}

	var color: PartyColor? = null
		set(value) {
			if (field != value) {
				field = value
				updateGlow()
				if (value != null) {
					this.broadcast("$SYSPREFIX The party's color was changed to ${value.chatColor}${value.name}${ChatColor.GRAY}.")
				} else {
					this.broadcast("$SYSPREFIX The party's color was unset.")
				}
				dirtyMarker?.markDirty(this)
			}
		}

	var respawns: Int = 0
		set(value) {
			if (field != value) {
				field = value
				this.broadcast("$SYSPREFIX The party's number of respawns was set to $value")
			}
		}

	var useRespawns: Boolean = false
		set(value) {
			if (field != value) {
				if (value) {
					this.broadcast("$SYSPREFIX The party is now using respawns")
				} else {
					this.broadcast("$SYSPREFIX The party is no longer using respawns")
				}
				field = value
			}
		}

	fun updateGlow() {
		if (this.color != null) {
			val glowingManager = GlobalContext.get().get<GlowingManager>()
			val profileManager = GlobalContext.get().get<ProfileManager>()
			this.members.asSequence()
				.mapNotNull { profileManager.getCurrentForProfile(it.profile) }
				.forEach { glowingManager.setGlowColor(it, this.color?.chatColor) }
		}
	}

	fun broadcast(component: Component) {
		val profileManager = GlobalContext.get().get<ProfileManager>()
		val spectatorManager = GlobalContext.get().get<PartySpectatorManager>()
		val players = members
			.asSequence()
			.mapNotNull { profileManager.getCurrentForProfile(it.profile) }
			.plus(spectatorManager.getSpectators(this))
		players.forEach { it.sendMessage(component) }
	}
	fun broadcast(message: String) = broadcast(legacyText(message))

	constructor(id: Int = -1,
				owner: Character,
				color: PartyColor?,
				members: Set<Character> = setOf(owner),
				name: String = "${owner.name}'s party",
				dirtyMarker: DirtyMarker<Party>? = null) {
		this.id = id
		this.owner = owner
		this.color = color
		this.members = members
		this.name = name

		this.dirtyMarker = dirtyMarker
		this.isInitialized = true
	}
}
