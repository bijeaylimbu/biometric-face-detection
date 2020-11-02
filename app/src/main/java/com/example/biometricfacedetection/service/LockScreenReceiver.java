package com.example.biometricfacedetection.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.biometricfacedetection.RecognizerActivity;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class LockScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //If the screen was just turned on or it just booted up, start your Lock Activity
        assert action != null;
        if(action.equals(ACTION_SCREEN_OFF) || action.equals(ACTION_BOOT_COMPLETED))
        {
            Intent i = new Intent(context, RecognizerActivity.class);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        }
    }
}
