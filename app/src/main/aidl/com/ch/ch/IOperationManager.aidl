// IOperationManager.aidl
package com.ch.ch;

// Declare any non-default types here with import statements

interface IOperationManager {
  //提供服务

   //接收两个参数，并将运算结果返回给客户端
     Parameter operation(in Parameter parameter1 , in Parameter parameter2);
}
