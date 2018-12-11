package com.massky.sraum.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.HandSceneAdapter;
import com.massky.sraum.base.BaseFragment1;
import com.massky.sraum.event.MyDialogEvent;
import com.massky.sraum.view.XListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.InjectView;
import okhttp3.Call;

import static com.massky.sraum.Utils.ApiHelper.sraum_getOneRoomInfo;

/**
 * Created by zhu on 2017/11/30.
 */

public class HandSceneFragment extends BaseFragment1 implements XListView.IXListViewListener {
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    private List<Map> list_hand_scene = new ArrayList<>();
    private HandSceneAdapter handSceneAdapter;
    String[] again_elements = {"全屋灯光全开", "全屋灯光全开", "回家模式",
            "离家", "吃饭模式", "看电视", "睡觉", "K歌"};
    private DialogUtil dialogUtil;

    @Override
    protected void onData() {

    }

    @Override
    protected void onEvent() {

    }

    @Override
    public void onEvent(MyDialogEvent eventData) {

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    /**
     * 获取手动场景
     *
     */
    private void sraum_getManuallyScenes() {
        Map map = new HashMap();
        String areaNumber = (String) SharedPreferencesUtil.getData(getActivity(),"areaNumber","");
        map.put("areaNumber", areaNumber);
        map.put("token", TokenUtil.getToken(getActivity()));
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }

//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getManuallyScenes

                , map, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getManuallyScenes();
                    }
                }, getActivity(), dialogUtil) {
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
                        scenelist = user.sceneList;
                        manuaList = user.manualList;
                        list.clear();
                        for (User.scenelist ud : user.sceneList) {
                            listtype.add(ud.sceneStatus);
                        }
                        for (User.scenelist us : scenelist) {
                            Map map = new HashMap();
                            map.put("type", us.type);
                            map.put("name", us.name);
                            map.put("sceneStatus", us.sceneStatus);
                            list.add(map);
                            setPicture(us.type);
                        }
                    }
                });
    }

    @Override
    protected int viewId() {
        return R.layout.hand_scene_lay;
    }

    @Override
    protected void onView(View view) {
        dialogUtil = new DialogUtil(getActivity());
        list_hand_scene = new ArrayList<>();
        for (String element : again_elements) {
            Map map = new HashMap();
            map.put("name", element);
            switch (element) {
                case "全屋灯光全开":
                    map.put("image", R.drawable.icon_deng_sm);
                    break;
                case "回家模式":
                    map.put("image", R.drawable.icon_huijia_sm);
                    break;
                case "离家":
                    map.put("image", R.drawable.icon_lijia_sm);
                    break;
                case "吃饭模式":
                    map.put("image", R.drawable.icon_chifan_sm);
                    break;
                case "看电视":
                    map.put("image", R.drawable.icon_kandiashi_sm);
                    break;
                case "睡觉":
                    map.put("image", R.drawable.icon_shuijiao_sm);
                    break;
                case "K歌":
                    map.put("image", R.drawable.icon_kge_sm);
                    break;
            }
            list_hand_scene.add(map);
        }

        handSceneAdapter = new HandSceneAdapter(getActivity(), list_hand_scene);
        xListView_scan.setAdapter(handSceneAdapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setXListViewListener(this);
        xListView_scan.setFootViewHide();
    }

    @Override
    public void onClick(View v) {

    }

    public static HandSceneFragment newInstance() {
        HandSceneFragment newFragment = new HandSceneFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
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
}
