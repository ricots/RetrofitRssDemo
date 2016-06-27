package com.example.josh.retrofitrssdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Josh on 6/22/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }


}
