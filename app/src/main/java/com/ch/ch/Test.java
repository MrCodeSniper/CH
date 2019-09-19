package com.ch.ch;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * 接口继承IInterface 便于作为双方的传递媒介 之后再强转
 */
public interface Test extends android.os.IInterface {


    /**
     * stub具备服务的特性 也具备binder进程通信能力
     */
    public static abstract class Stub extends Binder implements IOperationManager {

        /**
         * 给binder设置死亡代理 当binder死亡时会收到通知
         * @param recipient
         * @param flags
         */
        @Override
        public void linkToDeath(@NonNull DeathRecipient recipient, int flags) {
            super.linkToDeath(recipient, flags);
        }

        private static final java.lang.String DESCRIPTOR = "com.ch.ch.IOperationManager";

        public Stub() {
            //binder绑定接口IInterface
            this.attachInterface(this, DESCRIPTOR);
        }

        //将ibinder对象转换为接口 在service绑定好后使用
        public static com.ch.ch.IOperationManager asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            //根据描述符从Ibinder的表中查找interface
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.ch.ch.IOperationManager))) {
                return ((com.ch.ch.IOperationManager) iin);
            }
            return new Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        /**
         * Binder.transact远程调用中包括Binder.onTransact()重写这个函数
         * 进行参数的拼接从parcel中取参数数据 并调用函数
         *
         * @param code
         * @param data
         * @param reply
         * @param flags
         * @return
         * @throws RemoteException
         */
        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            java.lang.String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_connect: {
                    data.enforceInterface(descriptor);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    connect(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_disConnect: {
                    data.enforceInterface(descriptor);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    disConnect(_arg0);
                    reply.writeNoException();
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        //函数flag位
        static final int TRANSACTION_connect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_disConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);

        private static class Proxy implements com.ch.ch.IOperationManager {

            private android.os.IBinder mRemote;//

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }
//提供服务

            @Override
            public String connect(java.lang.String mes) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                //将参数写入parcel
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(mes);
                    //开始远程调用
                    mRemote.transact(Stub.TRANSACTION_connect, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return "";
            }

            @Override
            public void disConnect(java.lang.String mes) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(mes);
                    mRemote.transact(Stub.TRANSACTION_disConnect, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }




    //因为是跨进程方法可能出现远程调用错误
    public void connect(java.lang.String mes) throws android.os.RemoteException;

    public void disConnect(java.lang.String mes) throws android.os.RemoteException;
}
