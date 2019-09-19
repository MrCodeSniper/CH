package com.ch.ch.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.ch.ch.IBinderPool;
import com.ch.ch.LogUtils;
import com.ch.ch.aidl.impl.ComputeImpl;
import com.ch.ch.aidl.impl.SecurityCenterImpl;

import java.util.concurrent.CountDownLatch;


/**
 * AIDL实现 主要是进行转发
 */
public class BinderPool {

    public static final String TAG="BinderPool";

    //binder tag
    public static final int BINDER_NONE=-1;
    public static final int BINDER_COMPUTE=0;
    public static final int BINDER_SECURITY=1;

    private IBinderPool mBinderPool;


    private IBinder.DeathRecipient mBinderPoolDeathRecipient=new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            LogUtils.log("死亡通知");
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient,0);
            mBinderPool=null;
            connectBinderPoolService();
        }
    };

//    CountDownLatch是一个计数器闭锁，通过它可以完成类似于阻塞当前线程的功能，
//    即：一个线程或多个线程一直等待，直到其他线程执行的操作完成
    private CountDownLatch mCountDownLatch;

    private ServiceConnection mBinderPoolConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool=IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public static class BinderPoolImpl extends IBinderPool.Stub{
        @Override
        public IBinder queryBinder(int bindercode) throws RemoteException {
            IBinder binder=null;
            switch (bindercode){
                case BINDER_SECURITY:
                    binder=new SecurityCenterImpl();
                    break;
                case BINDER_COMPUTE:
                    binder=new ComputeImpl();
                    break;
                 default:break;
            }
            return binder;
        }
    }

    /**
     * 外界暴露进行远程调用
     * @param bindercode
     * @return
     */
    public IBinder queryBinder(int bindercode){
        IBinder iBinder=null;
        if(mBinderPool!=null){
            try {
                iBinder=mBinderPool.queryBinder(bindercode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return iBinder;
    }


    private Context mContext;

    private BinderPool(Context context){
        mContext=context.getApplicationContext();
        connectBinderPoolService();
    }

    private static volatile BinderPool sInstance;


    public static BinderPool getInstance(Context context){
        if(sInstance==null){
            synchronized (BinderPool.class){
                sInstance=new BinderPool(context);
            }
        }
        return sInstance;
    }

    private synchronized void connectBinderPoolService(){
        mCountDownLatch=new CountDownLatch(1);
        Intent intent=new Intent(mContext,BinderPoolService.class);
        mContext.bindService(intent,mBinderPoolConnection,Context.BIND_AUTO_CREATE);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }







}
