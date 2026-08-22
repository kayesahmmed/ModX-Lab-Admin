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
                setPadding(64, 36, 64, 36)
                elevation = 30f // Deep floating shadow
                
                val bgDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 200f // Fully rounded (Pill/গোল shape)
                    colors = intArrayOf(Color.parseColor("#0F172A"), Color.parseColor("#1E293B"))
                    setStroke(4, Color.parseColor("#10B981"))
                }
                background = bgDrawable

                // Animate the stroke color to create a glowing effect
                val colorAnim = android.animation.ValueAnimator.ofArgb(
                    Color.parseColor("#10B981"), // Emerald
                    Color.parseColor("#059669"), // Darker Emerald
                    Color.parseColor("#34D399"), // Lighter Emerald
                    Color.parseColor("#10B981")
                ).apply {
                    duration = 1500
                    repeatCount = android.animation.ValueAnimator.INFINITE
                    addUpdateListener { animator ->
                        bgDrawable.setStroke(4, animator.animatedValue as Int)
                    }
                }
                colorAnim.start()

                // Save animator as tag so we can cancel it later
                tag = colorAnim
            }

            val textView = TextView(context).apply {
                text = "✨ $signature"
                setTextColor(Color.WHITE)
                textSize = 15f
                letterSpacing = 0.05f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(8, 0, 8, 0)
            }

            layout.addView(textView)

            if (context is android.app.Activity) {
                val decorView = context.window.decorView as? android.view.ViewGroup
                if (decorView != null) {
                    val params = android.widget.FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                        bottomMargin = 400 // Float higher
                    }
                    
                    layout.alpha = 0f
                    decorView.addView(layout, params)
                    
                    // Fade in and out like a toast
                    layout.animate().alpha(1f).setDuration(400).withEndAction {
                        layout.postDelayed({
                            layout.animate().alpha(0f).setDuration(400).withEndAction {
                                (layout.tag as? android.animation.ValueAnimator)?.cancel()
                                decorView.removeView(layout)
                            }
                        }, 3500)
                    }
                }
            } else {
                // Fallback for non-activity context
                val toast = Toast(context.applicationContext).apply {
                    duration = Toast.LENGTH_LONG
                    view = layout
                }
                toast.show()
            }
            
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
