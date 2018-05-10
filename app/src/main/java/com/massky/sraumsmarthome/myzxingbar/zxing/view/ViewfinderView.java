/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.massky.sraumsmarthome.myzxingbar.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.google.zxing.ResultPoint;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.myzxingbar.zxing.camera.CameraManager;
import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

  private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
  private static final long ANIMATION_DELAY = 15L;
  private static final int OPAQUE = 0xFF;

  private final Paint paint;
//  private final GradientDrawable mDrawable;//扫描线的渐变drawable
private Drawable mDrawable;
    private final Rect mRect;
    private Bitmap resultBitmap;
  private final int maskColor;
  private final int resultColor;
  private final int frameColor;
  private final int laserColor;
  private final int resultPointColor;
  private int scannerAlpha;
  private Collection<ResultPoint> possibleResultPoints;
  private Collection<ResultPoint> lastPossibleResultPoints;
  
  /**
	 * �ĸ���ɫ�߽Ƕ�Ӧ�ĳ���
	 */
	private int ScreenRate;
	
	/**
	 * �ĸ���ɫ�߽Ƕ�Ӧ�Ŀ��
	 */
	private static final int CORNER_WIDTH = 10;
	/**
	 * ɨ����е��м��ߵĿ��
	 */
	private static final int MIDDLE_LINE_WIDTH = 6;
	
	/**
	 * ɨ����е��м��ߵ���ɨ������ҵļ�϶
	 */
	private static final int MIDDLE_LINE_PADDING = 5;
	
	/**
	 * �м�������ÿ��ˢ���ƶ��ľ���
	 */
	private static final int SPEEN_DISTANCE = 3;
	
	/**
	 * �ֻ�����Ļ�ܶ�
	 */
	private static float density;
	/**
	 * �����С
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * �������ɨ�������ľ���
	 */
	private static final int TEXT_PADDING_TOP = 30;
	
	/**
	 * �м们���ߵ����λ��
	 */
	private int slideTop;
	
	/**
	 * �м们���ߵ���׶�λ��
	 */
	private int slideBottom;
	
	boolean isFirst;
    private  Context context;
    private int diameter;//获取屏幕，二维码扫描宽高
    private int laserColor_left;
    private int laserColor_center;
    private int laserColor_right;


    // This constructor is used when the class is built from an XML resource.
  public ViewfinderView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context  = context;
    density = context.getResources().getDisplayMetrics().density;//2.0->4.4, 3.0 ->7.0
   	//������ת����dp
//   	ScreenRate = (int)(20 * density);
      ScreenRate =  dip2px(context,20);
    // Initialize these once for performance rather than calling them every time in onDraw().
    paint = new Paint();
    Resources resources = getResources();
    maskColor = resources.getColor(R.color.transparent);//
    resultColor = resources.getColor(R.color.result_view);
    frameColor = resources.getColor(R.color.viewfinder_frame);
    laserColor = resources.getColor(R.color.viewfinder_laser);
    resultPointColor = resources.getColor(R.color.possible_result_points);
    scannerAlpha = 0;
    possibleResultPoints = new HashSet<ResultPoint>(5);
        //扫描线的渐变drawable,二维码扫描线
      mRect = new Rect();
      laserColor_left = resources.getColor(R.color.viewfinder_laser_left);// 设置 扫描线的 的颜色
      laserColor_center = resources.getColor(R.color.green);// 设置 扫描线的 的颜色
      laserColor_right = resources.getColor(R.color.viewfinder_laser_right);// 设置 扫描线的 的颜色
//      mDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] { laserColor_left,
//              laserColor_center, laserColor_right });
      //加载进度小圆点
      mDrawable = getResources().getDrawable(R.drawable.icon_saomiaoxian);// 圆点图片
      initview();
  }

    private void initview() {
        diameter = 3 * getScreenWidth() / 5;
    }

    /**
     * 得到屏幕宽度
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 得到屏幕高度
     * @return
     */
    private int getScreenHeight() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


    @Override
  public void onDraw(Canvas canvas) {

        Rect frame = new Rect(getScreenWidth() / 5,3 * getScreenWidth() / 5,4* getScreenWidth() / 5,6 *getScreenWidth() / 5);//float left, float top, float right, float bottom
//    Rect frame = CameraManager.get().getFramingRect();//不用系统的区域了
    if (frame == null) {
      return;
    }
    int width = canvas.getWidth();
    int height = canvas.getHeight();
    
  //��ʼ���м��߻��������ϱߺ����±�
		if(!isFirst){
			isFirst = true;
			slideTop = frame.top;
			slideBottom = frame.bottom;
		}

		//����ɨ����������Ӱ���֣����ĸ����֣�ɨ�������浽��Ļ���棬ɨ�������浽��Ļ����
	  	//ɨ��������浽��Ļ��ߣ�ɨ�����ұߵ���Ļ�ұ�
    // Draw the exterior (i.e. outside the framing rect) darkened
    paint.setColor(resultBitmap != null ? resultColor : maskColor);
    canvas.drawRect(0, 0, width, frame.top, paint);
    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
    canvas.drawRect(0, frame.bottom + 1, width, height, paint);

  //�ж��Ƿ���ͼƬ����ʾ���еĻ�����ʾ�ĺõĶ�ά��ͼƬ
    if (resultBitmap != null) {
      // Draw the opaque result bitmap over the scanning rectangle
      paint.setAlpha(OPAQUE);
      canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
    } else {
    	//������ڿ�
      // Draw a two pixel solid black border inside the framing rect
//      paint.setColor(frameColor);
//      canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
//      canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
//      canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
//      canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

    	//���ǻ����м�ĺ�ɫ��
      // Draw a red "laser scanner" line through the middle to show decoding is active
//      paint.setColor(laserColor);
//      paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
//      scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
//      int middle = frame.height() / 2 + frame.top;
//      canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

    	 //��ɨ�����ϵĽǣ��ܹ�8������
        paint.setColor(getResources().getColor(R.color.scan_code_lan));
        canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate,
  				frame.top + CORNER_WIDTH, paint); //left,top,right,bottom --- //左上第一个
  		canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top
  				+ ScreenRate, paint);//left,top,right,bottom --//左上第二个
  		canvas.drawRect(frame.right - ScreenRate, frame.top, frame.right,
  				frame.top + CORNER_WIDTH, paint);//右上第一个
  		canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top
  				+ ScreenRate, paint);//右上第二个

  		canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left
  				+ ScreenRate, frame.bottom, paint);//left,top,right,bottom ,,左下第二个

  		canvas.drawRect(frame.left, frame.bottom - ScreenRate,
  				frame.left + CORNER_WIDTH, frame.bottom, paint);//left,top,right,bottom,左下第一个

  		canvas.drawRect(frame.right - ScreenRate, frame.bottom - CORNER_WIDTH,
  				frame.right, frame.bottom, paint);
  		canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - ScreenRate,
  				frame.right, frame.bottom, paint);

  		//�����м����,ÿ��ˢ�½��棬�м���������ƶ�SPEEN_DISTANCE����ɫ���м���
  		slideTop += SPEEN_DISTANCE;
  		if(slideTop >= frame.bottom){
  			slideTop = frame.top;
  		}
