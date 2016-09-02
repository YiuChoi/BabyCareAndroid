package com.llcwh.babycare;

import android.app.Application;
import android.content.Context;

/**
 * Created by 蔡小木 on 2016/9/2 0002.
 */
public class BabyApplication extends Application {

    static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }
}
