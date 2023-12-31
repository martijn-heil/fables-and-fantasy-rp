package com.fablesfantasyrp.plugin.utils.extensions.bukkit

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.TranslatableComponent
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtIo
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.io.ByteArrayOutputStream
import kotlin.math.min
import net.minecraft.world.item.ItemStack as NMSItemStack

fun ItemStack.splitStacks(stackSize: Int = this.maxStackSize): Collection<ItemStack> {
	check(stackSize > 0)

	val stacks = ArrayList<ItemStack>()
	var amount = this.amount

	while (amount > 0) {
		stacks.add(this.asQuantity(min(stackSize, amount)))
		amount -= stackSize
	}

	return stacks
}

fun ItemStack.toBytes(): ByteArray {
	val outputStream = ByteArrayOutputStream()

	val nbtTagCompoundItem = CraftItemStack.asNMSCopy(this).save(CompoundTag())

	val nbtTagListItems = {
		val list = ListTag()
		list.add(nbtTagCompoundItem)
		list
	}

	NbtIo.writeCompressed(nbtTagCompoundItem, outputStream)
	return outputStream.toByteArray()
}

object ItemStackCompanion
fun ItemStackCompanion.fromBytes(data: ByteArray): ItemStack {
	val nbtTagCompoundRoot = NbtIo.readCompressed(data.inputStream())
	val nmsItem = NMSItemStack.of(nbtTagCompoundRoot)
	return CraftItemStack.asBukkitCopy(nmsItem)
}

val PLAYER_HOTBAR_SLOTS = 36..44

fun EquipmentSlot.getInventorySlotIndex(heldItemSlot: Int): Int {
	return when (this) {
		EquipmentSlot.HAND -> heldItemSlot
		EquipmentSlot.OFF_HAND -> 40
		EquipmentSlot.FEET -> 36
		EquipmentSlot.LEGS -> 37
		EquipmentSlot.CHEST -> 38
		EquipmentSlot.HEAD -> 39
	}
}

/**
 * Creates a new [ItemStack] and opens a builder for it.
 */
inline fun itemStack(material: Material, builder: ItemStack.() -> Unit) = ItemStack(material).apply(builder)

/**
 * Opens a builder with the current meta.
 * @param T the specific type of the meta
 */
inline fun <reified T : ItemMeta> ItemStack.meta(builder: T.() -> Unit) {
	val curMeta = itemMeta as? T
	itemMeta = if (curMeta != null) {
		curMeta.apply(builder)
		curMeta
	} else {
		itemMeta(type, builder)
	}
}

/** @see meta */
@JvmName("simpleMeta")
inline fun ItemStack.meta(builder: ItemMeta.() -> Unit) = meta<ItemMeta>(builder)

/**
 * Resets the meta and opens a builder to create the new one.
 * @param T the specific type of the meta
 */
inline fun <reified T : ItemMeta> ItemStack.setMeta(builder: T.() -> Unit) {
	itemMeta = itemMeta(type, builder)
}

/** @see setMeta */
@JvmName("simpleSetMeta")
inline fun ItemStack.setMeta(builder: ItemMeta.() -> Unit) = setMeta<ItemMeta>(builder)

/**
 * Creates new a [ItemMeta] instance of the given material and opens a builder for it.
 * @param T the specific type of the meta
 */
inline fun <reified T : ItemMeta> itemMeta(material: Material, builder: T.() -> Unit): T? {
	val meta = Bukkit.getItemFactory().getItemMeta(material)
	return if (meta is T) meta.apply(builder) else null
}

/** @see itemMeta */
@JvmName("simpleItemMeta")
inline fun itemMeta(material: Material, builder: ItemMeta.() -> Unit) = itemMeta<ItemMeta>(material, builder)

/**
 * Sets the lore (description) of the item.
 */
inline fun ItemMeta.setLore(builder: ItemMetaLoreBuilder.() -> Unit) {
	lore(ItemMetaLoreBuilder().apply(builder).lorelist)
}

/**
 * Adds new lines to the lore (description) of the item.
 */
inline fun ItemMeta.addLore(builder: ItemMetaLoreBuilder.() -> Unit) {
	val newLore = lore() ?: mutableListOf<Component>()
	newLore.addAll(ItemMetaLoreBuilder().apply(builder).lorelist)
	lore(newLore)
}

