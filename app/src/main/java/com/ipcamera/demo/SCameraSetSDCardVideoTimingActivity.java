package com.ipcamera.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.Util.DialogUtil;
import com.base.Basecactivity;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.ipcamera.demo.bean.DefenseConstant;
import com.ipcamera.demo.utils.SensorTimeUtil;
import com.massky.sraum.R;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import butterknife.InjectView;

public class SCameraSetSDCardVideoTimingActivity extends Basecactivity implements
        OnClickListener {
    private LinearLayout timing_backlayout; //返回
    private TextView timing_textView1, timing_textView2; //开始时间结束时间
    private TimePicker timing_timePicker1, timing_timePicker2; //时间选择器
    private CheckBox timing_id1, timing_id2, timing_id3, timing_id4, timing_id5, timing_id6, timing_id7; //选择星期
    private Button timing_start_delete, timing_start_save, timing_save;
    private String startTime = "00:00", endTime = "24:00";
    private int status = 1;
    private TreeSet<Integer> dateset;
    private int type = 0;
    private int value, key;
    private int absValue;
    private LinearLayout timing_edit_layout;  //编辑布局
    private TextView tv_camera_timingaddplan; //标题
    @InjectView(R.id.status_view)
    StatusView statusView;
    private DialogUtil dialogUtil;
    @InjectView(R.id.sleep_time_rel)
    RelativeLayout sleep_time_rel;
    @InjectView(R.id.get_up_rel)
    RelativeLayout get_up_rel;
    private String hourPicker;
    private String minutePicker;
    String time_content;
    @InjectView(R.id.start_time_txt)
    TextView start_time_txt;
    @InjectView(R.id.end_time_txt)
    TextView end_time_txt;
    @InjectView(R.id.time_select_linear)
    LinearLayout time_select_linear;
    private TimePickerView pvCustomTime;
    private int index_select;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.back)
    TextView back;

