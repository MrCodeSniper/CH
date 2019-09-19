package com.ch.ch;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ch.ch.aidl.BinderPool;
import com.ch.ch.aidl.impl.ComputeImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private IOperationManager manager;


    private Messenger messenger;


    private Messenger getReplyMessenger=new Messenger(new MessengerHandler());


    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    LogUtils.log("message from reply:");
                    break;
            }
        }
    }

    //死亡代理
    private IBinder.DeathRecipient deathRecipient=new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            //binder死亡时的通知
            //解除死亡代理并置空
            manager.asBinder().unlinkToDeath(deathRecipient,0);
            manager=null;
            //重新绑定远程服务
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        Intent intent=new Intent(this,AidlServer.class);
        final Bundle bundle=new Bundle();
        bundle.putString("process","测试数据传输");
        intent.putExtra("bundle",bundle);
        intent.setClassName("com.ch.ch","com.ch.ch.AidlServer");

        Intent intents=new Intent(this,TcpServerService.class);
        intents.setClassName("com.ch.ch","com.ch.ch.TcpServerService");
        bindService(intents, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //链接成功将ibinder取出接口实现
                manager=IOperationManager.Stub.asInterface(service);

                messenger=new Messenger(service);//执行代理实现并缓存
                Message msg=Message.obtain(null,1);
                msg.replyTo=getReplyMessenger;//设置返回信息接受的messenger
                Bundle bundle1=new Bundle();
                bundle1.putString("xxx","xxx");
                msg.setData(bundle1);
                try {
                    messenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    //binder绑定死亡代理
                    service.linkToDeath(deathRecipient,0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                manager=null;
            }
        }, Context.BIND_AUTO_CREATE);


        remoteServiceInvoke();

    }

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            Socket socket=null;
            try {
                socket=new Socket("localhost",6666);
                PrintWriter printWriter=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (!MainActivity.this.isFinishing()){
                    String msg=br.readLine();
                    LogUtils.log("receive:"+msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


     private void  connect() throws IOException {
         ThreadPoolManager.getInstance().execute(runnable);
     }


     private void dowork(){//在子线程运行
         BinderPool binderPool=BinderPool.getInstance(this);
         IBinder iBinder=binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
         ICompute iCompute= ComputeImpl.asInterface(iBinder);//不能直接强转还要缓存
         try {
             int sum=iCompute.add(1,33);
             LogUtils.log("结果:"+sum);
         } catch (RemoteException e) {
             e.printStackTrace();
         }

     }




    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (MainActivity.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    MainActivity.this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                } else {
                    //这里就是权限打开之后自己要操作的逻辑
                }
            }
        }
    }


    public void process(View view){
       // persistToFile();
//        UserManager.USER_ID=2;
//        startActivity(new Intent(this,OtherProcessActivity.class));

//        provider();

//        try {
//            connect();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

       ThreadPoolManager.getInstance().execute(new Runnable() {
           @Override
           public void run() {
               dowork();
           }
       });
    }


    //执行时创建Binder进程 内部函数运行在binder线程中
    private void provider(){
        Uri uri=Uri.parse("content://com.ch.ch.BookProvider/book");
        //获取查询的表的游标
        Cursor cursor=getContentResolver().query(uri,new String[]{"_id","name"},null,null,null);
        while (cursor.moveToNext()){
            //列数
            Book book=new Book(cursor.getInt(0),cursor.getString(1));
            LogUtils.log(book.toString());
        }
        cursor.close();


    }


    private void sendMsg(){

    }

    private void persistToFile(){
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                User user=new User("1","陈宏");
                String path= Environment.getExternalStorageDirectory()+"/chenhong/file.txt";
                File file=new File(path);
                if(!file.exists()){
                    FileUtil.createFile(path);
                }
                ObjectOutputStream objectOutputStream=null;
                try {
                    objectOutputStream=new ObjectOutputStream(new FileOutputStream(file));
                    objectOutputStream.writeObject(user);
                    LogUtils.log("成功写入");
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if(objectOutputStream!=null){
                            objectOutputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        ThreadPoolManager.getInstance().execute(runnable);
    }


    private void remoteServiceInvoke(){
        if(manager!=null){
            try {
                String returnmsg=manager.connect("测试");
                LogUtils.log("返回信息："+returnmsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
