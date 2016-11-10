package com.caycek.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.widget.Toast;

import com.caycek.MainActivity;
import com.caycek.R;
import com.caycek.model.MessageData;
import com.caycek.model.PushRequest;
import com.caycek.model.RequestHeader;
import com.caycek.util.PreferenceWrapper;
import com.google.gson.Gson;
import com.google.gson.internal.ObjectConstructor;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static android.R.attr.data;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.caycek.MainActivity.FCM_API_URL;
import static com.caycek.MainActivity.FCM_MESSAGE;
import static com.caycek.MainActivity.FCM_TOPIC;

/**
 * Created by ftoptas on 10/11/16.
 */

public class CaycekUtils {

    public static void sendPushRequest(final Context context) {
        // Create a strings_key.xml file then put R.string.fcm_server_key in this file (it is in .gitignore)
        String key = "key=" + context.getString(R.string.fcm_server_key);

        RequestHeader requestHeader = new RequestHeader("application/json", key);
        MessageData messageData = new MessageData(String.format(FCM_MESSAGE, CaycekUtils.getUsername(context)));
        PushRequest pushRequest = new PushRequest("/topics/" + FCM_TOPIC, messageData);

        FaRequestManager.getInstance().makePostRequest(FCM_API_URL, modelToString(requestHeader), modelToString(pushRequest), new FaRequestManager.OnFaResponseListener() {
            @Override
            public void onResponse(FaRequestManager.FaResponse faResponse) {
                Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static String getUsername(Context context) {
        AccountManager manager = AccountManager.get(context.getApplicationContext());
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
    }

    public static long calculateDiff() {
        return System.currentTimeMillis() - PreferenceWrapper.getInstance().readLastCayTime();
    }

    private static String modelToString(Object obj){
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static String getTimeStamp(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        return simpleDateFormat.format(Calendar.getInstance().getTime());
    }

}
