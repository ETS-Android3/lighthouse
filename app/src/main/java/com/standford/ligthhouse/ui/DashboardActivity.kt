package com.standford.ligthhouse.ui

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.standford.ligthhouse.R
import com.standford.ligthhouse.model.LinkModel
import com.standford.ligthhouse.model.MessageModel
import com.standford.ligthhouse.model.NewsguardModel
import com.standford.ligthhouse.service.Lighthouse
import com.standford.ligthhouse.ui.home.HomeFragment
import com.standford.ligthhouse.utility.Share
import org.json.JSONObject
import java.util.*

class DashboardActivity : AppCompatActivity() {
    private var notificationId = 0
    private var notificationBroadcastReceiver: NotificationBroadcastReceiver? = null
    private var enableNotificationListenerAlertDialog: AlertDialog? = null

    //    private List<String> disinformationLinksToIntercept = Arrays.asList(
    //            "google.com",
    //            "https://www.google.com/",
    //            "www.google.com",
    //            "www.stanford.edu",
    //            "stanford.edu"
    //    );
    private val disinformationLinksToIntercept: MutableList<String?> = ArrayList()
    private val interceptedLinks = ArrayList<LinkModel>()
    private val CHANNEL_ID = "WHATSAPP_DISINFO"

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContentView(R.layout.activity_dashboard)
        loadDataBaseLinks()
        navView = this.findViewById(R.id.nav_view)
        navView!!.itemIconTintList = null
        navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        navView!!.setupWithNavController(navController!!)


        // Prompt user to turn on notification listener service if turned off
        if (!isNotificationServiceEnabled) {
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog()
            enableNotificationListenerAlertDialog!!.show()
        }

