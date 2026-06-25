package com.info85.pdv85.util

import java.text.NumberFormat
import java.util.Locale

/** Format a Double as BRL currency string, e.g. "R$ 1.234,56" */
fun Double.toBRL(): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(this)

/** Safe string-to-double. Accepts Brazilian format ("1.234,56") or plain decimal ("1234,56" / "1234.56").
 *  Assumes thousands-separator periods are always paired with a comma decimal separator. */
fun String.toDoubleOrZero(): Double {
    // If the string contains both '.' and ',' treat '.' as thousands sep (Brazilian format).
    // Otherwise try replacing comma with period for plain Brazilian decimal.
    val normalised = if (contains('.') && contains(',')) {
        replace(".", "").replace(",", ".")
    } else {
        replace(",", ".")
    }
    return normalised.toDoubleOrNull() ?: 0.0
}
