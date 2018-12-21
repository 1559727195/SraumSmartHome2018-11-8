package com.example.jpushdemo;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.alibaba.fastjson.JSON;
import com.massky.sraum.User;
import com.massky.sraum.Util.LogUtil;
import com.massky.sraum.Util.MusicUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.SystemUtils;
import com.massky.sraum.activity.FastEditPanelActivity;
import com.massky.sraum.activity.MainGateWayActivity;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.base.Basecfragment;
import com.massky.sraum.base.Basecfragmentactivity;
import com.massky.sraum.fragment.SceneFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import cn.jpush.android.api.JPushInterface;

import static com.massky.sraum.activity.MainGateWayActivity.ACTION_SRAUM_SETBOX;
import static com.massky.sraum.activity.MainGateWayActivity.UPDATE_GRADE_BOX;


/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private Context context;
    public static final String ACTION_NOTIFICATION_OPENED_MAIN = "com.massky.sraum.action.notification_open";
    private String action = "";
    private MediaPlayer player;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        Bundle bundle = intent.getExtras();
        LogUtil.i(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
//        ToastUtil.showToast(context,"接收到推送下来的自定义消息");
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogUtil.i(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            SharedPreferencesUtil.saveData(context, "regId", regId);
            //send the Registration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            LogUtil.i(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//            ToastUtil.showToast(context,"接收到推送下来的自定义消息");
            processCustomMessage_toMainActivity(context, bundle);
            //接收下来的json数据
//            User user = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).
//                    create().fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA), User.class);
            User user = JSON.parseObject(bundle.getString(JPushInterface.EXTRA_EXTRA), User.class);
            LogUtil.i(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            //进行广播通知是否刷新设备和场景
            processCustomMessage(Integer.parseInt(user.type), bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            //接收下来的json数据
//            User user = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).
//                    create().fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA), User.class);
            User user = JSON.parseObject(bundle.getString(JPushInterface.EXTRA_EXTRA), User.class);
            LogUtil.i(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            //进行广播通知是否刷新设备和场景
            processCustomMessage(Integer.parseInt(user.type), bundle);
            LogUtil.i(TAG, Integer.parseInt(user.type) + "");
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//            User user = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).
//                    create().fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA), User.class);
            User user = JSON.parseObject(bundle.getString(JPushInterface.EXTRA_EXTRA), User.class);
            SharedPreferencesUtil.saveData(context, "usertype", user.type);
            //2代表场景信息1代表设备刷新信息
            //Intent i = null;
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);////{"type":"2"}
            JSONObject json = null;
            try {
                json = new JSONObject(extras);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String type = null;
            try {
                type = json.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //判断app进程是否存活
            if (SystemUtils.isAppAlive(context, "com.massky.sraum")) {
                switch (type) {
                    case "1":
                    case "2":
                    case "51":
                    case "53":
                        SharedPreferencesUtil.saveData(context, "tongzhi_time", 1);//门铃报警通知次数
                        break;
                    case "52":
                        //processCustomMessage_charge(context, bundle);
                        //打开自定义的Activity
                        SharedPreferencesUtil.saveData(context, "tongzhi_time", 1);
//                        if (soundPool != null) soundPool.pause(sampleId1);
                        MusicUtil.stopMusic(context, "doorbell");
                        break;
                }
//
                Log.e("robin debug", "MyReceive->context:" + context);
                if (BaseActivity.isForegrounds || Basecfragment.isForegrounds || Basecfragmentactivity.isForegrounds) {
                    common_main_tongzhi(context, bundle);//发送广播,可视
                } else if (BaseActivity.isDestroy || Basecfragment.isDestroy || Basecfragmentactivity.isDestroy) {
                    common_tongzhi(context, bundle, "create");//app退出去了，但进程没有被杀死
                    // 说明系统中不存在这个activity，或者说在后台
                } else { //在后台,去切换到前台，
                    common_tongzhi(context, bundle, "resume");
                }
            } else {
                Log.i("NotificationReceiver", "the app process is dead");
                Intent launchIntent = context.getPackageManager().
                        getLaunchIntentForPackage("com.massky.sraum");
                switch (type) {
                    case "1":
                    case "2":
                    case "51":
                    case "53":
                        SharedPreferencesUtil.saveData(context, "tongzhi_time", 1);//门铃报警通知次数
                        break;
                    case "52":
                        SharedPreferencesUtil.saveData(context, "tongzhi_time", 1);//门铃报警通知次数
//                        if (soundPool != null) soundPool.pause(sampleId1);
                        MusicUtil.stopMusic(context, "doorbell");
                        break;
                }
                launchIntent.putExtra(Constants.EXTRA_BUNDLE, bundle);
                launchIntent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(launchIntent);
            }
//            if (context instanceof MainfragmentActivity) {
//                MainfragmentActivity fca = (MainfragmentActivity) context;
//                if (user.type.equals("2")) {
//                    fca.setTabSelection(5);
//                } else if (user.type.equals("1")) {
//                    fca.setTabSelection(0);
//                } else if (user.type.equals("51")) {
//                    fca.setTabSelection(1);
//                }
//            } else {
//                //IntentUtil.startActivity(context, MainfragmentActivity.class, "usertype", user.type);
//                /*
//                *   i = new Intent(context, MainfragmentActivity.class);
//                if (i != null) {
//                    //打开自定义的Activity
//                    i.putExtras(bundle);
//                    //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    context.startActivity(i);
//                }*/
//
//
//            }
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            LogUtil.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            LogUtil.i(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            LogUtil.i(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    /**
     * 判断进程是否存活
     */
    public boolean isProcessExist(Context context, int pid) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists;
        if (am != null) {
            lists = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcess : lists) {
                if (appProcess.pid == pid) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 跳转到主界面的通知点开事件
     *
     * @param context
     * @param bundle
     */
    private void common_main_tongzhi(Context context, Bundle bundle) {
        Intent mIntent = new Intent(ACTION_NOTIFICATION_OPENED_MAIN);
        mIntent.putExtra(Constants.EXTRA_BUNDLE, bundle);
        context.sendBroadcast(mIntent);
    }


    private void common_tongzhi(Context context, Bundle bundle, String create) {
        switch (create) {
            case "create":

                break;
            case "resume":
//                    if(isProcessExist(context,android.os.Process.myPid())){
//                        Log.e("robin debug", "isProcessExist:" + context);
//                        AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
//                    }
                break;
        }
        Intent i_charge = new Intent(context, MainGateWayActivity.class);
        i_charge.putExtra(Constants.EXTRA_BUNDLE, bundle);//    launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i_charge.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i_charge);
    }

    private void processCustomMessage_toMainActivity(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);//账号已在别处登录！
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);////{"type":"2"}
        if (BaseActivity.isForegrounds || Basecfragment.isForegrounds || Basecfragmentactivity.isForegrounds) {//app可见时，才发送消息
            Intent msgIntent = new Intent(MainGateWayActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(MainGateWayActivity.KEY_MESSAGE, message);
            JSONObject extraJson = null;
            String type = "";
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    extraJson = new JSONObject(extras);
                    type = extraJson.getString("type");
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(MainGateWayActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }
            }

            if (type.equals("7")) {
                context.sendBroadcast(msgIntent);
            }
        } else {//app不可见,保存本地
//			SharedPreferencesUtil.saveData(context,"extras_login",extras);
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }
                LogUtil.e(TAG, bundle.getString(JPushInterface.EXTRA_EXTRA) + "数据");
                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]" + "1");
                    }
                } catch (JSONException e) {
                    LogUtil.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //进行广播通知是否刷新设备和场景
    private void processCustomMessage(int notifactionId, Bundle bundle) {
        Log.e("MyReceiver", "MyReceiver.notifactionId:" + notifactionId);
        action = "";
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);////{"type":"2"}
        if (notifactionId == 2) {
            if (TokenUtil.getPagetag(context).equals("3")) {
                action = SceneFragment.ACTION_INTENT_RECEIVER;
            } else if (TokenUtil.getPagetag(context).equals("6")) {
//                action = MysceneFragment.ACTION_INTENT_RECEIVER;
            }
            sendBroad(notifactionId, "");
        } else if (notifactionId == 1) {//
            JSONObject extraJson;
            if (!ExampleUtil.isEmpty(extras)) {//设备状态改变
                // 在extras中增加了字段panelid
                try {
                    extraJson = new JSONObject(extras);
                    String panelid = extraJson.getString("panelid");
                    if (panelid != null) {
//                        if (panelid.equals("")) {
//                            action = MacFragment.ACTION_INTENT_RECEIVER;
//                            sendBroad(notifactionId, "");
//                        } else {//快捷编辑
//                            action = FastEditPanelActivity.ACTION_SRAUM_FAST_EDIT;
//                            sendBroad(notifactionId, panelid);
//                        }
//                    } else {
//                        action = MacFragment.ACTION_INTENT_RECEIVER;
//                        sendBroad(notifactionId, "");
//                    }
//                        action = MacFragment.ACTION_INTENT_RECEIVER;
                        sendBroad(notifactionId, "");

                        action = FastEditPanelActivity.ACTION_SRAUM_FAST_EDIT;
                        sendBroad(notifactionId, panelid);
                    }
                } catch (JSONException e) {

                }
            }
        } else if (notifactionId == 5 || notifactionId == 3 || notifactionId == 4) {
            Map<String, Object> mapbox = new HashMap<>();
            mapbox.put("token", TokenUtil.getToken(context));
//            getBox(mapbox, notifactionId);
        } else if (notifactionId == 8) {//notifactionId = 8 ->设置网关模式，sraum_setBox
            action = ACTION_SRAUM_SETBOX;
            JSONObject extraJson;
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    extraJson = new JSONObject(extras);
                    String panelid = extraJson.getString("panelid");
                    if (extraJson.length() > 0) {
                        sendBroad(notifactionId, panelid);
                    }
                } catch (JSONException e) {

                }
            }
        } else if (notifactionId == 50) {//notifactionId = 50 ->升级网关
            action = UPDATE_GRADE_BOX;
            JSONObject extraJson;
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    extraJson = new JSONObject(extras);
                    String panelid = extraJson.getString("panelid");
                    if (extraJson.length() > 0) {
                        sendBroad(notifactionId, panelid);
                    }
                } catch (JSONException e) {

                }
            }
        } else if (notifactionId == 52) {//notifactionId = 50 ->升级网关
            //构建对象
            init_soundPool();

        }
    }

    /**
     * 门铃报警
     */
    private void init_soundPool() {
        MusicUtil.startMusic(context, 1, "doorbell");
    }

    private void sendBroad(int notifactionId, String second) {
        Intent mIntent = new Intent(action);
        mIntent.putExtra("notifactionId", notifactionId);
        mIntent.putExtra("panelid", second);
        context.sendBroadcast(mIntent);
    }

}
