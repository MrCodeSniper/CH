package com.ch.ch.aidl.impl;

import android.os.RemoteException;

import com.ch.ch.ICompute;

public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a+b;
    }
}
