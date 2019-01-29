package com.massky.sraum.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.activity.EditSceneSecondActivity;
import com.massky.sraum.activity.HistoryBackActivity;
import com.massky.sraum.activity.HistoryMessageDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class HistoryBackAdapter extends BaseAdapter {

    public HistoryBackAdapter(Context context, List<Map> list) {
        super(context, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.history_back_item, null);
            viewHolderContentType.device_type_pic = (ImageView) convertView.findViewById(R.id.device_type_pic);
            viewHolderContentType.hand_device_content = (TextView) convertView.findViewById(R.id.hand_device_content);
            viewHolderContentType.swipe_context = (LinearLayout) convertView.findViewById(R.id.swipe_context);
            viewHolderContentType.hand_gateway_content = (TextView) convertView.findViewById(R.id.hand_gateway_content);
            viewHolderContentType.hand_scene_btn = (TextView) convertView.findViewById(R.id.hand_scene_btn);


            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }


        String type = ((Map) getList().get(position)).get("type").toString();
        switch (type) {
            case "1":
                viewHolderContentType.hand_device_content.setText("功能建议");
                break;
            case "2":
                viewHolderContentType.hand_device_content.setText("性能问题");
                break;
            case "3":
                viewHolderContentType.hand_device_content.setText("其他");
                break;
        }

        viewHolderContentType.hand_gateway_content.setText(((Map) getList().get(position)).get("content").toString());
        viewHolderContentType.hand_scene_btn.setText(((Map) getList().get(position)).get("dt").toString());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HistoryMessageDetailActivity.class);
                intent.putExtra("id",  ((Map) getList().get(position)).get("id").toString());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolderContentType {
        ImageView device_type_pic;
        TextView hand_device_content;
        TextView hand_gateway_content;
        TextView hand_scene_btn;
        LinearLayout swipe_context;

    }
}
