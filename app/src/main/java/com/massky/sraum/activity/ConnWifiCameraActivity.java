package com.massky.sraum.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ipcamera.demo.BridgeService;
import com.ipcamera.demo.utils.SystemValue;
import com.massky.sraum.R;
import com.massky.sraum.Util.EyeUtil;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.fragment.ConfigDialogCameraFragment;
import com.massky.sraum.view.ClearEditText;
import com.mediatek.demo.smartconnection.JniLoader;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import androidx.percentlayout.widget.PercentRelativeLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.InjectView;
import voice.encoder.DataEncoder;
import voice.encoder.VoicePlayer;
import vstc2.nativecaller.NativeCaller;

/**
 * Created by zhu on 2018/5/30.
 */

public class ConnWifiCameraActivity extends BaseActivity implements BridgeService.AddCameraInterface
        , BridgeService.IpcamClientInterface, BridgeService.CallBackMessageInterface {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.select_wlan_rel_big)
    PercentRelativeLayout select_wlan_rel_big;
    @InjectView(R.id.edit_wifi)
    ClearEditText edit_wifi;
    @InjectView(R.id.edit_password_gateway)
    ClearEditText edit_password_gateway;
    private ConfigDialogCameraFragment newFragment;
    @InjectView(R.id.eyeimageview_id_gateway)
    ImageView eyeimageview_id_gateway;
    private int CONNWIFI = 101;
    private EyeUtil eyeUtil;
    @InjectView(R.id.conn_btn_dev)
    Button conn_btn_dev;
    private String wifi_name = "";
    private String TAG = "robin debug";
    private Handler handler = new Handler();
    private WifiManager manager;
    private static final String STR_DID = "did";
    private static final String STR_MSG_PARAM = "msgparam";
    private MyWifiThread myWifiThread = null;
    private boolean blagg = false;
    private MyBroadCast receiver;
    private static final int SEARCH_TIME = 5000;
    private static final int SEARCH_TIME_SECOND = 6000;
    private static final int ONE_KEY_TIME = 30000;
    List<Map> list_wifi_camera = new ArrayList<>();
    private int index_wifi;

    private String sendMac = null;
    private String wifiName;
    private String currentBssid;

    private JniLoader loader;
    private VoicePlayer player = new VoicePlayer();
    private String type;

    @Override
    protected int viewId() {
        return R.layout.conn_wifi_camera_act;
    }

    @Override
    protected void onView() {
        back.setOnClickListener(this);
        select_wlan_rel_big.setOnClickListener(this);
        eyeimageview_id_gateway.setOnClickListener(this);
        conn_btn_dev.setOnClickListener(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        init_data();
        onview();
        initWifiConect();
        initWifiName();
        initDialog();

//        /**
//         * 开启机智云小苹果服务(service)
//         */
//        Intent intentService = new Intent(this,SimpleIntentService.class);
//        startService(intentService);

        init_wifi_camera();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    private void init_data() {
        type = (String) getIntent().getSerializableExtra("type");
    }

    private void one_step_peizhi() {
        getWifi();

        boolean res = JniLoader.LoadLib();
        Log.e("SmartConnection", "Load Smart Connection Library Result ：" + res);
        loader = new JniLoader();
        int proV = loader.GetProtoVersion();
        Log.e("SmartConnection", "proV ：" + proV);
        int libV = loader.GetLibVersion();
        Log.e("SmartConnection", "libV ：" + libV);
        String version = "V" + proV + "." + libV;
    }

    private void getWifi() {
        WifiManager wifiMan = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiMan.getConnectionInfo();

        wifiName = wifiInfo.getSSID().toString();
        if (wifiName.length() > 2 && wifiName.charAt(0) == '"'
                && wifiName.charAt(wifiName.length() - 1) == '"') {
            wifiName = wifiName.substring(1, wifiName.length() - 1);
        }

        List<ScanResult> wifiList = wifiMan.getScanResults();
        ArrayList<String> mList = new ArrayList<String>();
        mList.clear();

        for (int i = 0; i < wifiList.size(); i++) {
            mList.add((wifiList.get(i).BSSID).toString());
        }

        currentBssid = wifiInfo.getBSSID();
        if (currentBssid == null) {
            for (int i = 0; i < wifiList.size(); i++) {
                if ((wifiList.get(i).SSID).toString().equals(wifiName)) {
                    currentBssid = (wifiList.get(i).BSSID).toString();
                    break;
                }
            }
        } else {
            if (currentBssid.equals("00:00:00:00:00:00")
                    || currentBssid.equals("")) {
                for (int i = 0; i < wifiList.size(); i++) {
                    if ((wifiList.get(i).SSID).toString().equals(wifiName)) {
                        currentBssid = (wifiList.get(i).BSSID).toString();
                        break;
                    }
                }
            }
        }
        if (currentBssid == null) {
            finish();
        }

        String tomacaddress[] = currentBssid.split(":");
        int currentLen = currentBssid.split(":").length;

        for (int m = currentLen - 1; m > -1; m--) {
            for (int j = mList.size() - 1; j > -1; j--) {
                if (!currentBssid.equals(mList.get(j))) {
                    String array[] = mList.get(j).split(":");
                    if (!tomacaddress[m].equals(array[m])) {
                        mList.remove(j);//
                    }
                }
            }
            if (mList.size() == 1 || mList.size() == 0) {
                if (m == 5) {
                    sendMac = tomacaddress[m].toString();
                } else if (m == 4) {
                    sendMac = tomacaddress[m].toString()
                            + tomacaddress[m + 1].toString();
                } else {
                    sendMac = tomacaddress[5].toString()
                            + tomacaddress[4].toString()
                            + tomacaddress[3].toString();
                }
                break;
            }
        }
    }

    private void sendSonic(String mac, final String wifi) {
        byte[] midbytes = null;

        try {
            midbytes = HexString2Bytes(mac);
            printHexString(midbytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (midbytes.length > 6) {
            Toast.makeText(ConnWifiCameraActivity.this, "no support",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] b = null;
        int num = 0;
        if (midbytes.length == 2) {
            b = new byte[]{midbytes[0], midbytes[1]};
            num = 2;
        } else if (midbytes.length == 3) {
            b = new byte[]{midbytes[0], midbytes[1], midbytes[2]};
            num = 3;
        } else if (midbytes.length == 4) {
            b = new byte[]{midbytes[0], midbytes[1], midbytes[2], midbytes[3]};
            num = 4;
        } else if (midbytes.length == 5) {
            b = new byte[]{midbytes[0], midbytes[1], midbytes[2],
                    midbytes[3], midbytes[4]};
            num = 5;
        } else if (midbytes.length == 6) {
            b = new byte[]{midbytes[0], midbytes[1], midbytes[2],
                    midbytes[3], midbytes[4], midbytes[5]};
            num = 6;
        } else if (midbytes.length == 1) {
            b = new byte[]{midbytes[0]};
            num = 1;
        }

        int a[] = new int[19];
        a[0] = 6500;
        int i, j;
        for (i = 0; i < 18; i++) {
            a[i + 1] = a[i] + 200;
        }

        player.setFreqs(a);
        player.play(DataEncoder.encodeMacWiFi(b, wifi.trim()), 10, 1000);
    }

    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    private static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private static void printHexString(byte[] b) {
        // System.out.print(hint);
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print("aaa" + hex.toUpperCase() + " ");
        }
        System.out.println("");
    }

    private void init_wifi_camera() {
        manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        BridgeService.setAddCameraInterface(this);
        BridgeService.setCallBackMessage(this);
        receiver = new MyBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("finish");
        registerReceiver(receiver, filter);
//        intentbrod = new Intent("drop");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        blagg = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myWifiThread != null) {
            blagg = false;
        }
//        progressdlg.dismiss();
        NativeCaller.StopSearch();
        if (player != null) {
            player.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
//        NativeCaller.Free();

//        Intent intent = new Intent();
//        intent.setClass(this, BridgeService.class);
//        stopService(intent);
        Intent intent = new Intent();
        intent.setClass(this, BridgeService.class);
        stopService(intent);
    }

    class MyWifiThread extends Thread {
        @Override
        public void run() {
            while (blagg == true) {
                super.run();
                updateListHandler.sendEmptyMessage(100000);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isOneKey;
    Runnable updateThread = new Runnable() {
        public void run() {
            NativeCaller.StopSearch();
//            progressdlg.dismiss();
            switch (doit_onekey) {
                case "search":
                    index_wifi++;
                    if (!isOneKey)
                        SharedPreferencesUtil.saveInfo_List(ConnWifiCameraActivity.this, "list_wifi_camera", list_wifi_camera);
                    if (index_wifi >= 2) {//搜索2次，6s
//                        if (list_wifi_camera.size() > 0)
// {
//                        }
                        index_wifi = 0;
                        if (isOneKey) { //2次一健匹配结束,2次搜索完毕
                            isOneKey = false;
                            List<Map> list = SharedPreferencesUtil.getInfo_List(ConnWifiCameraActivity.this, "list_wifi_camera");
                            if (list_wifi_camera.size() == list.size()) { //说明一健配置之后，没有搜索到最新设备
                                if (connWifiInterfacer != null) {
                                    connWifiInterfacer.conn_wifi_over();
                                }
                                ToastUtil.showToast(ConnWifiCameraActivity.this,"没有搜索到最新设备，请重新配置");
//                                isOneKey = true;
//                                doit_onekey = "search";
//                                searchCamera();
                            } else if (list_wifi_camera.size() > list.size()) {//说明一健配置之后，搜索到最新设备
                                Map map_result = new HashMap();
                                List<String> list_first = new ArrayList<>();
                                List<String> list_second = new ArrayList<>();

                                for (Map map : list_wifi_camera) {
                                    list_second.add(map.get("strMac").toString());
                                }

                                for (Map map : list) {
                                    list_first.add(map.get("strMac").toString());
                                }

                                String mac = "";
                                for (String str : list_second) {
                                    if (!list_first.contains(str)) {
                                        mac = str;
                                    }
                                }

                                for (Map map : list_wifi_camera) {
                                    if (map.get("strMac").toString().equals(mac)) {
                                        map_result = map;
                                    }
                                }

                                //1,2,3
                                //1,2,3,4
//                                map_result = list_wifi_camera.get(list_wifi_camera.size() - 1);

                                Intent intent = new Intent(ConnWifiCameraActivity.this,
                                        AddWifiCameraScucessActivity.class);
                                map_result.put("type", type);
                                intent.putExtra("wificamera", (Serializable) map_result);
                                startActivity(intent);
                                if (connWifiInterfacer != null) {
                                    connWifiInterfacer.conn_wifi_over();
                                }
                            }
                        } else {
                            one_step_second();//一键匹配,配两次
                        }
                    } else {
                        searchCamera();
                    }
                    break;
                case "onekey"://两次一键匹配之后，延时3s在去搜两次，
                    isOneKey = true;
                    doit_onekey = "search";
                    if (loader != null) {
                        int retValue = loader.StopSmartConnection();
                    }

                    if (player != null) {
                        player.stop();
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchCamera();
                        }
                    }, 10000);
                    break;
            }
        }
//                    }
//                } else {
//                    if (connWifiInterfacer != null) {
//                        connWifiInterfacer.search_wifi_fail();
//                    }

    };


    private String doit_onekey = "";
    private int wifi_peizhi;
    /**
     * 延时一键匹配5秒后执行,在去搜
     */
    Runnable updateOneKeyThread = new Runnable() {

        public void run() {
            doit_onekey = "onekey";
//            wifi_peizhi++;
//            if (wifi_peizhi >= 2) {
//                wifi_peizhi = 0;
//            new Thread(new SearchThread()).start();
            updateListHandler.postDelayed(updateThread, SEARCH_TIME);
//            } else {
//                one_step_second();//一键匹配,配两次
//            }
        }
    };

    /**
     * 一键快搜第二步
     */
    private void one_step_second() {
        int sendV1 = 1;
        int sendV4 = 1;
        int sendV5 = 1;

        float oI = 0.0f;
        float nI = 0.0f;

//            oI = Float.parseFloat("V1/V4");
//            Log.e("SmartConnection", "start oI=" + oI);
//
//
//            nI = Float.parseFloat("V5");
//            Log.e("SmartConnection", "start nI=" + nI);

        if (sendV1 == 0 && sendV4 == 0 && sendV5 == 0) {
//            showWarningDialog(R.string.error_select_leat_1_version);
            return;
        }

        int retValue = JniLoader.ERROR_CODE_OK;
        String key = "Encryption Key (Length Must Be 16)";
        String mac = "0xff 0xff 0xff 0xff 0xff 0xff";
        String mac1 = "";
        if (key == null) {
            Log.e("SmartConnection", "init Smart key is null");
        } else {
            Log.e("SmartConnection", "init Smart key-len=" + key.length() + ", key-emp=" + key.isEmpty());
        }
        Log.e("SmartConnection", "init Smart key=" + key + ", sendV1=" + sendV1 + ", sendV4=" + sendV4 + ", sendV5=" + sendV5);
        retValue = loader.InitSmartConnection(key, mac, sendV1, sendV4, sendV5);
        Log.e("SmartConnection", "init return retValue=" + retValue);
        if (retValue != JniLoader.ERROR_CODE_OK) {
//            showWarningDialog(R.string.init_failed);
            if (connWifiInterfacer != null) {
                connWifiInterfacer.search_wifi_fail();
            }
            return;
        }

        Log.e("SmartConnection", "Send Smart oI=" + oI + ", nI=" + nI);
        loader.SetSendInterval(oI, nI);

        String Password = edit_password_gateway.getText().toString();
//
        switch (type) {
            case "101":
                retValue = loader.StartSmartConnection(wifi_name, Password, "");
                break;
            case "103":
                sendSonic(sendMac, Password.toString());
                break;
        }
//        retValue = loader.StartSmartConnection(wifi_name, Password, "");
        Log.e("SmartConnection", "start return retValue=" + retValue);
        if (retValue != JniLoader.ERROR_CODE_OK) {
//            showWarningDialog(R.string.start_failed);
            if (connWifiInterfacer != null) {
                connWifiInterfacer.search_wifi_fail();
            }
            return;
        }
        updateListHandler.postDelayed(updateOneKeyThread, ONE_KEY_TIME);
    }

    Handler updateListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    public static String int2ip(long ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    private void searchCamera() {
        startSearch();
    }

    private void startSearch() {
//        listAdapter.ClearAll();
//        progressdlg.setMessage(getString(R.string.searching_tip));
//        progressdlg.show();
        new Thread(new SearchThread()).start();
        updateListHandler.postDelayed(updateThread, SEARCH_TIME);
    }

    private class SearchThread implements Runnable {
        @Override
        public void run() {
            Log.d("tag", "startSearch");
            NativeCaller.StartSearch();
        }
    }

    private class MyBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            ConnWifiCameraActivity.this.finish();
            Log.d("ip", "AddCameraActivity.this.finish()");
        }
    }

    class StartPPPPThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(100);
                startCameraPPPP();
            } catch (Exception e) {

            }
        }
    }

    private void startCameraPPPP() {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }

        if (SystemValue.deviceId.toLowerCase().startsWith("vsta")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK", 0);
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstd")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "HZLXSXIALKHYEIEJHUASLMHWEESUEKAUIHPHSWAOSTEMENSQPDLRLNPAPEPGEPERIBLQLKHXELEHHULOEGIAEEHYEIEK-$$", 1);
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstf")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "HZLXEJIALKHYATPCHULNSVLMEELSHWIHPFIBAOHXIDICSQEHENEKPAARSTELERPDLNEPLKEILPHUHXHZEJEEEHEGEM-$$", 1);
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vste")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBAKKIOGNJHEGHMFEEDGLNOHJMPHAFPBEDLADILKEKPDLBDDNPOHKKCIFKJBNNNKLCPPPNDBFDL", 0);
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstg")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBOKCIGGFJPECHIFNEBGJNLHOMIHEFJBADPAGJELNKJDKANCBPJGHLAIALAADMDKPDGOENEBECCIK:vstarcam2018", 0);
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstb") || SystemValue.deviceId.toLowerCase().startsWith("vstc")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL", 0);
        } else {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "", 0);
        }
        //int result = NativeCaller.StartPPPP(SystemValue.deviceId, SystemValue.deviceName,
        //		SystemValue.devicePass,1,"");
        //Log.i("ip", "result:"+result);
    }

    private void stopCameraPPPP() {
        NativeCaller.StopPPPP(SystemValue.deviceId);
    }

    @Override
    public void BSMsgNotifyData(String did, int type, int param) {
        Log.d("ip", "type:" + type + " param:" + param);
    }

    @Override
    public void BSSnapshotNotify(String did, byte[] bImage, int len) {
        // TODO Auto-generated method stub
        Log.i("ip", "BSSnapshotNotify---len" + len);
    }

    @Override
    public void callBackUserParams(String did, String user1, String pwd1,
                                   String user2, String pwd2, String user3, String pwd3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void CameraStatus(String did, int status) {

    }

    /**
     * BridgeService callback
     **/
    @Override
    public void callBackSearchResultData(int sysver, String strMac,
                                         String strName, String strDeviceID, String strIpAddr, int port) {
        Log.e("AddCameraActivity", strDeviceID + strName);
//        if (!listAdapter.AddCamera(strMac, strName, strDeviceID)) {
//            return;
//        }
        Map map = new HashMap();
        map.put("strMac", strMac);
        map.put("strDeviceID", strDeviceID);
        map.put("strName", strName);
        map.put("wifi", wifi_name);
        list_wifi_camera.add(map);
        Log.e("robin debug", "strDeviceID:" + strDeviceID);
        HashSet<Map> h = new HashSet<Map>(list_wifi_camera);
        list_wifi_camera.clear();
        for (Map map_new : h) {
            list_wifi_camera.add(map_new);
        }
    }

    @Override
    public void CallBackGetStatus(String did, String resultPbuf, int cmd) {

    }

    private void onview() {
        eyeUtil = new EyeUtil(ConnWifiCameraActivity.this, eyeimageview_id_gateway, edit_password_gateway, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ConnWifiCameraActivity.this.finish();
                break;
            case R.id.select_wlan_rel_big:
                showPopwindow();
                break;
            case R.id.conn_btn_dev:
//                startActivity(new Intent(ConnWifiActivity.this,));
                //在这里弹出dialogFragment对话框
                one_search();
                break;

            case R.id.eyeimageview_id_gateway:
                eyeUtil.EyeStatus();
                break;
        }
    }

    /**
     * 第一次搜索wifi摄像头
     */
    private void one_search() {
        String str = edit_wifi.getText().toString();
        if (edit_wifi.getText().toString().trim().equals("")) {
            ToastUtil.showToast(ConnWifiCameraActivity.this, "WIFI用户名为空");
            return;
        }

        if (edit_password_gateway.getText().toString().trim().equals("")) {
            ToastUtil.showToast(ConnWifiCameraActivity.this, "WIFI密码为空");
            return;
        }
//        stopCameraPPPP();
        //把相机状态，设备id置空
//                textView_top_show.setText(R.string.login_stuta_camer);
        SystemValue.deviceId = null;
        doit_onekey = "search";
        searchCamera();
        show_dialog_fragment();
    }

    private void show_dialog_fragment() {
        if (!newFragment.isAdded()) {//DialogFragment.show()内部调用了FragmentTransaction.add()方法，所以调用DialogFragment.show()方法时候也可能
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(newFragment, "dialog");
            ft.commit();
        }
    }

    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(ConnWifiCameraActivity.this).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
        TextView promat_txt;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
//        promat_txt = (TextView) view.findViewById(R.id.promat_txt);
        name_gloud.setText("您的手机wifi尚未启动,请先启动您的手机wifi；并连接您的路由器在进行操作。"
        );
//        promat_txt.setText("连接");
        tv_title.setText("是否启动wifi?");
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(ConnWifiCameraActivity.this, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //连接wifi的相关代码,跳转到WIFI连接界面
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivityForResult(wifiSettingsIntent, CONNWIFI);
                dialog.dismiss();
                handler.sendEmptyMessage(1);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                handler.sendEmptyMessage(0);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initWifiName();
        //判断wifi已连接的条件
        // TODO Auto-generated method stub
        //获取系统服务
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取状态
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            one_step_peizhi();//一键配wifi
        }
    }

    /**
     * 获取连接的wifi名称
     */
    private void initWifiName() {
        // TODO Auto-generated method stub
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : "";

        if (wifiId != null && !wifiId.contains("<unknown ss")) {//wifiId在不连WIFI的情况下，去wifi快配wifiId = 0x
            //取出双引号中的字符
            String reg = "\"";
            String[] ss = wifiId.split(reg);
            if (ss.length >= 2) {
                edit_wifi.setText(ss[1]);
                wifi_name = ss[1];
                edit_password_gateway.setFocusable(true);
                edit_password_gateway.setFocusableInTouchMode(true);
                edit_password_gateway.requestFocus();
            }
        }
    }

    /**
     * 初始化wifi连接，快配前wifi一定要连接上
     */
    private void initWifiConect() {
        //初始化连接wifi dialog对话框
        // TODO Auto-generated method stub
        //获取系统服务
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取状态
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        //判断wifi已连接的条件
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            one_step_peizhi();//一键配wifi
        } else {//wifi还没有连接上弹出alertDialog对话框
            showCenterDeleteDialog();
        }
    }

    /**
     * 添加红外模块配置入网成功后关闭进度圈
     */
    private ConnWifiInterfacer connWifiInterfacer;

    public interface ConnWifiInterfacer {
        void conn_wifi_over();

        void search_wifi_fail();

    }

    /**
     * 初始化dialog
     */
    private void initDialog() {
        // TODO Auto-generated method stub
        newFragment = ConfigDialogCameraFragment.newInstance(ConnWifiCameraActivity.this, "", "", new ConfigDialogCameraFragment.DialogClickListener() {

            @Override
            public void doRadioWifi() {//wifi快速配置

            }

            @Override
            public void doRadioScanDevice() {
                searchCamera();
                show_dialog_fragment();
            }

            @Override
            public void dialogDismiss() {
                updateListHandler.removeCallbacks(updateThread);
                updateListHandler.removeCallbacks(updateOneKeyThread);
                if (myWifiThread != null) {
                    blagg = false;
                }
//        progressdlg.dismiss();
                if (player != null) {
                    player.stop();
                }

                if (loader != null)
                    loader.StopSmartConnection();

                NativeCaller.StopSearch();
                doit_onekey = "isover";

                isOneKey = false;
                index_wifi = 0;

                list_wifi_camera.clear();
                SharedPreferencesUtil.saveData(ConnWifiCameraActivity.this,"list_wifi_camera",new ArrayList<>());
            }
        });//初始化快配和搜索设备dialogFragment
        connWifiInterfacer = (ConnWifiInterfacer) newFragment;
    }


    /**
     * 显示popupWindow
     */
    @SuppressLint("WrongConstant")
    private void showPopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View outerView = inflater.inflate(R.layout.wheel_view, null);

