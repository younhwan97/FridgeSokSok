package com.yh.fridgesoksok.presentation.common.extension

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

fun Context.isNotificationPermissionGranted(): Boolean {
    return NotificationManagerCompat.from(this).areNotificationsEnabled()
}

fun launchNotificationSettings(context: Context) {
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }.also {
        context.startActivity(it)
    }
}