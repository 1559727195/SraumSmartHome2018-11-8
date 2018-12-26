package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.lljjcoder.style.citylist.sortlistview.CharacterParser;
import com.lljjcoder.style.citylist.sortlistview.PinyinComparator;
import com.lljjcoder.style.citylist.sortlistview.SideBar;
import com.lljjcoder.style.citylist.sortlistview.SortAdapter;
import com.lljjcoder.style.citylist.sortlistview.SortModel;
import com.lljjcoder.utils.PinYinUtils;
import com.massky.sraum.R;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import com.yaokan.sdk.api.YkanSDKManager;
import com.yaokan.sdk.ir.YKanHttpListener;
import com.yaokan.sdk.ir.YkanIRInterface;
import com.yaokan.sdk.ir.YkanIRInterfaceImpl;
import com.yaokan.sdk.model.BaseResult;
import com.yaokan.sdk.model.Brand;
import com.yaokan.sdk.model.BrandResult;
import com.yaokan.sdk.model.DeviceDataStatus;
import com.yaokan.sdk.model.DeviceType;
import com.yaokan.sdk.model.DeviceTypeResult;
import com.yaokan.sdk.model.YKError;
import com.yaokan.sdk.utils.ProgressDialogUtils;
import com.yaokan.sdk.wifi.DeviceController;
import com.yaokan.sdk.wifi.listener.LearnCodeListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

public class YKCodeAPIActivity extends BaseActivity implements View.OnClickListener, LearnCodeListener {

    private ProgressDialogUtils dialogUtils;

    private YkanIRInterface ykanInterface;

    private String TAG = YKCodeAPIActivity.class.getSimpleName();

    private GizWifiDevice currGizWifiDevice;

    ListView sortListView;

    TextView mDialog;

    SideBar mSidrbar;

    private String deviceId;
    private List<Brand> brands = new ArrayList<Brand>(); // 品牌
    private DeviceType currDeviceType = null; // 当前设备类型
    private List<DeviceType> deviceType = new ArrayList<DeviceType>();// 设备类型
    private Brand currBrand = null; // 当前品牌
    private DeviceController driverControl = null;

    SimpleDateFormat simpleFormatter;
    private String tid = "";
    private List<SortModel> sourceDateList;
    private SortAdapter adapter;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    public PinYinUtils mPinYinUtils = new PinYinUtils();
    private RelativeLayout first_rel;
    private LinearLayout spand_list_linear;
    private DialogUtil dialogUtil;
    @InjectView(R.id.project_select)
    TextView project_select;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;


    @Override
    protected int viewId() {
        return R.layout.act_codeapi;
    }

    /**
     * 填充品牌数据源
     */
    private void setCityData() {

        if (brands == null || brands.size() == 0) {
            return;
        }
        int count = brands.size();
        String[] list = new String[count];
        for (int i = 0; i < count; i++)
            list[i] = brands.get(i).getName();

        sourceDateList.addAll(filledData());
        // 根据a-z进行排序源数据
        Collections.sort(sourceDateList, pinyinComparator);


//        for(int i = 0; i < sourceDateList.size(); i++) {
//            if (sourceDateList.get(i).getSortLetters().equals("@")) {
//                sourceDateList.get(i).setSortLetters("常用");
//            }
//        }

//        adapter.notifyDataSetChanged();
//        sourceDateList = new ArrayList<SortModel>();
        adapter = new SortAdapter(YKCodeAPIActivity.this, sourceDateList);//Brand [bid=104, name=格力, common=1]
        sortListView.setAdapter(adapter);
    }


