package com.massky.sraumsmarthome.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.gson.GsonBuilder;
import com.massky.sraumsmarthome.User;
import com.massky.sraumsmarthome.Util.NullStringToEmptyAdapterFactory;
import com.massky.sraumsmarthome.Util.ToastUtil;
import java.util.Timer;
import java.util.TimerTask;
import static com.massky.sraumsmarthome.activity.LoginGateWayActivity.MESSAGE_SRAUM_LOGIN;
import static com.massky.sraumsmarthome.activity.LoginGateWayActivity.MESSAGE_SRAUM_VERIFI_SOCKET;

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
        String tcpreceiver = intent.getStringExtra("tcpreceiver");
        ToastUtil.showToast(context, "tcpreceiver:" + tcpreceiver);
        //解析json数据
        final User user = new GsonBuilder().registerTypeAdapterFactory(
                new NullStringToEmptyAdapterFactory()).create().fromJson(tcpreceiver, User.class);//json字符串转换为对象
        if (user == null) return;

        switch (user.command) {
            case "sraum_beat":
                break;
            case "sraum_verifySocket":
                processCustomMessage(context, MESSAGE_SRAUM_VERIFI_SOCKET, tcpreceiver);
                break;//认证有效的TCP链接（APP->网关）
            case "sraum_login":
                //MESSAGE_SRAUM_LOGIN
                processCustomMessage(context, MESSAGE_SRAUM_LOGIN, tcpreceiver);
                break;//登录网关 （APP - 》 网关）
            case "sraum_getGatewayInfo":

                break;//获取网关基本信息 （APP - 》 网关）
            case "sraum_pushGatewayName":
                processCustomMessage(context, ApiTcpReceiveHelper.Sraum_PushGateWayName, tcpreceiver);
                break;//推送修改网关名称（网关->APP）
            case "sraum_pushGatewayPassword":
                processCustomMessage(context, ApiTcpReceiveHelper.Sraum_PushGatewayPassword, tcpreceiver);
                break;//推送修改网关密码（网关->APP）
            case "sraum_pushAddDevice":
                processCustomMessage(context, ApiTcpReceiveHelper.Sraum_PushAddDevice, tcpreceiver);
                break;//推送添加设备（网关->APP）
            case "sraum_controlButton"://控制按钮（APP->网关）
                processCustomMessage(context, ApiTcpReceiveHelper.Sraum_Control_Button, tcpreceiver);
                break;

        }
    }

    //send msg to MyReceiver
    private void processCustomMessage(Context context
            , String action, String strcontent) {

        Intent msgIntent = new Intent(action);
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
        msgIntent.putExtra("strcontent", strcontent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
    }
}

