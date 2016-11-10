package com.caycek;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caycek.util.CaycekUtils;
import com.caycek.util.PreferenceWrapper;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.caycek.util.CaycekUtils.calculateDiff;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String FCM_TOPIC = "cay";
    public static final String FCM_MESSAGE = "%s: Çay çek";
    private static final int REQ_CODE_PERM = 1001;
    public static final int COOL_DOWN = 5000;
    public static final int PERIOD = 50;

    private DonutProgress mDonutProgress;
    private ImageView mButton;
    private TextView mInfo;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC);

        mButton = (ImageView) findViewById(R.id.btnSend);
        mButton.setOnClickListener(this);

        mDonutProgress = (DonutProgress) this.findViewById(R.id.donut_progress);
        mInfo = (TextView) this.findViewById(R.id.tvInfo);
        initTimer();
        mInfo.setText(PreferenceWrapper.getInstance().readLastPush());
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(refreshListener, new IntentFilter("refresh"));
    }


    private void initTimer() {
        mCountDownTimer = new CountDownTimer(COOL_DOWN + PERIOD, PERIOD) {
            @Override
            public void onTick(long l) {
                long diff = calculateDiff();
                int progress = (int) (diff / 20);
                mDonutProgress.setProgress(progress);
                if (progress >= 1000) {
                    endTimer();
                }
            }

            @Override
            public void onFinish() {
                Log.v("asd", "finis");
                endTimer();
            }
        };
    }

    private void startTimer() {
        PreferenceWrapper.getInstance().writeLastCayTime();
        mButton.setEnabled(false);
        mButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cay_disabled));
        mCountDownTimer.start();
        mDonutProgress.setTextSize(0);
    }

    private void endTimer() {
        mCountDownTimer.cancel();
        mButton.setEnabled(true);
        mButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cay));
        mDonutProgress.setProgress(0);
    }


    private BroadcastReceiver refreshListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mInfo != null) {
                mInfo.setText(PreferenceWrapper.getInstance().readLastPush());
            }
        }
    };

    @Override
    public void onClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, REQ_CODE_PERM);
        } else {
            CaycekUtils.sendPushRequest(this);
            startTimer();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_PERM:
                if (grantResults.length > 0) {
                    CaycekUtils.sendPushRequest(this);
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCountDownTimer != null && calculateDiff() < COOL_DOWN) {
            mCountDownTimer.start();
        }
        mInfo.setText(PreferenceWrapper.getInstance().readLastPush());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCountDownTimer.cancel();
    }
}
