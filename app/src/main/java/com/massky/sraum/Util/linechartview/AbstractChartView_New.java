package com.massky.sraum.Util.linechartview;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.massky.sraum.Util.SharedPreferencesUtil;

import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.animation.ChartDataAnimator;
import lecho.lib.hellocharts.animation.ChartDataAnimatorV14;
import lecho.lib.hellocharts.animation.ChartDataAnimatorV8;
import lecho.lib.hellocharts.animation.ChartViewportAnimator;
import lecho.lib.hellocharts.animation.ChartViewportAnimatorV14;
import lecho.lib.hellocharts.animation.ChartViewportAnimatorV8;
import lecho.lib.hellocharts.computator.ChartComputator;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.ChartRenderer;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Created by zhu on 2018/10/23.
 */

public abstract class AbstractChartView_New extends View implements Chart {
    private final Context context;
    protected ChartComputator chartComputator;
    protected AxesRenderer axesRenderer;
    protected ChartTouchHandler touchHandler;
    protected ChartRenderer chartRenderer;
    protected ChartDataAnimator dataAnimator;
    protected ChartViewportAnimator viewportAnimator;
    protected boolean isInteractive;
    protected boolean isContainerScrollEnabled;
    protected ContainerScrollType containerScrollType;
    private float x1;
    private float y1;
    private float x2;
    private float y2;

