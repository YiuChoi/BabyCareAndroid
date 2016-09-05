package com.llcwh.babycare;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * Created by 蔡小木 on 2016/9/2 0002.
 */
public class BabyApplication extends Application {

    static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        Logger.init(BuildConfig.APPLICATION_ID)                 // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(2);                // default 0
    }

    public static Context getContext() {
        return sContext;
    }
}