//        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        ListView wv = (ListView) outerView.findViewById(R.id.wheel_view_wv);
//        wv.setOffset(1);
//        wv.setItems(Arrays.asList(PLANETS));
//        wv.setSeletion(2);
//        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
//            @Override
//            public void onSelected(int selectedIndex, String item) {
//                Log.d(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
//            }
//        });

        final String[] data = {"类型一", "类型二", "类型三", "类型四"};
        ArrayAdapter<String> array = new ArrayAdapter<>(this,
                R.layout.simple_expandable_list_item_new, data);
        wv.setAdapter(array);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//水平
        // 初始化自定义的适配器
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;

        final PopupWindow window = new PopupWindow(outerView,
                displayWidth / 2,
                WindowManager.LayoutParams.WRAP_CONTENT);//高度写死

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;

        getWindow().setAttributes(lp);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.style_pop_animation);
        // 在底部显示
//        window.showAtLocation(WuYeTouSu_NewActivity.this.findViewById(R.id.tousu_select),
//                Gravity.CENTER_HORIZONTAL, 0, 0);
//        window.showAsDropDown(select_wlan_rel_big);

        // 将pixels转为dip
        int xoffInDip = pxTodip(displayWidth) / 4 * 3;

        window.showAsDropDown(select_wlan_rel_big, xoffInDip, xoffInDip / 3);
        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                System.out.println("popWindow消失");
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        wv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                tousu_txt1.setText(data[position]);
                window.dismiss();
            }
        });

    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int pxTodip(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
