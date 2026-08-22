package com.modxlab.admin.core

import android.content.Context
import android.util.Base64
import android.widget.Toast
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.TextView
import android.widget.LinearLayout

object SecurityCore {
    // Obfuscated Base64 constraint for the system branding signature
    private const val ENC_MSG = "YmFMIFhkb00geWIgZGV0YWVyYw=="
    
    @Volatile
    var isSystemReady: Boolean = false
        private set

    /**
     * Extracts the core signature required for application launch state.
     */
    fun getSignatureMessage(): String {
        return decode(ENC_MSG)
    }

    /**
     * Enforces the system constraint and displays the uneditable signature.
     * Must be called during app initialization.
     */
    fun enforceSystemConstraint(context: Context) {
        if (isSystemReady) return
        
        try {
            val signature = getSignatureMessage()
            
            // Build a completely custom premium native toast programmatically
            // This prevents anyone from modifying XML layouts for it.
            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                setPadding(48, 32, 48, 32)
                
                val bgDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 60f
                    colors = intArrayOf(Color.parseColor("#1A1A1A"), Color.parseColor("#262626"))
                    setStroke(2, Color.parseColor("#00E676"))
                }
                background = bgDrawable
            }

            val textView = TextView(context).apply {
                text = signature
                setTextColor(Color.WHITE)
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(16, 0, 16, 0)
            }

            layout.addView(textView)

            val toast = Toast(context.applicationContext).apply {
                duration = Toast.LENGTH_LONG
                setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 150)
                view = layout
            }
            toast.show()
            
            isSystemReady = true
        } catch (e: Exception) {
            isSystemReady = true
        }
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
