package com.modxlab.admin.core

import android.util.Base64

object SecurityCore {
    // Obfuscated Base64 constraint for the system branding signature
    private const val ENC_MSG = "YmFMIFhkb00geWIgZGV0YWVyYw=="

    /**
     * Extracts the core signature required for application launch state.
     */
    fun getSignatureMessage(): String {
        return decode(ENC_MSG)
    }

    private fun decode(encoded: String): String {
        return try {
            val decodedBytes = Base64.decode(encoded, Base64.DEFAULT)
            String(decodedBytes).reversed()
        } catch (e: Exception) {
            "Verification Failed"
        }
    }
}
