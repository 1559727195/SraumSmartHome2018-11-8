package com.massky.sraum;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massky.sraum.Util.ICallback;
import com.massky.sraum.Util.IConnectTcpback;
import com.massky.sraum.Util.InetUtil;
import com.massky.sraum.Util.MD5Util;
import com.massky.sraum.Util.NullStringToEmptyAdapterFactory;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TCPClient;
import com.massky.sraum.Util.Timeuti;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.UDPClient;
import com.massky.sraum.activity.NextPageActivity;
import com.massky.sraum.service.MyService;

import java.io.DataInputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//UDP  ->发送对方的PORT,接收自己的PORT
public class Main_New_Activity extends AppCompatActivity implements View.OnClickListener {

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
    private Button test_tcp_login;
    private Button test_tcp_next_page;

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
        String ip = InetUtil.getIPAddress(this);//192.168.169.217
        txt_server = (TextView) findViewById(R.id.txt_server);
//        new ReceivBroadCastUdp().start();
        click_search_ip = (Button) findViewById(R.id.click_search_ip);
        click_search_ip.setOnClickListener(this);

        test_tcp_click = (Button) findViewById(R.id.test_tcp_click);
        test_tcp_click.setOnClickListener(this); //test_tcp_login

        test_tcp_login = (Button) findViewById(R.id.test_tcp_login);
        test_tcp_login.setOnClickListener(this); //test_tcp_login

        //test_tcp_next_page

