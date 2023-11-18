package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.repository.*

open class SimpleNamedEntityRepository<K, T, C>(child: C) : SimpleEntityRepository<K, T, C>(child), NamedRepository<T>
	where T : Identifiable<K>,
		  T : Named,
		  C : KeyedRepository<K, T>,
		  C : MutableRepository<T>,
		  C : HasDirtyMarker<T>,
		  C : NamedRepository<T> {
    private val byName = HashMap<String, K>()

	override fun init() {
		super.init()
		this.all().forEach { byName[it.name] = it.id }
	}

	override fun forName(name: String): T? = byName[name]?.let { forId(it) }
	override fun allNames(): Set<String> = byName.keys

	override fun markDirty(v: T, what: String, oldValue: Any?, newValue: Any?) {
		super.markDirty(v, what, oldValue, newValue)
		if (what == "name") {
			check(oldValue != null && newValue != null)
			byName.remove(oldValue as String)
			byName[newValue as String] = v.id
		}
	}

	override fun create(v: T): T {
		require(!nameExists(v.name))
		val created = super.create(v)
		byName[created.name] = created.id
		return created
	}

	override fun destroy(v: T) {
		super.destroy(v)
		byName.remove(v.name)
	}
}
