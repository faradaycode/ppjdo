package com.cyclone.ppjdo;

import android.graphics.Color;
import android.support.annotation.StringDef;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by cyclone on 7/15/17.
 */

public class Config {
    public static String urlHandler = "https://data.magentamedia.co.id/";
    public static String keyDb = "NAHSAKEYDBMAMAMAO";
    public static Snackbar snackbar;

    //ferdi's code
    public static String marketings = "MARKETING";

    public static void alert(String msg, Integer duration, View view) {
        if (snackbar != null) {
            snackbar.dismiss();
        }

        snackbar = Snackbar
                .make(view, msg, Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(100);  // show multiple line
        textView.setTextColor(Color.parseColor("#FEF59F"));
        textView.setVerticalScrollBarEnabled(true);
        textView.setTextSize(13);

        if (duration > 0) {
            snackbar.setDuration(duration);
        }

        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        snackbar.setActionTextColor(Color.WHITE);

        snackbar.show();
    }
}
