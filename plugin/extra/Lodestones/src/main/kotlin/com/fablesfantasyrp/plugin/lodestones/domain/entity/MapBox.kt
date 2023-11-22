package com.fablesfantasyrp.plugin.lodestones.domain.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import org.bukkit.Location
import org.bukkit.World

data class PlanarCoordinates(val x: Float, val z: Float)
data class PlanarBounds(val leftTop: PlanarCoordinates, val width: Int, val height: Int) {
	val maxX = leftTop.x + width
	val minX = leftTop.x

	val maxZ = leftTop.z + height
	val minZ = leftTop.z
}
data class PlanarAnchor(val world: World, val bounds: PlanarBounds)
data class AnchoredPlane(val bounds: PlanarBounds, val anchor: PlanarAnchor)


class MapBox : DataEntity<Int, MapBox> {
	override var dirtyMarker: DirtyMarker<MapBox>? = null
	override val id: Int
	val region: WorldGuardRegion
	val location: Location
	val plane: AnchoredPlane

	constructor(id: Int,
				region: WorldGuardRegion,
				location: Location,
				plane: AnchoredPlane) {
		this.id = id
		this.region = region
		this.location = location
		this.plane = plane
	}
}
