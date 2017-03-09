package com.zearoconsulting.zearopos.presentation.view.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by saravanan on 26-08-2016.
 */
public class AlertView
{
    public static void showError(String message, Context ctx)
    {
        showAlert("Error", message, ctx);
    }

    public static void showAlert(String message, Context ctx)
    {
        showAlert("Alert", message, ctx);
    }

    public static void showAlert(String title, String message, Context ctx)
    {
        //Create a builder
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        //add buttons and listener
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        //Create the dialog
        AlertDialog ad = builder.create();
        //show
        ad.show();
    }
}