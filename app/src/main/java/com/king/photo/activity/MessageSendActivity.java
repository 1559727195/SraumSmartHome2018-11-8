package com.king.photo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.king.photo.util.Bimp;
import com.king.photo.util.FileUtils;
import com.king.photo.util.ImageItem;
import com.king.photo.util.PublicWay;
import com.king.photo.util.Res;
import com.massky.sraum.R;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.activity.HistoryBackActivity;
import com.massky.sraum.activity.PersonMessageActivity;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.widget.ListViewForScrollView;
import com.yanzhenjie.statusview.StatusUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.InjectView;
import lecho.lib.hellocharts.model.Line;


/**
 * 首页面activity
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:48:34
 */
public class MessageSendActivity extends BaseActivity  {

	private ListViewForScrollView noScrollgridview;
	private GridAdapter adapter;
//	private View parentView;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	public static Bitmap bimap ;
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
	/**
	 * 底部弹出拍照，相册弹出框
	 */
	private void addViewid() {
		account_view = LayoutInflater.from(MessageSendActivity.this).inflate(R.layout.camera, null);
		linear_popcamera = (LinearLayout) account_view.findViewById(R.id.linear_popcamera);
		cancelbtn_id = (Button) account_view.findViewById(R.id.cancelbtn_id);
		photoalbum = (Button) account_view.findViewById(R.id.photoalbum);
		camera_id = (Button) account_view.findViewById(R.id.camera_id);
		dialogUtil = new DialogUtil(MessageSendActivity.this, account_view, 1);

		camera_id.setOnClickListener(this);
		photoalbum.setOnClickListener(this);
		cancelbtn_id.setOnClickListener(this);
		hostory_back_txt.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	public void Init() {
		
//		pop = new PopupWindow(MessageSendActivity.this);
//
//		View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
//
//		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
//
//		pop.setWidth(LayoutParams.MATCH_PARENT);
//		pop.setHeight(LayoutParams.WRAP_CONTENT);
//		pop.setBackgroundDrawable(new BitmapDrawable());
//		pop.setFocusable(true);
//		pop.setOutsideTouchable(true);
//		pop.setContentView(view);
//
//		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
//		Button bt1 = (Button) view
//				.findViewById(R.id.item_popupwindows_camera);
//		Button bt2 = (Button) view
//				.findViewById(R.id.item_popupwindows_Photo);
//		Button bt3 = (Button) view
//				.findViewById(R.id.item_popupwindows_cancel);
//		parent.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				//
//				// TODO Auto-generated method stub
//				pop.dismiss();
//				ll_popup.clearAnimation();
//			}
//		});
//		bt1.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				photo();
//				pop.dismiss();
//				ll_popup.clearAnimation();
//			}
//		});
//		bt2.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				Intent intent = new Intent(MessageSendActivity.this,
//						AlbumActivity.class);
//				startActivity(intent);
//				overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
//				pop.dismiss();
//				ll_popup.clearAnimation();
//			}
//		});
//		bt3.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				pop.dismiss();
//				ll_popup.clearAnimation();
//			}
//		});
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
//					ll_popup.startAnimation(AnimationUtils.loadAnimation(MessageSendActivity.this,R.anim.activity_translate_in));
//					pop.showAtLocation(pic_select_linear, Gravity.BOTTOM, 0, 0);
					dialogUtil.loadViewBottomdialog();
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
				dialogUtil.removeviewBottomDialog();
//				gotoCarema();
				photo();
//				pop.dismiss();
//				ll_popup.clearAnimation();
				break;

			case R.id.photoalbum:
				//跳转到调用系统图库
				dialogUtil.removeviewBottomDialog();
//				gotoPhoto();
				Intent intent = new Intent(MessageSendActivity.this,
						AlbumActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
//				pop.dismiss();
//				ll_popup.clearAnimation();
				break;
			case R.id.cancelbtn_id://取消打开照相
				dialogUtil.removeviewBottomDialog();
//				ll_popup.clearAnimation();

			case R.id.hostory_back_txt:
				startActivity(new Intent(MessageSendActivity.this,HistoryBackActivity.class));
				break;
			case R.id.back:
				MessageSendActivity.this.finish();
				break;
			case R.id.btn_cancel_wangguan:
				MessageSendActivity.this.finish();
				break;//提交
		}
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
			if(Bimp.tempSelectBitmap.size() == 9){
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

			if (position ==Bimp.tempSelectBitmap.size()) {
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

//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
////		PublicWay.activityList.add(this);
////		parentView = getLayoutInflater().inflate(R.layout.activity_selectimg, null);
////		setContentView(parentView);
//	}

	@Override
	protected void onView() {
		Res.init(this);
		bimap = BitmapFactory.decodeResource(
				getResources(),
				R.drawable.icon_addpic_unfocused);
		Init();
		StatusUtils.setFullToStatusBar(this);
		listViewForScrollView.smoothScrollTo(0, 0);
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

//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			for (int i = 0; i < PublicWay.activityList.size(); i++) {
//				if (null != PublicWay.activityList.get(i)) {
//					PublicWay.activityList.get(i).finish();
//					PublicWay.activityList.remove(i);
//				}
//			}
////			System.exit(0);
//
////			for (int i = 0;  i < Bimp.tempSelectBitmap.size(); i ++) {
////				Bimp.tempSelectBitmap = new ArrayList<>();
////			}
//			Bimp.tempSelectBitmap.clear();
//			Bimp.max = 0;
//		}
//		return true;
//	}


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

