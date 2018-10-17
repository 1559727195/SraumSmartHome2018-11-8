package com.massky.sraum.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.activity.DeviceSettingDelRoomActivity;
import com.massky.sraum.activity.WangGuanDetailItemActivity;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class DeviceListAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();

    public DeviceListAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.devicelist_item, null);
            viewHolderContentType.img_guan_scene = (ImageView) convertView.findViewById(R.id.img_guan_scene);
            viewHolderContentType.panel_scene_name_txt = (TextView) convertView.findViewById(R.id.panel_scene_name_txt);
            viewHolderContentType.rel_scene_set = (RelativeLayout) convertView.findViewById(R.id.rel_scene_set);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }
        viewHolderContentType.panel_scene_name_txt.setText((String) list.get(position).get("name"));
        viewHolderContentType.rel_scene_set.setOnClickListener(new View.OnClickListener() {//解决content SwipeMenulayout
            //item点击事件被吃掉，
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, DeviceSettingDelRoomActivity.class);
////                intent.putExtra("name",(String) list.get(position).get("name"));
//                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolderContentType {
       ImageView img_guan_scene;
       TextView  panel_scene_name_txt;
//       TextView  execute_scene_txt;
        RelativeLayout rel_scene_set;
        TextView content;
    }
}
