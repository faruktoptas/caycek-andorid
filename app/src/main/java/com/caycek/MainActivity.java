package com.caycek;

import android.*;
import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.jar.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String FCM_TOPIC = "cay";
    public static final String FCM_MESSAGE = "%s: Çay çek";
    private static final int REQ_CODE_PERM = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC);
        findViewById(R.id.btnSend).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, REQ_CODE_PERM);
        } else {
            sendPush();
        }
    }

    private void sendPush() {
        // Create a strings_key.xml file then put R.string.fcm_server_key in this file (it is in .gitignore)
        String key = "key=" + getString(R.string.fcm_server_key);
        try {
            JSONObject headers = new JSONObject();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", key);


            JSONObject data = new JSONObject();
            data.put("message", String.format(FCM_MESSAGE, getUsername()));
            JSONObject body = new JSONObject();
            body.put("to", "/topics/" + FCM_TOPIC);
            body.put("data", data);

            FaRequestManager.getInstance().makePostRequest(FCM_API_URL, headers.toString(), body.toString(), new FaRequestManager.OnFaResponseListener() {
                @Override
                public void onResponse(FaRequestManager.FaResponse faResponse) {
                    Toast.makeText(MainActivity.this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String getUsername() {
        AccountManager manager = AccountManager.get(this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQ_CODE_PERM:
                if (grantResults.length > 0){
                    sendPush();
                }
        }
    }
}
