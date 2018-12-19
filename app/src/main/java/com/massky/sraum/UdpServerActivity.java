package com.massky.sraum;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.massky.sraum.Util.InetUtil;
import com.massky.sraum.Util.ServerBroadCastUdp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.massky.sraum.Util.AES.Decrypt;
import static com.massky.sraum.Util.AES.Encrypt;

/**
 * Created by zhu on 2017/11/10.
 */

public class UdpServerActivity extends AppCompatActivity implements
        View.OnClickListener {
    /*
  * ------------------------UDP服务器 参数------------------------
   */
    TextView label_udp;
    static DatagramSocket udpSocket = null;
    static DatagramPacket udpPacket = null;
    private boolean serVerUDP;
    public static final int DEFAULT_PORT = 43708;
    private static final int MAX_DATA_PACKET_LENGTH = 1400;//报文长度
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    private static String LOG_TAG = "WifiBroadcastActivity";
    /*

 * ------------------------UDP服务器 参数------------------------

/**
     * --------------------TCP服务器端 参数-----------------------------
     */
    ServerSocket serverSocket;//创建ServerSocket对象
    Button startButton;//发送按钮
    EditText portEditText;//端口号
    Socket clicksSocket;
    Button sendButton;//发送按钮
    EditText sendEditText;//发送消息框
    OutputStream outputStream;//创建输出数据流
    private String socketName;
    private boolean serSocFlag;//ServerSocket标志位
    public CopyOnWriteArrayList<Receive_Thread> list_recev_server = new CopyOnWriteArrayList<Receive_Thread>();
    private ListView list_show;

    private CopyOnWriteArrayList<ConcurrentHashMap> list_map;
    private ServerSocket_thread serversocket_thread;
    private Timer timer_server_tcp;
    private TimerTask task_server_tcp;
    private TextView cpu_use;
    private int rate;
    private EditText txt_send;
    private EditText txt_show;
    private Button click_send;


    private boolean activi_destroy_client;
    Socket clientSocket;//客户端socket
    private boolean isRevFromServer;
    private DataInputStream dataInputStream;//客户端接收流
    private String ip_own;
    String content;
    private EditText edit_send;
    private Button btn_send;


    //int i = 0;
    /**
     * --------------------TCP服务器端 参数-----------------------------
     */


    /**
     * 初始化UDP接收
     */
    private void initServerUDP() {
        new BroadCastUdpServer().start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_server);
        label_udp = (TextView) findViewById(R.id.label_udp);
        edit_send = (EditText) findViewById(R.id.edit_send);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        //192.168.169.217
        ip_own = InetUtil.getIPAddress(this);
        initServerUDP();
        initTcpServerListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                final String content_tuisong = edit_send.getText().toString().trim();

                Map map = new HashMap();
                map.put("result", content_tuisong);
                String key = "masskysraum-6206";//masskysraum-6206
                // 解密
                String DeString = null;
                try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                    DeString = Encrypt(new Gson().toJson(map), key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String finalDeString = DeString;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        response_to_app_tcp(finalDeString);//response to tcpCLientSocket
                    }
                }).start();
                break;
        }
    }

    /*
* ------------------------UDP服务器 参数------------------------
*/
    public class BroadCastUdpServer extends Thread {
        @Override
        public void run() {
            serVerUDP = true;
            byte[] data = new byte[1400];//报文长度
            try {
                udpSocket = new DatagramSocket(8881);//网关UDP端口号8881
                udpPacket = new DatagramPacket(data, data.length);
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
            while (serVerUDP) {

                try {
                    udpSocket.receive(udpPacket);
                } catch (Exception e) {
                }
                if (null != udpPacket.getAddress()) {
                    final String quest_ip = udpPacket.getAddress().toString();

                    final String codeString = new String(data, 0, udpPacket.getLength());
//                    final String codeString =  bytesToHexString(data,udpPacket.getLength());//将byte转换为16进制字符串
                    //在这里解析二维码，变成房号
                    // 密钥
                    String key = "masskysraum-6206";//masskysraum-6206
                    // 解密
                    try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                        content = Decrypt(codeString, key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    label_udp.post(new Runnable() {

                        @Override
                        public void run() {
                            label_udp.append("收到来自：" + quest_ip + "UDP请求。。。\n");
                            label_udp.append("请求内容：" + content + "\n\n");
                        }
                    });
                    final String ip = udpPacket.getAddress().toString()
                            .substring(1);
                    label_udp.post(new Runnable() {

                        @Override
                        public void run() {
                            label_udp.append("发送socket请求到：" + ip + "\n");
                        }
                    });
                    Map map = new HashMap();

                    map.put("ip", ip_own);

//                    doUdp(codeString,buffer,MAX_DATA_PACKET_LENGTH);//doUDP

                    // 解密
                    try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                        content = Encrypt(new Gson().toJson(map), key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //响应UDP客户端
                    new ServerBroadCastUdp(content, buffer, MAX_DATA_PACKET_LENGTH).start();
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        serVerUDP = false;
        if (udpSocket != null)
            udpSocket.close();

        /**
         * -------------------TCP服务器 参数-----------------------
         */
        serSocFlag = false;
        activi_destroy_client = true;//说明activity被销毁
        try {
            if (dataInputStream != null)//退出客户端socket时及时通知服务器端Socket，客户端socket已经断线
                dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        /**
         * -------------------TCP服务器 参数-----------------------
         */
    }


    private void initTcpServerListener() {
        /**
         * 启动服务器监听线程
         */
        serversocket_thread = new ServerSocket_thread();
        serversocket_thread.start();
    }

    /**
     * 服务器监听线程
     */
    class ServerSocket_thread extends Thread {
        public void run()//重写Thread的run方法
        {
            try {
                int port = 32678;//获取portEditText中的端口号
                serSocFlag = true;
                serverSocket = new ServerSocket(port);//监听port端口，这个程序的通信端口就是port了
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            while (serSocFlag) {
                try {
                    //监听连接 ，如果无连接就会处于阻塞状态，一直在这等着
                    Socket clicksSocket = serverSocket.accept();
                    //跑到这里就是clientSocket 连上了ServerSocket
                    addClient(clicksSocket);//添加客户端
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加客户端Socket
     *
     * @param clicksSocket
     */
    private void addClient(Socket clicksSocket) {
        socketName = clicksSocket.getRemoteSocketAddress().toString();//连接上的socket名字
        if (list_recev_server.size() < 10) {//当客户端线程小于3时,限制加入的客户端线程的数量
            Receive_Thread receive_thread = new Receive_Thread(socketName, clicksSocket);//创建客户端处理线程对象
            list_recev_server.add(receive_thread);//客户端每次打开连接，我们都会把客户端线程添加到list线程列表中
            Thread t = new Thread(receive_thread);//创建客户端处理线程

            t.start();//启动线程
            //启动接收线程

        } else {//当大于3时，就让其杀死
            try {
                clicksSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 接收线程
     */
    private class Receive_Thread implements Runnable//实现Runnable
    {
        public Handler mHandler = null;
        //        private Socket clientSocket;
        private String name;
        private InputStream inputStream;
        public boolean RecevFlag_thread;//客户端接收子线程标志位，它的生命周期随着客户端物理连接断开，或者3min心跳后，客户端
        public int add;
        private Timer timer;
        private TimerTask task;

        //虽然连接，单3min之后依然没响应，要关掉该客户端子线程
        public Receive_Thread(final String name, final Socket clientSocket1) {
            this.name = name;
            clientSocket = clientSocket1;
            RecevFlag_thread = true;
            try {
                final InputStream inputStream = clientSocket.getInputStream();
                this.inputStream = inputStream;
                //说明是哪个客户端连接上的
                runOnUiThread(new Runnable() {
                    public void run() {
//                        client_connect.setText("客户端:" + name + "已连接上");

                    }
                });

                //搞个定时器3min，后没有反应,关闭该ClientSocket
                initTimer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void run()//重写run方法
        {
            while (RecevFlag_thread)//true
            {
                try {
                    final byte[] buf = new byte[1024];
                    final int len = inputStream.read(buf);//这里阻塞,从这里开始计时如果超过3min，客户端流没有输入就关闭它,
                    if (len == -1)//判断是否断开，则说明该客户端子线程已经关闭，
                    {
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if (task != null) {
                            task.cancel();
                            task = null;
                        }
                        closeClientSocket_by_you();//关闭客户端socket
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (len != -1) {
                                final String content = new String(buf, 0, len);
//                                {
//                                    "timeStamp": "1510650707711",
//                                        "command": "sraum_verifySocket",
//                                        "user": "sraum",
//                                        "signature": "cc6cbda5d341f0dd62dec2bcf1e73481"
//                                }
//
//                                for (int i = 0; i < list_map.size(); i++) {
//                                    if (clientSocket == list_map.get(i).get("clientSocket")) {//说明是拿个socket客户端发来的消息
//
////                                        new String(buf, 0, len);
////                                        final String content = ByteUtils.bytesToHexString(buf,len);//将byte转换为16进制字符串
////                                        list_map.get(i).put("rev", content);
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//
//                                            }
//                                        });
//                                        Map map = new HashMap();
//                                        map.put("result","100");
//                                        response_to_app_tcp(new Gson().toJson(map));//response to tcpCLientSocket
//                                        break;
//                                    }
//                                }

//                                        final String content = ByteUtils.bytesToHexString(buf,len);//将byte转换为16进制字符串
//                                        list_map.get(i).put("rev", content);
                                runOnUiThread(new Runnable() {
                                    public void run() {
//                                                            //0a4ab23ad13aac565069283aac3882e5
//                                                            //在这里解析二维码，变成房号
                                        // 密钥
                                        String key = "masskysraum-6206";//masskysraum-6206
                                        // 解密
                                        String DeString = null;
                                        try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                                            DeString = Decrypt(content, key);
                                            Log.e("robin debug", "DeString:" + DeString);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        label_udp.append("tcpSocket：" + DeString + "\n");
                                    }
                                });
                                final Map map = new HashMap();
                                map.put("result", "100");
                                String key = "masskysraum-6206";//masskysraum-6206
//                                // 解密
                                String DeString = null;
                                try {
////                    content = "0a4ab23ad13aac565069283aac3882e5";
                                    DeString = Encrypt(new Gson().toJson(map), key);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                final String finalDeString = DeString;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        response_to_app_tcp(new Gson().toJson(map));//response to tcpCLientSocket
                                        response_to_app_tcp(finalDeString);//response to tcpCLientSocket
                                    }
                                }).start();
                            }
                        }
                    });

                } catch (Exception e) {
                    // TODO Auto-generated catch block
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
                        if (add > 3000 * 60) {//3min= 1000 * 60 * 3
                            try {
                                add = 0;
                                closeTimerAndClientSocket();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (!serSocFlag) {//长连接倒计时3分钟未到，TcpServer退出，要停掉该定时器，并关闭clientSocket即客户端socket
                            try {
                                closeTimerAndClientSocket();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    /**
                     * 关闭定时器和socket客户端
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
                        closeClientSocket();//关闭clientSocket即客户端socket
                    }
                };
            timer.schedule(task, 100, 1); // 1s后执行task,经过1ms再次执行
        }

        /**
         * 3min自动关闭客户端socket
         */
        private void closeClientSocket() throws IOException {
            runOnUiThread(new Runnable() {
                public void run() {
                    for (int i = 0; i < list_map.size(); i++) {
                        if (clientSocket == list_map.get(i).get("clientSocket")) {//说明是拿个socket客户端发来的消息
                            list_map.remove(i);//删除关闭的socket

                            break;
                        }
                    }
                }
            });
            RecevFlag_thread = false;
            inputStream.close();
            //遍历子线程list，删除该子接收线程Item项
            for (int j = 0; j < list_recev_server.size(); j++) {
                if (list_recev_server.get(j) == this) {//删除掉该子客户端线程
                    list_recev_server.remove(list_recev_server.get(j));
                }
            }
        }

        /**
         * 手动关闭客户端socket
         */
        private void closeClientSocket_by_you() throws IOException {
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    for (int i = 0; i < list_map.size(); i++) {
//                        if (clientSocket == list_map.get(i).get("clientSocket")) {//说明是拿个socket客户端发来的消息
//                            list_map.remove(i);//删除关闭的socket
//
//                            break;
//                        }
//                    }
//                }
//            });
//            RecevFlag_thread = false;
//            inputStream.close();
//            //遍历子线程list，删除该子接收线程Item项
//            for (int j = 0; j < list_recev_server.size(); j++) {
//                if (list_recev_server.get(j) == this) {//删除掉该子客户端线程
//                    list_recev_server.remove(list_recev_server.get(j));
//                }
//            }
        }

        /**
         * runOnUiThread
         */
        private void runOnUiThread_new(final String content) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

    }

    /**
     * response to tcpCLientSocket
     */
    private void response_to_app_tcp(String content) {
        //发什么回它什么给TcpSocket
//            Toast.makeText(UdpServerActivity.this,"content:" + content,Toast.LENGTH_SHORT).show();
        OutputStream outputStream = null;
        try {
            outputStream = clientSocket.getOutputStream();
//                byte [] data = ByteUtils.hexStringToBytes(content);//将16进制字符串转换为byte
            byte[] data = content.getBytes();
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
