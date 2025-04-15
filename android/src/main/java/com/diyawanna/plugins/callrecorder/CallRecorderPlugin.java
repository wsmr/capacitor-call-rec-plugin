package com.diyawanna.plugins.callrecorder;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// @CapacitorPlugin(name = "CallRecorder")
// public class CallRecorderPlugin extends Plugin {
//
//     private CallRecorder implementation = new CallRecorder();
//
//     @PluginMethod
//     public void echo(PluginCall call) {
//         String value = call.getString("value");
//
//         JSObject ret = new JSObject();
//         ret.put("value", implementation.echo(value));
//         call.resolve(ret);
//     }
// }

@CapacitorPlugin(
    name = "CallRecorder",
    permissions = {
        @Permission(
            alias = "phone",
            strings = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.READ_CALL_LOG
            }
        ) ,
        @Permission(
            alias = "storage",
            strings = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            }
        ),
        @Permission(
            alias = "microphone",
            strings = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            }
        )
    }
)
public class CallRecorderPlugin extends Plugin {

    private static final String TAG = "CallRecorderPlugin";
    private static final int REQUEST_PERMISSIONS = 1;

    private boolean isRecordingEnabled = false;
    private String recordingsDirectory = "";

    @PluginMethod
    public void initialize(PluginCall call) {
        String directory = call.getString("directory", "");

        if (directory.isEmpty()) {
            recordingsDirectory = new File(Environment.getExternalStorageDirectory(), "CallRecordings").getAbsolutePath();
        } else {
            recordingsDirectory = new File(Environment.getExternalStorageDirectory(), directory).getAbsolutePath();
        }

        // Check if we have all required permissions
        if (hasRequiredPermissions()) {
            startCallDetectionService();
            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
        } else {
            requestAllPermissions(call, "permissionsCallback");
        }
    }

    @PermissionCallback
    private void permissionsCallback(PluginCall call) {
        if (hasRequiredPermissions()) {
            startCallDetectionService();
            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
        } else {
            call.reject("Required permissions not granted");
        }
    }

    @PluginMethod
    public void enableCallRecording(PluginCall call) {
        isRecordingEnabled = call.getBoolean("enabled", true);

        // Update the service with the new setting
        Intent intent = new Intent(getContext(), CallDetectionService.class);
        intent.putExtra("RECORDING_ENABLED", isRecordingEnabled);
        getContext().startService(intent);

        JSObject ret = new JSObject();
        ret.put("enabled", isRecordingEnabled);
        call.resolve(ret);
    }

    @PluginMethod
    public void getRecordings(PluginCall call) {
        try {
            File directory = new File(recordingsDirectory);
            if (!directory.exists()) {
                JSObject ret = new JSObject();
                ret.put("recordings", new JSArray());
                call.resolve(ret);
                return;
            }

            File[] files = directory.listFiles((dir, name) -> name.startsWith("call_") && name.endsWith(".mp3"));

            JSArray recordings = new JSArray();

            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    // Parse metadata from filename
                    // Format: call_[type]_[phoneNumber]_[timestamp].mp3
                    String[] parts = fileName.split("_");
                    if (parts.length >= 4) {
                        String callType = parts[1];
                        String phoneNumber = parts[2];
                        String timestamp = parts[3].replace(".mp3", "");

                        JSObject recording = new JSObject();
                        recording.put("id", fileName);
                        recording.put("filePath", file.getAbsolutePath());
                        recording.put("phoneNumber", phoneNumber);
                        recording.put("isOutgoing", callType.equals("outgoing"));
                        recording.put("timestamp", Long.parseLong(timestamp));
                        recording.put("duration", 0); // Duration would need to be stored separately

                        recordings.put(recording);
                    }
                }
            }

            JSObject ret = new JSObject();
            ret.put("recordings", recordings);
            call.resolve(ret);
        } catch (Exception e) {
            Log.e(TAG, "Error getting recordings: " + e.getMessage());
            call.reject("Error getting recordings", e);
        }
    }

    @PluginMethod
    public void deleteRecording(PluginCall call) {
        String recordingId = call.getString("id");
        if (recordingId == null || recordingId.isEmpty()) {
            call.reject("Recording ID is required");
            return;
        }

        try {
            File file = new File(recordingsDirectory, recordingId);
            boolean deleted = file.exists() && file.delete();

            JSObject ret = new JSObject();
            ret.put("success", deleted);
            call.resolve(ret);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting recording: " + e.getMessage());
            call.reject("Error deleting recording", e);
        }
    }

    public boolean hasRequiredPermissions() {
        // Check for all required permissions
        return hasPermission(Manifest.permission.READ_PHONE_STATE) &&
               hasPermission(Manifest.permission.PROCESS_OUTGOING_CALLS) &&
               hasPermission(Manifest.permission.READ_CALL_LOG) &&
               hasPermission(Manifest.permission.RECORD_AUDIO) &&
               hasPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) &&
               hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) &&
               hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void startCallDetectionService() {
        Intent intent = new Intent(getContext(), CallDetectionService.class);
        intent.putExtra("RECORDING_ENABLED", isRecordingEnabled);
        intent.putExtra("RECORDINGS_DIRECTORY", recordingsDirectory);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getContext().startForegroundService(intent);
        } else {
            getContext().startService(intent);
        }
    }
}
