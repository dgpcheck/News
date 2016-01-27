package com.dinamalarnellai.views;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.dinamalarnellai.app.AppController;

import app.dinamalarnellai.com.news.R;

public class SplashActivity extends BaseActivity {

    private AppController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = (AppController) getApplicationContext();
        controller.setContext(this);

        if (controller.isOnline()) {
            setContentView(R.layout.activity_splash);
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {

                        Intent slideactivity = new Intent(SplashActivity.this, HomeScreenActivity.class);
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
                        startActivity(slideactivity, bndlanimation);
                        finish();
                    }
                }
            };
            timerThread.start();
        } else {
            AlertDialog.Builder alertDialogBuilder;
            AlertDialog alertDialog;
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(getResources().getString(R.string.no_network_connection));
            alertDialogBuilder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                            try {
                                android.os.Process
                                        .killProcess(android.os.Process.myPid());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }


    // Unused Codes
    /*
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/

       /*  Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        startActivity(intent);
                        finish();*/

}
