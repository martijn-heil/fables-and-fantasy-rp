package com.fablesfantasyrp.plugin.magic.web

import com.fablesfantasyrp.plugin.magic.dal.repository.SpellDataRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.magic.web.model.WebMage
import com.fablesfantasyrp.plugin.magic.web.model.transform
import com.fablesfantasyrp.plugin.web.loaders.BaseWebRoutingLoader
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin

@Resource("/mages")
private class Mages() {
	@Resource("{id}")
    class Id(val parent: Mages = Mages(), val id: Int)
}

internal class WebRouting(private val plugin: Plugin,
				 private val mages: MageRepository,
				 private val spells: SpellDataRepository) : BaseWebRoutingLoader() {
	private val logger = plugin.logger

	override val routes: Route.() -> Unit = {
		get<Mages> {
			val results = mages.all().map { it.transform() }
			call.respond(results)
		}

		get<Mages.Id> {
			val result = mages.forId(it.id.toLong())?.transform() ?: run {
				call.respond(HttpStatusCode.NotFound)
				return@get
			}
			call.respond(result)
		}

		post<Mages.Id> {
			val webMage = call.receive<WebMage>()
			withContext(plugin.minecraftDispatcher) {

				mages.create(Mage(
					id = webMage.id,
					magicPath = webMage.magicPath,
					magicLevel = webMage.magicLevel,
					spells = spells.forIds(webMage.spells.asSequence()).toList()
				))

				call.respond(HttpStatusCode.OK)
			}
		}

		put<Mages.Id> {
			val webMage = call.receive<WebMage>()
			withContext(plugin.minecraftDispatcher) {
				val existingMage = mages.forId(it.id.toLong()) ?: run {
					call.respond(HttpStatusCode.NotFound)
					return@withContext
				}

				existingMage.magicLevel = webMage.magicLevel
				existingMage.magicPath = webMage.magicPath
				existingMage.spells = webMage.spells.mapNotNull { spells.forId(it) }

				call.respond(HttpStatusCode.OK)
			}
		}
	}
}
