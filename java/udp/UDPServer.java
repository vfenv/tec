package com.tes.udp;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * TCP/IP协议是一个协议簇。里面包括很多协议的。UDP只是其中的一个。之所以命名为TCP/IP协议，因为TCP,IP协议是两个很重要的协议，就用他两命名了
 * UDP是第四层传输层协议。ip层的上一层。
 * <p>
 * TCP与UDP的主要区别：
 * TCP传输控制协议,提供的是面向连接、可靠的字节流服务。当客户和服务器彼此交换数据前，必须先在双方之间建立一个TCP连接，之后才能传输数据。
 * TCP提供超时重发，丢弃重复数据，检验数据，流量控制等功能，保证数据能从一端传到另一端。
 * <p>
 * UDP用户数据报协议，是一个简单的面向数据报的运输层协议。UDP不提供可靠性，它只是把应用程序传给IP层的数据报发送出去，但是并不能保证它们能到达目的地。
 * 由于UDP在传输数据报前不用在客户和服务器之间建立一个连接，且没有超时重发等机制，故而传输速度很快.
 * <p>
 * TCP传输效率相对较低。
 * UDP传输效率高，适用于对高速传输和实时性有较高的通信或广播通信。也适用于音频，视频和普通数据。
 * <p>
 * TCP连接只能是点到点、一对一的。
 * UDP支持一对一，一对多，多对一和多对多的交互通信。
 * <p>
 * <p>
 * 1.基于连接与无连接
 * 2.TCP要求系统资源较多，UDP较少
 * 3.UDP程序结构较简单
 * 4.流模式（TCP）与数据报模式(UDP)
 * 5.TCP保证数据正确性，UDP可能丢包
 * 6.TCP保证数据顺序，UDP不保证
 * <p>
 * <p>
 * TCP与UDP区别总结：
 * 1、TCP面向连接（如打电话要先拨号建立连接）;UDP是无连接的，即发送数据之前不需要建立连接
 * 2、TCP提供可靠的服务。也就是说，通过TCP连接传送的数据，无差错，不丢失，不重复，且按序到达;UDP尽最大努力交付，即不保证可靠交付
 * 3、TCP面向字节流，实际上是TCP把数据看成一连串无结构的字节流;UDP是面向报文的
 * UDP没有拥塞控制，因此网络出现拥塞不会使源主机的发送速率降低（对实时应用很有用，如IP电话，实时视频会议等）
 * 4、每一条TCP连接只能是点到点的;UDP支持一对一，一对多，多对一和多对多的交互通信
 * 5、TCP首部开销20字节;UDP的首部开销小，只有8个字节
 * 6、TCP的逻辑通信信道是全双工的可靠信道，UDP则是不可靠信道
 * <p>
 * <p>
 * UDP传输数据包的大小限制：
 * 1500字节被称为链路层的MTU(最大传输单元).1500-20-8=1472
 * 在普通的局域网环境下，我建议将UDP的数据控制在1472字节以下为好
 * 还有地方说还应该有个PPP的包头包尾的开销（8Bytes),那就为1492了
 * UDP 包的大小就应该是 1492 - IP头(20) - UDP头(8) = 1464(BYTES)
 * TCP 包的大小就应该是 1492 - IP头(20) - TCP头(20) = 1452(BYTES)
 * <p>
 * <p>
 * IP使用的32位（IPv4）或者128位（IPv6）位无符号数字，它是传输层协议TCP，UDP的基础。
 * InetAddress是Java 对IP地址的封装，在java.net中有许多类都使用到了InetAddress，
 * 包括 ServerSocket，Socket，DatagramSocket等等
 * <p>
 * <p>
 * Java中的DatagramPacket与DatagramSocket
 * DatagramSocket表示接受或发送数据报的套接字
 * DatagramPacket类用于处理报文，它将byte数组、目标地址、目标端口等数据包装成报文或者将报文拆卸成byte数组
 */
public class UDPServer {
    public static void main(String[] args) {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(8800);

            while (true) {
                DatagramPacket packet = new DatagramPacket(new byte[512], 512);

                datagramSocket.receive(packet); //阻塞方法，等待接收报文到packet中

                String msg = new String(packet.getData(), 0, packet.getLength(), "GB2312");

                if ("reqFile".equals(msg)) {
//                System.out.println(packet.getAddress() + ":" + packet.getPort());
//                System.out.println(msg);

                    File f = new File("C:\\11.mp4");
                    byte[] b = new byte[504];
                    FileInputStream fileInputStream = new FileInputStream(f);
                    //读取文件字节数
                    int totalSize = fileInputStream.available();
                    //设置每次传输的文件字节数量是504，使用总字节数求出包的数量
                    int totalPackageSize = (totalSize + 503) / 504;
                    System.out.println("总大小:" + totalSize + "---总包数:" + totalPackageSize);
                    byte[] s1 = ByteUtils.intToBytes(totalPackageSize);
                    int curpage = 0;
                    while (fileInputStream.read(b) != -1) {
                        curpage++;
                        byte[] s2 = ByteUtils.intToBytes(curpage);
                        byte[] data = new byte[512];
                        System.arraycopy(s1, 0, data, 0, 4);
                        System.arraycopy(s2, 0, data, 4, 4);
                        //从b字节数组的坐标0开始拷贝，拷贝到data的8位开始，504位，一共是512位
                        System.arraycopy(b, 0, data, 8, 504);
                        //将要发送的数据塞进datagramPacket中
                        packet.setData(data);
                        //发送报文到 packet所指定地址packet.getAddress(),packet.getPort()
                        datagramSocket.send(packet);
                        Long st = System.nanoTime();
                        while (true) {
                            Long mid = System.nanoTime();
                            if (mid - st > 50 * 1000) break;
                        }
                    }

                    packet.setData("sfinish".getBytes());
                    datagramSocket.send(packet);

                    System.out.println("send ok->当前数据包的包号：" + curpage);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
