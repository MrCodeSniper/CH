package com.ch.ch;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class OtherProcessActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        //进程开启后会再走Application

        //USERID不会变 首先不同进程属于不同虚拟机内存是相隔的 静态变量存在虚拟机内存的方法区中
        //进程1改变了值是在自身内存的方法区改变 并不会影响其他进程
        LogUtils.log("USERID："+UserManager.USER_ID);
    }
}
