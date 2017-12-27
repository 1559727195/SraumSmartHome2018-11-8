package com.massky.sraumsmarthome.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.Util.ICallback;
import com.massky.sraumsmarthome.Util.ToastUtil;
import com.massky.sraumsmarthome.service.MyService;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhu on 2017/11/16.
 */

public class NextPageActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        send_tcp_heart ();
    }

    /**
     * 发送tcp心跳包
     */
    public void send_tcp_heart () {
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

        MyService.getInstance().sraum_send_tcp(map,"sraum_beat");
    }
}
