package com.massky.sraum.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.king.photo.adapter.AlbumGridViewAdapter;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.AddHandSceneAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

import static com.massky.sraum.Utils.ApiHelper.sraum_getManuallyScenes;
import static com.massky.sraum.adapter.AddHandSceneAdapter.getIsSelected;

/**
 * Created by zhu on 2018/1/5.
 */

public class EditSceneSecondActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private List<Map> list_hand_scene;
    private Handler mHandler = new Handler();
    private AddHandSceneAdapter addhandSceneAdapter;
    private CheckBox cb;
    private List<Map> list = new ArrayList<>();
    private String number;
    private DialogUtil dialogUtil;
    private List<Map> list_scene = new ArrayList<>();
    private boolean isfirst_com;


    @Override
    protected int viewId() {
        return R.layout.edit_scene_second_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        dialogUtil = new DialogUtil(this);
        isfirst_com = true;
        list_hand_scene = new ArrayList<>();
        number = (String) getIntent().getSerializableExtra("number");
        addhandSceneAdapter = new AddHandSceneAdapter(EditSceneSecondActivity.this, list_hand_scene, new AddHandSceneAdapter.AddHandSceneListener() {
            @Override
            public void addhand_scene_list(boolean ischecked, int position,String type) {
//                String type = (String) list_hand_scene.get(position).get("type");
                String name = (String) list_hand_scene.get(position).get("name");
                switch (type) {
                    case "A204":
                        break;
                    case "A501":
                    case "A401":
                    case "A303":
                        Intent intent = new Intent(EditSceneSecondActivity.this, HandAddSceneDeviceDetailActivity.class);
                        intent.putExtra("type", type);
                        intent.putExtra("name", name);
                        startActivity(intent);
                        break;
                }
            }
        });
        xListView_scan.setAdapter(addhandSceneAdapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setFootViewHide();
        xListView_scan.setXListViewListener(this);
        xListView_scan.setOnItemClickListener(this);
    }


    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sraum_getOneSceneInfo(number);
    }

    //下拉刷新
    private void sraum_getOneSceneInfo(final String number) {
        Map<String, String> mapdevice = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(EditSceneSecondActivity.this
                , "areaNumber", "");
        mapdevice.put("token", TokenUtil.getToken(EditSceneSecondActivity.this));
        mapdevice.put("number", number);
        mapdevice.put("areaNumber", areaNumber);
        MyOkHttp.postMapString(ApiHelper.sraum_getOneSceneInfo

                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getOneSceneInfo(number);
                    }
                }, EditSceneSecondActivity.this, dialogUtil) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void pullDataError() {
                        super.pullDataError();
                    }

                    @Override
                    public void emptyResult() {
                        super.emptyResult();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                        //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
                    }

                    @Override
                    public void wrongBoxnumber() {
                        super.wrongBoxnumber();
                    }

                    @Override
                    public void onSuccess(final User user) {
                        List<Map> list_current = new ArrayList<>();
                        for (User.list_scene udsce : user.list) {
                            Map map = new HashMap<>();
                            map.put("type", udsce.type);
                            map.put("number", udsce.number);
                            map.put("name", udsce.name);
                            map.put("status", udsce.status);
                            map.put("dimmer", udsce.dimmer);
                            map.put("mode", udsce.mode);
                            map.put("temperature", udsce.temperature);
                            map.put("speed", udsce.speed);
                            map.put("panelName", udsce.panelName);
                            map.put("boxName", udsce.boxName);
                            map.put("isselect", false);
                            list_current.add(map);
                        }

                        if (isfirst_com) {
                            isfirst_com = false;
                        } else {// 更新
                            for (int i = 0; i < list_current.size(); i++) {
                                for (Map map : list_scene) {
                                    if (map.get("number").toString().equals(list_current.get(i).get("number").toString())) {
                                        list_current.set(i, map);
                                    }
                                }
                            }
                        }

                        list_scene.clear();
                        list_scene = list_current;
                        addhandSceneAdapter.setLists(list_scene);
                        addhandSceneAdapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                EditSceneSecondActivity.this.finish();
                break;
            case R.id.next_step_txt:
                Intent intent = new Intent(EditSceneSecondActivity.this,
                        GuanLianSceneBtnActivity.class);
                intent.putExtra("excute", "hand");//自动的
                startActivity(intent);
                break;
        }
    }

    private void onLoad() {
        xListView_scan.stopRefresh();
        xListView_scan.stopLoadMore();
        xListView_scan.setRefreshTime("刚刚");
    }


    @Override
    public void onRefresh() {
        onLoad();
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 1000);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        View v = parent.getChildAt(position - xListView_scan.getFirstVisiblePosition());
//        cb = (CheckBox) v.findViewById(R.id.checkbox);
//        cb.toggle();
//        //设置checkbox现在状态
//        getIsSelected().put(position, cb.isChecked());
//        for (Map map : list_scene) {
//            map.put("isselect", cb.isChecked());
//        }
    }
}
