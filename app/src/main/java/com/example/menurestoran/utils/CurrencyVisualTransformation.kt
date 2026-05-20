package com.example.menurestoran.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.*

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val number = originalText.toLongOrNull() ?: 0L
        val formatted = "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(number)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset == 0) return 0
                val textSoFar = originalText.substring(0, offset)
                val numberSoFar = textSoFar.toLongOrNull() ?: 0L
                val formattedSoFar = "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(numberSoFar)
                return formattedSoFar.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                val formattedWithoutPrefix = formatted.removePrefix("Rp ")
                if (offset <= 3) return 0
                val actualOffset = (offset - 3).coerceIn(0, formattedWithoutPrefix.length)
                val textWithoutDots = formattedWithoutPrefix.substring(0, actualOffset).replace(".", "")
                return textWithoutDots.length.coerceAtMost(originalText.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

fun formatToRupiah(amount: String): String {
    val number = amount.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L
    return "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(number)
}
