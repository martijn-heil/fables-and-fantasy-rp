package com.fablesfantasyrp.plugin.economy

fun formatMoney(amount: Int) = "$CURRENCY_SYMBOL${"%,d".format(amount)}"
