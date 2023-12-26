package com.fablesfantasyrp.plugin.wardrobe.gui

import com.fablesfantasyrp.plugin.characters.shortName
import com.fablesfantasyrp.plugin.gui.GuiSingleChoice
import com.fablesfantasyrp.plugin.gui.Icon
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.itemStack
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.meta
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.name
import com.fablesfantasyrp.plugin.wardrobe.OriginalPlayerProfileService
import com.fablesfantasyrp.plugin.wardrobe.SkinService
import com.fablesfantasyrp.plugin.wardrobe.data.ProfileSkin
import com.fablesfantasyrp.plugin.wardrobe.data.ProfileSkinRepository
import com.fablesfantasyrp.plugin.wardrobe.data.SkinRepository
import com.fablesfantasyrp.plugin.wardrobe.data.skin
import com.fablesfantasyrp.plugin.wardrobe.flaunch
import com.fablesfantasyrp.plugin.wardrobe.frunBlocking
import com.github.shynixn.mccoroutine.bukkit.launch
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import kotlinx.coroutines.CompletableDeferred
import net.kyori.adventure.text.Component
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import java.time.Instant
import java.util.*
import kotlin.math.min

class WardrobeGui(private val plugin: JavaPlugin,
				  private val player: Player,
				  private val profile: Profile,
				  private val slotCount: Int,
				  private val skinRepository: SkinRepository,
				  private val profileSkinRepository: ProfileSkinRepository,
				  private val skinService: SkinService,
				  private val originalPlayerProfileService: OriginalPlayerProfileService)
	: InventoryGui(plugin, player, "${frunBlocking { profile.shortName() }}'s wardrobe", arrayOf("gggggggxd")) {
	private val server = plugin.server

	init {
		this.addElement(DynamicGuiElement('g') { viewer ->
			val skins = profileSkinRepository.forProfile(profile).sortedByDescending { it.lastUsedAt }
			val group = GuiElementGroup('g')

			group.addElements(skins.map { profileSkin ->
				StaticGuiElement('g', getItem(profileSkin), {
					applySkin(profileSkin)
					true
				}, "${ChatColor.GOLD}${profileSkin.description}", "${ChatColor.GRAY}Click to apply this skin.")
			})

			group.addElements((min(skins.size, slotCount) until slotCount).map {
				StaticGuiElement('g', ItemStack(Material.SKELETON_SKULL), {
					saveSkin()
					true
				},
					"${ChatColor.DARK_AQUA}Click to save!",
					"${ChatColor.GRAY}Clicking this will allow you to name",
					"${ChatColor.GRAY}and save your current launcher skin.")
			})

			group.addElements((slotCount..6).map {
				StaticGuiElement('g', ItemStack(Material.WITHER_SKELETON_SKULL), { true },
					"${ChatColor.RED}Locked slot!",
					"${ChatColor.GRAY}Higher VIP ranks may access this slot.")
			})

			group
		})

		this.addElement(StaticGuiElement('x', Icon.X, {
			resetSkin()
			true
		}, "${ChatColor.GREEN}Clear skin",
			"${ChatColor.GRAY}Click here to revert to the skin",
			"${ChatColor.GRAY}you currently wear in the launcher."))

		this.addElement(StaticGuiElement('d', Icon.TRASH_BIN, {
			deleteSkin()
			true
		}, "${ChatColor.RED}Delete a skin",
			"${ChatColor.GRAY}Click here to select a skin to delete.", ))
	}

	private fun applySkin(profileSkin: ProfileSkin) {
		skinService.setSkin(player, profileSkin.skin)
		profileSkinRepository.update(profileSkin.copy(lastUsedAt = Instant.now()))
	}

	private fun deleteSkin() {
		flaunch {
			val profileSkin = GuiSingleChoice<ProfileSkin>(plugin, "Please select a skin to delete",
				profileSkinRepository.forProfile(profile).sortedByDescending { it.lastUsedAt }.asSequence(),
				{ getItem(it) },
				{ "${ChatColor.GOLD}${it.description}\n${ChatColor.GRAY}Click to delete this skin." }).execute(player)

			profileSkinRepository.destroy(profileSkin)
			draw()
		}
	}

	private fun saveSkin() {
		this.close()
		flaunch {
			try {
				val item = itemStack(Material.BLACK_STAINED_GLASS_PANE) { meta { name = Component.empty() } }
				val deferred = CompletableDeferred<String>()
				AnvilGUI.Builder()
					.plugin(plugin)
					.title("Enter a description")
					.itemLeft(item)
					.itemOutput(item)
					.onClick { _, snapshot ->
						deferred.complete(snapshot.text.trim())
						listOf(AnvilGUI.ResponseAction.close())
					}
					.onClose {
						deferred.cancel()
					}
					.open(player)
				val description = deferred.await()

				val originalProfile = originalPlayerProfileService.getOriginalPlayerProfile(player)!!

				if (originalProfile.textures.skin == null) {
					player.sendError("You have no skin")
					return@flaunch
				}

				val skin = skinRepository.create(originalProfile.skin)
				profileSkinRepository.createOrUpdate(ProfileSkin(profile, skin, description, null))
			} finally {
				this@WardrobeGui.show(player)
			}
		}
	}

	private fun resetSkin() {
		skinService.setSkin(player, null)
	}

	private fun getItem(profileSkin: ProfileSkin): ItemStack
		= itemStack(Material.PLAYER_HEAD) {
			meta<SkullMeta> {
				val profile = server.createProfile(UUID.randomUUID(), null)
				profile.setProperty(profileSkin.skin.toProfileProperty())
				this.playerProfile = profile
			}
		}
}
