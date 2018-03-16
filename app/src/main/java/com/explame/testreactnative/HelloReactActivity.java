package com.explame.testreactnative;

import android.content.Intent;

import com.facebook.react.ReactActivity;

/**
 * Created by admin on 2018/3/15.
 */
public class HelloReactActivity extends ReactActivity {

    @Override
    protected String getMainComponentName() {
        return "navigation";
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
