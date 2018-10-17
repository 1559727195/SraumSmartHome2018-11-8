package com.massky.sraum.thread;

import android.os.Message;
import android.util.Log;

import com.massky.sraum.MainActivity;
import com.massky.sraum.Util.ICallback;
import com.massky.sraum.Util.SharedPreferencesUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhu on 2017/11/15.
 */

public class Receivetuisong_Thread  implements Runnable//实现Runnable
{
    private Socket clientSocket;
    private InputStream inputStream;
    //客户端接收子线程标志位，它的生命周期随着客户端物理连接断开，或者3min心跳后，客户端
    private Boolean Recev_flag;
    private  String command;
    private  DataInputStream input;
    private  ICallback iCallback; //Ctrl + H 就是搜索引擎

    //虽然连接，单3min之后依然没响应，要关掉该客户端子线程
    public Receivetuisong_Thread(final Socket clientSocket, String command, ICallback icallback) {
        this.clientSocket = clientSocket;
        this.Recev_flag = true;
        this.iCallback = icallback;
        try {
            InputStream inputStream = clientSocket.getInputStream();
            this.inputStream = inputStream;
            input = new DataInputStream(inputStream);
//            dataInputStream = input;
            this.command = command;
        }catch (Exception ex) {

        }
    }

    @Override
    public void run() {
        byte[] b = new byte[1024];//new byte[10000];
        while(Recev_flag)
        {
            int length = 0;
            try {
                length = input.read(b);
                if (length == -1)  {//当服务器端断开时，input.read(b)就会返回 -1，//开启心跳包每个3min 去连接，
                    Recev_flag = false;//杀死该线程,然后
//                    String ip = (String) SharedPreferencesUtil.getData(MainActivity.this, "ip", "");
//                    initSocket(ip);//tcp客户端主动连接tcpServer有20s的超时时间
                    iCallback.error("");
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
                    if (command.equals("command")) {//保持长连接，接收推送消息
                            iCallback.process(null);
                    } else {
                        Message message = Message.obtain();

                        Map map = new HashMap();
                        map.put("command", command);
                        map.put("content_tcp_rev", Msg);
                        message.obj = map;
//                        hand_tcp_client_rev.sendMessage(message);
                        Recev_flag = false;//杀死该线程,然后
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

//    private void client_destroy() {
//        activi_destroy = true;//说明activity被销毁
//        try {
//            if (dataInputStream != null)//退出客户端socket时及时通知服务器端Socket，客户端socket已经断线
//                dataInputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * 发送tcpSocket数据流
//     */
//    public void send_tcp_socket (String  tcp_client_content) {
//        //获取输出流
//        OutputStream outputStream = null;
//        try {
//            if (clicksSocket != null)
//                outputStream = clicksSocket.getOutputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////发送数据
//        try {
//            if (outputStream != null)
//                outputStream.write(tcp_client_content.getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
