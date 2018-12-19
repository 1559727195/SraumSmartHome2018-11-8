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
    private List<User.scenelist> scenelist = new ArrayList<>();
    private List<Map> list = new ArrayList<>();
    private List<String> listtype = new ArrayList();

    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();


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
        sraum_getManuallyScenes();
        common_second();
    }


    /**
     * 获取手动场景
     */
    private void sraum_getManuallyScenes() {
        Map map = new HashMap();
        String areaNumber = (String) SharedPreferencesUtil.getData(getActivity(), "areaNumber", "");
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
                        list.clear();
                        for (User.scenelist us : scenelist) {
                            Map map = new HashMap();
                            map.put("type", us.type);
                            map.put("name", us.name);
                            map.put("number", us.number);
                            list.add(map);
                            setPicture(us.type);
                        }
                        handSceneAdapter.setList_s(list, listint, listintwo, true);
                        handSceneAdapter.notifyDataSetChanged();
                    }
                });
    }


    private void setPicture(String type) {
        switch (type) {
            case "1":
                listint.add(R.drawable.add_scene_homein);
                listintwo.add(R.drawable.gohome2);
                break;
            case "2":
                listint.add(R.drawable.add_scene_homeout);
                listintwo.add(R.drawable.leavehome2);
                break;
            case "3":
                listint.add(R.drawable.add_scene_sleep);
                listintwo.add(R.drawable.sleep2);
                break;
            case "4":
                listint.add(R.drawable.add_scene_nightlamp);
                listintwo.add(R.drawable.getup_night2);
                break;
            case "5":
                listint.add(R.drawable.add_scene_getup);
                listintwo.add(R.drawable.getup_morning2);
                break;
            case "6":
                listint.add(R.drawable.add_scene_cup);
                listintwo.add(R.drawable.rest2);
                break;
            case "7":
                listint.add(R.drawable.add_scene_book);
                listintwo.add(R.drawable.study2);
                break;
            case "8":
                listint.add(R.drawable.add_scene_moive);
                listintwo.add(R.drawable.movie2);
                break;
            case "9":
                listint.add(R.drawable.add_scene_meeting);
                listintwo.add(R.drawable.meeting2);
                break;
            case "10":
                listint.add(R.drawable.add_scene_cycle);
                listintwo.add(R.drawable.sport2);
                break;
            case "11":
                listint.add(R.drawable.add_scene_noddle);
                listintwo.add(R.drawable.dinner2);
                break;
            case "12":
                listint.add(R.drawable.add_scene_lampon);
                listintwo.add(R.drawable.open_all2);
                break;
            case "13":
                listint.add(R.drawable.add_scene_lampoff);
                listintwo.add(R.drawable.close_all2);
                break;
            case "14":
                listint.add(R.drawable.defaultpic);
                listintwo.add(R.drawable.defaultpicheck);
                break;
            case "101"://101-手动云场景
            default:
                listint.add(R.drawable.defaultpic);
                listintwo.add(R.drawable.defaultpicheck);
                break;
        }
    }

    @Override
    protected int viewId() {
        return R.layout.hand_scene_lay;
    }

    @Override
    protected void onView(View view) {
        dialogUtil = new DialogUtil(getActivity());

        handSceneAdapter = new HandSceneAdapter(getActivity(), list, listint, listintwo, dialogUtil, new HandSceneAdapter.RefreshListener() {
            @Override
            public void refresh() {
                sraum_getManuallyScenes();
                common_second();
            }
        });
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
        sraum_getManuallyScenes();
        common_second();
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
