package com.massky.sraumsmarthome.thread;

import android.os.Message;
import android.util.Log;

import com.massky.sraumsmarthome.Util.ICallback;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.massky.sraumsmarthome.R.id.txt_server;
import static com.massky.sraumsmarthome.Util.AES.Decrypt;
import static com.massky.sraumsmarthome.Util.UDPClient.udp_client_destroy;

/**
 * Created by zhu on 2017/11/16.
 */

public class ReceivBroadCastUdp extends Thread {
    private DatagramSocket udpSocket;
    DatagramPacket udpPacket = null;
    public int add;
    private Timer timer;
    private TimerTask task;
    private boolean UDPServer;
    private  String dataString;
    private  String command;
    private ICallback iCallback;
    public ReceivBroadCastUdp(String dataString, String command, ICallback iCallback) {
        this.dataString = dataString;
        this.command  = command;
        UDPServer = true;
        this.iCallback = iCallback;
    }



    @Override
    public void run() {
        //搞个定时器10s后,关闭该UDPReceverSocket

        initTimer();
        byte[] data = new byte[256];
        try {
            udpSocket = new DatagramSocket(9991);//服务器端UDP端口号，网关端口9991
            udpPacket = new DatagramPacket(data, data.length);
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        while (UDPServer) {
            try {
                if (udpSocket != null) {
                    udpSocket.receive(udpPacket);
                }

                if (udpPacket != null) {
                    if (null != udpPacket.getAddress()) {

                        UDPServer = false;
                        final String quest_ip = udpPacket.getAddress().toString();
                        final String codeString = new String(data, 0, udpPacket.getLength());
                        //0a4ab23ad13aac565069283aac3882e5
//                                                            //在这里解析二维码，变成房号
                        // 密钥
                        String key = "masskysraum-6206";//masskysraum-6206
                        // 解密
                        String DeString = null;
                        try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                            DeString = Decrypt(codeString, key);
                            Log.e("robin debug","DeString:" + DeString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Message message = Message.obtain();
                        Map map = new HashMap();
                        map.put("command", command);
                        map.put("content", DeString);
//                    message.obj = map;
//                    handler_udp.sendMessage(message);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            txt_server.append("请求内容：" + codeString + "\n\n");
//                            txt_server.append("请求内容ip：" + quest_ip + "\n\n");
//                        }
//                    });


                        iCallback.process(map);
                    }
                } else {
                    iCallback.error("");
                    UDPServer = false;
                }
                if (udpSocket != null)
                    udpSocket.close();
            } catch (IOException e) {
                iCallback.error("");
                UDPServer = false;
                if (udpSocket != null)
                    udpSocket.close();
                if (e != null)
                    e.printStackTrace();
            }
        }
    }



    /**
     * 初始化定时器
     */
    private void initTimer() {
        if (timer == null)
            timer = new Timer();
        //3min
        //关闭clientSocket即客户端socket
        if (task == null)
            task = new TimerTask() {

                @Override
                public void run() {
                    add++;
//                        Log.e("robin debug", "add: " + add);
                    if (add > 400) {//10s= 1000 * 10
                        try {
                            add = 0;
                            closeTimerAndClientSocket();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (udp_client_destroy) {//长连接倒计时3分钟未到，TcpServer退出，要停掉该定时器，并关闭clientSocket即客户端socket
                        try {
                            closeTimerAndClientSocket();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                /**
                 * 关闭定时器和socket客户端
                 *
                 * @throws IOException
                 */
                private void closeTimerAndClientSocket() throws IOException {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    if (task != null) {
                        task.cancel();
                        task = null;
                    }
                    closeUDPServerSocket();//关闭clientSocket即客户端socket
                }
            };
        timer.schedule(task, 100, 25); // 1s后执行task,经过1ms再次执行
    }

    /**
     * 关闭UDPServerSocket服务器端
     */
    private void closeUDPServerSocket() {
        UDPServer = false;
        if (udpSocket != null)
            udpSocket.close();
    }
}
