package com.massky.sraumsmarthome.receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.massky.sraumsmarthome.Util.ToastUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 * 处理从tcp服务端发送过来的数据
 */
public class MyReceiver extends BroadcastReceiver {

    private Context context;
    private String action = "";
    private Timer timer;
    private TimerTask task;

    @Override
    public void onReceive(Context context, Intent intent) {////tcpSocket收到tcpServer异常时，进入到这里
        this.context = context;
       String tcpreceiver =  intent.getStringExtra("tcpreceiver");
        ToastUtil.showToast(context,"tcpreceiver:" + tcpreceiver);
    }
}

