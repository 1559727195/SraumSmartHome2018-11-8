package com.massky.sraum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.massky.sraum.R;
import com.massky.sraum.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class AirModeListAdapter extends android.widget.BaseAdapter {

    private List<User.device> listint = new ArrayList<>();
    private List<Map> list = new ArrayList<>();
    private Context context;
    ExcutecuteListener excutecuteListener;
    private int selectedEditTextPosition;
    private  String[] text = new String[]{};
    public int index;
    private myWatcher mWatcher;


    public AirModeListAdapter(Context context, List<User.device> listint, String[]list_name, ExcutecuteListener excutecuteListener) {

        this.listint = listint;
        this.context = context;
        this.excutecuteListener = excutecuteListener;
        this.text = list_name;
    }


    @Override
    public int getCount() {
        return text.length;
    }

    @Override
    public Object getItem(int position) {
        return text[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType  mHolder = null;
        if (null == convertView) {
            mHolder = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.air_mode_item, null);
            mHolder.first_txt = (TextView) convertView.findViewById(R.id.first_txt);
            mHolder.edtInput = (EditText) convertView.findViewById(R.id.edit_one);
            mHolder.button_one_id = (Button) convertView.findViewById(R.id.button_one_id);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolderContentType) convertView.getTag();
        }


        mHolder.edtInput.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    index = position;
                }
                ((ViewGroup) v.getParent())
                        .setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                return false;
            }
        });


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup) v)
                        .setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                Toast.makeText(context, "点击了" + text[position], Toast.LENGTH_SHORT).show();
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
        mHolder.edtInput.setText(text[position]);//这一定要放在clearFocus()之后，否则最后输入的内容在拉回来时会消失
        mHolder.edtInput.setSelection(mHolder.edtInput.getText().length());
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
            text[index] = s.toString();//为输入的位置内容设置数组管理器，防止item重用机制导致的上下内容一样的问题
//            backToMainListener.sendToMain(text);
        }
    }




    public void setlist(List<User.device> listint, String[] list_name) {
        this.listint = listint;
        this.text = list_name;
        notifyDataSetChanged();
    }



        class ViewHolderContentType {

        TextView first_txt;
        EditText edtInput;
        Button button_one_id;
    }


    public interface ExcutecuteListener {
        void excute_cordition();

        void excute_result();
    }
}
