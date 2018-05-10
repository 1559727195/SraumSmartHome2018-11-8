package com.massky.sraumsmarthome.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.activity.DeviceExcuteOpenActivity;
import com.massky.sraumsmarthome.activity.GuanLianSceneRealBtnActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class SelectExcuteSceneResultAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();

    public SelectExcuteSceneResultAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.guanlian_scene_item, null);
            viewHolderContentType.img_guan_scene = (ImageView) convertView.findViewById(R.id.img_guan_scene);
            viewHolderContentType.panel_scene_name_txt = (TextView) convertView.findViewById(R.id.panel_scene_name_txt);
            viewHolderContentType.execute_scene_txt = (TextView) convertView.findViewById(R.id.execute_scene_txt);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }


        int element = (Integer) list.get(position).get("image");
        viewHolderContentType.img_guan_scene.setImageResource(element);
        viewHolderContentType.panel_scene_name_txt.setText(list.get(position).get("name").toString());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DeviceExcuteOpenActivity.class);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolderContentType {
       ImageView img_guan_scene;
       TextView  panel_scene_name_txt;
       TextView  execute_scene_txt;
    }
}
