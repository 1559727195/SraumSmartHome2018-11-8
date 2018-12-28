package com.massky.sraum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class SelectDeviceMessageAdapter extends BaseAdapter {
    private final Context context;
    // 用来控制CheckBox的选中状况

    // 用来显示全部空的checkbox的
    private HashMap<Integer, Boolean> isCheckBoxVisiable = new HashMap<>();
    private List<Map> list = new ArrayList<>();

    public SelectDeviceMessageAdapter(Context context, List<Map> list) {
        isCheckBoxVisiable = new HashMap<>();
        this.list = list;
        this.context = context;
    }


    public HashMap<Integer, Boolean> getIsCheckBoxVisiable() {
        return isCheckBoxVisiable;
    }


//    public static HashMap<Integer, Boolean> getIsItemRead() {
//
//        return isItemRead;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.select_devicemessage_item, null);
            viewHolderContentType.img_guan_read = (ImageView) convertView.findViewById(R.id.img_guan_read);
            viewHolderContentType.panel_scene_name_txt = (TextView) convertView.findViewById(R.id.panel_scene_name_txt);
            viewHolderContentType.execute_scene_txt = (TextView) convertView.findViewById(R.id.execute_scene_txt);
            viewHolderContentType.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            viewHolderContentType.gateway_name_txt = (TextView) convertView.findViewById(R.id.gateway_name_txt);
            //event_time_txt
            viewHolderContentType.event_time_txt = (TextView) convertView.findViewById(R.id.event_time_txt);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        viewHolderContentType.panel_scene_name_txt.setText(list.get(position).get("messageTitle").toString());
        //gateway_name_txt

        viewHolderContentType.gateway_name_txt.setText(list.get(position).get("deviceName").toString());
        viewHolderContentType.event_time_txt.setText(list.get(position).get("eventTime").toString());
        if (getIsCheckBoxVisiable().get(position)) {
            viewHolderContentType.checkbox.setVisibility(View.VISIBLE);
            viewHolderContentType.event_time_txt.setVisibility(View.GONE);
        } else {
            viewHolderContentType.checkbox.setVisibility(View.GONE);
            viewHolderContentType.event_time_txt.setVisibility(View.VISIBLE);
        }

//        if(getIsSelected().get(position) != null) {
//            viewHolderContentType.checkbox.setChecked(getIsSelected().get(position));
//        }
        boolean ischecked = (boolean) list.get(position).get("ischecked");
        viewHolderContentType.checkbox.setChecked(ischecked);
        String readStatus = list.get(position).get("readStatus").toString();

        if (readStatus != null) {
            switch (readStatus) {
                case "1":
                    viewHolderContentType.img_guan_read.setImageResource(R.drawable.icon_yidu);
                    break;
                case "0":
                    viewHolderContentType.img_guan_read.setImageResource(R.drawable.icon_weidu);
                    break;
            }
        }

        return convertView;
    }

    public void setList(List<Map> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolderContentType {
        ImageView img_guan_read;
        TextView panel_scene_name_txt;
        TextView execute_scene_txt;
        CheckBox checkbox;
        TextView gateway_name_txt;
        TextView event_time_txt;
    }
}
