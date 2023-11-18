package com.fablesfantasyrp.plugin.math

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.Vector
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Generates a bitmap array from an image file.
 * Pixels whose average color is less than 128 are considered black (false).
 * Pixels whose average color is more than 128 are considered white (true).
 */
fun booleanArrayFromImage(file: File): Array<BooleanArray> {
	val image = ImageIO.read(file)!!
	val array = mutableListOf<BooleanArray>()
	for (x in 0 until image.width) {
		val row = mutableListOf<Boolean>()
		for (y in 0 until image.height) {
			val rgb = image.getRGB(x, y)
			val red = rgb and 0xff0000 shr 16
			val green = rgb and 0xff00 shr 8
			val blue = rgb and 0xff
			val avg = (red + green + blue) / 3
			row.add(y, avg >= 127)
		}
		array.add(x, row.toBooleanArray())
	}
	return array.toTypedArray()
}

/**
 * A set of utilities that can draw particles from a given boolean array.
 * The standard option is to drawBooleanArrayAndRotate() - which will rotate to match a location's direction.
 * Additionally, you may find the direction between two locations first and use that as reference.
 * See drawBooleanArrayAndDirect().
 * Additionally, you can generate a bitmap array from an image file - see booleanArrayFromImage().
 */

/**
 * Draws a 2D bitmap and rotates it in space based on the location's presumed direction.
 */
fun drawBooleanArrayAndRotate(array: Array<BooleanArray>, color: Color = Color.FUCHSIA,
						loc: Location, offset: Double = 0.25) {

	val pitch = loc.pitch * (PI / 180)
	val yaw = loc.yaw * (PI / 180)

	val sinPitch = sin(pitch)
	val cosPitch = cos(pitch)
	val sinYaw = sin(yaw)
	val cosYaw = cos(yaw)

	val dustOptions = Particle.DustOptions(color, 1.0f)
	var i = 0

	val world = loc.world!!

	while (i < array.size) {
		var j = 0
		while (j < array[i].size) {
			if (array[i][j]) {
				val u = (i - array.size / 2) * offset // y offset
				val v = (j - array[j].size / 2) * offset // x-z offset
				val vec = Vector(
						cosYaw * v - sinYaw * sinPitch * u,
						cosPitch * u,
						sinYaw * v + cosYaw * sinPitch * u)
				world.spawnParticle(Particle.REDSTONE, loc + vec, 3, dustOptions)
			}
			j++
		}
		i++
	}

}

/**
 * Draws a 2D bitmap and rotates it in space at point A in direction of B.
 */
fun drawBooleanArrayAndDirect(array: Array<BooleanArray>, color: Color = Color.FUCHSIA,
						A: Location, B: Location, offset: Double = 0.25) {
	val loc = B - A
	drawBooleanArrayAndRotate(array, color, loc, offset)
}