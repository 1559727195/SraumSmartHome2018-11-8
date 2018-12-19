package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.adapter.ExcuteOneSceneAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.PullToRefreshLayout;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2018/6/27.
 */

public class ExcuteSomeOneSceneActivity extends BaseActivity
        implements
        AdapterView.OnItemClickListener,
        PullToRefreshLayout.OnRefreshListener {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.refresh_view)
    PullToRefreshLayout refresh_view;
    @InjectView(R.id.maclistview_id)
    ListView maclistview_id;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private DialogUtil dialogUtil;
    private List<User.scenelist> scenelist = new ArrayList<>();
    private List<Integer> listint = new ArrayList<>();
    private List<String> listtype = new ArrayList();
    private ExcuteOneSceneAdapter adapter;
    private Map sensor_map = new HashMap();//传感器map
    private List<String> listbox = new ArrayList();

    @Override
    protected int viewId() {
        return R.layout.excute_someone_scenelay;
    }

    @Override
    protected void onView() {
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        maclistview_id.setOnItemClickListener(this);
        refresh_view.setOnRefreshListener(this);
        sensor_map = (Map) getIntent().getSerializableExtra("sensor_map");
//        refresh_view.autoRefresh();
        dialogUtil = new DialogUtil(this);
        ondata();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    private void ondata() {

        adapter = new ExcuteOneSceneAdapter(ExcuteSomeOneSceneActivity.this, scenelist, listint, false);
        maclistview_id.setAdapter(adapter);
        maclistview_id.setAdapter(adapter);
//        xListView_scan.setPullLoadEnable(false);
//        xListView_scan.setFootViewHide();
//        xListView_scan.setXListViewListener(this);

//        uploader_refresh();
        getData();
    }

    private void getData() {

        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(ExcuteSomeOneSceneActivity.this));
        String areaNumber = (String) SharedPreferencesUtil.getData(ExcuteSomeOneSceneActivity.this, "areaNumber",
                "");
        map.put("areaNumber", areaNumber);
        MyOkHttp.postMapObject(ApiHelper.sraum_getLinkScene, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                final Map<String, Object> mapdevice = new HashMap<>();
                String boxNumber = (String) SharedPreferencesUtil.getData(ExcuteSomeOneSceneActivity.this, "boxnumber", "");
                mapdevice.put("token", TokenUtil.getToken(ExcuteSomeOneSceneActivity.this));
                mapdevice.put("boxNumber", boxNumber);
                getData();
            }
        }, ExcuteSomeOneSceneActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);

            }

            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                scenelist.clear();
                listint.clear();
                scenelist = user.sceneList;
                for (User.scenelist ud : user.sceneList) {
                    listtype.add(ud.sceneStatus);
                }
                for (User.scenelist us : scenelist) {
                    setPicture(us.sceneType);
                    listbox.add(us.boxName);
                }
                adapter = new ExcuteOneSceneAdapter(ExcuteSomeOneSceneActivity.this, scenelist, listint, false);
                maclistview_id.setAdapter(adapter);
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    private void setPicture(String type) {
        switch (type) {
            case "1":
                listint.add(R.drawable.add_scene_homein);
                break;
            case "2":
                listint.add(R.drawable.add_scene_homeout);
                break;
            case "3":
                listint.add(R.drawable.add_scene_sleep);
                break;
            case "4":
                listint.add(R.drawable.add_scene_nightlamp);
                break;
            case "5":
                listint.add(R.drawable.add_scene_getup);
                break;
            case "6":
                listint.add(R.drawable.add_scene_cup);
                break;
            case "7":
                listint.add(R.drawable.add_scene_book);
                break;
            case "8":
                listint.add(R.drawable.add_scene_moive);
                break;
            case "9":
                listint.add(R.drawable.add_scene_meeting);
                break;
            case "10":
                listint.add(R.drawable.add_scene_cycle);
                break;
            case "11":
                listint.add(R.drawable.add_scene_noddle);
                break;
            case "12":
                listint.add(R.drawable.add_scene_lampon);
                break;
            case "13":
                listint.add(R.drawable.add_scene_lampoff);
                break;
            case "14":
                listint.add(R.drawable.defaultpic);
                break;
            default:
                listint.add(R.drawable.defaultpic);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ExcuteSomeOneSceneActivity.this.finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String sceneId = scenelist.get(position).sceneId;
        String sceneName = scenelist.get(position).sceneName;
        String sceneType = scenelist.get(position).sceneType;


//    String scenelist.get(position);
        Map map_value = new HashMap();
        map_value.put("name", "场景");
        map_value.put("type", "100");
        map_value.put("name1", "场景");
        map_value.put("action", sceneName);
        map_value.put("boxName", listbox.get(position));
        map_value.put("number", sceneId);
        map_value.put("status", "1");
        map_value.put("dimmer", "");
        map_value.put("mode", "");
        map_value.put("temperature", "");
        map_value.put("speed", "");
        map_value.put("panelMac", "");
        map_value.put("gatewayMac", "");


        AppManager.getAppManager().finishActivity_current(SelectiveLinkageActivity.class);
        AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity.class);
        Intent intent = new Intent(ExcuteSomeOneSceneActivity.this, EditLinkDeviceResultActivity.class);
        intent.putExtra("device_map", (Serializable) map_value);
        intent.putExtra("sensor_map", (Serializable) sensor_map);
        startActivity(intent);
        ExcuteSomeOneSceneActivity.this.finish();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
        getData();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

    }
}
