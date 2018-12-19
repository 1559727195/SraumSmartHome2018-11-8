package com.massky.sraum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.massky.sraum.R;
import com.massky.sraum.User;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by masskywcy on 2017-03-21.
 */
//我的场景adapter
public class ExcuteOneSceneAdapter extends android.widget.BaseAdapter {
    private List<User.scenelist> list = new ArrayList<>();
    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();
    private boolean viewFlag;
    private Context context;

    public ExcuteOneSceneAdapter(Context context, List list, List<Integer> listint,
                                 boolean viewFlag) {
        this.list = list;
        this.listint = listint;
        this.listintwo = listintwo;
        this.viewFlag = viewFlag;
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.ecute_somescene_item, null);
            viewHolderContentType.img_guan_scene = (ImageView) convertView.findViewById(R.id.img_guan_scene);
            viewHolderContentType.panel_scene_name_txt = (TextView) convertView.findViewById(R.id.panel_scene_name_txt);
            viewHolderContentType.execute_scene_txt = (TextView) convertView.findViewById(R.id.execute_scene_txt);
            viewHolderContentType.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            //gateway_name_txt
            viewHolderContentType.gateway_name_txt = (TextView) convertView.findViewById(R.id.gateway_name_txt);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolder) convertView.getTag();
        }

        viewHolderContentType.panel_scene_name_txt.setText(list.get(position).sceneName);
        viewHolderContentType.gateway_name_txt.setText(list.get(position).boxName);

        viewHolderContentType.img_guan_scene.setImageResource(listint.get(position));

        return convertView;
    }

    public void setList_s(List<User.scenelist> scenelist, List<Integer> listint, List<Integer> listintwo, boolean b) {
        this.list = new ArrayList<>();
        this.listint = new ArrayList<>();
        this.listintwo = new ArrayList<>();
        this.list = scenelist;
        this.listint = listint;
        this.listintwo = listintwo;
        this.viewFlag = b;
    }

    class ViewHolder {
        ImageView img_guan_scene;
        TextView panel_scene_name_txt;
        TextView execute_scene_txt;
        CheckBox checkbox;
        public TextView gateway_name_txt;
    }

}