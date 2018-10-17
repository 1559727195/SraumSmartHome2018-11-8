package com.massky.sraum.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.activity.HandAddSceneDeviceDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class AddHandSceneAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();

    public AddHandSceneAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.add_hand_scene_item, null);

            viewHolderContentType.hand_device_name = (TextView) convertView.findViewById(R.id.hand_device_name);
//            viewHolderContentType.hand_gateway_content = (TextView) convertView.findViewById(R.id.hand_gateway_content);
            viewHolderContentType. hand_scene_btn = (Button) convertView.findViewById(R.id. hand_scene_btn);

            viewHolderContentType.activity_group_radioGroup_light = (RadioGroup) convertView.findViewById(R.id.activity_group_radioGroup_light);

            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }
        String type = (String) list.get(position).get("type");
        switch (type){
            case "A204":
                viewHolderContentType.activity_group_radioGroup_light.setVisibility(View.VISIBLE);
                break;
            case "A501":

                break;
            case "A401":

                break;
            case "A303":

                break;
        }
        viewHolderContentType.hand_device_name.setText(list.get(position).get("name").toString());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ShangChuanBaoJingActivity.class);
//                intent.putExtra("id", (Serializable) list.get(position).get("id").toString());
//                context.startActivity(intent);

                String type = (String) list.get(position).get("type");
                String name = (String) list.get(position).get("name");
                switch (type){
                    case "A204":
                        break;
                    case "A501":
                    case "A401":
                    case "A303":
                        Intent intent = new Intent(context, HandAddSceneDeviceDetailActivity.class);
                        intent.putExtra("type", type);
                        intent.putExtra("name", name);
                        context.startActivity(intent);
                        break;
                }
            }
        });
        return convertView;
    }

    class ViewHolderContentType {
       ImageView device_type_pic;
       TextView  hand_device_name;
       TextView  hand_gateway_content;
       Button    hand_scene_btn;
       RadioGroup activity_group_radioGroup_light;
    }
}
