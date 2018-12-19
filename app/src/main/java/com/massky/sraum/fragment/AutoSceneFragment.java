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
import com.massky.sraum.adapter.AutoSceneAdapter;
import com.massky.sraum.base.BaseFragment1;
import com.massky.sraum.event.MyDialogEvent;
import com.massky.sraum.view.XListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2017/11/30.
 */

public class AutoSceneFragment extends BaseFragment1 implements XListView.IXListViewListener {
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    private String[] autoElements = {"人体传感联动", "厨房联动", "PM2.5联动", "地下室", "防盗门打开"
            , "漏水"};
    private List<Map> list_hand_scene = new ArrayList<>();
    private AutoSceneAdapter autoSceneAdapter;
    private DialogUtil dialogUtil;
    private List<User.deviceLinkList> list = new ArrayList<>();

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
    protected int viewId() {
        return R.layout.auto_scene_lay;
    }

    @Override
    protected void onView(View view) {
        dialogUtil = new DialogUtil(getActivity());

        autoSceneAdapter = new AutoSceneAdapter(getActivity(), list, dialogUtil, new AutoSceneAdapter.RefreshListener() {
            @Override
            public void refresh() {
//                get_myDeviceLink();
//                common_second();
                sraum_getAutoScenes();
                common_second();
            }
        });
        xListView_scan.setAdapter(autoSceneAdapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setXListViewListener(this);
        xListView_scan.setFootViewHide();
    }

    @Override
    public void onClick(View v) {

    }

    public static AutoSceneFragment newInstance() {
        AutoSceneFragment newFragment = new AutoSceneFragment();
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

    @Override
    public void onResume() {
        super.onResume();
        sraum_getAutoScenes();
        common_second();
    }

    /**
     * 获取自动场景
     */
    private void sraum_getAutoScenes() {
        Map map = new HashMap();
        String areaNumber = (String) SharedPreferencesUtil.getData(getActivity(), "areaNumber", "");
        map.put("areaNumber", areaNumber);
        map.put("token", TokenUtil.getToken(getActivity()));
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }

//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getAutoScenes

                , map, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getAutoScenes();
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
                        list.clear();
//                listtype.clear();
                        list.addAll(user.deviceLinkList);
                        autoSceneAdapter.clear();
                        autoSceneAdapter.addAll(list);//不要new adapter
                    }
                });
    }

    /**
     * 清除联动信息
     */
    private void common_second() {
        SharedPreferencesUtil.saveData(     getActivity(), "linkId", "");
        SharedPreferencesUtil.saveInfo_List(getActivity(), "list_result", new ArrayList<Map>());
        SharedPreferencesUtil.saveInfo_List(getActivity(), "list_condition", new ArrayList<Map>());
        SharedPreferencesUtil.saveData(     getActivity(), "editlink", false);
        SharedPreferencesUtil.saveInfo_List(getActivity(), "link_information_list", new ArrayList<Map>());
        SharedPreferencesUtil.saveData(     getActivity(), "add_condition", false);
    }

}
