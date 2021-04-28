package com.test.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SorcketServer1 {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8888);
            while (true){
                Socket socket = server.accept();
                DealThread dealThread = new DealThread(socket);
                Thread th = new Thread(dealThread);
                th.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static  class DealThread implements Runnable{
        Socket socket;

        public DealThread(Socket s) {
            this.socket=s;
        }
        @Override
        public void run() {
            try {
                OutputStream outputStream = socket.getOutputStream();
                File f = new File("C:\\11.mp4");
                byte[] b = new byte[1024];
                FileInputStream fileInputStream = new FileInputStream(f);
                while(fileInputStream.read(b)!=-1){
                    outputStream.write(b);
                    outputStream.flush();
                }
                outputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
