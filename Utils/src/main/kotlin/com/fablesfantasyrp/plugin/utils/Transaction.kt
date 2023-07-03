package com.fablesfantasyrp.plugin.utils

import kotlin.reflect.KMutableProperty

data class TransactionStep(val execute: () -> Unit, val undo: () -> Unit)

class Transaction(val steps: MutableList<TransactionStep> = ArrayList()) {
	fun execute() {
		var success = false
		var exception: Throwable? = null
		val executed = steps.takeWhile {
			try {
				it.execute()
				success = true
				true
			} catch (ex: Throwable) {
				exception = ex
				false
			}
		}

		if (!success) {

			try {
				executed.reversed().forEach { it.undo() }
			} catch (ex: Exception) {
				throw IllegalStateException("Exception trying to roll back transaction!", ex)
			}
		}

		exception?.let { throw it }
	}

	fun<T> setProperty(property: KMutableProperty<T>, value: T) {
		val oldValue = property.getter.call()
		steps.add(TransactionStep(
			execute = { property.setter.call(value) },
			undo = { property.setter.call(oldValue) }
		))
	}
}
