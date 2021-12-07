package com.github.raagavi158.lighthouse;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Intent.ACTION_VIEW;

public class  MainActivity extends AppCompatActivity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private int notificationId;
    private NotificationBroadcastReceiver notificationBroadcastReceiver;
    private AlertDialog enableNotificationListenerAlertDialog;
//    private List<String> disinformationLinksToIntercept = Arrays.asList(
//            "google.com",
//            "https://www.google.com/",
//            "www.google.com",
//            "www.stanford.edu",
//            "stanford.edu"
//    );
    private List<String> disinformationLinksToIntercept = new ArrayList<>();
    private ArrayList<LinkModel> interceptedLinks = new ArrayList<>();


    private String CHANNEL_ID = "WHATSAPP_DISINFO";
    private SQLDatabaseHelper db;
    private LinkModelAdapter adapter;
    private ListView linkListView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_main);
        loadDataBaseLinks();
        /*
        linkListView is a frontend component that uses a list view rendering LinkModel objects using
        the LinkModelAdapter class. An onClickListener for each list element uses the LinkModel object's
        link attribute to hyperlink the original disinfo link. This will eventually be replaced by
        a link to the debunking article.
        */
        linkListView = findViewById(R.id.link_list);
        adapter = new LinkModelAdapter(this, interceptedLinks);
        linkListView.setAdapter(adapter);

        // hyperlink list object to original website
        linkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinkModel obj = (LinkModel) linkListView.getAdapter().getItem(position);
                Uri uri = Uri.parse("https://www." + obj.link);
                Intent intent = new Intent(ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        // Prompt user to turn on notification listener service if turned off
        if(!isNotificationServiceEnabled()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }

        // Finally we register a receiver to tell the MainActivity when a notification has been received
        notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.github.raagavi.lighthouse");
        registerReceiver(notificationBroadcastReceiver,intentFilter);
        notificationId = 0;
    }

    /**
     * Helper method to populate local links struct with domains from SQLite Database.
     * */
   // @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadDataBaseLinks() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            AssetManager mg = getResources().getAssets();
            InputStream file = mg.open("newsguard.json");
            Log.i("json file", "sz" + file.available());
//            List<NewsguardModel> articles = Arrays.asList(mapper.readValue(Paths.get("newsguard.json").toFile(), NewsguardModel[].class));\
            List<NewsguardModel> articles = Arrays.asList(mapper.readValue(mg.open("newsguard.json"), NewsguardModel[].class));
            Log.i("json file", "file read");
            for(NewsguardModel article : articles) {
                disinformationLinksToIntercept.add(article.identifier);
            }
            Log.i("json file", String.valueOf(disinformationLinksToIntercept.size()));
            file.close();

        } catch (Exception ex) {
            Log.i("json file", ex.toString());
            ex.printStackTrace();
        }

        
    }

    /**
     * Detect links in a message, add to detected links data structure with name of sender.
     * */
    public static ArrayList<List<String>> extractLinks(String text, String messageSender) {
        ArrayList<List<String>> links = new ArrayList<>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            List<String> linkInfo = new ArrayList<>();
            linkInfo.add(url);
            linkInfo.add(messageSender);
            links.add(linkInfo);
        }
        return links;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationBroadcastReceiver);
    }

    /**
     * One time notification channel set up.
     * */

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Lighthouse";
            String description =  "Lighthouse";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Change Intercepted Notification
     * Changes the MainActivity based on which notification was intercepted
     * @param notificationCode The intercepted notification code
     */
    private void changeInterceptedNotificationInfo(int notificationCode, String messageContent, String messageSender){
        if(notificationCode == Lighthouse.InterceptedNotificationCode.WHATSAPP_CODE){
            ArrayList<List<String>> detectedLinks = extractLinks(messageContent, messageSender);
            ArrayList<List<String>>  disinformationLinks = dummyVerifyExtractedLinks(detectedLinks);
            notifyDisinformationLinks(disinformationLinks, messageContent);
        }
    }

    /**
     * Basic verification implementation that iterates through the detected message links and
     * compares against populated disinformation link database.
     * */

    private ArrayList<List<String>>  dummyVerifyExtractedLinks(ArrayList<List<String>> detectedLinks) {
        ArrayList<List<String>>  disinformationLinks = new ArrayList<>();
        for(List<String> link : detectedLinks) {
            if (disinformationLinksToIntercept.contains(link.get(0))) {
                disinformationLinks.add(link);
            }
        }
        return disinformationLinks;
    }

    /**
     * Create custom Lighthouse notification alerting user of recently detected links using the
     * android Notification API. This involves using the notification builder to create a new
     * custom notification on the app's notification channel by adding sender info to the pop up
     * notification.
     * */

    private void notifyDisinformationLinks(ArrayList<List<String>> disinformationLinks, String originalMessage) {
        if(disinformationLinks == null || disinformationLinks.size() == 0)
            return;

        String notificationMessage = "";
        String linkSender = "";
        for(List<String> link : disinformationLinks) {
            String linkInfo = link.get(1) + ": " + link.get(0);
            JSONObject writeup = new JSONObject();
            ArrayList<MessageModel> messagesContainingDomain = new ArrayList<>();
            LinkModel linkInfoListFormat = new LinkModel(
                    link.get(1),
                    link.get(0),
                    originalMessage,
                    "identifier",
                    "topline",
                    "rank",
                    0,
                    writeup,
                    messagesContainingDomain
                    );
            interceptedLinks.add(linkInfoListFormat);
            /*
            The list adapter is notified of the change in data contained by the MainActivity
            list in the UI so as to reflect any newly added data.
            */
            adapter.notifyDataSetChanged();
            notificationMessage = notificationMessage.concat(linkInfo);
            linkSender = link.get(1);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.lighthouse_logo)
                .setContentTitle("Lighthouse")
                .setContentText(linkSender + " sent you messages that may contain misleading information")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());
        notificationId++;

        Context context = getApplicationContext();
        CharSequence text = "Detected fishy link from " + linkSender;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if enabled, false otherwise.
     */
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Change Broadcast Receiver.
     * We use this Broadcast Receiver to notify the Main Activity when
     * a new notification has arrived
     * */
    public class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("json file", "new activity");
            int receivedNotificationCode = intent.getIntExtra("Notification Code",-1);
            String receivedNotificationContent = intent.getStringExtra("Notification Message");
            String receivedNotificationContentSender = intent.getStringExtra("Notification Message Sender");
            changeInterceptedNotificationInfo(receivedNotificationCode, receivedNotificationContent, receivedNotificationContentSender );
        }
    }


    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     * @return An alert dialog which leads to the notification enabling screen
     */
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }
}
