package com.massky.sraum.activity;

import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.massky.sraum.R;
import com.massky.sraum.adapter.AddRoomAdapter;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/25.
 */

public class AddRoomNewActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.mineRoom_list)
    GridView mineRoom_list;
    private AddRoomAdapter addRoomAdapter;
    String [] again_elements = {"客厅","卧室","厨房","游戏","餐厅","影音室","老人房","院子"};
    private List<Map> list_hand_scene;
    @InjectView(R.id.back)
    ImageView back;

    @Override
    protected int viewId() {
        return R.layout.add_room_new_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        list_hand_scene = new ArrayList<>();
        for (String element : again_elements) {
            Map map = new HashMap();
            map.put("name",element);
            switch (element) {
                case "客厅":
                    map.put("image",R.drawable.icon_keting);
                    break;
                case "卧室":
                    map.put("image",R.drawable.icon_woshi);
                    break;
                case "厨房":
                    map.put("image",R.drawable.icon_chufang);
                    break;
                case "餐厅":
                    map.put("image",R.drawable.icon_canting);
                    break;
                case "游戏":
                    map.put("image",R.drawable.icon_youxi);
                    break;
                case "影音室":
                    map.put("image",R.drawable.icon_yingyinfang);
                    break;
                case "老人房":
                    map.put("image",R.drawable.icon_laorenfang);
                case "院子":
                    map.put("image",R.drawable.icon_yuanzi);
                    break;
            }
            map.put("type","0");
            list_hand_scene.add(map);
        }

        addRoomAdapter = new AddRoomAdapter(this,list_hand_scene);
        mineRoom_list.setAdapter(addRoomAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AddRoomNewActivity.this.finish();
                break;
        }
    }
}
