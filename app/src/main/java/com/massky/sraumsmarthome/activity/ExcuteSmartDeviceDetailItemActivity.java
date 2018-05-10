package com.massky.sraumsmarthome.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview_new.OptionsPickerView;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/16.
 */

public class ExcuteSmartDeviceDetailItemActivity extends BaseActivity{
    @InjectView(R.id.back)
    ImageView back;
    private String deviceName;
    @InjectView(R.id.water_in_linear)
    LinearLayout water_in_linear;
    @InjectView(R.id.door_open_close_linear)
    LinearLayout door_open_close_linear;
    @InjectView(R.id.door_ringeye_linear)
    LinearLayout door_ringeye_linear;
    @InjectView(R.id.check_person_linear)
    LinearLayout check_person_linear;
    @InjectView(R.id.fordoor_close_linear)
    LinearLayout fordoor_close_linear;
    @InjectView(R.id.project_select)
    TextView project_select;
    @InjectView(R.id.check_verify_body_wrong_linear)
    LinearLayout check_verify_body_wrong_linear;
    @InjectView(R.id.check_code_txt)
    TextView check_code_txt;
    private OptionsPickerView pvNoLinkOptions;
    private ArrayList<String> clothes = new ArrayList<>();
    @InjectView(R.id.pepole_body_linear)
    LinearLayout pepole_body_linear;

    @Override
    protected int viewId() {
        return R.layout.excutesmartdevice_detailitemact;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        getNoLinkData();
//        initNoLinkOptionsPicker();
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        check_verify_body_wrong_linear.setOnClickListener(this);
    }

    @Override
    protected void onData() {//"主卫水浸","防盗门门磁","防盗门猫眼","人体监测","防盗门门锁"
            //,"漏水"
        deviceName = (String) getIntent().getSerializableExtra("name");
        switch (deviceName)  {
            case "主卫水浸":
                    water_in_linear.setVisibility(View.VISIBLE);
                break;
            case "防盗门门磁":
                door_open_close_linear.setVisibility(View.VISIBLE);
                break;
            case "防盗门猫眼":
                door_ringeye_linear.setVisibility(View.VISIBLE);
                break;
            case "人体检测":
                check_person_linear.setVisibility(View.VISIBLE);
                break;
            case "防盗门门锁":
                fordoor_close_linear.setVisibility(View.VISIBLE);
                break;
        }
        project_select.setText(deviceName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ExcuteSmartDeviceDetailItemActivity.this.finish();//
                break;
            case R.id.check_verify_body_wrong_linear://身份验证错误弹出框
                pvNoLinkOptions.show();
                break;
        }
    }

//    private void initNoLinkOptionsPicker() {// 不联动的多级选项
//        pvNoLinkOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
//
//            @Override
//            public void onOptionsSelect(int options1, int options2, int options3, View v) {
//
////                String str = "food:" + food.get(options1)
////                        + "\nclothes:" + clothes.get(options2)
////                        + "\ncomputer:" + computer.get(options3);
////
////                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
//                check_code_txt.setText(options1 + "");
//            }
//        }).setCancelColor(getResources().getColor(R.color.black))
//                .setSubmitColor(getResources().getColor(R.color.black))
//                .setSubCalSize(15)
//                .build();
//        pvNoLinkOptions.setPicker(clothes);
//    }

    private void getNoLinkData() {
        clothes.add("0");
        clothes.add("1");
        clothes.add("2");
        clothes.add("3");
        clothes.add("4");
        clothes.add("5");
    }
}
