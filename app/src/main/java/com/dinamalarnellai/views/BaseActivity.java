package com.dinamalarnellai.views;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.dinamalarnellai.app.AppConfig;
import com.dinamalarnellai.app.Commons;

import java.util.Locale;

import app.dinamalarnellai.com.news.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Commons.print("Gnanaprakasam");
        Configuration config = new Configuration();
        Locale locale = config.locale;
        locale = new Locale(AppConfig.LANGUAGE);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

    }

}
