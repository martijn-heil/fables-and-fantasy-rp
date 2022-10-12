package com.fablesfantasyrp.plugin.magic.exception

import com.fablesfantasyrp.plugin.magic.MAX_TEARS_PER_MAGE

class TooManyTearsException : OpenTearException("You may not have more than $MAX_TEARS_PER_MAGE tears open at once.")
