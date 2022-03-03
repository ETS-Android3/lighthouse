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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.standford.ligthhouse.R
import com.standford.ligthhouse.api.APIClient
import com.standford.ligthhouse.api.APIInterface
import com.standford.ligthhouse.db.SQLiteHelper
import com.standford.ligthhouse.model.ApiData
import com.standford.ligthhouse.model.Data
import com.standford.ligthhouse.model.LinkModel
import com.standford.ligthhouse.model.MessageModel
import com.standford.ligthhouse.service.Lighthouse
import com.standford.ligthhouse.ui.home.HomeFragment
import com.standford.ligthhouse.utility.Share
import com.standford.ligthhouse.utility.SharedPrefs
import com.standford.ligthhouse.utility.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class DashboardActivity : AppCompatActivity() {
    private var TAG = "DashboardActivity"
    private var notificationId = 0
    private var notificationBroadcastReceiver: NotificationBroadcastReceiver? = null
    private var enableNotificationListenerAlertDialog: AlertDialog? = null
    private val disinformationLinksToIntercept: MutableList<Data?> = ArrayList()
    private val CHANNEL_ID = "WHATSAPP_DISINFO"
    var dbHelper: SQLiteHelper? = null

    var apiInterface: APIInterface? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = SQLiteHelper(this@DashboardActivity)
        dbHelper!!.open()

        createNotificationChannel()
        setContentView(R.layout.activity_dashboard)

        Log.e(TAG, "onCreate: DATA==>" + dbHelper!!.getAllData(this@DashboardActivity).size)

        if (dbHelper!!.getAllData(this@DashboardActivity).size > 0) {
            disinformationLinksToIntercept.addAll(dbHelper!!.getAllData(this@DashboardActivity))
        } else {
            getAndSetData()
        }



        navView = this.findViewById(R.id.nav_view)
        navView!!.itemIconTintList = null
        navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        navView!!.setupWithNavController(navController!!)


        if (SharedPrefs.getAllFilteredMessage(this) != null) {
            if (SharedPrefs.getAllFilteredMessage(this).size > 0) {
                Share.interceptedLinks.addAll(SharedPrefs.getAllFilteredMessage(this))
            }
        }

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

    private fun getAndSetData() {

        Log.e("MainActivity", "getAndSetData()")

        Util.showProgress(this@DashboardActivity, "Loading...")

        apiInterface = APIClient.client?.create(APIInterface::class.java)

        val call: Call<ApiData>? = apiInterface?.doGetAllData()
        call!!.enqueue(object : Callback<ApiData> {
            override fun onResponse(call: Call<ApiData>, response: Response<ApiData>) {
                if (response.body() != null) {
                    val templateData = response.body()
                    loadDataBaseLinkNew(templateData!!.data!!.data)
                    Util.dismissProgress()
                } else {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<ApiData>, t: Throwable) {
                Util.dismissProgress()
                Log.e("MainActivity", "onFailure:" + t.message)
                Toast.makeText(
                    this@DashboardActivity,
                    "onFailure:==>" + t.message,
                    Toast.LENGTH_LONG
                ).show()
                call.cancel()
            }
        })
    }

    fun loadDataBaseLinkNew(templateData: List<Data>?) {
        try {
            val gson = Gson()
            for (model in templateData!!) {
                Log.i(TAG, "json read->${model.id}")

                dbHelper!!.stuffInsert(
                    model.createdDate,
                    model.id,
                    model.profileId,
                    model.identifier,
                    model.topline,
                    model.rank,
                    model.score,
                    model.country,
                    model.language,
                    gson.toJson(model.writeup).toString(),
                    model.criteria.toString(),
                    model.active,
                    model.healthGuard,
                    model.locale
                )

                disinformationLinksToIntercept.add(model)
            }

        } catch (ex: Exception) {
            Log.i(TAG, "json Exception->${ex.message}")
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
    private fun dummyVerifyExtractedLinks(detectedLinks: ArrayList<List<String?>>): ArrayList<Data?> {
        val disinformationLinks = ArrayList<Data?>()
        for (link in detectedLinks) {
            for (storedData in disinformationLinksToIntercept) {
                if (storedData!!.identifier.toString().equals(link[0].toString())) {
                    storedData.name = link[1]
                    disinformationLinks.add(storedData)
                }
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
        disinformationLinks: ArrayList<Data?>?,
        originalMessage: String?
    ) {
        if (disinformationLinks == null || disinformationLinks.size == 0) return
        var notificationMessage = ""
        var linkSender = ""
        for (link in disinformationLinks) {

            val linkInfo = link!!.name + ": " + link.identifier
            val writeup = link.writeup
            val messagesContainingDomain = ArrayList<MessageModel>()
            val linkInfoListFormat = LinkModel(
                link.name!!,
                link.identifier!!,
                originalMessage!!,
                link.identifier!!,
                link.topline!!,
                link.rank!!,
                link.score,
                writeup,
                messagesContainingDomain
            )


            dbHelper!!.stuffInsertMessage(
                LinkModel(
                    link.name!!,
                    link.identifier!!,
                    originalMessage,
                    link.identifier!!,
                    link.topline!!,
                    link.rank!!,
                    link.score,
                    writeup,
                    messagesContainingDomain
                )
            )
            Share.interceptedLinks.add(linkInfoListFormat)

            for (i in 0 until Share.interceptedLinks.size) {
                var j = i + 1
                while (j < Share.interceptedLinks.size) {
                    if (Share.interceptedLinks.get(i).identifier.equals(Share.interceptedLinks.get(j).identifier)) {
                        Share.interceptedLinks.removeAt(j)
                        j--
                    }
                    j++
                }
            }

            SharedPrefs.saveFilteredMessage(Share.interceptedLinks, this)
            /*
            The list adapter is notified of the change in data contained by the DashboardActivity
            list in the UI so as to reflect any newly added data.
            */

            if (HomeFragment.adapter != null) {
                HomeFragment.adapter!!.notifyDataSetChanged()
            }


            notificationMessage += linkInfo
            linkSender = link.name!!
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