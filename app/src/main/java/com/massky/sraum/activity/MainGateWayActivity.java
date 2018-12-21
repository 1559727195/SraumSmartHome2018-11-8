package com.massky.sraum.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.alibaba.fastjson.JSON;
import com.dialog.CommonData;
import com.dialog.ToastUtils;
import com.example.jpushdemo.Constants;
import com.example.jpushdemo.ExampleUtil;
import com.example.jpushdemo.Logger;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.ipcamera.demo.BridgeService;
import com.larksmart7618.sdk.communication.tools.commen.ToastTools;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.LogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Util.UpdateManager;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.App;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.Utils.NetUtils;
import com.massky.sraum.Utils.VersionUtil;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.fragment.HomeFragment;
import com.massky.sraum.fragment.MineFragment;
import com.massky.sraum.fragment.SceneFragment;
import com.massky.sraum.permissions.RxPermissions;
import com.yaokan.sdk.api.YkanSDKManager;
import com.yaokan.sdk.ir.InitYkanListener;
import com.yaokan.sdk.utils.Utility;
import com.yaokan.sdk.wifi.DeviceManager;
import com.yaokan.sdk.wifi.GizWifiCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import vstc2.nativecaller.NativeCaller;

import static com.example.jpushdemo.MyReceiver.ACTION_NOTIFICATION_OPENED_MAIN;


/**
 * Created by zhu on 2018/1/2.
 */

public class MainGateWayActivity extends BaseActivity implements InitYkanListener {

    private HomeFragment fragment1;
    private SceneFragment fragment2;
    private MineFragment fragment3;
    private Fragment currentFragment;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static String ACTION_SRAUM_SETBOX = "ACTION_SRAUM_SETBOX";//notifactionId = 8 ->设置网关模式，sraum_setBox
    public static String UPDATE_GRADE_BOX = "com.massky.sraum.update_grade_box";
    private int init_jizhiyun;//机智云index
    private DialogUtil dialogUtil;

    public static final String MESSAGE_RECEIVED_FROM_ABOUT_FRAGMENT = "com.massky.sraum.from_about_fragment";
    private static final int TONGZHI_APK_UPGRATE = 0x0012;


    PopupWindow popupWindow;
    //    private RelativeLayout addmac_id, addscene_id, addroom_id;
    //主要保存当前显示的是第几个fragment的索引值

    private long exitTime = 0;
    private Button checkbutton_id, qxbutton_id;
    private TextView dtext_id, belowtext_id;
    private DialogUtil viewDialog;
    private String usertype, Version;
    private int versionCode;

