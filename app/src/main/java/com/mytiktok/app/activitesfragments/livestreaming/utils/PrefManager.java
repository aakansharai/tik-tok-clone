package com.mytiktok.app.activitesfragments.livestreaming.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.mytiktok.app.activitesfragments.livestreaming.Constants;

public class PrefManager {
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }
}
