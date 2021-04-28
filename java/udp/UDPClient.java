package com.tes.udp;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这个客户端只是个demo，没处理丢包的情况。只是利用了RandomAccessFile，随机访问这个类的seek指针，在包所在的未知写入内容。
 * <p>
 * 实际应该设计二种：
 * 1种是视频等不怕丢包的文件，只判断当前包的包号，有一个地方记录包号，新接收到的包号比之前的大就播放，小就丢弃，其余正常播放，这样即可。
 * 2是文件传输，需要根据总包数，已经收到的包，查找丢的包，或者自己写个类似tcp的协议，客户端收到一个包，通知服务端，成功，再发送第二个包，这样不如用tcp了
 */
public class UDPClient {

    static ExecutorService service = new ThreadPoolExecutor(20, 200, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));

    static AtomicInteger size = new AtomicInteger(0);//利用原子属性计数

    static BlockingQueue queue = new ArrayBlockingQueue(1024000);

//    static RandomAccessFile file;

    static AtomicInteger writesize = new AtomicInteger(0);//记录已经写完的

    static class Consumer implements Runnable {
        BlockingQueue queue = null;

        public Consumer(BlockingQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                File f = new File("D:\\bb.mp4");
                if (f.exists()) {
                    f.delete();
                    f.createNewFile();
                }
                RandomAccessFile file = new RandomAccessFile(f, "rw");
                int allSize = 0;
                while (true) {
                    //阻塞，和while-true配合最好是使用阻塞的，不然如果使用非阻塞的poll返回null会吃内存。导致崩溃
                    Object obj = queue.take();
                    if (obj != null) {
                        try {
                            byte[] data = (byte[]) obj;
                            if (data != null && data.length > 8) {
                                allSize = ByteUtils.bytesToint(Arrays.copyOfRange(data, 0, 4));
                                int packageNo = ByteUtils.bytesToint(Arrays.copyOfRange(data, 4, 8));
                                byte[] fileData = Arrays.copyOfRange(data, 8, data.length);

                                int seek = (packageNo - 1) * 504;
                                file.seek(seek);
                                file.write(fileData, 0, fileData.length);

                                writesize.incrementAndGet();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (writesize.intValue() == allSize) {
                        file.close();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws Exception {
        Thread thread = new Thread(new Consumer(queue));
        thread.start();

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setReceiveBufferSize(1024 * 1024 * 200);

            byte[] b = "reqFile".getBytes("GB2312");
            InetAddress serverip = InetAddress.getByName("127.0.0.1");
            DatagramPacket packet = new DatagramPacket(b, b.length, serverip, 8800);
            socket.send(packet);

            while (true) {
                DatagramPacket inPacket = new DatagramPacket(new byte[512], 512);
                socket.receive(inPacket);//阻塞接包
                String s = new String(inPacket.getData(), 0, packet.getLength(), "GB2312");
                if ("sfinish".equals(s)) {
                    break;
                }
                service.submit(() -> {
                    if (inPacket.getData() != null) {
                        int i1 = size.incrementAndGet();
                        byte[] data = inPacket.getData();
                        if (data != null && data.length > 8) {
                            try {
                                queue.put(data);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("总量" + i1);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
        System.exit(0);
    }
}
