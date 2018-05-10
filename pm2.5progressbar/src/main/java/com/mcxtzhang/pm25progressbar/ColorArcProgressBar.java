package com.mcxtzhang.pm25progressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * colorful arc progress bar
 * Created by shinelw on 12/4/15.
 */
public class ColorArcProgressBar extends View {

    private int mWidth;
    private int mHeight;
    private int diameter = 500;  //直径
    private float centerX;  //圆心X坐标
    private float centerY;  //圆心Y坐标

    private Paint allArcPaint;
    private Paint progressPaint;
    private Paint vTextPaint;
    private Paint hintPaint;
    private Paint degreePaint;
    private Paint curSpeedPaint;

    private RectF bgRect;

    private ValueAnimator progressAnimator;
    private PaintFlagsDrawFilter mDrawFilter;
    private SweepGradient sweepGradient;
    private Matrix rotateMatrix;

    private float startAngle = 105;
    private float sweepAngle = 270;
    private float currentAngle = 0; //105 - 330
    private float lastAngle;
    private int[] colors = new int[]{Color.GREEN, Color.YELLOW, Color.RED, Color.RED};
    private int[] colors_array = new int[]{Color.parseColor("#AB033D"),
            Color.parseColor("#450F6F"),
            Color.parseColor("#450F6C"),
            Color.parseColor("#450F6C"),
            Color.parseColor("#8BCD1E"),
            Color.parseColor("#E3DF1A"),
            Color.parseColor("#ED870D"),
            Color.parseColor("#FE1303"),
    };//指示器的颜色变化

    private float maxValues = 60;
    private float curValues = 32;
    private float bgArcWidth = dipToPx(2);
    private float progressWidth = dipToPx(10);
    private float textSize = dipToPx(60);
    private float hintSize = dipToPx(15);
    private float curSpeedSize = dipToPx(13);
    private int aniSpeed = 1000;
    private float longdegree = dipToPx(13);
    private float shortdegree = dipToPx(5);
    private final int DEGREE_PROGRESS_DISTANCE = dipToPx(8);

    private String hintColor = "#FFFFFF";
    private String longDegreeColor = "#111111";
    private String shortDegreeColor = "#111111";
    private String bgArcColor = "#111111";
    private String titleString;
    private String hintString;

    private boolean isShowCurrentSpeed = true;
    private boolean isNeedTitle;
    private boolean isNeedUnit;
    private boolean isNeedDial;
    private boolean isNeedContent;
    private Drawable mThumb, mThumbPress;
    private int paddingOuterThumb;

    // sweepAngle / maxValues 的值
    private float k;
    private Paint vTextPaint_zhi;
    private RectF oval;
    private float radius;//圆环半径

    /**
     * 当前进度
     */
    private float progress = 190;//最大对应498-》150刻度
    //progress = 310（实际值） - 0刻度
    //progress =375（实际值） - 50（对应虚拟刻度(pm2.5)）
    //progress =439（实际值） - 100（对应虚拟刻度(pm2.5)）
    //progress =498（实际值） - 150（对应虚拟刻度(pm2.5)）
    //progress =61 （实际值）- 200（对应虚拟刻度(pm2.5)）
    //progress =124（实际值） - 250（对应虚拟刻度(pm2.5)）
    //progress =190（实际值） - 500（对应虚拟刻度(pm2.5)）

    public ColorArcProgressBar(Context context) {
        super(context, null);
        initView();
    }


