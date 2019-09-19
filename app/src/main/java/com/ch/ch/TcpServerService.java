package com.ch.ch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServerService extends Service {

    private boolean isServiceDestoryed=false;

    public TcpServerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ThreadPoolManager.getInstance().execute(new TcpServer());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
   return null;
    }


    private class TcpServer implements Runnable{
        @Override
        public void run() {
            ServerSocket serverSocket=null;
            try {
                serverSocket=new ServerSocket(6666);
                while (!isServiceDestoryed){
                    final Socket client=serverSocket.accept();//开始接受信息 匹配上客户端socket
                    ThreadPoolManager.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                response(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }


    private void response(Socket client) throws IOException{
        BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
        out.println("服务器返回客户端");
        while (!isServiceDestoryed){
            String str=in.readLine();
            LogUtils.log(str);
            if(str==null){
                break;
            }
            out.println("测试返回");
        }

         out.close();
        in.close();
        client.close();


    }


    @Override
    public void onDestroy() {
        isServiceDestoryed=true;
        super.onDestroy();
    }
}
