package com.massky.sraum.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.massky.sraum.Util.ICallback;
import com.massky.sraum.Util.IConnectTcpback;
import com.massky.sraum.thread.Receive_Thread;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import static com.massky.sraum.Util.AES.Encrypt;

/**
 * Created by zhu on 2017/11/16.
 */

public class MyService extends Service {//Service 的生命周期
    private static MyService _instance;

    /**
     * ----------------TCPClient------------------------
     */
    private Timer timer;
    private TimerTask task;
    private boolean isRevFromServer;
    private Socket clicksSocket = null;//长连接Socket，

    private boolean activi_destroy;//activity有没有销毁
    private DataInputStream dataInputStream;
    private int connect_ctp = 2;//连接tcp服务器端的次数

    /**
     * ----------------TCPClient------------------------
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
    }

    /**
     * @return
     */
    public static MyService getInstance() {
        return _instance;
    }


    /**
     * ----------------TCPClient------------------------
     */

    /**
     * 连接Tcp
     */
    public void connectTCP(String wangguan_ip, final IConnectTcpback iconnect, ICallback iCallback) {
        initSocket(wangguan_ip, iconnect, iCallback);
    }

    /***
     * 断开TCP
     */
    public void  quitTCP() {
        connect_ctp = -1;
        if(clicksSocket != null)
            try {
                clicksSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    /**
     * socket初始化
     */
    private void initSocket(String wangguan_ip, final IConnectTcpback iconnect, ICallback iCallback) {
        connect_ctp--;
        clicksSocket = null;
        try {
//            clicksSocket= new Socket("192.168.169.134", 8080);

            clicksSocket = new Socket();
            SocketAddress socAddress = new InetSocketAddress(wangguan_ip, 32678);//wangguan_ip,32678
            //TcpServer 端口 32678
            clicksSocket.connect(socAddress, 5000);//此处超时间为20s--超时5s

            isRevFromServer = true;//说明已经连接到服务器端Socket,不需要在用心跳定时器
//            clicksSocket.setSoTimeout(5000);
            clicksSocket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            Receive_Thread receive_thread = new Receive_Thread(clicksSocket, "command", _instance, iCallback);//创建客户端处理线程对象
            Thread t = new Thread(receive_thread);//创建客户端处理线程
//                //
//            Message message = Message.obtain();
//            message.obj = "sraum_verifySocket";
//            hand_tcp_client_send.sendMessage(message);

            //连接tcp成功
            if (iconnect != null)
                connect_ctp = 2;
            iconnect.process();
            //
            t.start();//启动线程
            //启动接收线程
        } catch (IOException e) {//连接超时异常，重新去连接30分钟e.printStackTrace();//如果服务器端没有连，客户端超过3min，就会抛出异常
            //开启心跳每个3min,去连接
            isRevFromServer = false;//连接服务器端超时，开启定时器
//            isServerClose(clicksSocket);
            if (iconnect != null) //tcpSocket连接tcpServer连接不上去时，进入到这里
                iconnect.error(connect_ctp);
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
    public void sraum_send_tcp(final Map map, final String command_send) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Receive_Thread receive_thread = new Receive_Thread(clicksSocket, command_send,icallback);//创建客户端处理线程对象
//                Thread t = new Thread(receive_thread);//创建客户端处理线程
//                t.start();
//                list_recev_tcp.add(receive_thread);

                String json_str = new Gson().toJson(map);
                try {
//                   String aes_str =  Encrypt(json_str,"masskysraum-6206");

//                    0a4ab23ad13aac565069283aac3882e5
//                    在这里解析二维码，变成房号
                    // 密钥
                    String key = "masskysraum-6206";//masskysraum-6206
                    // 解密
                    String DeString = null;
                    try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                        DeString = Encrypt(json_str, key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    send_tcp_socket(DeString);//send tcpclient socket
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }  //

    /**
     * 发送tcpSocket数据流
     */
    public void send_tcp_socket(String tcp_client_content) {

        //获取输出流
        OutputStream outputStream = null;
        try {
            if (clicksSocket != null)
                outputStream = clicksSocket.getOutputStream(); //获取输出
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
