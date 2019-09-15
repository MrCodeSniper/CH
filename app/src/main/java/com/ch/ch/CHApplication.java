package com.ch.ch;

import android.app.Application;

//多进程 Application会重复创建
public class CHApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.log("Main Applicaiton Created");
    }
}