    private int index;
    private Handler handler_wifi = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            init_jizhiyun();
        }
    };
    public static final String MESSAGE_RECEIVED_ACTION_APK_LOAD = "com.Util.MESSAGE_RECEIVED_ACTION_APK_LOAD";
    private Dialog dialog1;
    private MessageReceiver mMessageReceiver_aboutfragment;
    public static final String MESSAGE_TONGZHI_DOOR = "com.massky.sraum.message.tongzhi.door";

    public static final String SRAUM_IS_DOWN_LOAD = "sraum_is_download";
    private MessageReceiver mMessageReceiver_apk_down_load;
    private WeakReference<Context> weakReference;
    private boolean iswait_down_load;//等待NotificationListenerService这个服务唤醒
    private String isdo;
    private MessageReceiver mMessageReceiver_tongzhi_open;

    @Override
    protected int viewId() {
        return R.layout.main_gateway_act;
    }

    @Override
    protected void onView() {

        add_page_select();
        common_second();
        //        iswait_down_load = false;
        init_jizhiyun_1();
        init_video();
        init_receiver();
    }

    private void init_receiver() {
        //在这里发送广播，expires_in是86400->24小时
        String expires_in = (String) SharedPreferencesUtil.getData(MainGateWayActivity.this, "expires_in", "");
        Intent broadcast = new Intent("com.massky.sraum.broadcast");
        broadcast.putExtra("expires_in", expires_in);
        broadcast.putExtra("timestamp", TokenUtil.getLogintime(MainGateWayActivity.this));
        sendBroadcast(broadcast);

//        addPopwinwow();
        versionCode = Integer.parseInt(VersionUtil.getVersionCode(MainGateWayActivity.this));
        getDialog();
        registerMessageReceiver();
        registerMessageReceiver_fromAbout();
        registerMessageReceiver_tongzhi_open();
//        registerMessageReceiver_fromApk_Down();
//        init_notifacation();//通知初始化
        SharedPreferencesUtil.saveData(MainGateWayActivity.this, "loadapk", false);//apk版本正在更新中
    }

    private void init_jizhiyun_1() {
        init_jizhiyun = 1;//机智云index
        dialogUtil = new DialogUtil(this);
        initPermission();
//        toggleNotificationListenerService();
        over_camera_list();//结束wifi摄像头的tag
    }

    private void init_video() {
        Intent intent = new Intent();
        intent.setClass(MainGateWayActivity.this, BridgeService.class);
        startService(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NativeCaller.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL");
                    Thread.sleep(3000);
                    Message msg = new Message();
                    NativeCaller.SetAPPDataPath(getApplicationContext().getFilesDir().getAbsolutePath());
                } catch (Exception e) {

                }
            }
        }).start();
    }

    /**
     * 获取文件长度
     */
    public long getFileSize(File file) {
        if (file.exists() && file.isFile()) {
            String fileName = file.getName();
            System.out.println("文件" + fileName + "的大小是：" + file.length());
            return file.length();
        }
        return 0;
    }


    @Override
    public void onInitStart() {

    }

    @Override
    public void onInitFinish(int status, final String errorMsg) {
//         ToastUtil.showToast(MainfragmentActivity.this,"初始化成功");
//         DeviceManager.instanceDeviceManager(getApplicationContext()).userLoginAnonymous();//匿名登录
        if (dialogUtil != null)
            dialogUtil.removeDialog();
        if (status == INIT_SUCCESS) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                     ToastUtil.showToast(MainfragmentActivity.this,"SDK初始化成功");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DeviceManager.instanceDeviceManager(getApplicationContext()).userLoginAnonymous();
                        }
                    }, 2000);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastTools.short_Toast(MainGateWayActivity.this, errorMsg);
                    new AlertDialog.Builder(MainGateWayActivity.this).setTitle("error").setMessage(errorMsg).setPositiveButton("ok", null).create().show();
                }
            });
        }
    }

    private String TAG = "robin debug";
    private GizWifiCallBack mGizWifiCallBack = new GizWifiCallBack() {

        @Override
        public void didUnbindDeviceCd(GizWifiErrorCode result, String did) {
            super.didUnbindDeviceCd(result, did);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                Logger.d(TAG, "解除绑定成功");
            } else {
                // 解绑失败
                Logger.d(TAG, "解除绑定失败");
            }
        }

        @Override
        public void didBindDeviceCd(GizWifiErrorCode result, String did) {
            super.didBindDeviceCd(result, did);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 绑定成功
                Logger.d(TAG, "绑定成功");
                ToastUtil.showToast(MainGateWayActivity.this, "绑定成功");
            } else {
                // 绑定失败
                Logger.d(TAG, "绑定失败");
            }
        }

        @Override
        public void didSetSubscribeCd(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
            super.didSetSubscribeCd(result, device, isSubscribed);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                Logger.d(TAG, (isSubscribed ? "订阅" : "取消订阅") + "成功");
                ToastUtil.showToast(MainGateWayActivity.this, (isSubscribed ? "订阅" : "取消订阅") + "成功");
            } else {
                // 解绑失败
                Logger.d(TAG, "订阅失败");
            }
        }

        @Override
        public void discoveredrCb(GizWifiErrorCode result,
                                  List<GizWifiDevice> deviceList) {
            Logger.d(TAG,
                    "discoveredrCb -> deviceList size:" + deviceList.size()
                            + "  result:" + result);
            switch (result) {
                case GIZ_SDK_SUCCESS:
                    Logger.e(TAG, "load device  sucess");
                    update(deviceList);
//                    if(deviceList.get(0).getNetStatus()==GizWifiDeviceNetStatus.GizDeviceOffline)
                    break;
                default:
                    break;
            }
        }
    };

    void update(List<GizWifiDevice> gizWifiDevices) {
        GizWifiDevice mGizWifiDevice = null;
        if (gizWifiDevices != null) {
            Log.e("DeviceListActivity", gizWifiDevices.size() + "");
            Log.e("MainActivity", gizWifiDevices.size() + "");
            //红外转发器绑定设备
            for (int i = 0; i < gizWifiDevices.size(); i++) {
                mGizWifiDevice = gizWifiDevices.get(i);
                // list_hand_scene
                // 绑定选中项
                if (!Utility.isEmpty(mGizWifiDevice) && !mGizWifiDevice.isBind()) {
                    DeviceManager.instanceDeviceManager(getApplicationContext()).bindRemoteDevice(mGizWifiDevice);
                    final GizWifiDevice finalMGizWifiDevice = mGizWifiDevice;
                    handler_wifi.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DeviceManager.instanceDeviceManager(getApplicationContext()).setSubscribe(finalMGizWifiDevice, true);
                        }
                    }, 1000);
                }
            }
        }