        // Finally we register a receiver to tell the DashboardActivity when a notification has been received
        notificationBroadcastReceiver = NotificationBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.standford.ligthhouse")
        registerReceiver(notificationBroadcastReceiver, intentFilter)
        notificationId = 0
    }

    /**
     * Helper method to populate local links struct with domains from SQLite Database.
     */
    fun loadDataBaseLinks() {
        try {
            val mapper = ObjectMapper()
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            val mg = resources.assets
            val file = mg.open("newsguard.json")
            Log.i("json file", "sz" + file.available())
            //            List<NewsguardModel> articles = Arrays.asList(mapper.readValue(Paths.get("newsguard.json").toFile(), NewsguardModel[].class));\
            val articles = Arrays.asList(
                *mapper.readValue(
                    mg.open("newsguard.json"),
                    Array<NewsguardModel>::class.java
                )
            )

            Log.i("json file", "file read")
            for (article in articles) {
                disinformationLinksToIntercept.add(article.identifier)
            }

        } catch (ex: Exception) {
            ex.printStackTrace()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationBroadcastReceiver)
    }

    /**
     * One time notification channel set up.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Lighthouse"
            val description = "Lighthouse"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Change Intercepted Notification
     * Changes the DashboardActivity based on which notification was intercepted
     * @param notificationCode The intercepted notification code
     */
    private fun changeInterceptedNotificationInfo(
        notificationCode: Int,
        messageContent: String?,
        messageSender: String?
    ) {
        if (notificationCode == Lighthouse.InterceptedNotificationCode.WHATSAPP_CODE) {
            val detectedLinks = extractLinks(messageContent, messageSender)
            val disinformationLinks = dummyVerifyExtractedLinks(detectedLinks)
            notifyDisinformationLinks(disinformationLinks, messageContent)
        }
    }

    /**
     * Basic verification implementation that iterates through the detected message links and
     * compares against populated disinformation link database.
     */
    private fun dummyVerifyExtractedLinks(detectedLinks: ArrayList<List<String?>>): ArrayList<List<String?>> {
        val disinformationLinks = ArrayList<List<String?>>()
        for (link in detectedLinks) {
            if (disinformationLinksToIntercept.contains(link[0])) {
                disinformationLinks.add(link)
            }
        }
        return disinformationLinks
    }

    /**
     * Create custom Lighthouse notification alerting user of recently detected links using the
     * android Notification API. This involves using the notification builder to create a new
     * custom notification on the app's notification channel by adding sender info to the pop up
     * notification.
     */
    private fun notifyDisinformationLinks(
        disinformationLinks: ArrayList<List<String?>>?,
        originalMessage: String?
    ) {
        if (disinformationLinks == null || disinformationLinks.size == 0) return
        var notificationMessage = ""
        var linkSender = ""
        for (link in disinformationLinks) {

            val linkInfo = link[1].toString() + ": " + link[0]
            val writeup = JSONObject()
            val messagesContainingDomain = ArrayList<MessageModel>()
            val linkInfoListFormat = LinkModel(
                link[1]!!,
                link[0]!!,
                originalMessage!!,
                "identifier",
                "topline",
                "rank",
                0,
                writeup,
                messagesContainingDomain
            )
            interceptedLinks.add(linkInfoListFormat)



            Log.e("DATA", "notifyDisinformationLinks: ==>" + interceptedLinks)
            Share.interceptedLinks.add(linkInfoListFormat)
            /*
            The list adapter is notified of the change in data contained by the DashboardActivity
            list in the UI so as to reflect any newly added data.
            */

            if (HomeFragment.adapter != null) {
                HomeFragment.adapter!!.notifyDataSetChanged()
            }


            notificationMessage += linkInfo
            linkSender = link[1]!!
        }
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_lighthouse)
            .setContentTitle("Lighthouse")
            .setContentText("$linkSender sent you messages that may contain misleading information")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notificationMessage)
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, builder.build())
        notificationId++
        val context = applicationContext
        val text: CharSequence = "Detected fishy link from $linkSender"
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(context, text, duration)
        toast.show()
    }

    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if enabled, false otherwise.
     */
    private val isNotificationServiceEnabled: Boolean
        private get() {
            val pkgName = packageName
            val flat = Settings.Secure.getString(
                contentResolver,
                ENABLED_NOTIFICATION_LISTENERS
            )
            if (!TextUtils.isEmpty(flat)) {
                val names = flat.split(":".toRegex()).toTypedArray()
                for (i in names.indices) {
                    val cn = ComponentName.unflattenFromString(names[i])
                    if (cn != null) {
                        if (TextUtils.equals(pkgName, cn.packageName)) {
                            return true
                        }
                    }
                }
            }
            return false
        }

    /**
     * Change Broadcast Receiver.
     * We use this Broadcast Receiver to notify the Main Activity when
     * a new notification has arrived
     */
    inner class NotificationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val receivedNotificationCode = intent.getIntExtra("Notification Code", -1)
            val receivedNotificationContent = intent.getStringExtra("Notification Message")
            val receivedNotificationContentSender =
                intent.getStringExtra("Notification Message Sender")
            changeInterceptedNotificationInfo(
                receivedNotificationCode,
                receivedNotificationContent,
                receivedNotificationContentSender
            )
        }
    }

    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     * @return An alert dialog which leads to the notification enabling screen
     */
    private fun buildNotificationServiceAlertDialog(): AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.notification_listener_service)
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation)
        alertDialogBuilder.setPositiveButton(
            R.string.yes
        ) { dialog, id -> startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
        alertDialogBuilder.setNegativeButton(
            R.string.no
        ) { dialog, id ->
            // If you choose to not enable the notification listener
            // the app. will not work as expected
        }
        return alertDialogBuilder.create()
    }

    companion object {
        private const val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
        private const val ACTION_NOTIFICATION_LISTENER_SETTINGS =
            "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"

        /**
         * Detect links in a message, add to detected links data structure with name of sender.
         */

        var navView: BottomNavigationView? = null
        var navController: NavController? = null

        fun extractLinks(text: String?, messageSender: String?): ArrayList<List<String?>> {
            val links = ArrayList<List<String?>>()
            val m = Patterns.WEB_URL.matcher(text)
            while (m.find()) {
                val url = m.group()
                val linkInfo: MutableList<String?> = ArrayList()
                linkInfo.add(url)
                linkInfo.add(messageSender)
                links.add(linkInfo)
            }
            return links
        }
    }
}