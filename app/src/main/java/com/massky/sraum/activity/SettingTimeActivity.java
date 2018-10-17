package com.massky.sraum.activity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import butterknife.InjectView;

/**
 * Created by zhu on 2017/12/29.
 */

public class SettingTimeActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;
    private TimePickerView pvCustomTime;
    @InjectView(R.id.sleep_time_rel)
    RelativeLayout sleep_time_rel;
    @InjectView(R.id.get_up_rel)
    RelativeLayout get_up_rel;
    @InjectView(R.id.sleep_time_txt)
    TextView sleep_time_txt;
    @InjectView(R.id.get_up_time_txt)
    TextView get_up_time_txt;
    @InjectView(R.id.wode_setting)
    TextView wode_setting;
    @Override
    protected int viewId() {
        return R.layout.setting_time_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        initCustomTimePicker();
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        sleep_time_rel.setOnClickListener(this);
        get_up_rel.setOnClickListener(this);
        wode_setting.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

//    private void init_permissions() {
//
//        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
//        RxPermissions permissions = new RxPermissions(this);
//        permissions.request(Manifest.permission.CAMERA).subscribe(new Observer<Boolean>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Boolean aBoolean) {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SettingTimeActivity.this.finish();
                break;
            case R.id.sleep_time_rel://睡觉
                pvCustomTime.show(); //弹出自定义时间选择器
                break;
            case R.id.get_up_rel://起床
                pvCustomTime.show(); //弹出自定义时间选择器
                break;
            case R.id.wode_setting:
                SettingTimeActivity.this.finish();
                break;
        }
    }

    private void initCustomTimePicker() {

        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(2014, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2027, 2, 28);
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                getTime(date);
                Log.e("robin debug","getTime(date):" + getTime(date));
                String  hourPicker = String.valueOf(date.getHours());
                String  minutePicker = String.valueOf(date.getMinutes());
                switch (String.valueOf(minutePicker).length()) {
                    case 1:
                        minutePicker = "0" + minutePicker;
                        break;
                }

                switch (String.valueOf(hourPicker).length()) {
                    case 1:
                        hourPicker = "0" + hourPicker;
                        break;
                }
//                start_scenetime.setText(hourPicker + ":" + minutePicker);
            }
        })
                /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
               /*.setDividerColor(Color.WHITE)//设置分割线的颜色
                .setTextColorCenter(Color.LTGRAY)//设置选中项的颜色
                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setTitleBgColor(Color.DKGRAY)//标题背景颜色 Night mode
                .setBgColor(Color.BLACK)//滚轮背景颜色 Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)*/
               /*.gravity(Gravity.RIGHT)// default is center*/
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final ImageView tvSubmit = (ImageView) v.findViewById(R.id.ok_bt);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.finish_bt);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });

                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setContentSize(18)
                .setType(new boolean[]{false, false, false, true, true, false})
                .setLabel("年", "月", "日", "小时", "分钟", "秒")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .build();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