    /**
     * 为ListView填充数据
     *
     * @return
     */
    private List<SortModel> filledData() {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < brands.size(); i++) {

            Brand result = brands.get(i);

            if (result != null) {

                SortModel sortModel = new SortModel();

                String cityName = result.getName();

                if (result.getCommon() == 1) {//1为常用的把，不用转换成拼音，直接放在首项，
                    sortModel.setName(cityName);
                    sortModel.setSortLetters("@");
                    sortModel.setBid(result.getBid());
                    mSortList.add(sortModel);
                } else {//非常用的汉字转换成拼音
                    //汉字转换成拼音
                    if (!TextUtils.isEmpty(cityName) && cityName.length() > 0) {

                        String pinyin = "";
                        if (cityName.equals("重庆市")) {
                            pinyin = "chong";
                        } else if (cityName.equals("长沙市")) {
                            pinyin = "chang";
                        } else if (cityName.equals("长春市")) {
                            pinyin = "chang";
                        } else {
                            pinyin = mPinYinUtils.getStringPinYin(cityName.substring(0, 1));
                        }

                        if (!TextUtils.isEmpty(pinyin)) {

                            sortModel.setName(cityName);
                            sortModel.setBid(result.getBid());
                            String sortString = pinyin.substring(0, 1).toUpperCase();

                            // 正则表达式，判断首字母是否是英文字母
                            if (sortString.matches("[A-Z]")) {
                                sortModel.setSortLetters(sortString.toUpperCase());
                            } else {
                                sortModel.setSortLetters("#");
                            }
                            mSortList.add(sortModel);
                        } else {
                            Log.d("citypicker_log", "null,cityName:-> " + cityName + "       pinyin:-> " + pinyin);
                        }
                    }
                }

            }
        }
        return mSortList;
    }


    /**
     * 初始化品牌选择器
     */
    private void initList() {
        sourceDateList = new ArrayList<SortModel>();
        adapter = new SortAdapter(YKCodeAPIActivity.this, sourceDateList);
        sortListView.setAdapter(adapter);

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mSidrbar.setTextView(mDialog);
        //设置右侧触摸监听
        mSidrbar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cityName = ((SortModel) adapter.getItem(position)).getName();
//                cityInfoBean = CityInfoBean.findCity(cityListInfo, cityName);
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("cityinfo", cityInfoBean);
//                intent.putExtras(bundle);
//                setResult(RESULT_OK, intent);
//                finish();
                SortModel sortModel = sourceDateList.get(position);
                Map map = new HashMap();
                map.put("tid", tid);
                map.put("bid", sortModel.getBid());
                map.put("name", sortModel.getName());
//                intent.putExtra("tid", getIntent().getSerializableExtra("tid"));
//                intent.putExtra("number", getIntent().getSerializableExtra("tid"));
                map.put("number", getIntent().getSerializableExtra("number"));
                Intent intent = new Intent(YKCodeAPIActivity.this, RemoteControlMatchingActivity.class);
                intent.putExtra("currBrand", (Serializable) map);
                intent.putExtra("GizWifiDevice", currGizWifiDevice);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        String name = "";
        tid = (String) getIntent().getSerializableExtra("tid");
        switch (tid) {
            case "2":
                name = "电视机";
                break;
            case "7":
                name = "空调";
                break;
        }
        project_select.setText("选择" + name + "品牌");//选择电视机

    }

    private void initDevice() {
        currGizWifiDevice = (GizWifiDevice) getIntent().getParcelableExtra(
                "GizWifiDevice");
        if (currGizWifiDevice != null) {
            deviceId = currGizWifiDevice.getDid();
            // 在下载数据之前需要设置设备ID，用哪个设备去下载
            YkanSDKManager.getInstance().setDeviceId(deviceId);
            if (currGizWifiDevice.isSubscribed() == false) {
                currGizWifiDevice.setSubscribe(true);
            }
        }
    }


    private void initView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        mDialog = (TextView) findViewById(R.id.dialog);
        mSidrbar = (SideBar) findViewById(R.id.sidrbar);
