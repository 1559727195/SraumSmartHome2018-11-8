package com.massky.sraum.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.massky.sraum.R;
import com.massky.sraum.activity.EditSceneActivity;
import com.massky.sraum.activity.EditSceneSecondActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class HandSceneAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();

    public HandSceneAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.hand_scene_item, null);
            viewHolderContentType.device_type_pic = (ImageView) convertView.findViewById(R.id.device_type_pic);
            viewHolderContentType.hand_device_content = (TextView) convertView.findViewById(R.id.hand_device_content);
            viewHolderContentType.swipe_context = (LinearLayout) convertView.findViewById(R.id.swipe_context);
//            viewHolderContentType.hand_gateway_content = (TextView) convertView.findViewById(R.id.hand_gateway_content);
            viewHolderContentType. hand_scene_btn = (ImageView) convertView.findViewById(R.id. hand_scene_btn);

            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        int element = (Integer) list.get(position).get("image");
        viewHolderContentType.device_type_pic.setImageResource(element);
        viewHolderContentType.hand_device_content.setText(list.get(position).get("name").toString());

        viewHolderContentType.swipe_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditSceneSecondActivity.class);
                context.startActivity(intent);
            }
        });

        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        viewHolderContentType. hand_scene_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalViewHolderContentType. hand_scene_btn.setImageResource(R.drawable.icon_root);
            }
        });
        return convertView;
    }

    class ViewHolderContentType {
       ImageView device_type_pic;
       TextView  hand_device_content;
       TextView  hand_gateway_content;
       ImageView   hand_scene_btn;
       LinearLayout swipe_context;

    }
}
