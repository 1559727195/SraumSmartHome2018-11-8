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

public class AgainAutoSceneAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();

    public AgainAutoSceneAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.again_auto_scene_item, null);

            viewHolderContentType.txt_again_autoscene = (TextView) convertView.findViewById(R.id.txt_again_autoscene);
            viewHolderContentType.img_again_autoscene = (ImageView) convertView.findViewById(R.id.img_again_autoscene);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        String type = (String) list.get(position).get("type");
        switch (type) {
            case "0":
                viewHolderContentType.img_again_autoscene.setVisibility(View.GONE);//
                break;
            case "1":
                viewHolderContentType.img_again_autoscene.setVisibility(View.VISIBLE);//
                break;
        }
        viewHolderContentType.txt_again_autoscene.setText(list.get(position).get("name").toString());

        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ShangChuanBaoJingActivity.class);
//                intent.putExtra("id", (Serializable) list.get(position).get("id").toString());
//                context.startActivity(intent);
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).put("type", "0");
                    if (i == position) {
                        if (finalViewHolderContentType.img_again_autoscene.getVisibility() == View.VISIBLE) {
                            list.get(i).put("type", "0");
                        } else {
                            list.get(i).put("type", "1");
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    class ViewHolderContentType {
       ImageView img_again_autoscene;
       TextView  txt_again_autoscene;
    }
}
