package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.magic.command.Commands
import com.fablesfantasyrp.plugin.magic.command.provider.MagicModule
import com.fablesfantasyrp.plugin.magic.data.MapTearRepository
import com.fablesfantasyrp.plugin.magic.data.SimpleSpellDataRepository
import com.fablesfantasyrp.plugin.magic.data.entity.EntityMageRepository
import com.fablesfantasyrp.plugin.magic.data.entity.EntityTearRepository
import com.fablesfantasyrp.plugin.magic.data.persistent.H2MageRepository
import com.fablesfantasyrp.plugin.magic.data.persistent.YamlSimpleSpellDataRepository
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.gitlab.martijn_heil.nincommands.common.bukkit.unregisterCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import org.bukkit.ChatColor.*
import org.bukkit.command.Command

val SYSPREFIX = "${DARK_PURPLE}${BOLD}[ ${AQUA}${BOLD}MAGIC${DARK_PURPLE}${BOLD} ]${GRAY}"
val MAX_TEARS_PER_MAGE = 3

val PLUGIN: FablesMagic get() = FablesMagic.instance

lateinit var tearRepository: EntityTearRepository<*>
lateinit var spellRepository: SimpleSpellDataRepository
lateinit var mageRepository: EntityMageRepository<*>


class FablesMagic : SuspendingJavaPlugin() {
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_MAGIC", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		val spellsDirectory = this.dataFolder.resolve("spells")
		spellsDirectory.mkdirs()

		spellRepository = YamlSimpleSpellDataRepository(this, spellsDirectory)
		mageRepository = EntityMageRepository(this, H2MageRepository(server, fablesDatabase))
		mageRepository.init()
		tearRepository = EntityTearRepository(MapTearRepository())
		tearRepository.all().forEach { it.spawn() }

		TearClosureManager(this, tearRepository)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(CharacterModule(server))
		injector.install(MagicModule(server))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("ability").registerMethods(Commands.Ability())
		rootDispatcherNode.registerMethods(Commands())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }
	}

	override fun onDisable() {
		mageRepository.saveAllDirty()
		tearRepository.saveAllDirty()
		tearRepository.all().forEach { it.despawn() }
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesMagic
	}
}
