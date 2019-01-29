package com.king.photo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import com.AddTogenInterface.AddTogglenInterfacer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.king.photo.util.Bimp;
import com.king.photo.util.FileUtils;
import com.king.photo.util.ImageItem;
import com.king.photo.util.PublicWay;
import com.king.photo.util.Res;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.BitmapUtil;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.activity.HistoryBackActivity;
import com.massky.sraum.activity.MessageActivity;
import com.massky.sraum.activity.PersonMessageActivity;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.massky.sraum.widget.ListViewForScrollView;
import com.yanzhenjie.statusview.StatusUtils;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Map;
import butterknife.InjectView;

import static com.massky.sraum.Util.BitmapUtil.bitmapToString;
import static com.massky.sraum.Util.DipUtil.dip2px;


/**
 * 首页面activity
 *
 * @author king
 * @version 2014年10月18日  下午11:48:34
 * @QQ:595163260
 */
public class MessageSendActivity extends BaseActivity {

    private ListViewForScrollView noScrollgridview;
    private GridAdapter adapter;
    //	private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;
    @InjectView(R.id.pic_select_linear)
    LinearLayout pic_select_linear;
    @InjectView(R.id.list_forscrollview)
    ScrollView listViewForScrollView;
    private View account_view;
    private Button cancelbtn_id, camera_id, photoalbum;
    private DialogUtil dialogUtil;
    private LinearLayout linear_popcamera;
    @InjectView(R.id.hostory_back_txt)
    TextView hostory_back_txt;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.btn_cancel_wangguan)
    Button btn_cancel_wangguan;
    @InjectView(R.id.activity_group_radioGroup_light)
    RadioGroup activity_group_radioGroup_light;
    @InjectView(R.id.edit_text_message)
    ClearEditText edit_text_message;
    @InjectView(R.id.phone_number)
    ClearEditText phone_number;
    private String type = "1";//功能建议
    private DialogUtil dialogUtil_bottom;

    /**
     * 底部弹出拍照，相册弹出框
     */
    private void addViewid() {
        account_view = LayoutInflater.from(MessageSendActivity.this).inflate(R.layout.camera, null);
        linear_popcamera = (LinearLayout) account_view.findViewById(R.id.linear_popcamera);
        cancelbtn_id = (Button) account_view.findViewById(R.id.cancelbtn_id);
        photoalbum = (Button) account_view.findViewById(R.id.photoalbum);
        camera_id = (Button) account_view.findViewById(R.id.camera_id);
        dialogUtil_bottom = new DialogUtil(MessageSendActivity.this, account_view, 1);

        camera_id.setOnClickListener(this);
        photoalbum.setOnClickListener(this);
        cancelbtn_id.setOnClickListener(this);
        hostory_back_txt.setOnClickListener(this);
        back.setOnClickListener(this);

        init_radio_group();
    }

    private void init_radio_group() {
        for (int i = 0; i < activity_group_radioGroup_light.getChildCount(); i++) {
            final int finalI = i;
            activity_group_radioGroup_light.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View view) {
                    switch (finalI) {
                        case 0:
                            type = "1";
                            break;
                        case 1:
                            type = "2";
                            break;
                        case 2:
                            type = "3";
                            break;
                    }
                }
            });
        }
    }

    public void Init() {
        addViewid();//添加底部弹出拍照，选择系统相册。
//
        noScrollgridview = (ListViewForScrollView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    Log.i("ddddddd", "----------");
                    if(dialogUtil_bottom != null)
                    dialogUtil_bottom.loadViewBottomdialog();
                } else {
                    Intent intent = new Intent(MessageSendActivity.this,
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_id:
                //跳转到调用系统相机
                dialogUtil_bottom.removeviewBottomDialog();
                photo();
                break;

            case R.id.photoalbum:
                //跳转到调用系统图库
                dialogUtil_bottom.removeviewBottomDialog();
                Intent intent = new Intent(MessageSendActivity.this,
                        AlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                break;
            case R.id.cancelbtn_id://取消打开照相
                dialogUtil_bottom.removeviewBottomDialog();
                break;
            case R.id.hostory_back_txt:
                startActivity(new Intent(MessageSendActivity.this, HistoryBackActivity.class));
                break;
            case R.id.back:
                MessageSendActivity.this.finish();
                break;
            case R.id.btn_cancel_wangguan:
//                MessageSendActivity.this.finish();
                String content = edit_text_message.getText().toString();
                if (content.equals("")) {
                    ToastUtil.showToast(MessageSendActivity.this, "请简要描述你的问题和意见");
                    return;
                }
                String img = "";
                if ( Bimp.tempSelectBitmap.size() != 0) {
//                    Bitmap bitmap = Bimp.tempSelectBitmap.get(0).getBitmap();
//                    Bitmap nBM = BitmapUtil.scaleBitmap(bitmap, 0.3f);
////                    Bitmap nBM = BitmapUtil.cropBitmap(bitmap);
//                    img = BitmapUtil.bitmaptoString(nBM);
                    img =  Bimp.tempSelectBitmap.get(0).getImagePath();
                    int x =  dip2px(MessageSendActivity.this,80);
                    int y =  dip2px(MessageSendActivity.this,80);
                    img =  bitmapToString(img,x,y);
                }


                String mobilePhone = phone_number.getText().toString();
                if (mobilePhone.length() != 11) {
                    ToastUtil.showToast(MessageSendActivity.this, "请输入正确手机号");
                    return;
                }
                if (mobilePhone.equals("")) {
                    ToastUtil.showToast(MessageSendActivity.this, "请输入您的手机号，方便工作人员联系");
                    return;
                }

                sraum_setComplaint(type,img,content,mobilePhone);
                break;//提交
        }
    }


    private void sraum_setComplaint(String type,String img,String content,String mobilePhone) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(MessageSendActivity.this));
        map.put("content", content);
        map.put("type", type);
        map.put("img", img);
        map.put("mobilePhone", mobilePhone);

        MyOkHttp.postMapObject(ApiHelper.sraum_setComplaint, map, new Mycallback
                (new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, MessageSendActivity.this, dialogUtil) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                MessageSendActivity.this.finish();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                ToastUtil.showToast(MessageSendActivity.this,"datetime 错误");
            }

            @Override
            public void threeCode() {
                super.threeCode();
                ToastUtil.showToast(MessageSendActivity.this,"图片错误");
            }
        });
    }


    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if (Bimp.tempSelectBitmap.size() == 9) {
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.btn_charutupian));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max >= Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    @Override
    protected int viewId() {
        return R.layout.activity_selectimg;
    }

    @Override
    protected void onView() {
        Res.init(this);
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);
        Init();
        StatusUtils.setFullToStatusBar(this);
        listViewForScrollView.smoothScrollTo(0, 0);
        dialogUtil = new DialogUtil(this);
    }

    @Override
    protected void onEvent() {
        btn_cancel_wangguan.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    private static final int TAKE_PICTURE = 0x000001;

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    FileUtils.saveBitmap(bm, fileName);

                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < PublicWay.activityList.size(); i++) {
            if (null != PublicWay.activityList.get(i)) {
                PublicWay.activityList.get(i).finish();
                PublicWay.activityList.remove(i);
            }
        }
        Bimp.tempSelectBitmap.clear();
        Bimp.max = 0;
    }
}