        test_tcp_next_page = (Button) findViewById(R.id.test_tcp_next_page);
        test_tcp_next_page.setOnClickListener(this); //test_tcp_login
        UDPClient.activity_destroy(false);
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.click_btn:
//                send_udp("12312423542354", "");
                break;
            case R.id.click_next:
                startActivity(new Intent(Main_New_Activity.this, UdpServerActivity.class));
                break;
            case R.id.click_search_ip://command:命令标识 sraum_searchFGateway
                udp_searchGateway();
                break;
            case R.id.test_tcp_click:
                send_tcp_heart();//发送心跳
                break;
            case R.id.test_tcp_login:
                login_tcp();
                break;//通过tcpClient登录到网关
            case R.id.test_tcp_next_page:
                startActivity(new Intent(Main_New_Activity.this, NextPageActivity.class));
                break;
        }
    }

    /**
     * command:命令标识 sraum_searchFGateway
     */
    private void udp_searchGateway() {
        Map map = new HashMap();
        map.put("command", "sraum_searchGateway");
//                send_udp(new Gson().toJson(map),"sraum_searchGateway");
        UDPClient.initUdp(new Gson().toJson(map), "sraum_searchGateway", new ICallback() {
            @Override
            public void process(final Object data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        udp_sraum_setGatewayTime();
//                        ToastUtil.showToast(Main_New_Activity.this,"sraum_searchGateway_success!");
                        Map map = (Map) data;
                        String command = (String) map.get("command");
                        String content = (String) map.get("content");
                        final User user = new GsonBuilder().registerTypeAdapterFactory(
                                new NullStringToEmptyAdapterFactory()).create().fromJson(content, User.class);//json字符串转换为对象
                        if (user == null) return;
                        SharedPreferencesUtil.saveData(Main_New_Activity.this, "tcp_server_ip", user.ip);
                        init_tcp_connect();
                    }
                });
            }

            @Override
            public void error(String data) {//Socket close
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(Main_New_Activity.this, "网关断线或异常");
                    }
                });
            }//
        });
    }

    /**
     * 初始化TCP连接
     */
    private void init_tcp_connect() {
        final String ip = (String) SharedPreferencesUtil.getData(Main_New_Activity.this, "tcp_server_ip", "");
        new Thread(new Runnable() {
            @Override
            public void run() {

                MyService.getInstance().connectTCP(ip, new IConnectTcpback() {//连接tcpServer成功
                    @Override
                    public void process() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(Main_New_Activity.this, "连接TCPServer成功");
                            }
                        });
                    }

                    @Override
                    public void error(final int connect_ctp) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (connect_ctp >= 0) {//去主动，网关断线后，每隔10s去连接。
                                    // 收到异常，重连，没收到，心跳之后，第一步，再次发心跳。超时5s，再次收到异常，显示网关断线。去连网关。
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            init_tcp_connect();
                                        }
                                    }, 10000);//10s
                                } else {//退出后，界面相应变化，网关异常断线，显示断线，不能直接退出。
                                    ToastUtil.showToast(Main_New_Activity.this, "显示网关断线。去连网关");
                                }
                            }
                        });
                    }
                }, new ICallback() {

                    @Override
                    public void process(Object data) {

                    }

                    @Override
                    public void error(String data) {
                        //收到tcp服务端断线
                        init_tcp_connect(); //网关断线后，每隔10s去连接。
                    }
                });
            }
        }).start();
    }


    /**
     * command:命令标识 sraum_searchFGateway
     */
    private void udp_sraum_setGatewayTime() {

        SimpleDateFormat foo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("foo:" + foo.format(new Date()));
        String time = foo.format(new Date());
        Map map = new HashMap();
        map.put("command", "sraum_setGatewayTime");
        map.put("time", time);
//        send_udp(new Gson().toJson(map), "sraum_setGatewayTime");

//        UDPClient.activity_destroy(false);
        UDPClient.initUdp(new Gson().toJson(map), "sraum_setGatewayTime", new ICallback() {
            @Override
            public void process(Object data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(Main_New_Activity.this, "sraum_setGatewayTime_success!");
                        //去连接TCPServer
                    }
                });
            }

            @Override
            public void error(String data) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        UDPClient.activity_destroy(true);//udp线程被杀死
        super.onDestroy();
    }


    /**
     * 发送tcp心跳包
     */
    public void send_tcp_heart() {
//        for (int i = 0; i < 2; i++) {
//            try {
//                if (clicksSocket != null)
//                    clicksSocket.sendUrgentData(0xFF);
//            } catch (IOException e) {
//                e.printStackTrace();
//                //tcp服务器端有异常
//            }
//        }

        Map map = new HashMap();
        map.put("command", "sraum_beat");
//                    sraum_send_tcp(map,"sraum_verifySocket");//认证有效的TCP链接
//        TCPClient.sraum_send_tcp(map,"sraum_beat",new ICallback() {
//
//            @Override
//            public void process(Object data) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.showToast(Main_New_Activity.this, "发送心跳成功");
//                    }
//                });
//            }
//
//            @Override
//            public void error(String data) {
//
//            }
//        });

        MyService.getInstance().sraum_send_tcp(map, "sraum_beat");
    }

    /**
     * 登录网关 （App->网关）
     */

    private void login_tcp() {
        Map map = new HashMap();
        map.put("command", "sraum_login");
        map.put("number", "112233445566");
        map.put("password", "445566");
//        TCPClient.sraum_send_tcp(map,"sraum_login",new ICallback() {
//
//            @Override
//            public void process(Object data) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.showToast(Main_New_Activity.this,"登录网关成功");
//                    }
//                });
//            }
//
//            @Override
//            public void error(String data) {
//
//            }
//        });

        MyService.getInstance().sraum_send_tcp(map, "sraum_login");
    }


    /***
     *
     //认证有效的TCP链接（APP-》网关）
     * @param map
     */
    private void sraum_verifySocket(Map map) {
        map.put("command", "sraum_verifySocket");
        map.put("user", "sraum");//系统分配用户
        String time = Timeuti.getTime();
        map.put("timeStamp", time);
        map.put("signature", MD5Util.md5("sraum" + "massky_gw2_6206" + time));
//                    sraum_send_tcp(map,"sraum_verifySocket");//认证有效的TCP链接
        TCPClient.sraum_send_tcp(map, "sraum_verifySocket", new ICallback() {

            @Override
            public void process(Object data) { //

            }

            @Override
            public void error(String data) {

            }
        });
    }
}
