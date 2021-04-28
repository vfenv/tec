package com.tes.udp;

/**
 * @descreption ByteUtils
 */
public class ByteUtils {
    public static byte[] intToBytes(int num) {
        //byte[] array = ByteBuffer.allocate(4).putInt(pksize).array(); 这个是最大位在第一位，可以直接转
        byte[] bytes = new byte[4];
        byte b1 = (byte) (num & 0xff);
        byte b2 = (byte) ((num & 0xff00) >> 8);
        byte b3 = (byte) ((num & 0xff0000) >> 16);
        byte b4 = (byte) ((num & 0xff000000) >> 24);
        bytes[0] = b1;
        bytes[1] = b2;
        bytes[2] = b3;
        bytes[3] = b4;
        return bytes;
    }

    public static int bytesToint(byte[] bytes) {
        if (bytes == null || bytes.length > 4) {
            throw new RuntimeException("byte errors");
        }
        int sum = 0;
        for (int i = 0; i < bytes.length; i++) {
            sum += (bytes[i] & 0XFF) << 8 * i;
        }
        return sum;
    }
}
