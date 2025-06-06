package com.diyawanna.plugins.callrecorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.media.MediaRecorder;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Objects;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class CallDetectionService extends Service {
    private static final String TAG = "CallDetectionService";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "CallRecorderChannel";

    private TelephonyManager telephonyManager;
    private CallStateListener callStateListener;
    private OutgoingCallReceiver outgoingCallReceiver;

    private boolean isRecordingEnabled = true;
    private String recordingsDirectory = "";
    private String currentPhoneNumber = "";
    private boolean isCallInProgress = false;


    private MediaRecorder recorder;

    @Override
    public void onCreate() {
        super.onCreate();


//        createNotificationChannel();
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Call Recording")
//                .setContentText("Recording call in progress...")
//                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
//                .build();
//        startForeground(1, notification);
//        startRecording();

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Call Recorder Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Start as a foreground service with notification
        startForeground(NOTIFICATION_ID, createNotification());

        // Initialize telephony manager and call state listener
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        callStateListener = new CallStateListener();
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        // Register broadcast receiver for outgoing calls
        outgoingCallReceiver = new OutgoingCallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(outgoingCallReceiver, intentFilter);
    }


//    private void startRecording() {
//        try {
//            String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CallRecordings/";
//            File dir = new File(outputPath);
//            if (!dir.exists()) dir.mkdirs();
//
//            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".3gp";
//            String filePath = outputPath + fileName;
//
//            recorder = new MediaRecorder();
//            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
//            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            recorder.setOutputFile(filePath);
//            recorder.prepare();
//            recorder.start();
//        } catch (IOException e) {
//            Log.e("CallRecorder", "Failed to start recording", e);
//        }
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            isRecordingEnabled = intent.getBooleanExtra("RECORDING_ENABLED", true);
            recordingsDirectory = intent.getStringExtra("RECORDINGS_DIRECTORY");
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // Unregister listeners and receivers
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(outgoingCallReceiver);

        super.onDestroy();

//        if (recorder != null) {
//            recorder.stop();
//            recorder.release();
//            recorder = null;
//        }
    }

//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Call Recorder Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            if (manager != null) {
//                manager.createNotificationChannel(channel);
//            }
//        }
//    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Call Recorder")
                .setContentText("Monitoring phone calls")
                .setSmallIcon(android.R.drawable.ic_menu_call)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }

    private void startCallRecording(String phoneNumber, boolean isOutgoing) {
        if (!isRecordingEnabled) return;

        currentPhoneNumber = phoneNumber;
        isCallInProgress = true;

        // Start the recording service
        Intent intent = new Intent(this, CallRecorderService.class);
        intent.putExtra("PHONE_NUMBER", phoneNumber);
        intent.putExtra("IS_OUTGOING", isOutgoing);
        intent.putExtra("RECORDINGS_DIRECTORY", recordingsDirectory);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void stopCallRecording() {
        if (!isCallInProgress) return;

        isCallInProgress = false;

        // Stop the recording service
        Intent intent = new Intent(this, CallRecorderService.class);
        stopService(intent);
    }

    // Phone state listener to detect incoming calls
    private class CallStateListener extends PhoneStateListener {
        private boolean wasRinging = false;

        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // Incoming call is ringing
                    wasRinging = true;
                    currentPhoneNumber = phoneNumber;
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // Call is active (off hook)
                    if (wasRinging) {
                        // This is an incoming call that was answered
                        startCallRecording(currentPhoneNumber, false);
                    }
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    // Call has ended or was rejected
                    if (isCallInProgress) {
                        stopCallRecording();
                    }
                    wasRinging = false;
                    break;
            }
        }
    }

    // Broadcast receiver to detect outgoing calls
    private class OutgoingCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            if (Objects.equals(intent.getAction(), Intent.ACTION_NEW_OUTGOING_CALL)) {
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                startCallRecording(phoneNumber, true);
            }
        }
    }
}