/**
 * Lore builder which uses an [ArrayList] under the hood.
 * It exists to provide overloaded operator functions.
 */
class ItemMetaLoreBuilder {
	val lorelist = ArrayList<Component>()

	/**
	 * Adds a new line to the lore.
	 *
	 * Note: Render [TranslatableComponent]s before adding them to the lore.
	 */
	operator fun Component.unaryPlus() {
		lorelist += this
	}

	/**
	 * Adds a new line to the lore.
	 */
	operator fun String.unaryPlus() {
		lorelist += text(this)
	}
}

/**
 * Add a new [ItemFlag] to the item flags.
 */
fun ItemMeta.flag(itemFlag: ItemFlag) = addItemFlags(itemFlag)

/**
 * Add several [ItemFlag]s to the item flags.
 */
fun ItemMeta.flags(vararg itemFlag: ItemFlag) = addItemFlags(*itemFlag)

/**
 * Removes a [ItemFlag] from the item flags.
 */
fun ItemMeta.removeFlag(itemFlag: ItemFlag) = removeItemFlags(itemFlag)

/**
 * Removes several [ItemFlag]s from the item flags.
 */
fun ItemMeta.removeFlags(vararg itemFlag: ItemFlag) = removeItemFlags(*itemFlag)

/**
 * Provides safe access to the items' displayName.
 *
 * Note: Render [TranslatableComponent]s before setting them as the displayName.
 */
var ItemMeta.name: Component?
	get() = if (hasDisplayName()) displayName() else null
	set(value) = displayName(value ?: Component.space())

/**
 * Provides safe access to the items' displayName.
 */
@Suppress("DEPRECATION")
@Deprecated("displaynames are saved as Components in Paper", ReplaceWith("name", "net.axay.kspigot.Items.name"))
var ItemMeta.stringName: String?
	get() = if (hasDisplayName()) displayName else null
	set(value) = setDisplayName(if (value == null || value == "") " " else value)

/**
 * Provides safe access to the items' customModelData.
 */
var ItemMeta.customModel: Int?
	get() = if (hasCustomModelData()) customModelData else null
	set(value) = setCustomModelData(value)

/**
 * Provides more consistent access to the items' localizedName.
 */
var ItemMeta.localName: TranslatableComponent
	get() = if (hasDisplayName()) displayName() as TranslatableComponent else translatable("")
	set(value) = displayName(value)

fun ItemStack.formatNameWithAmount(amount: Int = this.amount) =
	if (amount > 1) "$amount ${this.asQuantity(amount).fancyName}" else this.asQuantity(amount).fancyName

val ItemStack.fancyName: String get() {
	var id = this.type.name.lowercase().replace('_', ' ')
	if (id == "air") {
		return "nothing"
	}
	if (id == "ice" || id == "dirt" || id.endsWith("copper") || id.endsWith("cream")) {
		return id
	}
	return if (this.amount > 1) {
		if (id == "cactus") {
			return "cacti"
		}
		if (id.endsWith(" off")) {
			id = id.substring(0, id.length - 4)
		}
		if (id.endsWith(" on")) {
			id = id.substring(0, id.length - 3)
		}
		if (id == "rotten flesh" || id == "cooked fish" || id == "raw fish" || id.endsWith("s")) {
			return id
		}
		if (id.endsWith("y")) {
			return id.substring(0, id.length - 1) + "ies" // ex: lily -> lilies
		}
		if (id.endsWith("sh") || id.endsWith("ch")) {
			id + "es"
		} else id + "s"
		// iron sword -> iron swords
	} else {
		if (id == "cactus") {
			return "a cactus"
		}
		if (id.endsWith("s")) {
			return id
		}
		if (id.endsWith(" off")) {
			return "a " + id.substring(0, id.length - 4)
		}
		if (id.endsWith(" on")) {
			return "a " + id.substring(0, id.length - 3)
		}
		if (id.startsWith("a") || id.startsWith("e") || id.startsWith("i")
			|| id.startsWith("o") || id.startsWith("u")) {
			"an $id" // ex: emerald -> an emerald
		} else "a $id"
		// ex: diamond -> a diamond
	}
}
