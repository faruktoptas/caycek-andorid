package com.caycek.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ftoptas on 10/11/16.
 */

public class PreferenceWrapper {

    public static final String KEY_LAST_CAY = "LAST_CAY";
    private static final String KEY_LAST_PUSH = "LAST_PUSH";


    private static PreferenceWrapper instance = new PreferenceWrapper();

    private SharedPreferences mSharedPreferences;

    public static PreferenceWrapper getInstance(){
        return instance;
    }

    public PreferenceWrapper() {

    }

    public void init(Context context){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public long readLastCayTime(){
        return mSharedPreferences.getLong(KEY_LAST_CAY, 0);
    }

    public void writeLastCayTime(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(KEY_LAST_CAY, System.currentTimeMillis());
        editor.apply();
    }

    public void writeLastPush(String pushData){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_LAST_PUSH, pushData);
        editor.apply();
    }

    public String readLastPush(){
        return mSharedPreferences.getString(KEY_LAST_PUSH, "-");
    }

}
