package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.massky.sraum.R;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
import com.wheel.Utils;
import com.wheel.widget.TosAdapterView;
import com.wheel.widget.TosGallery;
import com.wheel.widget.WheelView;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by masskywcy on 2016-11-14.
 */

public class SelectPmDataActivity extends BaseActivity {
    //    @InjectView(R.id.datePicker)
//    DatePicker datePicker;
    private int yearb, monthb, dayb;
    @InjectView(R.id.wheel1)
    WheelView wheel1;
    @InjectView(R.id.wheel2)
    WheelView wheel2;
    @InjectView(R.id.wheel3)
    WheelView wheel3;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.project_select)
    TextView project_select;
    int[] mData = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    String text_pm = "";
    private Map map_link = new HashMap();
    private String condition = "0";

    @Override
    protected int viewId() {
        return R.layout.select_pm_data;
    }

    View mDecorView = null;

    boolean mStart = false;

    private TosAdapterView.OnItemSelectedListener mListener = new TosAdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(TosAdapterView<?> parent, View view, int position, long id) {
            formatData();
        }

        @Override
        public void onNothingSelected(TosAdapterView<?> parent) {

        }
    };


    private void formatData() {
        int pos1 = wheel1.getSelectedItemPosition();
        int pos2 = wheel2.getSelectedItemPosition();
        int pos3 = wheel3.getSelectedItemPosition();

        if (pos1 == 0) {
            text_pm = String.format("%d%d", pos2, pos3);
            if (pos2 == 0) {
                text_pm = String.format("%d", pos3);
            } else {
                text_pm = String.format("%d%d", pos2, pos3);
            }
        } else {
            text_pm = String.format("%d%d%d", pos1, pos2, pos3);
        }
        Log.e("robin debug", "text_pm:" + text_pm);
//        mTextView.setText(text);
//        condition = "1";
    }


    @Override
    protected void onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            statusView.setBackgroundColor(Color.BLACK);
        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        init_data();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    private void init_data() {
        init_view();
        map_link = (Map) getIntent().getSerializableExtra("map_link");
        if (map_link == null) return;
        switch (map_link.get("pm_action").toString()) {
            case "0":
                init_common_data("温度", 0, 2, 6);
                condition = "2";
                break;
            case "1":
                condition = "3";
                init_common_data("湿度", 0, 5, 0);
                break;
            case "2":
                condition = "1";
                init_common_data("PM2.5", 1, 0, 0);
                break;
        }
    }

    private void init_common_data(String s, int one, int two, int thee) {
        project_select.setText(s);
//        wheel1.setSelection(9);
//        wheel2.setSelection(9);
//        wheel3.setSelection(9);
        wheel1.setSelection(one);
        wheel2.setSelection(two);
        wheel3.setSelection(thee);
    }

    private void init_view() {
        wheel1.setScrollCycle(true);
        wheel2.setScrollCycle(true);
        wheel3.setScrollCycle(true);

        wheel1.setAdapter(new NumberAdapter());
        wheel2.setAdapter(new NumberAdapter());
        wheel3.setAdapter(new NumberAdapter());

        wheel1.setOnItemSelectedListener(mListener);
        wheel2.setOnItemSelectedListener(mListener);
        wheel3.setOnItemSelectedListener(mListener);


//        formatData();

        mDecorView = getWindow().getDecorView();
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SelectPmDataActivity.this.finish();
                break;
            case R.id.next_step_txt:
                result_selectpmdata();
                break;
        }
    }

    private void result_selectpmdata() {
        boolean add_condition = (boolean) SharedPreferencesUtil.getData(SelectPmDataActivity.this, "add_condition", false);
        Intent intent = null;
        map_link.put("condition", condition);
//                map_link.put("pm_condition", "0");
        String temp = "";
        switch (map_link.get("pm_condition").toString()) {
            case "0":
                temp = "大于等于 ";
                break;
            case "1":
                temp = "小于等于 ";
                break;
        }
        switch (map_link.get("pm_condition").toString()) {
            case "0":

                map_link.put("maxValue", text_pm);
                break;
            case "1":

                map_link.put("minValue", text_pm);
                break;
        }

        switch (map_link.get("pm_action").toString()) {
            case "0":
                map_link.put("action", "温度 " + temp + text_pm + "℃");
                break;
            case "1":
                map_link.put("action", "湿度 " + temp + text_pm + "%");
                break;
            case "2":
                map_link.put("action", "PM2.5 " + temp + text_pm);
                break;
        }


        if (add_condition) { //
//                    AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
            //                AppManager.getAppManager().finishActivity_current(AirLinkageControlActivity.class);
//                AppManager.getAppManager().finishActivity_current(SelectiveLinkageDeviceDetailSecondActivity.class);
            AppManager.getAppManager().finishActivity_current(SelectSensorActivity.class);
            AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity.class);
            intent = new Intent(SelectPmDataActivity.this, EditLinkDeviceResultActivity.class);
            intent.putExtra("sensor_map", (Serializable) map_link);
            startActivity(intent);
            SelectPmDataActivity.this.finish();
        } else { //
            intent = new Intent(SelectPmDataActivity.this,
                    SelectiveLinkageActivity.class);
            intent.putExtra("link_map", (Serializable) map_link);
            startActivity(intent);
        }
    }

    private class NumberAdapter extends BaseAdapter {
        int mHeight = 50;

        public NumberAdapter() {
            mHeight = (int) Utils.pixelToDp(SelectPmDataActivity.this, mHeight);
        }

        @Override
        public int getCount() {
            return (null != mData) ? mData.length : 0;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txtView = null;

            if (null == convertView) {
                convertView = new TextView(SelectPmDataActivity.this);
                convertView.setLayoutParams(new TosGallery.LayoutParams(-1, mHeight));
                txtView = (TextView) convertView;
                txtView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                txtView.setTextColor(Color.GRAY);
                txtView.setGravity(Gravity.CENTER);
            }

            String text = String.valueOf(mData[position]);
            if (null == txtView) {
                txtView = (TextView) convertView;
            }
            txtView.setText(text);
            return convertView;
        }
    }
}
