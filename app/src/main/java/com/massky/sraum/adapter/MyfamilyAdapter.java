package com.massky.sraum.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.view.ClearEditText;
import com.massky.sraum.view.ClearLengthEditText;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by masskywcy on 2016-09-21.
 */
//我的家庭页面adapter
public class MyfamilyAdapter extends BaseAdapter<User.familylist> {
    private List<User.familylist> list;
    private DialogUtil dialogUtil;
    private String accountType;
    private String authType;
    private String areaNumber;

    public MyfamilyAdapter(Context context, List list,
                           String authType, String areaNumber, String token, DialogUtil dialogUtil, String accountType) {
        super(context, list);
        this.list = list;
        this.dialogUtil = dialogUtil;
        this.accountType = accountType;
        this.authType = authType;
        this.areaNumber = areaNumber;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder = null;
        if (null == convertView) {
            mHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.myfamilyitem, null);
            mHolder.mytext = (TextView) convertView.findViewById(R.id.myfamitemtext);
            mHolder.myfamtext = (TextView) convertView.findViewById(R.id.myfamtext);
            mHolder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
            mHolder.rename_btn = (Button) convertView.findViewById(R.id.rename_btn);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.mytext.setText(list.get(position).name);
        mHolder.myfamtext.setText(list.get(position).mobilePhone);
        final View finalConvertView = convertView;
//        String authType = (String) SharedPreferencesUtil.getData(context, "authType", "");
        switch (authType) {//判断家庭成员类型
            case "1":
                ((SwipeMenuLayout) finalConvertView).setSwipeEnable(true);
                break;//业主
            case "2":
                ((SwipeMenuLayout) finalConvertView).setSwipeEnable(false);
                break;//成员
        }

        mHolder.rename_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除动画关闭
                ((SwipeMenuLayout) finalConvertView).quickClose();
                showRenameDialog(areaNumber,list.get(position).name
                        , list.get(position).mobilePhone,position);
            }
        });

        mHolder.btnDelete.setOnClickListener(new View.OnClickListener() {//---
            @Override
            public void onClick(View v) {
                //删除动画关闭
                ((SwipeMenuLayout) finalConvertView).quickClose();
                showCenterDeleteDialog(list.get(position).name
                        , list.get(position).mobilePhone, position);
            }
        });
        return convertView;
    }

    //删除家庭成员
    private void deleteFamily(final String mobilePhone, final int x) {
        Map<String, Object> map = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(context, "areaNumber", "");
        map.put("token", TokenUtil.getToken(context));
        map.put("mobilePhone", mobilePhone);
        map.put("areaNumber", areaNumber);
        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(ApiHelper.sraum_deleteFamily, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                deleteFamily(mobilePhone, x);
            }
        },
                context, dialogUtil) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                list.remove(x);
                notifyDataSetChanged();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });

    }


    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String name, final String mobilePhone, final int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(context).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
//        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
        tv_title.setText(name);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据

        final Dialog dialog = new Dialog(context, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFamily(mobilePhone, position);
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                linkage_delete(linkId, dialog);
                dialog.dismiss();
            }
        });
    }


    class ViewHolderContentType {
        ImageView device_type_pic;
        TextView area_name_txt;
        TextView hand_gateway_content;
        Button rename_btn;
        RelativeLayout swipe_context;
        com.mcxtzhang.swipemenulib.SwipeMenuLayout swipemenu_layout;
        Button delete_btn;
    }

    //自定义dialog,自定义重命名dialog

    public void showRenameDialog(final String areaNumber,final String name, final String mobilePhone, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.editscene_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        final ClearLengthEditText edit_password_gateway = (ClearLengthEditText) view.findViewById(R.id.edit_password_gateway);
        edit_password_gateway.setText(name);
        edit_password_gateway.setSelection(edit_password_gateway.getText().length());
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(context, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_password_gateway.getText().toString() == null ||
                        edit_password_gateway.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(context, "区域名称为空");
                    return;
                }
                sraum_updateFamilyName(areaNumber, edit_password_gateway.getText().toString(),
                        mobilePhone,position);
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 修改区域名称
     */
    private void sraum_updateFamilyName(final String areaNumber, final String newName, final String mobilePhone, final int position) {
//        areaNumber：区域编号
//        newName：新的区域名称
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(context));
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        mapdevice.put("areaNumber", areaNumber);
        mapdevice.put("familyName", newName);
        mapdevice.put("mobilePhone", mobilePhone);
        MyOkHttp.postMapString(ApiHelper.sraum_updateFamilyName
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_updateFamilyName(areaNumber, newName, mobilePhone, position);
                    }
                }, context, null) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void pullDataError() {
                        super.pullDataError();
                    }

                    @Override
                    public void emptyResult() {
                        super.emptyResult();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                        //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(context, "areaNumber\n" +
                                "不存在");
                    }

                    @Override
                    public void onSuccess(final User user) {
//                        if (refreshListener != null)
//                            refreshListener.refresh();
                        list.get(position).name = newName;
                        notifyDataSetChanged();
                    }

                    @Override
                    public void threeCode() {
                        ToastUtil.showToast(context, "名字已存在");
                    }
                });
    }

    class ViewHolder {
        TextView mytext, myfamtext;
        Button btnDelete;
        Button rename_btn;
    }
}