//        first_rel = (RelativeLayout) findViewById(R.id.first_rel);
        spand_list_linear = (LinearLayout) findViewById(R.id.spand_list_linear);
        dialogUtils = new ProgressDialogUtils(this);
        dialogUtil = new DialogUtil(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onView() {
        // 遥控云数据接口分装对象对象
        ykanInterface = new YkanIRInterfaceImpl(getApplicationContext());
        // 遥控云数据接口分装对象对象
        ykanInterface = new YkanIRInterfaceImpl(getApplicationContext());
        simpleFormatter = new SimpleDateFormat("HH:mm:ss");
        initView();
        initDevice();
//        List<GizWifiDevice> gizWifiDevices = DeviceManager
//                .instanceDeviceManager(getApplicationContext()).getCanUseGizWifiDevice();
//        if (gizWifiDevices != null) {
//            Log.e("YKCodeAPIActivity", gizWifiDevices.size() + "");
//        }
        initData();
        initList();
//        setCityData(CityListLoader.getInstance().getCityListData());
        get_device_and_brand();
    }


    @Override
    public void onClick(View v) {
        //new DownloadThread(v.getId()).start();
        switch (v.getId()) {
            case R.id.back:
                YKCodeAPIActivity.this.finish();
                break;
        }
    }

    @Override
    public void didReceiveData(DeviceDataStatus dataStatus, String data) {
        switch (dataStatus) {
            case DATA_SAVE_SUCCESS:
                Toast.makeText(getApplicationContext(), "写入成功", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     * 获取遥控器设备和品牌列表
     */
    public void get_device_and_brand() {
        new DownloadThread("getDeviceType").start();
    }

    class DownloadThread extends Thread {
        private String doit;
        String result = "";

        public DownloadThread(String doit) {
            this.doit = doit;
        }

        @Override
        public void run() {
//            dialogUtils.sendMessage(1);
            final Message message = mHandler.obtainMessage();
            switch (doit) {
                case "getDeviceType"://获取设备列表
                    if (ykanInterface == null) {
                        return;
                    }

                    if (currGizWifiDevice == null ||currGizWifiDevice.getMacAddress() == null) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogUtil.loadDialog();
                        }
                    });
                    ykanInterface.getDeviceType(currGizWifiDevice.getMacAddress(), new YKanHttpListener() {
                        @Override
                        public void onSuccess(BaseResult baseResult) {
                            DeviceTypeResult deviceResult = (DeviceTypeResult) baseResult;
                            message.what = 0;
                            deviceType = deviceResult.getRs();//获取设备类型
                            currDeviceType = deviceType.get(0);
                            Log.d(TAG, " getDeviceType result:" + result);
                            mHandler.sendMessage(message);
                        }

                        @Override
                        public void onFail(YKError ykError) {
                            Log.e(TAG, "ykError:" + ykError.toString());
                            common_end_handler(message);
                        }
                    });
                    break;
                case "getBrandByType"://获取设备品牌集合
                    // int type = 7 ;//1:机顶盒，2：电视机
                    if (currDeviceType != null) { //DeviceType [tid=7, name=空调],DeviceType [tid=2, name=电视机]
                        ykanInterface
                                .getBrandsByType(currGizWifiDevice.getMacAddress(), Integer.parseInt(tid), new YKanHttpListener() {
                                    @Override
                                    public void onSuccess(BaseResult baseResult) {
                                        BrandResult brandResult = (BrandResult) baseResult;
                                        brands = brandResult.getRs();
                                        message.what = 1;
                                        Log.d(TAG, " getBrandByType result:" + brandResult);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                setCityData();
                                            }
                                        });
                                        mHandler.sendMessage(message);
                                        common_end_handler(message);
                                    }

                                    @Override
                                    public void onFail(YKError ykError) {
                                        Log.e(TAG, "ykError:" + ykError.toString());
                                        common_end_handler(message);
                                    }
                                });
                    } else {
                        result = "请调用获取设备接口";
                    }
                    break;
                default:
                    break;
            }
//            message.obj = result;
        }
    }

    private void common_end_handler(Message message) {
//            dialogUtils.sendMessage(0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogUtil.removeDialog();
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://获取设备列表成功
                    new DownloadThread("getBrandByType").start();
                    break;
                case 1://获取品牌列表成功

                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (driverControl != null) {
            driverControl.learnStop433or315();
            driverControl.learnStop();
        }
    }
}
