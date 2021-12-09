package com.standford.ligthhouse.service

import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi

class Lighthouse : NotificationListenerService() {
    /*
        These are the package names of the apps. for which we want to
        listen the notifications
     */
    private object ApplicationPackageNames {
        const val WHATSAPP_PACK_NAME = "com.whatsapp"
    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */
    object InterceptedNotificationCode {
        const val WHATSAPP_CODE = 2
        const val OTHER_NOTIFICATIONS_CODE = 4 // We ignore all notification with code == 4
    }

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notificationCode = matchNotificationCode(sbn)
        val messageSender = sbn.notification.extras.getString("android.title")
        val messageContent = sbn.notification.extras.getString("android.text")
        if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
            val intent = Intent("com.standford.ligthhouse")
            intent.putExtra("Notification Code", notificationCode)
            intent.putExtra("Notification Message", messageContent)
            intent.putExtra("Notification Message Sender", messageSender)
            sendBroadcast(intent)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        return
    }

    private fun matchNotificationCode(sbn: StatusBarNotification): Int {
        val packageName = sbn.packageName
        return if (packageName == ApplicationPackageNames.WHATSAPP_PACK_NAME) {
            InterceptedNotificationCode.WHATSAPP_CODE
        } else {
            InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE
        }
    }
}