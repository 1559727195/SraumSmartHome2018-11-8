package com.massky.sraum;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massky.sraum.Util.InetUtil;
import com.massky.sraum.Util.MD5Util;
import com.massky.sraum.Util.NullStringToEmptyAdapterFactory;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.Timeuti;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//UDP  ->发送对方的PORT,接收自己的PORT
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int MAX_DATA_PACKET_LENGTH = 1400;//报文长度
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    private Button click_btn;
    private Button click_next1;
    private TextView txt_server;
    private Button click_search_ip;


    /**
     * ----------------TCPClient------------------------
     */
    private Timer timer;
    private TimerTask task;
    private boolean isRevFromServer;
    private EditText txt_send;
    private EditText txt_show;
    private Button click_send;
    private Socket clicksSocket;
    private boolean activi_destroy;//activity有没有销毁
    private DataInputStream dataInputStream;
    private Button test_tcp_click;

    /**
     * ----------------TCPClient------------------------
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        click_btn = (Button) findViewById(R.id.click_btn);//click_next
        click_btn.setOnClickListener(this);

        click_next1 = (Button) findViewById(R.id.click_next);//click_next
        click_next1.setOnClickListener(this);
//        new BroadCastUdp("021001000021F403").start();
       String ip =  InetUtil.getIPAddress(this);//192.168.169.217
        txt_server = (TextView) findViewById(R.id.txt_server);
//        new ReceivBroadCastUdp().start();
        click_search_ip = (Button) findViewById(R.id.click_search_ip);
        click_search_ip.setOnClickListener(this);
//        Map map = new HashMap();
//        map.put("command","sraum_searchGateway");
//        send_udp(new Gson().toJson(map),"sraum_searchGateway");

        test_tcp_click = (Button) findViewById(R.id.test_tcp_click);
        test_tcp_click.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.click_btn:
                send_udp("12312423542354", "");
                break;
            case R.id.click_next:
                startActivity(new Intent(MainActivity.this,UdpServerActivity.class));
                break;
            case R.id.click_search_ip://command:命令标识 sraum_searchFGateway
                Map map = new HashMap();
                map.put("command","sraum_searchGateway");
                send_udp(new Gson().toJson(map),"sraum_searchGateway");
                break;
            case R.id.test_tcp_click:
                send_tcp_heart ();
                break;
        }
    }

    private void send_udp(String udp_content, String command) {
        new BroadCastUdp(udp_content,command).start();
    }


    public class BroadCastUdp extends Thread {
        private String dataString;
        private DatagramSocket udpSocket;
        private  String command;
        public BroadCastUdp(String dataString, String command) {
            this.dataString = dataString;
            this.command = command;
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
                udpSocket.send(dataPacket);
                udpSocket.close();
                //客户端UDP发送之后就开始监听，服务器端UDP返回数据

                new ReceivBroadCastUdp(dataString,command).start();

//				sleep(10);
            } catch (Exception e) {
                Log.e("robin debug", e.toString());
            }
            // }
        }
    }


    /**
     * 接收来自UDP服务器端发送过来,UDP 通信是应答模式，你发我收
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
        public ReceivBroadCastUdp(String dataString, String command) {
            this.dataString = dataString;
            this.command  = command;
            UDPServer = true;
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
                    if (udpSocket != null){
                        udpSocket.receive(udpPacket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (udpPacket != null) {
                    if (null != udpPacket.getAddress()) {
                        //02 1001 0012 62a001ff1008 313030385352677700000000643603
//                        String codeString = ByteUtils.bytesToHexString(data, udpPacket.getLength());
//                        if (codeString.length() > 22) {
//                            String wanggguan_mac = codeString.substring(10, 22);//62a001ff1008
//                            Log.e("zhu", "wanggguan_mac:" + wanggguan_mac);
//                            Message message = new Message();
//                            message.obj = wanggguan_mac;
//                        }

                        UDPServer = false;
                        final String quest_ip = udpPacket.getAddress().toString();
                        final String codeString = new String(data, 0, udpPacket.getLength());
                        Message message = Message.obtain();
                        Map map = new HashMap();
                        map.put("command",command);
                        map.put("content",codeString);
                        message.obj = map;
                        handler_udp.sendMessage(message);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txt_server.append("请求内容：" + codeString + "\n\n");
                                txt_server.append("请求内容ip：" + quest_ip + "\n\n");
                            }
                        });
                    }
                } else {
                    UDPServer = false;
                }
            }
            if (udpSocket != null)
            udpSocket.close();
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
                        } else if (activi_destroy) {//长连接倒计时3分钟未到，TcpServer退出，要停掉该定时器，并关闭clientSocket即客户端socket
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




    Handler handler_udp = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Map map = (Map) msg.obj;
            String command = (String) map.get("command");
            String content = (String) map.get("content");
           final  User user = new GsonBuilder().registerTypeAdapterFactory(
                    new NullStringToEmptyAdapterFactory()).create().fromJson(content, User.class);//json字符串转换为对象
         switch (command) {
                case "sraum_searchGateway"://搜索网关信息(APP -> 网关)
                    send_searchGateway("");
                    break;
                case "sraum_setGatewayTime" ://设置时间成功
                    //4.TCP协议命令
                    //认证有效的TCP链接 （APP-》网关）

                    //处理通过UDP拿到网关IP，用TCP连接网关
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            initSocket(user.ip);
                        }
                    }).start();
                    SharedPreferencesUtil.saveData(MainActivity.this,"ip",user.ip);
                    break;//同步网关时间（APP -> 网关）
            }
        }
    };

    /**
     * 搜索网关信息(APP -> 网关)
     * @param dataString
     */
    private void  send_searchGateway(String dataString) {
        Log.e("robin debug","codeString:" + dataString);
        SimpleDateFormat foo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("foo:" + foo.format(new Date()));
        String time = foo.format(new Date());
        Map map = new HashMap();
        map.put("command", "sraum_setGatewayTime");
        map.put("time", time);
        send_udp(new Gson().toJson(map), "sraum_setGatewayTime");
    }


    @Override
    protected void onDestroy() {
        client_destroy();
        super.onDestroy();
    }


    /**
     * ----------------TCPClient------------------------
     */
    /**
     * 处理通过UDP拿到网关IP，用TCP连接网关
     */