    public ColorArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initCofig(context, attrs);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCofig(context, attrs);
        initView();
    }

    /**
     * 初始化布局配置
     *
     * @param context
     * @param attrs
     */
    private void initCofig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorArcProgressBar);
        int color1 = a.getColor(R.styleable.ColorArcProgressBar_front_color1, Color.GREEN);
        int color2 = a.getColor(R.styleable.ColorArcProgressBar_front_color2, color1);
        int color3 = a.getColor(R.styleable.ColorArcProgressBar_front_color3, color1);
        int color4 = a.getColor(R.styleable.ColorArcProgressBar_front_color4, color1);
        int color5 = a.getColor(R.styleable.ColorArcProgressBar_front_color5, color1);
        colors = new int[]{color1, color2, color3, color4, color5, color1};

        sweepAngle = a.getInteger(R.styleable.ColorArcProgressBar_total_engle, 270);
        bgArcWidth = a.getDimension(R.styleable.ColorArcProgressBar_back_width, dipToPx(2));
        progressWidth = a.getDimension(R.styleable.ColorArcProgressBar_front_width, dipToPx(10));
        isNeedTitle = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_title, false);
        isNeedContent = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_content, false);
        isNeedUnit = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_unit, false);
        isNeedDial = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_dial, false);
        hintString = a.getString(R.styleable.ColorArcProgressBar_string_unit);
        titleString = a.getString(R.styleable.ColorArcProgressBar_string_title);
        curValues = a.getFloat(R.styleable.ColorArcProgressBar_current_value, 100);
        maxValues = a.getFloat(R.styleable.ColorArcProgressBar_max_value, 60);
