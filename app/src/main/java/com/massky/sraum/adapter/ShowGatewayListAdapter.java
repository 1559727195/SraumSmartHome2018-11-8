package com.massky.sraum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.massky.sraum.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhu on 2017/7/4.
 */

public class ShowGatewayListAdapter extends BaseAdapter {
    List<Map> list = new ArrayList<>();

    public ShowGatewayListAdapter(Context context, List<Map> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.show_gateway_list_item, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.txt_sao_one.setText(getList().get(position).toString());
        if (position == list.size() - 1) {
            mHolder.sao_rel.setBackgroundResource(R.drawable.rectangle_shape);
        } else {
            mHolder.sao_rel.setBackgroundResource(R.drawable.rectangle_top_shape);
        }
        return convertView;
    }

    class ViewHolder {

        @InjectView(R.id.txt_sao_one)
        TextView txt_sao_one;
        @InjectView(R.id.sao_rel)
        RelativeLayout sao_rel;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
