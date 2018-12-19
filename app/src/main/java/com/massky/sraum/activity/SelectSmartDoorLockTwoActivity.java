package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import java.util.HashMap;
import java.util.Map;
import butterknife.InjectView;

/**
 * Created by zhu on 2018/5/30.
 */

public class SelectSmartDoorLockTwoActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.next_step_id)
    Button next_step_id;
    @InjectView(R.id.open_door_lock)
    TextView open_door_lock;
    @InjectView(R.id.open_door_lock_second)
    TextView open_door_lock_second;
    @InjectView(R.id.img_door_lock)
    ImageView img_door_lock;

    int[] contents = {R.string.open_door_lock, R.string.open_door_lock_txt_one, R.string.open_door_lock_txt_two, R.string.open_door_lock_txt_three
            , R.string.open_door_lock_txt_four, R.string.open_door_lock_txt_five, R.string.open_door_lock_txt_six};

    int[] imgs = {R.drawable.pic_zigebee_zhinengmensuo_1, R.drawable.pic_zigebee_zhinengmensuo_2,
            R.drawable.pic_zigebee_zhinengmensuo_3
            , R.drawable.pic_zigebee_zhinengmensuo_4, R.drawable.pic_zigebee_zhinengmensuo_5, R.drawable.pic_zigebee_zhinengmensuo_6,
            R.drawable.pic_zigebee_zhinengmensuo_7};
    private int page_index;
    private DialogUtil dialogUtil;
    private Map map = new HashMap();

    @Override
    protected int viewId() {
        return R.layout.select_smart_door_two_act;
    }

    @Override
    protected void onView() {
        back.setOnClickListener(this);
        next_step_id.setOnClickListener(this);
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {
        map = (Map) getIntent().getSerializableExtra("map");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
//                SelectSmartDoorLockTwoActivity.this.finish();
                page_index--;
                if (page_index < 0) {
                    page_index = 0;
                    SelectSmartDoorLockTwoActivity.this.finish();
                }
                common_item();
                break;
            case R.id.next_step_id:
                page_index++;
                if (page_index > 6) {
                    page_index = 6;
                    sraum_setBox_accent();
                }
                common_item();

//                SelectSmartDoorLockTwoActivity.this.finish();
        }
    }

    private void init_intent() {
        Intent intent = null;
        intent = new Intent(SelectSmartDoorLockTwoActivity.this, AddDoorLockDevActivity.class);
        intent.putExtra("map", getIntent().getStringExtra("map"));
        startActivity(intent);
    }


    private void sraum_setBox_accent() {
        String status = (String) map.get("status");
        final String gateway_number = (String) map.get("number");
        String areaNumber = (String) SharedPreferencesUtil.getData(SelectSmartDoorLockTwoActivity.this, "areaNumber", "");
        //在这里先调
        //设置网关模式-sraum-setBox
        Map map = new HashMap();
//        String phoned = getDeviceId(SelectZigbeeDeviceActivity.this);
        map.put("token", TokenUtil.getToken(SelectSmartDoorLockTwoActivity.this));
        map.put("boxNumber", gateway_number);
        String regId = (String) SharedPreferencesUtil.getData(SelectSmartDoorLockTwoActivity.this, "regId", "");
        map.put("regId", regId);
        map.put("status", status);
        map.put("areaNumber", areaNumber);

        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(ApiHelper.sraum_setGateway, map, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_setBox_accent();
                    }
                }, SelectSmartDoorLockTwoActivity.this, dialogUtil)

                {
                    @Override
                    public void onSuccess(User user) {
                        init_intent();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(SelectSmartDoorLockTwoActivity.this,
                                "该网关不存在");
                    }
                }
        );
    }


    private void common_item() {
        switch (page_index) {
            case 0:
                open_door_lock_second.setVisibility(View.VISIBLE);
                open_door_lock_second.setText(R.string.open_door_lock_txt);
                break;
            case 1:
                open_door_lock_second.setVisibility(View.GONE);
                break;
            case 2:
                open_door_lock_second.setVisibility(View.GONE);
                break;
            case 3:
                open_door_lock_second.setVisibility(View.GONE);
                break;
            case 4:
                open_door_lock_second.setVisibility(View.GONE);
                break;
            case 5:
                open_door_lock_second.setVisibility(View.GONE);
                break;
            case 6:
                open_door_lock_second.setVisibility(View.GONE);
                break;
        }
        open_door_lock.setText(contents[page_index]);
        img_door_lock.setImageResource(imgs[page_index]);
    }


    @Override
    public void onBackPressed() {
//        SelectSmartDoorLockTwoActivity.this.finish();
        page_index--;
        if (page_index < 0) {
            page_index = 0;
            SelectSmartDoorLockTwoActivity.this.finish();
        }
        common_item();
    }
}