//        setCurrentValues(curValues);
        setMaxValues(maxValues);
        a.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 4 * getScreenWidth() / 5;
        int height = 4 * getScreenWidth() / 6 + getScreenWidth() / 10;
        setMeasuredDimension(width, height);
    }

    private void initView() {

        diameter = 3 * getScreenWidth() / 5;
        //弧形的矩阵区域
        bgRect = new RectF();
        bgRect.top = longdegree + progressWidth / 2 + DEGREE_PROGRESS_DISTANCE;
        bgRect.left = longdegree + progressWidth / 2 + DEGREE_PROGRESS_DISTANCE;
        bgRect.right = diameter + (longdegree + progressWidth / 2 + DEGREE_PROGRESS_DISTANCE);
        bgRect.bottom = diameter + (longdegree + progressWidth / 2 + DEGREE_PROGRESS_DISTANCE);

        oval = new RectF(longdegree + progressWidth / 2 + DEGREE_PROGRESS_DISTANCE + progressWidth / 2
                , longdegree + progressWidth / 2 + DEGREE_PROGRESS_DISTANCE + progressWidth / 2,
                diameter + (longdegree + progressWidth / 2 + DEGREE_PROGRESS_DISTANCE) - progressWidth / 2,
                diameter + (longdegree + progressWidth / 2 + DEGREE_PROGRESS_DISTANCE) - progressWidth / 2);//目的让字体显示出来

        //圆心
        centerX = (2 * longdegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE) / 2;
        centerY = (2 * longdegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE) / 2;

        //圆环半径
        radius = centerX - (longdegree + progressWidth / 2 + DEGREE_PROGRESS_DISTANCE + progressWidth / 2);

        //外部刻度线
        degreePaint = new Paint();
        degreePaint.setColor(Color.parseColor(longDegreeColor));

        //整个弧形
        allArcPaint = new Paint();
        allArcPaint.setAntiAlias(true);
        allArcPaint.setStyle(Paint.Style.STROKE);
        allArcPaint.setStrokeWidth(bgArcWidth);
        allArcPaint.setColor(Color.parseColor(bgArcColor));
        allArcPaint.setStrokeCap(Paint.Cap.ROUND);

        //当前进度的弧形
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setColor(Color.GREEN);

        //内容显示文字
        vTextPaint = new Paint();
        vTextPaint.setTextSize(textSize);
        vTextPaint.setColor(Color.WHITE);
        vTextPaint.setTextAlign(Paint.Align.CENTER);

        //指针数字显示
        vTextPaint_zhi = new Paint();
        vTextPaint_zhi.setTextSize(dipToPx(15));
        vTextPaint_zhi.setColor(Color.BLACK);
        vTextPaint_zhi.setTextAlign(Paint.Align.CENTER);


        //显示单位文字
        hintPaint = new Paint();
        hintPaint.setTextSize(hintSize);
        hintPaint.setColor(Color.parseColor(hintColor));
        hintPaint.setTextAlign(Paint.Align.CENTER);

        //显示标题文字
        curSpeedPaint = new Paint();
        curSpeedPaint.setTextSize(curSpeedSize);
        curSpeedPaint.setColor(Color.parseColor(hintColor));
        curSpeedPaint.setTextAlign(Paint.Align.CENTER);

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        sweepGradient = new SweepGradient(centerX, centerY, colors, null);
        rotateMatrix = new Matrix();

        //加载进度小圆点
        mThumbPress = getResources().getDrawable(R.drawable.btn_lamp_plate_down);// 圆点图片
        int thumbHalfheight = mThumbPress.getIntrinsicHeight() / 2;
        int thumbHalfWidth = mThumbPress.getIntrinsicWidth() / 2;
        mThumbPress.setBounds(-thumbHalfWidth, -thumbHalfheight, thumbHalfWidth, thumbHalfheight);
        paddingOuterThumb = thumbHalfheight;//按下的白色图标
        minValidateTouchArcRadius = (int) (radius - paddingOuterThumb * 1.5f);
        maxValidateTouchArcRadius = (int) (radius + paddingOuterThumb * 1.5f);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        String targetText[] = getContext().getResources().getStringArray(R.array.clock);//12,1,2,3.....11
        //抗锯齿
        canvas.setDrawFilter(mDrawFilter);
        if (isNeedDial) {
            //画刻度线
            for (int i = 0; i < 8; i++) {
                if (i == 3) {
                    canvas.rotate(45, centerX, centerY);//刻度 = 200 ，从45度开始。canvas画布选装45度
                    continue;
                }
                canvas.rotate(45, centerX, centerY);
                degreePaint.setStrokeWidth(dipToPx(2));
                degreePaint.setColor(colors_array[i]);
                canvas.drawCircle(centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE + progressWidth / 2
                        , dipToPx(3), degreePaint);
//                canvas.drawText(targetText[i],
//                        centerX, centerY - diameter / 2 - progressWidth - DEGREE_PROGRESS_DISTANCE + progressWidth / 2,
//                        vTextPaint_zhi);//colors_array
            }// diameter为圆的直径

            //竖直数字
            canvas.save();
            canvas.translate(centerX, centerY);
            for (int i = 0; i < 8; i++) {
                drawNum(canvas, (i + 1) * 45, targetText[i], vTextPaint_zhi);//关键点就是字体旋转多少角度就回多少角度
            }
            canvas.restore();
        }

//        //整个弧
//        canvas.drawArc(oval, startAngle, sweepAngle, false, allArcPaint);

        //设置渐变色
        rotateMatrix.setRotate(113, centerX, centerY);
        sweepGradient.setLocalMatrix(rotateMatrix);
        progressPaint.setShader(sweepGradient);

        //当前进度
        canvas.drawArc(oval, 113, 315, false, progressPaint);

        if (isNeedContent) {
            canvas.drawText(String.format("%.0f", curValues), centerX, centerY + textSize / 3, vTextPaint);
        }
        if (isNeedUnit) {
            canvas.drawText(hintString, centerX, centerY + 2 * textSize / 3, hintPaint);
        }
        if (isNeedTitle) {
            canvas.drawText(titleString, centerX, centerY - 2 * textSize / 3, curSpeedPaint);
        }

        //画移动白点
        //paint.setStyle(Paint.Style.FILL_AND_STROKE);


        PointF progressPoint = ChartUtil.calcArcEndPointXY(centerX, centerY, radius, progress, 115);
        //起始角度是105-》中止角度105 + 270 = 375，  // 165 - 150 = 15,
        // 50-60, 100 - 115,150 - 155,

        //canvas.drawCircle(progressPoint.x, progressPoint.y, pointRadius, paint);
        // 画Thumb
        canvas.save();
        canvas.translate(progressPoint.x, progressPoint.y);
        mThumbPress.draw(canvas);
        canvas.restore();
        invalidate();
    }


    /**
     * 数字
     *
     * @param canvas
     * @param degree
     * @param text
     * @param paint
     */
    private void drawNum(Canvas canvas, int degree, String text, Paint paint) {
        Rect textBound = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBound);
        canvas.rotate(degree);