//        adapter.notifyDataSetChanged();
    }


    private void init_notifacation() {
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = getIntent().getBundleExtra(Constants.EXTRA_BUNDLE);
            String title = null;//JingRuiApp
            String content = null;//2017-08-31 10:40:16,客厅,模块报警
            if (bundle != null) {
                SharedPreferencesUtil.saveData(MainGateWayActivity.this, "tongzhi_time", 1);
                //视频监控，极光push不太好用；
                init_nofication(intent);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {//进到这里来说明app没有退出去；
        SharedPreferencesUtil.saveData(MainGateWayActivity.this, "tongzhi_time", 1);
        //视频监控，极光push不太好用；
        getNotify(intent);
        setIntent(intent);
    }

    private void getNotify(Intent intent) {
        init_nofication(intent); //暂时注销掉，fragment->childactivity-> fragment->mainactivity时,执行
        // 这里不是用的commit提交，用的commitAllowingStateLoss方式。commit不允许后台执行，不然会报Deferring update until onResume 错误
        super.onNewIntent(intent);
    }

    /**
     * 初始化通知
     *
     * @param intent
     */
    private void init_nofication(Intent intent) {
        if (null != intent) {
            Bundle bundle = intent.getBundleExtra(Constants.EXTRA_BUNDLE);
//            String title = null;//JingRuiApp
//            String content = null;//2017-08-31 10:40:16,客厅,模块报警
            if (bundle != null) { //bundle = null  了
                String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);////{"type":"2"}
                JSONObject json = null;
                try {
                    json = new JSONObject(extras);
                    String type = json.getString("type");
                    String uid = json.getString("uid");
                    sendBroad(type, uid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String KEY_TITLE = "title";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public void registerMessageReceiver_fromAbout() {
        mMessageReceiver_aboutfragment = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_FROM_ABOUT_FRAGMENT);
        registerReceiver(mMessageReceiver_aboutfragment, filter);
    }

    public void registerMessageReceiver_tongzhi_open() {
        mMessageReceiver_tongzhi_open = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(ACTION_NOTIFICATION_OPENED_MAIN);
        registerReceiver(mMessageReceiver_tongzhi_open, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    boolean loginflag = (boolean) SharedPreferencesUtil.getData(CommonData.mNowContext, "loginflag", false);
//                    ToastUtil.showToast(CommonData.mNowContext,"MainfragmentActivity-loginflag:" + loginflag);
                    if (loginflag)
                        ToastUtils.getInstances().showDialog("账号在其他地方登录，请重新登录。");
                } else if (MESSAGE_RECEIVED_FROM_ABOUT_FRAGMENT.equals(intent.getAction())) {
                    String UpApkUrl = ApiHelper.UpdateApkUrl + "sraum" + Version + ".apk";
                    String apkName = "sraum" + Version + ".apk";
                    Log.e("fei", "UpApkUrl:" + UpApkUrl);
                    UpdateManager manager = new UpdateManager(MainGateWayActivity.this, UpApkUrl, apkName);
                    updateApkListener = (UpdateApkListener) manager;
                    manager.showDownloadDialog();
                } else if (SRAUM_IS_DOWN_LOAD.equals(intent.getAction())) {//apk正在下载
//                    ToastUtil.showToast(MainfragmentActivity.this, "apk正在下载中");
                    iswait_down_load = true;
                } else if (ACTION_NOTIFICATION_OPENED_MAIN.equals(intent.getAction())) {
                    SharedPreferencesUtil.saveData(MainGateWayActivity.this, "tongzhi_time", 1);
                    AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                    init_nofication(intent); //暂时注销掉，fragment->childactivity-> fragment->mainactivity时,执行
                }
            } catch (Exception e) {//SRAUM_IS_DOWN_LOAD

            }
        }
    }

    /*
     * 通知
     * */
    private void sendBroad(String content, String bundle) {
//        Intent mIntent = new Intent(MESSAGE_TONGZHI);
//        mIntent.putExtra("uid", bundle == null ? "" : bundle);//    launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
//        mIntent.putExtra("type", content);
//        sendBroadcast(mIntent);
    }


    private void updateApk() {
        sraum_get_version();
    }

    private void initPermission() {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void sraum_get_version() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(MainGateWayActivity.this));
        MyOkHttp.postMapObject(ApiHelper.sraum_getVersion, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_get_version();
                    }
                }, MainGateWayActivity.this, viewDialog) {
                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        Version = user.version;
                        Log.e("fei", "Version:" + Version);
                        int sracode = Integer.parseInt(user.versionCode);
                        if (versionCode < sracode) {
                            //在这里判断有没有正在更新的apk,文件大小小于总长度即可
                            weakReference = new WeakReference<Context>(App.getInstance());
                            File apkFile = new File(weakReference.get().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "app_name.apk");
                            if (apkFile != null && apkFile.exists()) {
//                                long istext = main_get_real_size(apkFile);
//                                long istext = 0;
//                                try {
//                                    istext = getFileSizes(apkFile);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
////                                long istext = SDCardSizeTest(apkFile);
//                                Log.e("robin debug", "istext:" + istext + "");
                                long apksize = 0;
                                try {
                                    apksize = getFileSize(apkFile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //获取已经下载字节数
//                               String totalSize =  getDataTotalSize(MainfragmentActivity.this,apkFile.getAbsolutePath());
//                              Integer.parseInt(totalSize);

                                int totalapksize = (int) SharedPreferencesUtil.getData(MainGateWayActivity.this, "apk_fileSize", 0);
                                if (totalapksize == 0) {//则说明，还没有下载过
                                    belowtext_id.setText("版本更新至" + Version);
                                    viewDialog.loadViewdialog();
                                    return;
                                }
                                if (apksize - totalapksize == 0) { //说明正在下载或者下载完毕，安装失败时，//->或者是下载完毕后没有去安装；
//                                    down_load_thread();
                                    ToastUtil.showToast(MainGateWayActivity.this, "检测到有新版本，正在下载中");
                                }
                            } else {//不存在，apk文件
                                belowtext_id.setText("版本更新至" + Version);
                                viewDialog.loadViewdialog();
                            }
                        } else {//没有可更新的apk时
                            SharedPreferencesUtil.saveData(MainGateWayActivity.this, "apk_fileSize", 0);
                        }
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }
                });
    }


    //用于设置dialog展示
    private void getDialog() {
        View view = getLayoutInflater().inflate(R.layout.check, null);
        checkbutton_id = (Button) view.findViewById(R.id.checkbutton_id);
        qxbutton_id = (Button) view.findViewById(R.id.qxbutton_id);
        dtext_id = (TextView) view.findViewById(R.id.dtext_id);
        belowtext_id = (TextView) view.findViewById(R.id.belowtext_id);
        dtext_id.setText("发现新版本");
        checkbutton_id.setText("立即更新");
        qxbutton_id.setText("以后再说");
        viewDialog = new DialogUtil(MainGateWayActivity.this, view);
        checkbutton_id.setOnClickListener(this);
        qxbutton_id.setOnClickListener(this);
    }

    /**
     * 切换Fragment
     *
     * @param
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private void tongzhi_door(String type, String uid) {
        Intent mIntent = new Intent(MESSAGE_TONGZHI_DOOR);
        mIntent.putExtra("type", type);
        mIntent.putExtra("uid", uid);
        sendBroadcast(mIntent);
    }

    private UpdateApkListener updateApkListener;

    public interface UpdateApkListener {
        void sendTo_UPApk();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.showDelToast(MainGateWayActivity.this, "再按一次退出程序");
                exitTime = System.currentTimeMillis();

            } else {
                iswait_down_load = true;
                over_camera_list();//结束wifi摄像头的tag
                MainGateWayActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String name1, final String name2) {
        View view = LayoutInflater.from(MainGateWayActivity.this).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
        name_gloud.setText(name1);
        tv_title.setText(name2);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        dialog1 = new Dialog(MainGateWayActivity.this, R.style.BottomDialog);
        dialog1.setContentView(view);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setCancelable(false);//设置它可以取消
        dialog1.setCanceledOnTouchOutside(false);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog1.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog1.getWindow().setAttributes(p);  //设置生效
//        dialog1.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                dialog1.dismiss();
                over_camera_list();//结束wifi摄像头的tag
                MainGateWayActivity.this.finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferencesUtil.saveData(MainfragmentActivity.this, "loadapk", false);
//                over_broad_apk_load();
                startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), TONGZHI_APK_UPGRATE);
            }
        });
    }

    private void over_broad_apk_load() {
//        Intent broadcast = new Intent(MESSAGE_RECEIVED_ACTION_APK_LOAD);
//        sendBroadcast(broadcast);
        if (updateApkListener != null) {
            updateApkListener.sendTo_UPApk();
        }
        MainGateWayActivity.this.finish();
        over_camera_list();//结束wifi摄像头的tag
    }

    /**
     * 清除wifi摄像头列表
     */
    private void over_camera_list() {
        List<Map> list = SharedPreferencesUtil.getInfo_List(MainGateWayActivity.this, "list_wifi_camera_first");
        List<Map> list_second = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map map = new HashMap();
            map.put("did", list.get(i).get("did"));
            map.put("tag", 0);
            list_second.add(map);
        }
        SharedPreferencesUtil.saveInfo_List(MainGateWayActivity.this, "list_wifi_camera_first", new ArrayList<Map>());
        SharedPreferencesUtil.saveInfo_List(MainGateWayActivity.this, "list_wifi_camera_first", list_second);
    }


    @Override
    protected void onResume() {
        init_jizhiyun_onresume();
        super.onResume();
    }


    /**
     * 初始化onResume动作
     */
    private void init_jizhiyun_onresume() {
        init_jizhi_cloud();//耗时动作，最好都放在onResume里，此时屏幕已经亮了
        isForegrounds = true;
        Log.e("zhu-", "MainfragmentActivity:onResume():isForegrounds:" + isForegrounds);
        init_jlogin();
        boolean netflag = NetUtils.isNetworkConnected(MainGateWayActivity.this);
        if (netflag) {//获取版本号
//            updateApk();
        }
    }

    /**
     * 机智云小苹果初始化
     */
    private void init_jizhi_cloud() {
        //小苹果机智云初始化
//        init_jizhiyun = 1;

        if (init_jizhiyun == 1) {
            init_jizhiyun = 2;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
//                    init_visible();
                    init_jizhiyun();
                    Looper.loop();
                }
            }).start();
            Log.e("robin debug", "    Looper.prepare()");
            init_notifacation();//通知初始化,在这里执行，onResume之后屏幕就亮了，屏幕亮后才加载UI资源文件
        }
    }

    private void init_jlogin() {
        String mobilePhone = (String) SharedPreferencesUtil.getData(MainGateWayActivity.this, "loginPhone", "");
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        String szImei = TelephonyMgr.getDeviceId();
        String szImei = (String) SharedPreferencesUtil.getData(MainGateWayActivity.this, "regId", "");
        init_islogin(mobilePhone, szImei);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case TONGZHI_APK_UPGRATE:
//                if (isEnabled()) {//监听通知权限开启
//                    if (dialog1 != null)
//                        dialog1.dismiss();
//                    updateApk();
//                } else {
//                    if (!dialog1.isShowing()) {
//                        dialog1.show();
//                    }
//                }
//                break;
//        }
    }

    /**
     * 初始化登录
     *
     * @param
     * @param mobilePhone
     * @param szImei
     */
    private void init_islogin(final String mobilePhone, final String szImei) {
        Map<String, Object> map = new HashMap<>();
        map.put("mobilePhone", mobilePhone);
        map.put("phoneId", szImei);
        LogUtil.eLength("查看数据", JSON.toJSONString(map));
        MyOkHttp.postMapObject(ApiHelper.sraum_isLogin, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                init_islogin(mobilePhone, szImei);
            }
        }, MainGateWayActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(MainGateWayActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {

            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }

            @Override
            public void threeCode() {
                //103已经登录，需要退出app
//                dialog.show();
                ToastUtils.getInstances().showDialog("账号在其他地方登录，请重新登录。");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ToastUtil.showToast(MainfragmentActivity.this,"MainfragmentActivity:destroy");
        over_camera_list();//结束wifi摄像头的tag
        common_second();
//        over_broad_apk_load();
        if (dialog1 != null) {
            dialog1.dismiss();
        }
        unregisterReceiver(mMessageReceiver_aboutfragment);
//        unregisterReceiver(mMessageReceiver_apk_down_load);
//        iswait_down_load = false;

//        /**
//         * 开启download监听service
//         */
//        Intent intent_apk_down = new Intent();
//        intent_apk_down.setClass(MainfragmentActivity.this, NotificationMonitorService.class);
//        stopService(intent_apk_down);
    }


    /**
     * 初始化机智云小苹果
     */
    private void init_jizhiyun() {
        initListener();
        // 初始化SDK

//        dialogUtil.loadDialog();

        YkanSDKManager.init(MainGateWayActivity.this, MainGateWayActivity.this);
        //需要剥离机智云的用户调用此方法初始化
//        YkanSDKManager.custInit(this,false);
        // 设置Log信息是否打印
        YkanSDKManager.getInstance().setLogger(true);
    }

    private void initListener() {
        DeviceManager.instanceDeviceManager(getApplicationContext()).setGizWifiCallBack(new GizWifiCallBack() {

            @Override
            public void didBindDeviceCd(GizWifiErrorCode result, String did) {
                super.didBindDeviceCd(result, did);
            }

            @Override
            public void didTransAnonymousUser(GizWifiErrorCode result) {
                super.didTransAnonymousUser(result);
            }

            /** 用于用户登录的回调 */
            @Override
            public void userLoginCb(GizWifiErrorCode result, String uid, String token) {
                ToastUtil.showToast(MainGateWayActivity.this, "result:" + result);
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {// 登陆成功
                    Constants.UID = uid;
                    Constants.TOKEN = token;
//                    toDeviceList();
                    //登录成功
                    //去绑定订阅
//
//                    ToastUtil.showToast(MainfragmentActivity.this, "小苹果登录成功!");
//                    wiif_login_scuess();
                    SharedPreferencesUtil.saveData(MainGateWayActivity.this, "apple_login", true);
                } else if (result == GizWifiErrorCode.GIZ_OPENAPI_USER_NOT_EXIST) {// 用户不存在

                } else if (result == GizWifiErrorCode.GIZ_OPENAPI_USERNAME_PASSWORD_ERROR) {//// 用户名或者密码错误

                } else {
//                    toast("登陆失败，请重新登录");
                    index++;
                    if (index >= 2) {

                    } else {
                        DeviceManager.instanceDeviceManager(getApplicationContext()).userLoginAnonymous();//匿名登录
                    }
                    SharedPreferencesUtil.saveData(MainGateWayActivity.this, "apple_login", false);
                }
            }

            /** 用于用户注册的回调 */
            @Override
            public void registerUserCb(GizWifiErrorCode result, String uid, String token) {


            }

            /** 用于发送验证码的回调 */
            @Override
            public void didRequestSendPhoneSMSCodeCb(GizWifiErrorCode result) {

            }

            /** 用于重置密码的回调 */
            @Override
            public void didChangeUserPasswordCd(GizWifiErrorCode result) {

            }
        });
    }


    /**
     * fragment卡片选择
     */
    private void add_page_select() {
        for (int i = 0; i < 3; i++) {
            setTabSelection(i);
        }
        setTabSelection(0);
        radioGroup = (RadioGroup) findViewById(R.id.activity_group_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int tabId) {

                if (tabId == R.id.order_process) {
//                    switchFragment(fragment1).commit();
                    setTabSelection(0);
                }
                if (tabId == R.id.order_query) {
//                    switchFragment(fragment2).commit();
                    setTabSelection(1);
                }
                if (tabId == R.id.merchant_manager) {
//                    switchFragment(fragment3).commit();
                    setTabSelection(2);
                }
            }
        });
    }


    public void setTabSelection(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragments(transaction);
        Fragment frament = null;
        switch (index) {
            case 0:
                if (fragment1 == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
//                    firstPageFragment = new FirstPageFragment(menu1);
                    fragment1 = HomeFragment.newInstance();
                    transaction.add(R.id.container, fragment1);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fragment1);
                }
                break;
            case 1:
                if (fragment2 == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    fragment2 = SceneFragment.newInstance();
                    transaction.add(R.id.container, fragment2);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fragment2);
                }
                break;
            case 2:
                if (fragment3 == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    fragment3 = MineFragment.newInstance();
                    transaction.add(R.id.container, fragment3);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fragment3);
                }

                break;
        }
        transaction.commit();
    }


    private void hideFragments(FragmentTransaction transaction) {
        if (fragment1 != null) {
            transaction.hide(fragment1);
        }
        if (fragment2 != null) {
            transaction.hide(fragment2);
        }

        if (fragment3 != null) {
            transaction.hide(fragment3);
        }
    }


    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {

    }

    private RadioGroup radioGroup;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton mTab = (RadioButton) radioGroup.getChildAt(i);
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag((String) mTab.getTag());
            FragmentTransaction ft = fm.beginTransaction();
            if (fragment != null) {
                if (!mTab.isChecked()) {
                    ft.hide(fragment);
                }
            }
            ft.commit();
        }
    }


    /**
     * 清除联动的本地存储
     */
    private void common_second() {
        SharedPreferencesUtil.saveData(MainGateWayActivity.this, "linkId", "");
        SharedPreferencesUtil.saveInfo_List(MainGateWayActivity.this, "list_result", new ArrayList<Map>());
        SharedPreferencesUtil.saveInfo_List(MainGateWayActivity.this, "list_condition", new ArrayList<Map>());
        SharedPreferencesUtil.saveData(MainGateWayActivity.this, "editlink", false);
        SharedPreferencesUtil.saveInfo_List(MainGateWayActivity.this, "link_information_list", new ArrayList<Map>());
        SharedPreferencesUtil.saveData(MainGateWayActivity.this, "add_condition", false);
    }
}
