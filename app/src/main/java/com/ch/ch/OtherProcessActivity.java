package com.ch.ch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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


    private void fileread(){
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                //保存的是序列化之后的数据
                String path= Environment.getExternalStorageDirectory()+"/chenhong/file.txt";
                User user=null;
                File file=new File(path);
                if(file.exists()){
                    ObjectInputStream objectInputStream=null;
                    try {
                        objectInputStream=new ObjectInputStream(new FileInputStream(file));
                        user= (User) objectInputStream.readObject();
                        LogUtils.log("拿到文件数据："+user.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }


            }
        };

        ThreadPoolManager.getInstance().execute(runnable);
    }
}
