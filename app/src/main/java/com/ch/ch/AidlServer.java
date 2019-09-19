package com.ch.ch;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

public class AidlServer extends Service {


    private Messenger messenger=new Messenger(new MessengerHandler());


    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Bundle bundle=msg.getData();
                    if(bundle==null) return;
                    String data=bundle.getString("xxx");
                    LogUtils.log("message from remote:"+data);

                    Messenger messenger=msg.replyTo;
                    Message replyMsg=Message.obtain(null,1);
                    try {
                        messenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }




    /**
     * bind返回的IBinder为服务代理实现
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(intent!=null){
            Bundle bundle=intent.getBundleExtra("bundle");
            if(bundle!=null){
                String msg=bundle.getString("process");
                LogUtils.log("bundle数据："+msg);
            }
        }
        //
        //return stub;
        return messenger.getBinder();//返回包含send函数接口的ibinder
    }

    private static final String TAG = "AIDLService";

    //代理实现
    private IOperationManager.Stub stub=new IOperationManager.Stub() {
        @Override
        public String connect(String mes) throws RemoteException {
            LogUtils.log("connect:"+mes);
            return "服务端进程返回信息";
        }

        @Override
        public void disConnect(String mes) throws RemoteException {
            LogUtils.log("disconnect:"+mes);
        }
    };



}
