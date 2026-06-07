package com.pdv85.app.util

import java.text.NumberFormat
import java.util.Locale

/** Format a Double as BRL currency string, e.g. "R$ 1.234,56" */
fun Double.toBRL(): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(this)

/** Safe string-to-double (accepts "1.234,56" and "1234.56") */
fun String.toDoubleOrZero(): Double {
    val clean = this.replace(".", "").replace(",", ".")
    return clean.toDoubleOrNull() ?: 0.0
}
