package com.test.socket;

import java.io.*;
import java.net.Socket;

public class SocketClient1 {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            byte[] b = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            File f = new File("D:\\a.mp4");
            OutputStream out = new FileOutputStream(f,true);
            while(inputStream.read(b)!=-1){
                out.write(b);
                out.flush();
            }
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