//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.videotape_timing_setting_act);
//		getDate();
//		findview();
//	}

    @Override
    protected int viewId() {
        return R.layout.videotape_timing_setting_act;
    }

    @Override
    protected void onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            statusView.setBackgroundColor(Color.BLACK);
        }
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        getDate();
        findview();
        onEvent();
    }

    private void onEvent() {
        initCustomTimePicker();
        sleep_time_rel.setOnClickListener(this);
        get_up_rel.setOnClickListener(this);
        startTime = start_time_txt.getText().toString();
        endTime = end_time_txt.getText().toString();
        next_step_txt.setOnClickListener(this);
        back.setOnClickListener(this);
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
                Log.e("robin debug", "getTime(date):" + getTime(date));
                hourPicker = String.valueOf(date.getHours());
                minutePicker = String.valueOf(date.getMinutes());
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

                time_content = hourPicker + ":" + minutePicker;
                handler.sendEmptyMessage(index_select);
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
                        tvSubmit.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });

                        ivCancel.setOnClickListener(new OnClickListener() {
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

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    start_time_txt.setText(time_content);
                    startTime = start_time_txt.getText().toString();
                    break;//开始
                case 1:
                    end_time_txt.setText(time_content);
                    endTime = end_time_txt.getText().toString();
                    break;//结束
            }
        }
    };

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }


    private void getDate() {
        // TODO Auto-generated method stub
        Intent it = getIntent();
        type = it.getIntExtra("type", 0);
        absValue = it.getIntExtra("value", 0);
        value = Math.abs(absValue);
        key = it.getIntExtra("key", 0);
        dateset = new TreeSet<Integer>(new Comparator<Integer>() {

            @Override
            public int compare(Integer c1, Integer c2) {
                // TODO Auto-generated method stub
                if (c1 > c2) {
                    return -1;
                } else if (c1 == c2) {
                    return 0;
                } else {
                    return 1;
                }

            }

        });
    }

    //初始化数据
    public void findview() {
        timing_backlayout = (LinearLayout) findViewById(R.id.timing_backlayout);
        timing_textView1 = (TextView) findViewById(R.id.timing_textView1);
        timing_textView2 = (TextView) findViewById(R.id.timing_textView2);
        timing_timePicker1 = (TimePicker) findViewById(R.id.timing_timePicker1);
        timing_timePicker2 = (TimePicker) findViewById(R.id.timing_timePicker2);
        timing_id1 = (CheckBox) findViewById(R.id.timing_id1);
        timing_id2 = (CheckBox) findViewById(R.id.timing_id2);
        timing_id3 = (CheckBox) findViewById(R.id.timing_id3);
        timing_id4 = (CheckBox) findViewById(R.id.timing_id4);
        timing_id5 = (CheckBox) findViewById(R.id.timing_id5);
        timing_id6 = (CheckBox) findViewById(R.id.timing_id6);
        timing_id7 = (CheckBox) findViewById(R.id.timing_id7);
        timing_start_delete = (Button) findViewById(R.id.timing_start_delete);
        timing_start_save = (Button) findViewById(R.id.timing_start_save);
        timing_save = (Button) findViewById(R.id.timing_save);
        timing_edit_layout = (LinearLayout) findViewById(R.id.timing_edit_layout);
        tv_camera_timingaddplan = (TextView) findViewById(R.id.tv_camera_timingaddplan);
        timing_textView1.setText(getResources().getString(
                R.string.camera_defense_starttime)
                + ":" + startTime);
        timing_textView2.setText(getResources().getString(
                R.string.camera_defense_endtime)
                + ":" + endTime);
        //开始时间
        timing_timePicker1.setIs24HourView(true);
        timing_timePicker1.setCurrentHour(0);
        timing_timePicker1.setCurrentMinute(0);
        timing_timePicker1.setOnTimeChangedListener(new OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String myMinute = "00";
                String myHour = "00";
                if (hourOfDay < 10) {
                    myHour = "0" + hourOfDay;
                } else {
                    myHour = hourOfDay + "";
                }
                if (minute < 10) {
                    myMinute = "0" + minute;
                } else {
                    myMinute = "" + minute;
                }

                startTime = myHour + ":" + myMinute;
                timing_textView1.setText(getResources().getString(
                        R.string.camera_defense_starttime)
                        + ":" + startTime);
            }
        });
        //结束时间
        timing_timePicker2.setIs24HourView(true);
        timing_timePicker2.setCurrentHour(0);
        timing_timePicker2.setCurrentMinute(0);
        timing_timePicker2.setOnTimeChangedListener(new OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String myMinute = "00";
                String myHour = "00";
                if (hourOfDay < 10) {
                    myHour = "0" + hourOfDay;
                } else {
                    myHour = hourOfDay + "";
                }
                if (minute < 10) {
                    myMinute = "0" + minute;
                } else {
                    myMinute = "" + minute;
                }

                endTime = myHour + ":" + myMinute;

                if (hourOfDay == 0 && minute == 0) {
                    endTime = "24:00";
                }
                timing_textView2.setText(getResources().getString(
                        R.string.camera_defense_endtime)
                        + ":" + endTime);
            }
        });

        if (type == 0) {
            tv_camera_timingaddplan.setText(getResources().getString(
                    R.string.add_period));
        } else {
            timing_edit_layout.setVisibility(View.VISIBLE);
            timing_save.setVisibility(View.GONE);
            tv_camera_timingaddplan.setText(getResources().getString(
                    R.string.edit_valid_time));
            if (value != 0) {
                int bStarttime = value & 0x7ff;
                int bEndTime = (value >> 12) & 0x7ff;
                int tp1H = getTimeHour(bStarttime);
                int tp1M = getTimeMinute(bStarttime);
                timing_timePicker1.setCurrentHour(tp1H);
                timing_timePicker1.setCurrentMinute(tp1M);
                int tp2H = getTimeHour(bEndTime);
                int tp2M = getTimeMinute(bEndTime);
                timing_timePicker2.setCurrentHour(tp2H);
                timing_timePicker2.setCurrentMinute(tp2M);
                String myHour;
                if (tp1H < 10) {
                    myHour = "0" + tp1H;
                } else {
                    myHour = tp1H + "";
                }
                String myMinute;
                if (tp1M < 10) {
                    myMinute = "0" + tp1M;
                } else {
                    myMinute = "" + tp1M;
                }


                String myHour1;
                if (tp2H < 10) {
                    myHour1 = "0" + tp2H;
                } else {
                    myHour1 = tp2H + "";
                }
                String myMinute1;
                if (tp2M < 10) {
                    myMinute1 = "0" + tp2M;
                } else {
                    myMinute1 = "" + tp2M;
                }
                start_time_txt.setText(myHour + ":" + myMinute);
                end_time_txt.setText(myHour1 + ":" + myMinute1);
                startTime = tp1H + ":" + tp1M;
                endTime = tp2H + ":" + tp2M;
            }
        }
        timing_backlayout.setOnClickListener(this);
        timing_start_delete.setOnClickListener(this);
        timing_start_save.setOnClickListener(this);
        timing_save.setOnClickListener(this);
        CheckBoxListener listener = new CheckBoxListener();
        timing_id1.setOnCheckedChangeListener(listener);
        timing_id2.setOnCheckedChangeListener(listener);
        timing_id3.setOnCheckedChangeListener(listener);
        timing_id4.setOnCheckedChangeListener(listener);
        timing_id5.setOnCheckedChangeListener(listener);
        timing_id6.setOnCheckedChangeListener(listener);
        timing_id7.setOnCheckedChangeListener(listener);
        if (type == 1) {
            getWeekPlan(value);
        }
    }

    private void getWeekPlan(int time) {
        for (int i = 24; i < 31; i++) {
            int weeks = (time >> i) & 1;
            switch (i) {
                case 24:
                    if (weeks == 1) {
                        timing_id7.setChecked(true);
                    } else {
                        timing_id7.setChecked(false);
                    }
                    break;
                case 25:
                    if (weeks == 1) {
                        timing_id1.setChecked(true);
                    } else {
                        timing_id1.setChecked(false);
                    }
                    break;
                case 26:
                    if (weeks == 1) {
                        timing_id2.setChecked(true);
                    } else {
                        timing_id2.setChecked(false);
                    }
                    break;
                case 27:
                    if (weeks == 1) {
                        timing_id3.setChecked(true);
                    } else {
                        timing_id3.setChecked(false);
                    }
                    break;
                case 28:
                    if (weeks == 1) {
                        timing_id4.setChecked(true);
                    } else {
                        timing_id4.setChecked(false);
                    }
                    break;
                case 29:
                    if (weeks == 1) {
                        timing_id5.setChecked(true);
                    } else {
                        timing_id5.setChecked(false);
                    }
                    break;
                case 30:
                    if (weeks == 1) {
                        timing_id6.setChecked(true);
                    } else {
                        timing_id6.setChecked(false);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    private int getTimeMinute(int time) {
        if (time < 60) {
            return time;
        }
        int h = time / 60;
        int m = time - (h * 60);
        return m;

    }

    private int getTimeHour(int time) {
        if (time < 60) {
            return 0;
        }
        int h = time / 60;
        return h;

    }

    /**
     * 得到timePicker里面的android.widget.NumberPicker组件
     * （有两个android.widget.NumberPicker组件--hour，minute）
     *
     * @param viewGroup
     * @return
     */
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;

        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }

        return npList;
    }

    class CheckBoxListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
            // TODO Auto-generated method stub
            CompoundButton box = (CompoundButton) arg0;
            switch (box.getId()) {
                case R.id.timing_id1:
                    if (box.isChecked()) {
                        timing_id1.setTextColor(getResources().getColor(
                                R.color.color_startcode_bg));
                        dateset.add(1);
                    } else {
                        timing_id1.setTextColor(getResources().getColor(
                                R.color.color_alarm_textcolor));
                        if (dateset.contains(1)) {
                            dateset.remove(1);
                        }
                    }
                    break;
                case R.id.timing_id2:
                    if (box.isChecked()) {
                        timing_id2.setTextColor(getResources().getColor(
                                R.color.color_startcode_bg));
                        dateset.add(2);
                    } else {
                        timing_id2.setTextColor(getResources().getColor(
                                R.color.color_alarm_textcolor));
                        if (dateset.contains(2)) {
                            dateset.remove(2);
                        }
                    }
                    break;
                case R.id.timing_id3:
                    if (box.isChecked()) {
                        dateset.add(3);
                        timing_id3.setTextColor(getResources().getColor(
                                R.color.color_startcode_bg));
                    } else {
                        timing_id3.setTextColor(getResources().getColor(
                                R.color.color_alarm_textcolor));
                        if (dateset.contains(3)) {
                            dateset.remove(3);
                        }
                    }
                    break;
                case R.id.timing_id4:
                    if (box.isChecked()) {
                        dateset.add(4);
                        timing_id4.setTextColor(getResources().getColor(
                                R.color.color_startcode_bg));
                    } else {
                        timing_id4.setTextColor(getResources().getColor(
                                R.color.color_alarm_textcolor));
                        if (dateset.contains(4)) {
                            dateset.remove(4);
                        }
                    }
                    break;
                case R.id.timing_id5:
                    if (box.isChecked()) {
                        dateset.add(5);
                        timing_id5.setTextColor(getResources().getColor(
                                R.color.color_startcode_bg));
                    } else {
                        timing_id5.setTextColor(getResources().getColor(
                                R.color.color_alarm_textcolor));
                        if (dateset.contains(5)) {
                            dateset.remove(5);
                        }
                    }
                    break;
                case R.id.timing_id6:
                    if (box.isChecked()) {
                        dateset.add(6);
                        timing_id6.setTextColor(getResources().getColor(
                                R.color.color_startcode_bg));
                    } else {
                        timing_id6.setTextColor(getResources().getColor(
                                R.color.color_alarm_textcolor));
                        if (dateset.contains(6)) {
                            dateset.remove(6);
                        }
                    }
                    break;
                case R.id.timing_id7:
                    if (box.isChecked()) {
                        dateset.add(0);
                        timing_id7.setTextColor(getResources().getColor(
                                R.color.color_startcode_bg));
                    } else {
                        timing_id7.setTextColor(getResources().getColor(
                                R.color.color_alarm_textcolor));
                        if (dateset.contains(0)) {
                            dateset.remove(0);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private int passStartTime, passEndTime;

    private void checkTime() {
        // TODO Auto-generated method stub

        Log.e("vst", "startTime:" + startTime + ",endTime:" + endTime
                + "date:" + dateset.toString());
        // if (startTime.equals(endTime) || startTime == endTime) {
        // showToast(getResources().getString(R.string.defense_sametime));
        // return;
        // }
        startTime = start_time_txt.getText().toString();
        endTime = end_time_txt.getText().toString();

        String[] start_str = startTime.split(":");

        String[] end_str = endTime.split(":");
//		passStartTime = SensorTimeUtil.getPassMinutes(timing_timePicker1.getCurrentHour(),
//				timing_timePicker1.getCurrentMinute());
        passStartTime = SensorTimeUtil.getPassMinutes(Integer.parseInt(start_str[0]), Integer.parseInt(start_str[1]));
        passEndTime = 0;
//		int endtimeH = timing_timePicker2.getCurrentHour();
//		int endtimeM = timing_timePicker2.getCurrentMinute();
        int endtimeH = Integer.parseInt(end_str[0]);
        int endtimeM = Integer.parseInt(end_str[1]);
        if (endtimeH == 0 && endtimeM == 0) {
            passEndTime = SensorTimeUtil.getPassMinutes(24, 0);
        } else {
//			passEndTime = SensorTimeUtil.getPassMinutes(timing_timePicker2.getCurrentHour(),
//					timing_timePicker2.getCurrentMinute());
            passEndTime = SensorTimeUtil.getPassMinutes(Integer.parseInt(end_str[0]),
                    Integer.parseInt(end_str[1]));
        }
        Log.e("vst", "passStartTime" + passStartTime + "*passEndTime*" + passEndTime);
        if (passEndTime <= passStartTime) {
            //判断结束时间大于开始时间
            //showToast(getResources().getString(R.string.defense_timeoutride));
            //return;
        }

        if (dateset.size() == 0) {
            showToast(getResources().getString(R.string.defense_nodate));
            return;
        }

        // if(SensorTimeUtil.checkTime(startTime, endTime)){
        // showToast("该时间内已经有其他计划了");
        // return;
        // }

        // getPlanTime();
        getPlanTimerInt();

    }

    private void getPlanTimerInt() {

        String weeks = "0000000";
        if (dateset.size() != 0) {
            Iterator it = dateset.iterator();
            while (it.hasNext()) {
                int weekday = (Integer) it.next();
                weeks = SensorTimeUtil.replaceIndex(6 - weekday, weeks);
            }
        }

        Log.e("vst", "weeks" + weeks);
        String string23 = SensorTimeUtil.getMinutesString(passEndTime,
                passStartTime);
        String string32 = SensorTimeUtil.get2Strings(0 + "", weeks, string23);
        int int32 = SensorTimeUtil.string32toInt(string32);

        Intent its = new Intent();
        if (type == 1) {
            if (absValue < 0)
                int32 = 0 - int32;
            its.putExtra("jnitime", int32);
            its.putExtra("key", key);
            setResult(2016, its);
            finish();
        } else {
            its.putExtra("jnitime", int32);
            setResult(2015, its);
            finish();
        }
    }


    private String getMapKey(int day) {
        String key = "";
        switch (day) {
            case 1:
                key = DefenseConstant.key_Monday;
                break;
            case 2:
                key = DefenseConstant.key_Tuesday;
                break;
            case 3:
                key = DefenseConstant.key_Wednesday;
                break;
            case 4:
                key = DefenseConstant.key_Thursday;
                break;
            case 5:
                key = DefenseConstant.key_Friday;
                break;
            case 6:
                key = DefenseConstant.key_Saturday;
                break;
            case 7:
                key = DefenseConstant.key_Sunday;
                break;

            default:
                break;
        }
        return key;
    }

    public void showToast(String t) {
        Toast.makeText(SCameraSetSDCardVideoTimingActivity.this, t, 1000).show();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.timing_backlayout:
                finish();
                break;
            case R.id.timing_start_delete:
                showDefaultSetDialog();
                break;
            case R.id.timing_start_save:
                checkTime();
                break;
            case R.id.timing_save:
            case R.id.next_step_txt:

                checkTime();
//			Toast.makeText(
//					CameraSetSDTiming.this,
//					CameraSetSDTiming.this.getResources().getString(
//							R.string.camera_function_notsupport), Toast.LENGTH_SHORT)
//					.show();
//			finish();
                break;

            case R.id.sleep_time_rel://睡觉
                index_select = 1;
                pvCustomTime.show(); //弹出自定义时间选择器
                break;
            case R.id.get_up_rel://起床
                index_select = 0;
                pvCustomTime.show(); //弹出自定义时间选择器
                break;
            default:
                break;
            case R.id.back:
                SCameraSetSDCardVideoTimingActivity.this.finish();
                break;
        }
    }


    private AlertDialog dialog = null;

    private void showDefaultSetDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(SCameraSetSDCardVideoTimingActivity.this);
        builder.setMessage(R.string.del_ok);
        builder.setPositiveButton(R.string.str_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent its = new Intent();
                        its.putExtra("key", key);
						setResult(2017, its);
//                        setResult(2013, its);
                        finish();
                    }
                });
        builder.setNegativeButton(R.string.str_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();

                    }
                });
        dialog = builder.create();
        dialog.show();

    }

}
