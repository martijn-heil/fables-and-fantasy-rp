package com.fablesfantasyrp.plugin.magic.animations

import com.fablesfantasyrp.plugin.magic.FablesMagic
import com.fablesfantasyrp.plugin.magic.objects.MagicAnimation
import com.fablesfantasyrp.plugin.math.*
import kotlinx.coroutines.delay
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.io.File

class NecromancyBlightAnimation : MagicAnimation {

	override suspend fun execute(player: Player, location: Location) {
		drawLineUsingFunction(
				Color.SILVER,
				location + Vector(0.0, 0.5, 0.0),
				location + Vector(0, 3, 0),
				0.25,
				helix(8.0)
		)
		delay(500L)
		drawBooleanArrayAndDirect(
				booleanArrayFromImage(
						File(FablesMagic.instance.dataFolder.path + File.separator + "shapes",
								"necromancy_blight_0.png")
				),
				Color.SILVER,
				location + Vector(0.0, 0.05, 0.0),
				location + Vector(0, 1, 0)
		)
		player.sendMessage("executed necromancyblight")
	}


}
