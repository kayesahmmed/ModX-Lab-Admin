package com.modxlab.admin

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ModXAdminApp : Application() {

    override fun onCreate() {
        super.onCreate()
        CrashHandler.init(this)
    }
}

object CrashHandler {
    private const val TAG = "CrashHandler"
    private const val PREFS_NAME = "crash_prefs"
    private const val KEY_LAST_CRASH = "last_crash_report"

    fun init(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val report = generateCrashReport(context, thread, throwable)
                saveCrashReport(context, report)
                Log.e(TAG, "FATAL CRASH DETECTED:\n$report", throwable)

                // Launch CrashReportActivity
                val intent = Intent(context, CrashReportActivity::class.java).apply {
                    putExtra(CrashReportActivity.EXTRA_CRASH_REPORT, report)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                context.startActivity(intent)

                // Terminate current process
                android.os.Process.killProcess(android.os.Process.myPid())
                System.exit(10)
            } catch (e: Exception) {
                Log.e(TAG, "Error handling crash", e)
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }

    private fun generateCrashReport(context: Context, thread: Thread, throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        val stackTrace = sw.toString()

        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        val packageInfo = try {
            context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: Exception) {
            null
        }

        val versionName = packageInfo?.versionName ?: "1.0"
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo?.longVersionCode ?: 1L
        } else {
            @Suppress("DEPRECATION")
            packageInfo?.versionCode?.toLong() ?: 1L
        }

        val runtime = Runtime.getRuntime()
        val usedMemoryMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMemoryMb = runtime.maxMemory() / (1024 * 1024)

        return buildString {
            appendLine("==================================================")
            appendLine("             MODX LAB ADMIN CRASH LOG             ")
            appendLine("==================================================")
            appendLine("Time: $timeStamp")
            appendLine("App Package: ${context.packageName}")
            appendLine("App Version: $versionName ($versionCode)")
            appendLine("--------------------------------------------------")
            appendLine("DEVICE INFORMATION:")
            appendLine("Manufacturer: ${Build.MANUFACTURER}")
            appendLine("Model: ${Build.MODEL} (${Build.PRODUCT})")
            appendLine("Android OS Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Thread: ${thread.name} (id: ${thread.id})")
            appendLine("Memory Usage: ${usedMemoryMb}MB / ${maxMemoryMb}MB")
            appendLine("--------------------------------------------------")
            appendLine("EXCEPTION:")
            appendLine("Type: ${throwable.javaClass.name}")
            appendLine("Message: ${throwable.localizedMessage ?: throwable.message ?: "No message"}")
            appendLine("--------------------------------------------------")
            appendLine("FULL STACK TRACE:")
            appendLine(stackTrace)
            appendLine("==================================================")
        }
    }

    private fun saveCrashReport(context: Context, report: String) {
        try {
            // 1. Save to SharedPreferences
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LAST_CRASH, report)
                .commit()

            // 2. Save to External Files Directory (Accessible in File Manager)
            val externalDir = context.getExternalFilesDir(null)
            if (externalDir != null) {
                // Delete old timestamped files if any exist to prevent clutter
                externalDir.listFiles { file -> file.name.startsWith("crash_") && file.name.endsWith(".txt") && file.name != "crash_log.txt" }
                    ?.forEach { it.delete() }

                val crashFile = File(externalDir, "crash_log.txt")
                crashFile.writeText(report)
            }

            // 3. Save to Internal Files Directory (Always writable)
            val internalFile = File(context.filesDir, "latest_crash.txt")
            internalFile.writeText(report)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save crash log file", e)
        }
    }

    fun getLastCrashReport(context: Context): String? {
        // Try reading from file first
        try {
            val externalDir = context.getExternalFilesDir(null)
            if (externalDir != null) {
                val crashFile = File(externalDir, "crash_log.txt")
                if (crashFile.exists() && crashFile.length() > 0) {
                    return crashFile.readText()
                }
            }
        } catch (_: Exception) {}

        // Fallback to SharedPreferences
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LAST_CRASH, null)
    }

    fun clearLastCrashReport(context: Context) {
        try {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_LAST_CRASH)
                .apply()

            val externalDir = context.getExternalFilesDir(null)
            if (externalDir != null) {
                File(externalDir, "crash_log.txt").delete()
            }
            File(context.filesDir, "latest_crash.txt").delete()
        } catch (_: Exception) {}
    }
}
