package com.massky.sraum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraum.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class SelectLinkageAdapter extends android.widget.BaseAdapter {
    private List<Map> list = new ArrayList<>();
    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();
    private Context context;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected = new HashMap<>();

    public SelectLinkageAdapter(Context context, List<Map> list, List<Integer> listint, List<Integer> listintwo) {
        this.list = list;
        this.listint = listint;
        this.listintwo = listintwo;
        isSelected = new HashMap<Integer, Boolean>();
        initDate();
        this.context = context;
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
    }


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
            convertView = LayoutInflater.from(context).inflate(R.layout.select_sensor_item, null);
            viewHolderContentType.img_guan_scene = (ImageView) convertView.findViewById(R.id.img_guan_scene);
            viewHolderContentType.panel_scene_name_txt = (TextView) convertView.findViewById(R.id.panel_scene_name_txt);
            viewHolderContentType.execute_scene_txt = (TextView) convertView.findViewById(R.id.execute_scene_txt);
            viewHolderContentType.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            //gateway_name_txt
            viewHolderContentType.gateway_name_txt = (TextView) convertView.findViewById(R.id.gateway_name_txt);
            viewHolderContentType.wifi_name = (TextView) convertView.findViewById(R.id.wifi_name);

            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }


//        int element = (Integer) list.get(position).get("image");
//        viewHolderContentType.img_guan_scene.setImageResource(element);
//        viewHolderContentType.panel_scene_name_txt.setText(list.get(position).get("name").toString());


//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(context, DeviceExcuteOpenActivity.class);
////                context.startActivity(intent);
//
//
//            }
//        });
        viewHolderContentType.panel_scene_name_txt.setText(list.get(position).get("panelName").toString());
        String panelType = list.get(position).get("panelType").toString();
        if (panelType != null)
            switch (panelType) {
                case "AA02":
                    viewHolderContentType.gateway_name_txt.setVisibility(View.GONE);
                    viewHolderContentType.wifi_name.setVisibility(View.VISIBLE);
                    viewHolderContentType.panel_scene_name_txt.setVisibility(View.GONE);
                    viewHolderContentType.wifi_name.setText(list.get(position).get("panelName").toString());
                    break;
                default:
                    viewHolderContentType.gateway_name_txt.setText(list.get(position).get("boxName").toString());
                    break;
            }
        viewHolderContentType.checkbox.setChecked(getIsSelected().get(position));
        if (getIsSelected().get(position)) {
            viewHolderContentType.img_guan_scene.setImageResource(listintwo.get(position));
        } else {
            viewHolderContentType.img_guan_scene.setImageResource(listint.get(position));
        }
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        isSelected = isSelected;
    }

    public void setlist(List<Map> list, List<Integer> listint, List<Integer> listintwo) {
        this.list = list;
        initDate();
        this.listint = listint;
        this.listintwo = listintwo;
    }


    class ViewHolderContentType {
        ImageView img_guan_scene;
        TextView panel_scene_name_txt;
        TextView execute_scene_txt;
        CheckBox checkbox;
        public TextView gateway_name_txt;
        TextView wifi_name;
    }
}
