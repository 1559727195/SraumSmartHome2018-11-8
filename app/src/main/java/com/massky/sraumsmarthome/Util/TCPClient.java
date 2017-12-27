package com.massky.sraumsmarthome.Util;

import com.google.gson.Gson;
import com.massky.sraumsmarthome.thread.Receive_Thread;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.massky.sraumsmarthome.Util.AES.Encrypt;

/**
 * Created by zhu on 2017/11/15.
 */

public class TCPClient {

    /**
     * ----------------TCPClient------------------------
     */
    private Timer timer;
    private TimerTask task;
    private static boolean isRevFromServer;
    private static Socket clicksSocket = null;
    private boolean activi_destroy;//activity有没有销毁
    private DataInputStream dataInputStream;

    /**
     * ----------------TCPClient------------------------
     */

    /**
     * 连接Tcp
     */
    public static void connectTCP(String wangguan_ip,final ICallback icallback
    ,final IConnectTcpback iconnect) {
        initSocket(wangguan_ip,icallback,iconnect);
    }


    /**
     * socket初始化
     */
    private static  void initSocket(String wangguan_ip,final ICallback icallback
    ,final IConnectTcpback iconnect) {

        clicksSocket = null;
        try {
//            clicksSocket= new Socket("192.168.169.134", 8080);

            clicksSocket = new Socket();
            SocketAddress socAddress = new InetSocketAddress(wangguan_ip, 32678);//wangguan_ip,32678
            //TcpServer 端口 32678
            clicksSocket.connect(socAddress, 10000);//此处超时间为20s

            isRevFromServer = true;//说明已经连接到服务器端Socket,不需要在用心跳定时器
//            clicksSocket.setSoTimeout(5000);
            clicksSocket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
//            Receivetuisong_Thread receive_thread = new Receivetuisong_Thread(clicksSocket, "command",icallback);//创建客户端处理线程对象
//            Thread t = new Thread(receive_thread);//创建客户端处理线程
//                //
//            Message message = Message.obtain();
//            message.obj = "sraum_verifySocket";
//            hand_tcp_client_send.sendMessage(message);

              //连接tcp成功
              if (iconnect != null)
                  iconnect.process();

            //
//            t.start();//启动线程
            //启动接收线程
        } catch (IOException e) {//连接超时异常，重新去连接30分钟e.printStackTrace();//如果服务器端没有连，客户端超过3min，就会抛出异常
            //开启心跳每个3min,去连接
            isRevFromServer = false;//连接服务器端超时，开启定时器
//            isServerClose(clicksSocket);
        }
    }

    /**
     * tcp_socket发送数据
     */

    /**
     * 连接成功，去认证有效的TCP链接 （APP -》网关）
     *
     * @param
     */
    public static void sraum_send_tcp(final Map map, final String command_send,final ICallback icallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Receive_Thread receive_thread = new Receive_Thread(clicksSocket, command_send, null, icallback);//创建客户端处理线程对象
                Thread t = new Thread(receive_thread);//创建客户端处理线程
                t.start();
//                list_recev_tcp.add(receive_thread);
               String json_str =  new Gson().toJson(map);
                try {
//                   String aes_str =  Encrypt(json_str,"masskysraum-6206");

                    //0a4ab23ad13aac565069283aac3882e5
                    //在这里解析二维码，变成房号
                    // 密钥
                    String key = "masskysraum-6206";//masskysraum-6206
                    // 解密
                    String DeString = null;
                    try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                       DeString = Encrypt(json_str,key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    send_tcp_socket(DeString);//send tcpclient socket
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 发送tcpSocket数据流
     */
    public static void send_tcp_socket(String tcp_client_content) {
        //获取输出流
        OutputStream outputStream = null;
        try {
            if (clicksSocket != null)
                outputStream = clicksSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
//发送数据
        try {
            if (outputStream != null)
                outputStream.write(tcp_client_content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