//    Handler handler_udp = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            //wangguan_ip
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    activi_destroy = false;//说明activity被创建
//                    initSocket();
//                }
//            }).start();
//
//        }
//    };


    /**
     * socket初始化
     */
    private void initSocket(String wangguan_ip) {
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
            Receivetuisong_Thread receive_thread =  new Receivetuisong_Thread(clicksSocket,"command");//创建客户端处理线程对象
            Thread t = new Thread(receive_thread);//创建客户端处理线程
//                //
                Message message = Message.obtain();
                message.obj = "sraum_verifySocket";
                hand_tcp_client_send.sendMessage(message);

            //
            t.start();//启动线程
            //启动接收线程
        } catch (IOException e) {//连接超时异常，重新去连接30分钟e.printStackTrace();//如果服务器端没有连，客户端超过3min，就会抛出异常
            //开启心跳每个3min,去连接
            isRevFromServer = false;//连接服务器端超时，开启定时器
            isServerClose(clicksSocket);
        }
    }

    /**
     * 初始化定时器
     */
    private  int Add = 0;
    private void initTimer() {
        if (timer == null)
            timer = new Timer();
        //3min
        //关闭clientSocket即客户端socket
        if (task != null) {
            task.cancel();  //将原任务从队列中移除
            task = null;
            initTimeTask();
        } else {
            initTimeTask();//初始化TimeTask
        }
        timer.schedule(task, 100, 1); // 1s后执行task,经过1000ms再次执行
    }

    /**
     * 初始化TimeTask
     */
    private void initTimeTask() {
        task = new TimerTask() {
            @Override
            public void run() {

                if (isRevFromServer || activi_destroy) {//说明从服务器端接收正常了，关闭长连接定时器
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    if (task != null) {
                        task.cancel();
                        task = null;
                    }
                } else {
                    // ConnectToServer();//从服务器端断开两分钟后，每两分钟主动去连接服务器端Server
                    String ip = (String) SharedPreferencesUtil.getData(MainActivity.this, "ip", "");
                    initSocket(ip);//tcp客户端主动连接tcpServer有20s的超时时间
                }
            }
        };
    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     * @param socket
     * @return
     */
    public  void isServerClose(Socket socket){
//            socket.sendUrgentData(0xFF)
        initTimer();//开启心跳
    }

    /**
     *
     * 接收线程
     *
     */
    private  class Receive_Thread implements Runnable//实现Runnable
    {
        private  Socket clientSocket;
        private InputStream inputStream;
        //客户端接收子线程标志位，它的生命周期随着客户端物理连接断开，或者3min心跳后，客户端
        private Boolean Recev_flag;
        private  String command;

        //虽然连接，单3min之后依然没响应，要关掉该客户端子线程
        public Receive_Thread(final Socket clientSocket, String command) {
            this.clientSocket = clientSocket;
            this.Recev_flag = true;
            try {
                InputStream inputStream = clientSocket.getInputStream();
                this.inputStream = inputStream;
                DataInputStream input = new DataInputStream(inputStream);
                dataInputStream = input;
                this.command = command;
            } catch (Exception ex) {

            }
        }

        @Override
        public void run() {
            byte[] b = new byte[1024];//new byte[10000];
            while(Recev_flag && !activi_destroy)
            {
                int length = 0;
                try {
                    length = dataInputStream.read(b);
                    if (length == -1)  {//当服务器端断开时，input.read(b)就会返回 -1，//开启心跳包每个3min 去连接，
                        Recev_flag = false;//杀死该线程,然后
                        String ip = (String) SharedPreferencesUtil.getData(MainActivity.this, "ip", "");
                        initSocket(ip);//tcp客户端主动连接tcpServer有20s的超时时间
                    } else {
                        final String Msg = new String(b, 0, length);

                        //byte转换为字符串
//                        final String Msg =	ByteUtils.bytesToHexString(b,length);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                txt_show.setText(txt_show.getText() + Msg);
                            }
                        });
                        Log.v("data", Msg);
                        if (command.equals("command")) {//保持长连接，接收推送消息

                        } else {
                            Message message = Message.obtain();

                            Map map = new HashMap();
                            map.put("command", command);
                            map.put("content_tcp_rev", Msg);
                            message.obj = map;
                            hand_tcp_client_rev.sendMessage(message);
                            Recev_flag = false;//杀死该线程,然后
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     *
     * 接收线程--保持长连接，接收网关推送
     *
     */
    private  class Receivetuisong_Thread implements Runnable//实现Runnable
    {
        private  Socket clientSocket;
        private InputStream inputStream;
        //客户端接收子线程标志位，它的生命周期随着客户端物理连接断开，或者3min心跳后，客户端
        private Boolean Recev_flag;
        private  String command;

        //虽然连接，单3min之后依然没响应，要关掉该客户端子线程
        public Receivetuisong_Thread(final Socket clientSocket, String command) {
            this.clientSocket = clientSocket;
            this.Recev_flag = true;
            try {
                InputStream inputStream = clientSocket.getInputStream();
                this.inputStream = inputStream;
                DataInputStream input = new DataInputStream(inputStream);
                dataInputStream = input;
                this.command = command;
            }catch (Exception ex) {

            }
        }

        @Override
        public void run() {
            byte[] b = new byte[1024];//new byte[10000];
            while(Recev_flag && !activi_destroy)
            {
                int length = 0;
                try {
                    length = dataInputStream.read(b);
                    if (length == -1)  {//当服务器端断开时，input.read(b)就会返回 -1，//开启心跳包每个3min 去连接，
                        Recev_flag = false;//杀死该线程,然后
                        String ip = (String) SharedPreferencesUtil.getData(MainActivity.this, "ip", "");
                        initSocket(ip);//tcp客户端主动连接tcpServer有20s的超时时间
                    } else {
                        final String Msg = new String(b, 0, length);

                        //byte转换为字符串
//                        final String Msg =	ByteUtils.bytesToHexString(b,length);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                txt_show.setText(txt_show.getText() + Msg);
                            }
                        });
                        Log.v("data", Msg);
                        if (command.equals("command")) {//保持长连接，接收推送消息

                        } else {
                            Message message = Message.obtain();

                            Map map = new HashMap();
                            map.put("command", command);
                            map.put("content_tcp_rev", Msg);
                            message.obj = map;
                            hand_tcp_client_rev.sendMessage(message);
                            Recev_flag = false;//杀死该线程,然后
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void client_destroy() {
        activi_destroy = true;//说明activity被销毁
        try {
            if (dataInputStream != null)//退出客户端socket时及时通知服务器端Socket，客户端socket已经断线
                dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送tcpSocket数据流
     */
    public void send_tcp_socket (String  tcp_client_content) {
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

    /**
     * 发送tcp心跳包
     */
    public void send_tcp_heart () {
        for (int i = 0; i < 2; i++) {
            try {
                if (clicksSocket != null)
                    clicksSocket.sendUrgentData(0xFF);
            } catch (IOException e) {
                e.printStackTrace();
                //tcp服务器端有异常
            }
        }
    }

    /**
     * ----------------TCPClient------------------------
     */

    /**
     * deal tcpclient connecting....
     */
    Handler hand_tcp_client_send = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String command_tcp = (String) msg.obj;
            final Map map = new HashMap();
            switch (command_tcp) {
                case "sraum_verifySocket" ://连接成功，去认证有效的TCP链接 （APP -》网关）
                    map.put("command", "sraum_verifySocket");
                    map.put("user", "sraum");//系统分配用户
                    String time = Timeuti.getTime();
                    map.put("timeStamp", time);
                    map.put("signature", MD5Util.md5("sraum" + "massky_gw2_6206" + time));
                    sraum_send_tcp(map,"sraum_verifySocket");//认证有效的TCP链接
                    break;
                case "sraum_beat":
                    map.put("command", "sraum_beat");
                    sraum_send_tcp(map,"sraum_beat");
                    break;
                case "sraum_login":
                    map.put("command", "sraum_login");
                    map.put("number", "112233445566");
                    map.put("password", "445566");
                    sraum_send_tcp(map,"sraum_login");
                    break;
            }

            //认证有效的TCP链接（APP-》网关）
            //之后，发送心跳 （APP-》网关）
        }
    };




    /**
     * deal tcpclient connecting....
     */
    Handler hand_tcp_client_rev = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Map map = (Map) msg.obj;
            String command = (String) map.get("command");
            String content = (String) map.get("content_tcp_rev");
            final  User user = new GsonBuilder().registerTypeAdapterFactory(
                    new NullStringToEmptyAdapterFactory()).create().fromJson(content, User.class);//json字符串转换为对象
            switch (user.result) {
                case "100":
                    switch (command) {
                        case "sraum_verifySocket":
                            sraum_tcp_rev_success ("sraum_beat");
                            break;
                        case "sraum_beat" ://去登陆
                            sraum_tcp_rev_success ("sraum_login");
                            break;
                    }

                    break;
                case "101":
                    //登录失败
                    break;
                case "102":

                    break;
                case "103":

                    break;
            }


            //认证有效的TCP链接（APP-》网关）
            //之后，发送心跳 （APP-》网关）
        }
    };



    /**
     * 连接成功，去认证有效的TCP链接 （APP -》网关）
     * @param sraum_beat
     */
    private void sraum_tcp_rev_success(String sraum_beat) {
            Message message = Message.obtain();
            message.obj = sraum_beat;
            hand_tcp_client_send.sendMessage(message);
    }


    /**
     * 连接成功，去认证有效的TCP链接 （APP -》网关）
     * @param
     */
    private void sraum_send_tcp(final Map map, final String command_send) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Receive_Thread receive_thread =  new Receive_Thread(clicksSocket,command_send);//创建客户端处理线程对象
                Thread t = new Thread(receive_thread);//创建客户端处理线程
                t.start();
                send_tcp_socket(new Gson().toJson(map));//send tcpclient socket
            }
        }).start();
    }

}
