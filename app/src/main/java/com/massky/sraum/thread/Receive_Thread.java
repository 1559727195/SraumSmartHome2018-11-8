package com.massky.sraum.thread;

/**
 * Created by zhu on 2017/11/15.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.massky.sraum.Util.ICallback;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.service.MyService;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import static com.massky.sraum.Util.AES.Decrypt;
import static com.massky.sraum.Util.UDPClient.udp_client_destroy;

/**
 * 接收线程
 */
public class Receive_Thread implements Runnable//实现Runnable
{
    private Socket clientSocket;
    private InputStream inputStream;
    //客户端接收子线程标志位，它的生命周期随着客户端物理连接断开，或者3min心跳后，客户端
    public Boolean Recev_flag;
    private String command;
    private DataInputStream input;
    private Context context;
    private ICallback iCallback;
    private Timer timer;
    private TimerTask task;

    //虽然连接，单3min之后依然没响应，要关掉该客户端子线程
    public Receive_Thread(final Socket clientSocket, String command, Context _instance, ICallback iCallback) {
        this.clientSocket = clientSocket;
        this.Recev_flag = true;
        try {
            InputStream inputStream = clientSocket.getInputStream();
            this.inputStream = inputStream;
            input = new DataInputStream(inputStream);
//            dataInputStream = input;
            this.command = command;
            this.context = _instance;
            this.iCallback = iCallback;

        } catch (Exception ex) {

        }
    }

    @Override
    public void run() {
        byte[] b = new byte[1024];//new byte[10000];
        while (Recev_flag && !udp_client_destroy) {
            int length = 0;
            try {
                length = input.read(b);
                if (length == -1) {//当服务器端断开时，input.read(b)就会返回 -1，//开启心跳包每个3min 去连接，
//                    Recev_flag = false;//杀死该线程,然后
//                    String ip = (String) SharedPreferencesUtil.getData(MainActivity.this, "ip", "");
//                    initSocket(ip);//tcp客户端主动连接tcpServer有20s的超时时间
                    //遍历子线程list，删除该子接收线程Item项
                    iCallback.error("");
                    Recev_flag = false;
                    closeTimerAndClientSocket();
                } else {
                    final String Msg = new String(b, 0, length);

                    //byte转换为字符串
//                        final String Msg =	ByteUtils.bytesToHexString(b,length);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                                txt_show.setText(txt_show.getText() + Msg);
//                        }
//                    });
                    Log.v("data", Msg);
//                    // 密钥
                    String key = "masskysraum-6206";//masskysraum-6206
                    // 解密
                    String DeString = null;
                    try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                        DeString = Decrypt(Msg, key);
                        Log.e("robin debug", "DeString:" + DeString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendBroad(context, DeString);
                    initTimer();//刷新重新30s
                }

            } catch (IOException e) {
                iCallback.error("");
                Recev_flag = false;
                try {
                    closeTimerAndClientSocket();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送广播
     *
     * @param context
     * @param
     */
    private void sendBroad(Context context, String tcpreceiver) {
        Intent mIntent = new Intent("com.massky.sraumsmarthome.receivetcpsocket");
        mIntent.putExtra("tcpreceiver", tcpreceiver);
        context.sendBroadcast(mIntent);
    }

    //在这里搞一个30s的定时器，心跳30s.发一次。没有数据交互。网关断线后，每隔10s去连接。

    /**
     * 初始化定时器---心跳30s.发一次。没有数据交互。
     */
    private void initTimer() {
        try {
            closeTimerAndClientSocket();
        } catch (IOException e) {

        }
        final int[] add = {0};
        if (timer == null)
            timer = new Timer();
        //3min
        //关闭clientSocket即客户端socket
        if (task == null)
            task = new TimerTask() {
                @Override
                public void run() {
                    add[0]++;
                    Log.e("zhu", "add: " + add[0]);
                    if (add[0] > 10) {//10s= 1000 * 10
                        //10s后发送心跳包
                        add[0] = 0;
                        Map map = new HashMap();
                        map.put("command", ApiHelper.Sraum_Beat);
                        MyService.getInstance().sraum_send_tcp(map, ApiHelper.Sraum_Beat);//每隔30s发送心跳包
//                        SharedPreferencesUtil.saveData(context,"command",ApiHelper.Sraum_Beat);//flag-》标记为发送心跳包
                    } else if (udp_client_destroy) {//长连接倒计时3分钟未到，TcpServer退出，要停掉该定时器，并关闭clientSocket即客户端socket
                        try {
                            closeTimerAndClientSocket();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        timer.schedule(task, 0, 1000); // 1s后执行task,经过1ms再次执行
    }

    /**
     * 关闭定时器和socket客户端
     *
     * @throws IOException
     */
    private void closeTimerAndClientSocket() throws IOException {
        if (timer != null) { //
            timer.cancel();
            timer = null;
        }

        if (task != null) {
            task.cancel();
            task = null;
        }
//closeUDPServerSocket();//关闭clientSocket即客户端socket
    }
}
