package com.github.chagall.notificationlistenerexample;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import java.util.List;

public class CheckRunningActivity extends Thread {
    ActivityManager am = null;
    Context context = null;

    public CheckRunningActivity(Context con){
        context = con;
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public void run(){
        Looper.prepare();

        while(true){

            List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);

            String currentRunningActivityName = taskInfo.get(0).topActivity.getClassName();

            if (currentRunningActivityName.equals("com.whatsapp")) {
                CharSequence text = "Clicked oon whatsapp";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
        //Looper.loop();
    }
}
