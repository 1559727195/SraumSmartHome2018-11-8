package com.massky.sraumsmarthome.thread;

import android.util.Log;

import com.massky.sraumsmarthome.Main_New_Activity;
import com.massky.sraumsmarthome.Util.ICallback;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by zhu on 2017/11/16.
 */

public class BroadCastUdp extends Thread {
    private static final int MAX_DATA_PACKET_LENGTH = 1400;//报文长度
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    private String dataString;
    private DatagramSocket udpSocket;
    private  String command;
    private ICallback iCallback;
    public BroadCastUdp(String dataString, String command, ICallback iCallback) {
        this.dataString = dataString;
        this.command = command;
        this.iCallback = iCallback;
    }

    public void run() {
        DatagramPacket dataPacket = null;

        try {
            udpSocket = new DatagramSocket(8881);//接收来自服务器端端口9991发送来的数据

            dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
            byte[] data = dataString.getBytes();
//                byte[] data = ByteUtils.hexStringToBytes(dataString);//字符串转换为byte
            dataPacket.setData(data);
            dataPacket.setLength(data.length);
            dataPacket.setPort(8881);
            InetAddress broadcastAddr;
            broadcastAddr = InetAddress.getByName("255.255.255.255");
            dataPacket.setAddress(broadcastAddr);
        } catch (Exception e) {
            Log.e("robin debug", e.toString());
        }
        // while( start ){
        try {
            new ReceivBroadCastUdp(dataString,command,iCallback).start();
            udpSocket.send(dataPacket);
            udpSocket.close();
            //客户端UDP发送之后就开始监听，服务器端UDP返回数据

//				sleep(10);
        } catch (Exception e) {
            Log.e("robin debug", e.toString());
        }
        // }
    }
}
