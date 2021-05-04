package com.github.chagall.notificationlistenerexample;

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
import android.database.Cursor;
import android.os.Build;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.util.Patterns;
import android.widget.Toast;

import java.util.*;
import java.util.regex.Matcher;

/**
 * MIT License
 *
 *  Copyright (c) 2016 Fábio Alves Martins Pereira (Chagall)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class  MainActivity extends AppCompatActivity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private TextView interceptedNotificationTextView;
    private int notificationId;
    private ImageChangeBroadcastReceiver imageChangeBroadcastReceiver;
    private AlertDialog enableNotificationListenerAlertDialog;
    private List<String> disinformationLinksToIntercept = Arrays.asList(
            "google.com",
            "https://www.google.com/",
            "www.google.com",
            "www.stanford.edu",
            "stanford.edu"
    );
    //private List<String> disinformationLinksToIntercept = new ArrayList<>();
    private String CHANNEL_ID = "WHATSAPP_DISINFO";
    private Cursor initialDisinfoLinks;
    private SQLDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_main);
        //loadDataBaseLinks();
        interceptedNotificationTextView
                = (TextView) this.findViewById(R.id.image_change_explanation);

        // If the user did not turn the notification listener service on we prompt him to do so
        if(!isNotificationServiceEnabled()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }

        // Finally we register a receiver to tell the MainActivity when a notification has been received
        imageChangeBroadcastReceiver = new ImageChangeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.github.chagall.notificationlistenerexample");
        registerReceiver(imageChangeBroadcastReceiver,intentFilter);
        notificationId = 0;
    }

    public void loadDataBaseLinks() {
        db = new SQLDatabaseHelper(this);
        initialDisinfoLinks = db.getDisinfoLinks();
        while (initialDisinfoLinks.moveToNext ()){
            disinformationLinksToIntercept.add(initialDisinfoLinks.getString(initialDisinfoLinks.getColumnIndex("domain")));
        }
        initialDisinfoLinks.close();
    }
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
        unregisterReceiver(imageChangeBroadcastReceiver);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Whatsapp Disinfo";
            String description =  "Whatsapp Disinfo";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Change Intercepted Notification Image
     * Changes the MainActivity image based on which notification was intercepted
     * @param notificationCode The intercepted notification code
     */
    private void changeInterceptedNotificationInfo(int notificationCode, String messageContent, String messageSender){
        if(notificationCode == NotificationListenerExampleService.InterceptedNotificationCode.WHATSAPP_CODE){
            ArrayList<List<String>> detectedLinks = extractLinks(messageContent, messageSender);
            ArrayList<List<String>>  disinformationLinks = dummyVerifyExtractedLinks(detectedLinks);
            notifyDisinformationLinks(disinformationLinks);
        }
    }

    private ArrayList<List<String>>  dummyVerifyExtractedLinks(ArrayList<List<String>> detectedLinks) {
        ArrayList<List<String>>  disinformationLinks = new ArrayList<>();
        for(List<String> link : detectedLinks) {
            if (disinformationLinksToIntercept.contains(link.get(0))) {
                disinformationLinks.add(link);
            }
        }
        return disinformationLinks;
    }

    private void alertDisinformationLinks(ArrayList<List<String>> disinformationLinks) {
        if(disinformationLinks == null || disinformationLinks.size() == 0)
            return;
        String alertMessage = "The following links might contain misleading information:\n";
        for(List<String> link : disinformationLinks) {
            alertMessage = alertMessage.concat(link.get(1) + ": " + link.get(0)  + "\n");
        }

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Disinformation Found");
        alertDialog.setMessage(alertMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void notifyDisinformationLinks(ArrayList<List<String>> disinformationLinks) {
        if(disinformationLinks == null || disinformationLinks.size() == 0)
            return;

        //String notificationMessage = "The following links might contain misleading information:\n";
        String notificationMessage = "";
        String linkSender = "";
        for(List<String> link : disinformationLinks) {
            interceptedNotificationTextView.append("\n" + link.get(1) + ": " + link.get(0)  + "\n");
            notificationMessage = notificationMessage.concat(link.get(1) + ": " + link.get(0));
            linkSender = link.get(1);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.whatsapp_logo)
                .setContentTitle("Whatsapp Disinfo")
                .setContentText("You’ve been sent messages that may contain misleading information")
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

    public void createAlert(String alertMessage, Context context) {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("alarm_message", alertMessage);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();

        }
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
     * Image Change Broadcast Receiver.
     * We use this Broadcast Receiver to notify the Main Activity when
     * a new notification has arrived, so it can properly change the
     * notification image
     * */
    public class ImageChangeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
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
