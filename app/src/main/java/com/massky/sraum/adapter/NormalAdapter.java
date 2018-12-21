package com.massky.sraum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.view.ClearLengthEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NormalAdapter extends BaseAdapter {
    private Context mContext;
    private int index;
    private myWatcher mWatcher;
    //    private String[] text = new String[]{};
    private List<Map> text = new ArrayList<>();
    private BackToMainListener backToMainListener;
    private List<User.device> deviceList = new ArrayList<>();
    private Handler h;

    public NormalAdapter(Context context, List<Map> text, List<User.device> deviceList, BackToMainListener backToMainListener) {
        this.text = text;
        mContext = context;
        this.backToMainListener = backToMainListener;
        this.deviceList = deviceList;
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,
                        final ViewGroup parent) {
        Log.e("tag", parent.toString());
        Holder mHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_new
                    , null);
            mHolder = new Holder();
            mHolder.edtInput = (ClearLengthEditText) convertView
                    .findViewById(R.id.edtGroupContent);
            mHolder.button_one_id = (Button) convertView.findViewById(R.id.button_one_id);
            mHolder.first_txt = (TextView) convertView.findViewById(R.id.first_txt);
            convertView.setTag(mHolder);
        } else {
            mHolder = (Holder) convertView.getTag();
        }

        mHolder.button_one_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.showToast(mContext, "position:" + position);
                //找设备
                backToMainListener.finddevice(position);
            }
        });//先获取焦点

        final Holder finalMHolder = mHolder;

        mHolder.edtInput.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    index = position;
                }
                ((ViewGroup) v.getParent().getParent())
                        .setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
//                changeScrollView(finalMHolder.scrollView);
                if(backToMainListener != null) backToMainListener.srcolltotop(finalMHolder.edtInput);
                return false;
            }
        });
        mHolder.edtInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            //设置焦点监听，当获取到焦点的时候才给它设置内容变化监听解决卡的问题

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText et = (EditText) v;
                if (mWatcher == null) {
                    mWatcher = new myWatcher();
                }
                if (hasFocus) {
                    if (et.getText().toString().equals("")) {
                        deviceList.get(index).name = "";
                        backToMainListener.sendToMain(deviceList);
                    }
                    et.addTextChangedListener(mWatcher);//设置edittext内容监听
                } else {
                    et.removeTextChangedListener(mWatcher);
                }
            }
        });
        mHolder.edtInput.clearFocus();//防止点击以后弹出键盘，重新getview导致的焦点丢失
        if (index != -1 && index == position) {
            // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
            mHolder.edtInput.requestFocus();
        }
        mHolder.edtInput.setText(deviceList.get(position).name);//这一定要放在clearFocus()之后，否则最后输入的内容在拉回来时会消失
        mHolder.edtInput.setSelection(mHolder.edtInput.getText().length());
        mHolder.edtInput.setHint(text.get(position).get("name").toString());
        mHolder.first_txt.setText(text.get(position).get("name").toString());
        return convertView;
    }


    class myWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
//            text[index] = s.toString();//为输入的位置内容设置数组管理器，防止item重用机制导致的上下内容一样的问题
//            text.get(index).put("name",s.toString());
            deviceList.get(index).name = s.toString();
            backToMainListener.sendToMain(deviceList);
        }
    }

    private static final class Holder {
        ClearLengthEditText edtInput;
        Button button_one_id;
        TextView first_txt;
    }

    public interface BackToMainListener {
        void sendToMain(List<User.device> strings);

        void finddevice(int position);

        void  srcolltotop(ClearLengthEditText edtInput);
    }

}