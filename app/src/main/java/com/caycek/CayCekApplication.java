package com.caycek;

import android.app.Application;

import com.caycek.util.PreferenceWrapper;

/**
 * Created by ftoptas on 10/11/16.
 */

public class CayCekApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceWrapper.getInstance().init(getApplicationContext());
    }
}