//  		canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH/2,
//                frame.right - MIDDLE_LINE_PADDING,slideTop + MIDDLE_LINE_WIDTH/2, paint);
  		//二维码扫描线，渐变
        int r = 8;
        //shap 样式可以自己自定义 ，使用getResources().getDrawable(id)  进行调用,自定义渐变扫描线
//        mDrawable.setShape(GradientDrawable.RECTANGLE);//设置 矩形线
//        mDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);//LINEAR_GRADIENT  设置线的颜色渐变
//        setCornerRadii(mDrawable, r, r, r, r);
        mRect.set(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH/2,
                frame.right - MIDDLE_LINE_PADDING,slideTop + MIDDLE_LINE_WIDTH/2);
        mDrawable.setBounds(mRect);
        mDrawable.draw(canvas);

        //float left, float top, float right, float bottom, @NonNull Paint paint
        //��ɨ����������
        paint.setColor(Color.WHITE);
  		paint.setTextSize(TEXT_SIZE * density);
  		paint.setAlpha(0x40);
  		paint.setTypeface(Typeface.create("System", Typeface.BOLD));
//  		canvas.drawText(getResources().getString(R.string.scan_text), frame.left, (float) (frame.bottom + (float)TEXT_PADDING_TOP *density), paint);
    	//正在扫描
      Collection<ResultPoint> currentPossible = possibleResultPoints;
      Collection<ResultPoint> currentLast = lastPossibleResultPoints;
      if (currentPossible.isEmpty()) {
        lastPossibleResultPoints = null;
      } else {
        possibleResultPoints = new HashSet<ResultPoint>(5);
        lastPossibleResultPoints = currentPossible;
        paint.setAlpha(OPAQUE);
        paint.setColor(resultPointColor);
        //����������ڵĻ�ɫ���
//        for (ResultPoint point : currentPossible) {
//          canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
//        }
      }
      if (currentLast != null) {
        paint.setAlpha(OPAQUE / 2);
        paint.setColor(resultPointColor);
      //����������ڵĻ�ɫ���
//        for (ResultPoint point : currentLast) {
//          canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
//        }
      }

      // Request another update at the animation interval, but only repaint the laser line,
      // not the entire viewfinder mask.

        /**
         * 画白线，四条白线
         */
        paint.setColor(getResources().getColor(R.color.white));
//        canvas.drawColor(Color.WHITE);                  //设置背景颜色
        paint.setStrokeWidth(dip2px(context,1));
        //设置线宽
        canvas.drawLine(frame.left + ScreenRate, frame.top + dip2px(context,1),
                frame.right - ScreenRate,frame.top + dip2px(context,1), paint);        //绘制直线  ,左上到右上
        canvas.drawLine(frame.left + ScreenRate, frame.bottom - dip2px(context,1),
                frame.right - ScreenRate,frame.bottom - dip2px(context,1), paint);         //绘制左下到右下
        canvas.drawLine(frame.left +  dip2px(context,1), frame.top + ScreenRate ,
                frame.left +  dip2px(context,1),frame.bottom - ScreenRate, paint);     //
        canvas.drawLine(frame.right -  dip2px(context,1), frame.top + ScreenRate ,
                frame.right  -  dip2px(context,1),frame.bottom - ScreenRate, paint);     //


      postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    }
  }


    public void setCornerRadii(GradientDrawable drawable, float r0, float r1, float r2, float r3) {
        drawable.setCornerRadii(new float[] { r0, r0, r1, r1, r2, r2, r3, r3 });
    }


  public void drawViewfinder() {
    resultBitmap = null;
    invalidate();
  }

  /**
   * Draw a bitmap with the result points highlighted instead of the live scanning display.
   *
   * @param barcode An image of the decoded barcode.
   */
  public void drawResultBitmap(Bitmap barcode) {
    resultBitmap = barcode;
    invalidate();
  }

  public void addPossibleResultPoint(ResultPoint point) {
    possibleResultPoints.add(point);
  }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
