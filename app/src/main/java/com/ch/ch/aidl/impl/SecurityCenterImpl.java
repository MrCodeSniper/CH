package com.ch.ch.aidl.impl;

import android.os.RemoteException;

import com.ch.ch.ISecurityCenter;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class SecurityCenterImpl extends ISecurityCenter.Stub {
    @Override
    public String encrypt(String content) throws RemoteException {
        return new StringBuilder(content).reverse().toString();
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return new StringBuilder(password).reverse().toString();
    }
}
