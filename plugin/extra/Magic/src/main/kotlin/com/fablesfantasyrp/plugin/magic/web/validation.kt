package com.fablesfantasyrp.plugin.magic.web

import com.fablesfantasyrp.plugin.magic.dal.repository.SpellDataRepository
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.magic.web.model.WebMage
import com.fablesfantasyrp.plugin.web.loaders.BaseRequestValidationLoader
import io.ktor.server.plugins.requestvalidation.*
import org.bukkit.plugin.Plugin

class WebRequestValidation(private val plugin: Plugin,
						   private val mages: MageRepository,
						   private val spells: SpellDataRepository) : BaseRequestValidationLoader() {
	override val validation: RequestValidationConfig.() -> Unit = {
		validate<WebMage> {
			if (it.spells.any { spellName -> !spells.exists(spellName) }) {
				return@validate ValidationResult.Invalid("Invalid spell")
			}

			ValidationResult.Valid
		}
	}
}
