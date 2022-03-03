package com.standford.ligthhouse.utility;

import android.app.ProgressDialog;
import android.content.Context;

public class Util {


    private static ProgressDialog progressDialog;

    public static void showProgress(Context context, String message) {
        if (progressDialog != null) {
            progressDialog.setMessage(message);
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        } else {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(message);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
        }
    }

    public static void dismissProgress() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }


}
