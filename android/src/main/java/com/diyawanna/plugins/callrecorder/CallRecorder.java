package com.diyawanna.plugins.callrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;


//public class CallRecorder {
//
//    public String echo(String value) {
//        Log.i("Echo", value);
//        return value;
//    }
//}

public class CallRecorder extends BroadcastReceiver {
//public class CallReceiver extends BroadcastReceiver {
    private boolean isRecording = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            Log.d("CallReceiver", "Incoming call ringing...");
            // Prepare to record
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            Log.d("CallReceiver", "Call answered or outgoing...");
            // Start recording
            if (!isRecording) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, CallDetectionService.class));
                }
                isRecording = true;
            }
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            Log.d("CallReceiver", "Call ended...");
            // Stop recording
            context.stopService(new Intent(context, CallDetectionService.class));
            isRecording = false;
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//    }
}
