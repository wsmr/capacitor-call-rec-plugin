package com.diyawanna.plugins.callrecorder;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;



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
        ),
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
        recordingsDirectory = call.getString("directory", "");

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
        // Implementation to get list of recordings
        // This would scan the recordings directory and return metadata
        JSObject ret = new JSObject();
        // Add code to get recordings and add to ret
        call.resolve(ret);
    }

    @PluginMethod
    public void deleteRecording(PluginCall call) {
        String recordingId = call.getString("id");
        // Implementation to delete a specific recording
        JSObject ret = new JSObject();
        ret.put("success", true);
        call.resolve(ret);
    }

    private boolean hasRequiredPermissions() {
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