    public AbstractChartView_New(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public AbstractChartView_New(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractChartView_New(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.isInteractive = true;
        this.isContainerScrollEnabled = false;
        this.chartComputator = new ChartComputator();
        this.touchHandler = new ChartTouchHandler(context, this);
        this.axesRenderer = new AxesRenderer(context, this);
        if (Build.VERSION.SDK_INT < 14) {
            this.dataAnimator = new ChartDataAnimatorV8(this);
            this.viewportAnimator = new ChartViewportAnimatorV8(this);
        } else {
            this.viewportAnimator = new ChartViewportAnimatorV14(this);
            this.dataAnimator = new ChartDataAnimatorV14(this);
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.chartComputator.setContentRect(this.getWidth(), this.getHeight(), this.getPaddingLeft(), this.getPaddingTop(), this.getPaddingRight(), this.getPaddingBottom());
        this.chartRenderer.onChartSizeChanged();
        this.axesRenderer.onChartSizeChanged();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.isEnabled()) {
            this.axesRenderer.drawInBackground(canvas);
            int clipRestoreCount = canvas.save();
            canvas.clipRect(this.chartComputator.getContentRectMinusAllMargins());
            this.chartRenderer.draw(canvas);
            canvas.restoreToCount(clipRestoreCount);
            this.chartRenderer.drawUnclipped(canvas);
            this.axesRenderer.drawInForeground(canvas);
        } else {
            canvas.drawColor(ChartUtils.DEFAULT_COLOR);
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();

            Log.e("robin debug2", "x1:" + x1);

            Log.e("robin debug2", "x2:" + x2);
            //左划x1 > x2
            //右划 x1 < x2
            if (x1 < x2) {
                if (slidingAroundListener != null) slidingAroundListener.sliding_around(1);//右划
//                SharedPreferencesUtil.saveData(context,"sliding_around",1);
            } else if (x1 > x2) {//
                if (slidingAroundListener != null) slidingAroundListener.sliding_around(0);//左划
//                SharedPreferencesUtil.saveData(context,"sliding_around",0);
            }
        }


        if (this.isInteractive) {
            boolean needInvalidate;
            if (this.isContainerScrollEnabled) {
                needInvalidate = this.touchHandler.handleTouchEvent(event, this.getParent(), this.containerScrollType);
            } else {
                needInvalidate = this.touchHandler.handleTouchEvent(event);
            }

            if (needInvalidate) {
                ViewCompat.postInvalidateOnAnimation(this);
            }

            return true;
        } else {
            return false;
        }
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.isInteractive && this.touchHandler.computeScroll()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void startDataAnimation() {
        this.dataAnimator.startAnimation(-9223372036854775808L);
    }

    public void startDataAnimation(long duration) {
        this.dataAnimator.startAnimation(duration);
    }

    public void cancelDataAnimation() {
        this.dataAnimator.cancelAnimation();
    }

    public void animationDataUpdate(float scale) {
        this.getChartData().update(scale);
        this.chartRenderer.onChartViewportChanged();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void animationDataFinished() {
        this.getChartData().finish();
        this.chartRenderer.onChartViewportChanged();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setDataAnimationListener(ChartAnimationListener animationListener) {
        this.dataAnimator.setChartAnimationListener(animationListener);
    }

    public void setViewportAnimationListener(ChartAnimationListener animationListener) {
        this.viewportAnimator.setChartAnimationListener(animationListener);
    }

    public void setViewportChangeListener(ViewportChangeListener viewportChangeListener) {
        this.chartComputator.setViewportChangeListener(viewportChangeListener);
    }

    public ChartRenderer getChartRenderer() {
        return this.chartRenderer;
    }

    public void setChartRenderer(ChartRenderer renderer) {
        this.chartRenderer = renderer;
        this.resetRendererAndTouchHandler();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public AxesRenderer getAxesRenderer() {
        return this.axesRenderer;
    }

    public ChartComputator getChartComputator() {
        return this.chartComputator;
    }

    public ChartTouchHandler getTouchHandler() {
        return this.touchHandler;
    }

    public boolean isInteractive() {
        return this.isInteractive;
    }

    public void setInteractive(boolean isInteractive) {
        this.isInteractive = isInteractive;
    }

    public boolean isZoomEnabled() {
        return this.touchHandler.isZoomEnabled();
    }

    public void setZoomEnabled(boolean isZoomEnabled) {
        this.touchHandler.setZoomEnabled(isZoomEnabled);
    }

    public boolean isScrollEnabled() {
        return this.touchHandler.isScrollEnabled();
    }

    public void setScrollEnabled(boolean isScrollEnabled) {
        this.touchHandler.setScrollEnabled(isScrollEnabled);
    }

    public void moveTo(float x, float y) {
        Viewport scrollViewport = this.computeScrollViewport(x, y);
        this.setCurrentViewport(scrollViewport);
    }

    public void moveToWithAnimation(float x, float y) {
        Viewport scrollViewport = this.computeScrollViewport(x, y);
        this.setCurrentViewportWithAnimation(scrollViewport);
    }

    private Viewport computeScrollViewport(float x, float y) {
        Viewport maxViewport = this.getMaximumViewport();
        Viewport currentViewport = this.getCurrentViewport();
        Viewport scrollViewport = new Viewport(currentViewport);
        if (maxViewport.contains(x, y)) {
            float width = currentViewport.width();
            float height = currentViewport.height();
            float halfWidth = width / 2.0F;
            float halfHeight = height / 2.0F;
            float left = x - halfWidth;
            float top = y + halfHeight;
            left = Math.max(maxViewport.left, Math.min(left, maxViewport.right - width));
            top = Math.max(maxViewport.bottom + height, Math.min(top, maxViewport.top));
            scrollViewport.set(left, top, left + width, top - height);
        }

        return scrollViewport;
    }

    public boolean isValueTouchEnabled() {
        return this.touchHandler.isValueTouchEnabled();
    }

    public void setValueTouchEnabled(boolean isValueTouchEnabled) {
        this.touchHandler.setValueTouchEnabled(isValueTouchEnabled);
    }

    public ZoomType getZoomType() {
        return this.touchHandler.getZoomType();
    }

    public void setZoomType(ZoomType zoomType) {
        this.touchHandler.setZoomType(zoomType);
    }

    public float getMaxZoom() {
        return this.chartComputator.getMaxZoom();
    }

    public void setMaxZoom(float maxZoom) {
        this.chartComputator.setMaxZoom(maxZoom);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public float getZoomLevel() {
        Viewport maxViewport = this.getMaximumViewport();
        Viewport currentViewport = this.getCurrentViewport();
        return Math.max(maxViewport.width() / currentViewport.width(), maxViewport.height() / currentViewport.height());
    }

    public void setZoomLevel(float x, float y, float zoomLevel) {
        Viewport zoomViewport = this.computeZoomViewport(x, y, zoomLevel);
        this.setCurrentViewport(zoomViewport);
    }

    public void setZoomLevelWithAnimation(float x, float y, float zoomLevel) {
        Viewport zoomViewport = this.computeZoomViewport(x, y, zoomLevel);
        this.setCurrentViewportWithAnimation(zoomViewport);
    }

    private Viewport computeZoomViewport(float x, float y, float zoomLevel) {
        Viewport maxViewport = this.getMaximumViewport();
        Viewport zoomViewport = new Viewport(this.getMaximumViewport());
        if (maxViewport.contains(x, y)) {
            if (zoomLevel < 1.0F) {
                zoomLevel = 1.0F;
            } else if (zoomLevel > this.getMaxZoom()) {
                zoomLevel = this.getMaxZoom();
            }

            float newWidth = zoomViewport.width() / zoomLevel;
            float newHeight = zoomViewport.height() / zoomLevel;
            float halfWidth = newWidth / 2.0F;
            float halfHeight = newHeight / 2.0F;
            float left = x - halfWidth;
            float right = x + halfWidth;
            float top = y + halfHeight;
            float bottom = y - halfHeight;
            if (left < maxViewport.left) {
                left = maxViewport.left;
                right = left + newWidth;
            } else if (right > maxViewport.right) {
                right = maxViewport.right;
                left = right - newWidth;
            }

            if (top > maxViewport.top) {
                top = maxViewport.top;
                bottom = top - newHeight;
            } else if (bottom < maxViewport.bottom) {
                bottom = maxViewport.bottom;
                top = bottom + newHeight;
            }

            ZoomType zoomType = this.getZoomType();
            if (ZoomType.HORIZONTAL_AND_VERTICAL == zoomType) {
                zoomViewport.set(left, top, right, bottom);
            } else if (ZoomType.HORIZONTAL == zoomType) {
                zoomViewport.left = left;
                zoomViewport.right = right;
            } else if (ZoomType.VERTICAL == zoomType) {
                zoomViewport.top = top;
                zoomViewport.bottom = bottom;
            }
        }

        return zoomViewport;
    }

    public Viewport getMaximumViewport() {
        return this.chartRenderer.getMaximumViewport();
    }

    public void setMaximumViewport(Viewport maxViewport) {
        this.chartRenderer.setMaximumViewport(maxViewport);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setCurrentViewportWithAnimation(Viewport targetViewport) {
        if (null != targetViewport) {
            this.viewportAnimator.cancelAnimation();
            this.viewportAnimator.startAnimation(this.getCurrentViewport(), targetViewport);
        }

        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setCurrentViewportWithAnimation(Viewport targetViewport, long duration) {
        if (null != targetViewport) {
            this.viewportAnimator.cancelAnimation();
            this.viewportAnimator.startAnimation(this.getCurrentViewport(), targetViewport, duration);
        }

        ViewCompat.postInvalidateOnAnimation(this);
    }

    public Viewport getCurrentViewport() {
        return this.getChartRenderer().getCurrentViewport();
    }

    public void setCurrentViewport(Viewport targetViewport) {
        if (null != targetViewport) {
            this.chartRenderer.setCurrentViewport(targetViewport);
        }

        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void resetViewports() {
        this.chartRenderer.setMaximumViewport((Viewport) null);
        this.chartRenderer.setCurrentViewport((Viewport) null);
    }

    public boolean isViewportCalculationEnabled() {
        return this.chartRenderer.isViewportCalculationEnabled();
    }

    public void setViewportCalculationEnabled(boolean isEnabled) {
        this.chartRenderer.setViewportCalculationEnabled(isEnabled);
    }

    public boolean isValueSelectionEnabled() {
        return this.touchHandler.isValueSelectionEnabled();
    }

    public void setValueSelectionEnabled(boolean isValueSelectionEnabled) {
        this.touchHandler.setValueSelectionEnabled(isValueSelectionEnabled);
    }

    public void selectValue(SelectedValue selectedValue) {
        this.chartRenderer.selectValue(selectedValue);
        this.callTouchListener();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public SelectedValue getSelectedValue() {
        return this.chartRenderer.getSelectedValue();
    }

    public boolean isContainerScrollEnabled() {
        return this.isContainerScrollEnabled;
    }

    public void setContainerScrollEnabled(boolean isContainerScrollEnabled, ContainerScrollType containerScrollType) {
        this.isContainerScrollEnabled = isContainerScrollEnabled;
        this.containerScrollType = containerScrollType;
    }

    protected void onChartDataChange() {
        this.chartComputator.resetContentRect();
        this.chartRenderer.onChartDataChanged();
        this.axesRenderer.onChartDataChanged();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    protected void resetRendererAndTouchHandler() {
        this.chartRenderer.resetRenderer();
        this.axesRenderer.resetRenderer();
        this.touchHandler.resetTouchHandler();
    }

    public SlidingAroundListener slidingAroundListener;

    public void setOSlidingAroundListener(SlidingAroundListener touchListener) {
        if (null != touchListener) {
            this.slidingAroundListener = touchListener;
        }
    }

    public interface SlidingAroundListener {
        void sliding_around(int index);
    }

}