//        canvas.translate(0, borderWidth / 2 + maxScaleLength + textHeight * 3f / 4 - getWidth() / 3);
        canvas.translate(0, (float) ((-centerX - 6.5 * progressWidth - DEGREE_PROGRESS_DISTANCE) / 2.1));
        canvas.rotate(-degree);
//        canvas.drawText(text,-(textBound.right - textBound.left)/2,
//                (Math.abs(paint.ascent())-Math.abs(paint.descent()))/2,paint);
        canvas.drawText(text, textBound.width() / 30,
                textBound.height() / 2, paint);
        canvas.rotate(degree);
//        canvas.translate(0, getWidth() / 3 - (borderWidth / 2 + maxScaleLength + textHeight * 3f / 4));
        canvas.translate(0, (float) ((centerY + 6.5 * progressWidth + DEGREE_PROGRESS_DISTANCE) / 2.1));
        canvas.rotate(-degree);
    }


    public static class ChartUtil {

        /**
         * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
         *
         * @param cirX
         * @param cirY
         * @param radius
         * @param cirAngle
         * @return
         */
        public static PointF calcArcEndPointXY(float cirX, float cirY, float radius, float cirAngle) {//一个在270度那里，一个从270度开始叠加。
            //	    	//centerX, centerY, radius, 0, 270
            //cirAngle = (orginAngle + cirAngle) % 360;
            float posX = 0.0f;
            float posY = 0.0f;
            //将角度转换为弧度
            float arcAngle = (float) (Math.PI * cirAngle / 180.0);
            if (cirAngle < 90) {
                posX = cirX + (float) (Math.cos(arcAngle)) * radius;
                posY = cirY + (float) (Math.sin(arcAngle)) * radius;
            } else if (cirAngle == 90) {
                posX = cirX;
                posY = cirY + radius;
            } else if (cirAngle > 90 && cirAngle < 180) {
                arcAngle = (float) (Math.PI * (180 - cirAngle) / 180.0);
                posX = cirX - (float) (Math.cos(arcAngle)) * radius;
                posY = cirY + (float) (Math.sin(arcAngle)) * radius;
            } else if (cirAngle == 180) {
                posX = cirX - radius;
                posY = cirY;
            } else if (cirAngle > 180 && cirAngle < 270) {
                arcAngle = (float) (Math.PI * (cirAngle - 180) / 180.0);
                posX = cirX - (float) (Math.cos(arcAngle)) * radius;
                posY = cirY - (float) (Math.sin(arcAngle)) * radius;
            } else if (cirAngle == 270) {
                posX = cirX;
                posY = cirY - radius;
            } else {
                arcAngle = (float) (Math.PI * (360 - cirAngle) / 180.0);
                posX = cirX + (float) (Math.cos(arcAngle)) * radius;
                posY = cirY - (float) (Math.sin(arcAngle)) * radius;
            }
            return new PointF(posX, posY);     //new PointF();//   运动小点的位置坐标
        }

        public static PointF calcArcEndPointXY(float cirX, float cirY, float radius, float cirAngle, float orginAngle) {
            //centerX, centerY, radius, 0, 270
            cirAngle = (orginAngle + cirAngle) % 360;
            return calcArcEndPointXY(cirX, cirY, radius, cirAngle);
        }
    }

    private boolean downOnArc = false;

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                if (isTouchArc(x, y)) {
//                    downOnArc = true;
//                    updateArc(x, y);
//                    return true;
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (downOnArc) {
//                    updateArc(x, y);
//                    return true;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                downOnArc = false;
//                invalidate();
////                if (changeListener != null) {
////                    changeListener.onProgressChangeEnd(max, progress);
////                }
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    // 根据点的位置，更新进度
    private void updateArc(int x, int y) {
        int cx = x - getWidth() / 2;
        int cy = y - getHeight() / 2;
        // 计算角度，得出（-1->1）之间的数据，等同于（-180°->180°）
        double angle = Math.atan2(cy, cx) / Math.PI;
        // 将角度转换成（0->2）之间的值，然后加上90°的偏移量
        angle = ((2 + angle) % 2 + (90 / 180f)) % 2;
        // 用（0->2）之间的角度值乘以总进度，等于当前进度
        progress = (int) (angle * maxValues / 2);
//        if (changeListener != null) {
//            changeListener.onProgressChange(max, progress);
//        }
        Log.e("robin debug", "progress:" + progress);
        invalidate();
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > 498) {//maxProgress
            progress = (int) 498;
        }
        if (progress <= 498) {
            this.progress = progress;
            postInvalidate();
        }
    }


    private int minValidateTouchArcRadius; // 最小有效点击半径
    private int maxValidateTouchArcRadius; // 最大有效点击半径

    // 判断是否按在圆边上
    private boolean isTouchArc(int x, int y) {
        double d = getTouchRadius(x, y);
        if (d >= minValidateTouchArcRadius && d <= maxValidateTouchArcRadius) {
            return true;
        }
        return false;
    }

    // 计算某点到圆点的距离
    private double getTouchRadius(int x, int y) {
        int cx = x - getWidth() / 2;
        int cy = y - getHeight() / 2;
        return Math.hypot(cx, cy);
    }

    /**
     * 设置最大值
     *
     * @param maxValues
     */
    public void setMaxValues(float maxValues) {
        this.maxValues = maxValues;
        k = sweepAngle / maxValues;
    }

    /**
     * 设置当前值
     *
     * @param currentValues
     */
    public void setCurrentValues(float currentValues) {
        if (currentValues >= 0 && currentValues <= 200) {

        }
        if (currentValues > maxValues) {
            currentValues = maxValues;
        }
        if (currentValues < 0) {
            currentValues = 0;
        }
        this.curValues = currentValues;
        this.progress = currentValues;
        // 50-60, 100 - 115,150 - 155,
        if (progress >= 0 && progress < 50) {
            progress += 10;
        } else if (progress >= 50 && progress < 100) {
            progress += 20;
        } else if (progress >= 100 && progress < 150) {
            progress += 10;
        }
        lastAngle = currentAngle;
        setAnimation(lastAngle, currentValues * k, aniSpeed);
    }

    /**
     * 设置整个圆弧宽度
     *
     * @param bgArcWidth
     */
    public void setBgArcWidth(int bgArcWidth) {
        this.bgArcWidth = bgArcWidth;
    }

    /**
     * 设置进度宽度
     *
     * @param progressWidth
     */
    public void setProgressWidth(int progressWidth) {
        this.progressWidth = progressWidth;
    }

    /**
     * 设置速度文字大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * 设置单位文字大小
     *
     * @param hintSize
     */
    public void setHintSize(int hintSize) {
        this.hintSize = hintSize;
    }

    /**
     * 设置单位文字
     *
     * @param hintString
     */
    public void setUnit(String hintString) {
        this.hintString = hintString;
        invalidate();
    }

    /**
     * 设置直径大小
     *
     * @param diameter
     */
    public void setDiameter(int diameter) {
        this.diameter = dipToPx(diameter);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    private void setTitle(String title) {
        this.titleString = title;
    }

    /**
     * 设置是否显示标题
     *
     * @param isNeedTitle
     */
    private void setIsNeedTitle(boolean isNeedTitle) {
        this.isNeedTitle = isNeedTitle;
    }

    /**
     * 设置是否显示单位文字
     *
     * @param isNeedUnit
     */
    private void setIsNeedUnit(boolean isNeedUnit) {
        this.isNeedUnit = isNeedUnit;
    }

    /**
     * 设置是否显示外部刻度盘
     *
     * @param isNeedDial
     */
    private void setIsNeedDial(boolean isNeedDial) {
        this.isNeedDial = isNeedDial;
    }

    /**
     * 为进度设置动画
     *
     * @param last
     * @param current
     */
    private void setAnimation(float last, float current, int length) {
        progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(length)
                .setTarget(currentAngle);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle = (float) animation.getAnimatedValue();
                curValues = currentAngle / k;
            }
        });
        progressAnimator.start();
    }

    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 得到屏幕宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
