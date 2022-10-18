package com.fablesfantasyrp.plugin.targeting

import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Player

class PacketGlowingManager : GlowingManager {
	override fun glowFor(glowing: Player,viewing: Player) {
		require(viewing is CraftPlayer)

		val packet = ClientboundUpdateMobEffectPacket(glowing.entityId,
				MobEffectInstance(MobEffects.GLOWING, 999999, 1, false, false))
		viewing.handle.networkManager.send(packet)
	}

	override fun unglowFor(glowing: Player, viewing: Player) {
		require(viewing is CraftPlayer)

		val packet = ClientboundRemoveMobEffectPacket(glowing.entityId, MobEffects.GLOWING)
		viewing.handle.networkManager.send(packet)
	}
}
