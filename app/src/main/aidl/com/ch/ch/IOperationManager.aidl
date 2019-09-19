// IOperationManager.aidl
package com.ch.ch;

// Declare any non-default types here with import statements

interface IOperationManager {
  //提供服务
    String connect(String mes);
     void disConnect(String mes);
}
