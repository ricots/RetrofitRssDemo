package com.example.josh.retrofitrssdemo;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by Josh on 6/7/2016.
 */


public class MyApplication extends Application {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        // create an InitializerBuilder
//        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
//
//        // Enable Chrome DevTools
//        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
//
//        // Enable command line interface
//        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(getApplicationContext()));
//
//        // Use the InitializerBuilder to generate an Initializer
//        Stetho.Initializer initializer = initializerBuilder.build();
//
//        // Initialize Stetho with the Initializer
//        Stetho.initialize(initializer);
//
//
//    }
}
