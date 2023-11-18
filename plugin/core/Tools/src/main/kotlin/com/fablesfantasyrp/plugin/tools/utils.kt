package com.fablesfantasyrp.plugin.tools

fun getRealMoveSpeed(speed: Float, movementType: MovementType): Float {
	val maxSpeed = 1f
	val defaultSpeed = when(movementType) {
		MovementType.FLY -> 0.1f
		MovementType.WALK -> 0.2f
	}

	return if (speed < 0) {
		defaultSpeed * speed
	} else {
		val ratio = ((speed - 1) / 9) * (maxSpeed - defaultSpeed)
		ratio + defaultSpeed
	}
}
