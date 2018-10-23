package com.massky.sraum.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;


/**
 * Created by zhu on 2018/10/18.
 */

public class SingleChoiceRelativeLayout extends RelativeLayout implements Checkable {
    public SingleChoiceRelativeLayout(Context context) {
        super(context);
    }

    public SingleChoiceRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleChoiceRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {

    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void toggle() {

    }
}
