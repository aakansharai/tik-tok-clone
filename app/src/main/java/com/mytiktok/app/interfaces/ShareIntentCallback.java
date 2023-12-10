package com.mytiktok.app.interfaces;

import android.content.pm.ResolveInfo;

public interface ShareIntentCallback {
    void onResponse(ResolveInfo resolveInfo);
}
