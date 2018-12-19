package com.massky.sraum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class SelectLinkageItemSecondAdapter extends android.widget.BaseAdapter {

    private List<Map> listint = new ArrayList<>();
    private List<String> listintwo = new ArrayList<>();
    private Context context;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected = new HashMap<>();

    public SelectLinkageItemSecondAdapter(Context context, List<Map> listint) {

        this.listint = listint;
        this.context = context;
    }


    @Override
    public int getCount() {
        return listint.size();
    }

    @Override
    public Object getItem(int position) {
        return listint.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.select_linkage_second_item, null);
            viewHolderContentType.img_guan_scene = (ImageView) convertView.findViewById(R.id.img_guan_scene);
            viewHolderContentType.panel_scene_name_txt = (TextView) convertView.findViewById(R.id.panel_scene_name_txt);
            viewHolderContentType.execute_scene_txt = (TextView) convertView.findViewById(R.id.execute_scene_txt);
            //gateway_name_txt
            viewHolderContentType.gateway_name_txt = (TextView) convertView.findViewById(R.id.gateway_name_txt);
            //tiaoguang_value
            viewHolderContentType.tiaoguang_value = (TextView) convertView.findViewById(R.id.tiaoguang_value);
            viewHolderContentType.scene_set = (ImageView) convertView.findViewById(R.id.scene_set);
            convertView.setTag(viewHolderContentType);
//            viewHolderContentType.tiaoguang_value.setTag(R.id.tiaoguang_open, position_index++);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

//        int element = (Integer) list.get(position).get("image");
//        viewHolderContentType.img_guan_scene.setImageResource(element);
        viewHolderContentType.panel_scene_name_txt.setText(listint.get(position).get("name").toString());

        if (listint.get(position).get("tabname") != null)
            viewHolderContentType.gateway_name_txt.setText(listint.get(position).get("tabname").toString());
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(context, DeviceExcuteOpenActivity.class);
////                context.startActivity(intent);
//
//
//            }
//        });
//        viewHolderContentType.panel_scene_name_txt.setText(list.get(position).panelName);
//        viewHolderContentType.gateway_name_txt.setText(list.get(position).boxName);
//            viewHolderContentType.checkbox.setChecked(getIsSelected().get(position));
//            if (getIsSelected().get(position)) {
//                viewHolderContentType.img_guan_scene.setImageResource(listintwo.get(position));
//            } else {
//                viewHolderContentType.img_guan_scene.setImageResource(listint.get(position));
//            }
        if (listint.get(position).get("value") != null)
            switch (listint.get(position).get("value").toString()) {
                case "":
                    String tiaoguang = (String) listint.get(position).get("tiaoguang");
                    if (tiaoguang != null) {
                        switch (tiaoguang) {
                            case "open":
                                viewHolderContentType.scene_set.setImageResource(R.drawable.wode_right_arrow);
                                break;
                            case "close":
                                viewHolderContentType.scene_set.setImageResource(R.drawable.btn_xiala);
                                break;
                        }
                    }
//              convertView.setTag(R.id.tiaoguang_open,"close");
                    viewHolderContentType.tiaoguang_value.setText("");//backright
                    break;
                default:
                    viewHolderContentType.tiaoguang_value.setText("调光灯值" + listint.get(position).get("value").toString());
                    viewHolderContentType.panel_scene_name_txt.setText("");
                    viewHolderContentType.gateway_name_txt.setText("");
                    viewHolderContentType.scene_set.setImageResource(R.drawable.wode_right_arrow);
                    break;
            }

        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        isSelected = isSelected;
    }

    public void setlist(List<Map> listint) {

        this.listint = listint;

    }


    class ViewHolderContentType {
        ImageView img_guan_scene;
        TextView panel_scene_name_txt;
        TextView execute_scene_txt;
        public TextView gateway_name_txt;
        TextView tiaoguang_value;
        ImageView scene_set;
    }
}
