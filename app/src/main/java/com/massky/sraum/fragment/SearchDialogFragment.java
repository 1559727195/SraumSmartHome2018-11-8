package com.massky.sraum.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.massky.sraum.EditGateWayResultActivity;
import com.massky.sraum.R;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.activity.SearchGateWayActivity;
import com.massky.sraum.adapter.ShowUdpServerAdapter;
import com.massky.sraum.myzxingbar.qrcodescanlib.CaptureActivity;
import com.massky.sraum.tool.Constants;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.massky.sraum.Util.AES.Decrypt;


/**
 * Created by zhu on 2016/12/23.
 */

public class SearchDialogFragment extends DialogFragment implements View.OnClickListener, SearchGateWayActivity.SendParamsInterfacer {

    private Dialog dialog;
    private ImageView back;
    private RelativeLayout sao_rel;
    private RelativeLayout search_rel;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private ListView list_show_rev_item_detail;
    private ShowUdpServerAdapter showUdpServerAdapter;
    private List<String> list = new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                if (dialog != null) dialog.dismiss();
                break;
            case R.id.sao_rel:
                Intent openCameraIntent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(openCameraIntent, Constants.SCAN_REQUEST_CODE);
                break;
            case R.id.search_rel://局域网内扫描添加网关
                Intent searchGateWayIntent = new Intent(getActivity(), SearchGateWayActivity.class);
                startActivityForResult(searchGateWayIntent, Constants.SEARCH_GATEGAY);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == Constants.SCAN_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra("result");
                Log.e("robin debug", "content:" + content);
                if (TextUtils.isEmpty(content))
                    return;
                //在这里解析二维码，变成房号
                // 密钥
                String key = "ztlmassky6206ztl";
                // 解密
                String DeString = null;
                try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                    DeString = Decrypt(content, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (DeString == null) {
                    ToastUtil.showToast(getActivity(), "此二维码不是网关二维码");
//                    scanlinear.setVisibility(View.VISIBLE);
//                    detairela.setVisibility(View.GONE);
                } else {
//                    scanlinear.setVisibility(View.GONE);
//                    detairela.setVisibility(View.VISIBLE);
//                    gateid.setText(DeString);
//                    gatedditexttwo.setText("");
                    //跳转到编辑网关界面

                    Intent intent = new Intent(getActivity(), EditGateWayResultActivity.class);
                    intent.putExtra("gateid", DeString);
                    startActivity(intent);
                }
            }
        }
    }


    public SearchDialogFragment() {

    }

    static DialogClickListener mListener;
    public static SearchDialogFragment newInstance(Context context1, String title, String message,DialogClickListener listener) {
        SearchDialogFragment frag = new SearchDialogFragment();
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("message", message);
        frag.setArguments(b);
        mListener = listener;
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity(), R.style.DialogStyle);
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.search_dialog_fragment, null, false);
        //添加这一行
//       LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear);
//        linearLayout.getBackground().setAlpha(255);//0~255透明度值
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        initView(view);
        initEvent();
        //在这里配置wifi

        dialog.setContentView(view);
        setCancelable(true);//这句话调用这个方法时，按对话框以外的地方不起作用。按返回键也不起作用 -setCancelable (false);按返回键也不起作用
//        StatusBarCompat.compat(getActivity(), getResources().getColor(R.color.colorgraystatusbar));//更改标题栏的颜色
        return dialog;
    }


    /**
     * 让dialogFragment铺满整个屏幕的好办法
     */
    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        android.view.WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }


    private void initView(View view) {
        back = (ImageView) view.findViewById(R.id.back);//progress_loading_linear,loading_error_linear
        list_show_rev_item_detail = (ListView) view.findViewById(R.id.list_show_rev_item_detail);
        list_show_rev_item_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditGateWayResultActivity.class);
                intent.putExtra("gateid", list.get(position));//跳转到编辑网关密码界面
                startActivity(intent);
            }
        });
    }

    private void initEvent() {
        back.setOnClickListener(this);
    }




    public interface DialogClickListener {
        void dialogDismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mListener != null) mListener.dialogDismiss();
    }

    @Override
    public void sendparams(final List<String> list) {
        this.list = list;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showUdpServerAdapter = new ShowUdpServerAdapter(getActivity()
                        , list);
                list_show_rev_item_detail.setAdapter(showUdpServerAdapter);
            }
        },50);
    }
}
