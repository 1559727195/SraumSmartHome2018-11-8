package com.massky.sraum.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.activity.LinkageItemYaoKongQiActivity;
import com.massky.sraum.widget.SlideSwitchButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class SelectLinkageYaoKongQiAdapter extends android.widget.BaseAdapter {
    private List<Map> list = new ArrayList<>();
    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();
    private int temp = -1;
    private Activity activity;//上下文
    DialogUtil dialogUtil;
    RefreshListener refreshListener;
    private Map sensor_map = new HashMap();
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected = new HashMap<>();

    public SelectLinkageYaoKongQiAdapter(Activity context, List<Map> list, List<Integer> listint, List<Integer> listintwo, DialogUtil dialogUtil,
                                         RefreshListener refreshListener) {
        this.list = list;
        this.listint = listint;
        this.listintwo = listintwo;
        this.activity = context;
        this.activity = context;
        this.dialogUtil = dialogUtil;
        this.refreshListener = refreshListener;
    }
//
//    // 初始化isSelected的数据
//    private void initDate() {
//        for (int i = 0; i < list_bool.size(); i++) {
//            getIsSelected().put(i, false);
//        }
//    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(activity).inflate(R.layout.select_linkage_yaokongqi_item, null);
            viewHolderContentType.img_guan_scene = (ImageView) convertView.findViewById(R.id.img_guan_scene);
            viewHolderContentType.panel_scene_name_txt = (TextView) convertView.findViewById(R.id.panel_scene_name_txt);
            viewHolderContentType.execute_scene_txt = (TextView) convertView.findViewById(R.id.execute_scene_txt);
            viewHolderContentType.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            //gateway_name_txt
            viewHolderContentType.gateway_name_txt = (TextView) convertView.findViewById(R.id.gateway_name_txt);
//            viewHolderContentType.swipemenu_layout = (SwipeMenuLayout) convertView.findViewById(R.id.swipemenu_layout);
            viewHolderContentType.swipe_content_linear = (LinearLayout) convertView.findViewById(R.id.swipe_content_linear);

            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        viewHolderContentType.panel_scene_name_txt.setText((String) list.get(position).get("name"));
        viewHolderContentType.gateway_name_txt.setText("");
        viewHolderContentType.img_guan_scene.setImageResource(listint.get(position));
        viewHolderContentType.swipe_content_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map map = new HashMap();

                map.put("type", list.get(position).get("type").toString());
                map.put("number", list.get(position).get("number").toString());
                map.put("status", "");
                map.put("dimmer", "");
                map.put("mode", "");
                map.put("temperature", "");
                map.put("speed", "");
                map.put("name", list.get(position).get("name").toString());
                map.put("name1", list.get(position).get("name").toString());
                map.put("action", "");
                map.put("tiaoguang", "");
                map.put("tabname", "");
                map.put("value", "");
                map.put("position", "");
                map.put("panelMac", "");
                map.put("gatewayMac", "");
                map.put("boxNumber", "");
                Intent intent = new Intent(activity, LinkageItemYaoKongQiActivity.class);//     Intent intent = new Intent(SelectLinkageYaoKongQiActivity.this,
                // LinkageItemYaoKongQiActivity.class)

                //   Logger.d(TAG, "uid或token为空或ProductKeyList为空或productSecret为空或mac为空,无法绑定");
                intent.putExtra("device_map", (Serializable) map);
                intent.putExtra("sensor_map", (Serializable) sensor_map);
                activity.startActivity(intent);
            }
        });
        return convertView;
    }

    public void setLists(List<Map> list_hand_scene, List<Integer> listint, List<Integer> listintwo, Map sensor_map) {
        this.list = list_hand_scene;
        this.listint = listint;
        this.listintwo = listintwo;
        this.sensor_map = sensor_map;
    }

//    public static HashMap<Integer, Boolean> getIsSelected() {
//        return isSelected;
//    }
//
//    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
//        SelectSensorSingleAdapter.isSelected = isSelected;
//    }


    public static class ViewHolderContentType {
        public ImageView img_guan_scene;
        public TextView panel_scene_name_txt;
        public TextView execute_scene_txt;
        public CheckBox checkbox;
        public TextView gateway_name_txt;
        SlideSwitchButton hand_scene_btn;
        //        SwipeMenuLayout swipemenu_layout;
        LinearLayout swipe_content_linear;
    }

    public interface RefreshListener {
        void refresh();
    }
}
