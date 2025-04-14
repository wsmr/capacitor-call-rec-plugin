package com.diyawanna.plugins.callrecorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallRecorderService extends Service {
    private static final String TAG = "CallRecorderService";
    private static final int NOTIFICATION_ID = 2;
    private static final String CHANNEL_ID = "CallRecordingChannel";

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private String outputFile;
    private String phoneNumber;
    private boolean isOutgoing;
    private long startTime;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Call Recording Service",
                NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Start as a foreground service with notification
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            phoneNumber = intent.getStringExtra("PHONE_NUMBER");
            isOutgoing = intent.getBooleanExtra("IS_OUTGOING", false);
            String recordingsDirectory = intent.getStringExtra("RECORDINGS_DIRECTORY");

            startRecording(recordingsDirectory);
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
        stopRecording();
        super.onDestroy();
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Call Recording")
            .setContentText("Recording in progress...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }

    private void startRecording(String customDirectory) {
        if (isRecording) return;

        try {
            // Create directory for recordings
            File directory;
            if (customDirectory != null && !customDirectory.isEmpty()) {
                directory = new File(customDirectory);
            } else {
                directory = new File(getExternalFilesDir(null), "CallRecordings");
            }

            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create filename with timestamp and phone number
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String timestamp = dateFormat.format(new Date());
            String callType = isOutgoing ? "outgoing" : "incoming";
            String sanitizedPhoneNumber = phoneNumber != null ? phoneNumber.replaceAll("[^0-9]", "") : "unknown";

            outputFile = directory.getAbsolutePath() + "/call_" + callType + "_" + sanitizedPhoneNumber + "_" + timestamp + ".mp3";

            // Initialize media recorder
            mediaRecorder = new MediaRecorder();

            // For different Android versions, we might need different audio sources
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ - use voice communication for better quality
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            } else {
                // Older Android versions - try microphone
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            }

            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioEncodingBitRate(128000); // 128kbps
            mediaRecorder.setAudioSamplingRate(44100); // 44.1kHz
            mediaRecorder.setOutputFile(outputFile);

            // Prepare and start recording
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            startTime = System.currentTimeMillis();

            Log.d(TAG, "Started recording call to file: " + outputFile);
        } catch (IOException e) {
            Log.e(TAG, "Error starting recording: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (!isRecording) return;

        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;

            // Calculate duration
            long duration = System.currentTimeMillis() - startTime;

            // Save recording metadata
            saveRecordingMetadata(duration);

            Log.d(TAG, "Stopped recording call. Duration: " + (duration / 1000) + " seconds");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping recording: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveRecordingMetadata(long duration) {
        // Implementation to save recording metadata
        // This could write to a database or shared preferences
        // For a complete implementation, you would want to store:
        // - File path
        // - Phone number
        // - Call type (incoming/outgoing)
        // - Duration
        // - Timestamp

        // For simplicity, we're storing metadata in the filename itself
        // In a real app, you would use a database or shared preferences
    }
}
