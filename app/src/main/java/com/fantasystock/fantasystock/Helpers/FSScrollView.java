package com.fantasystock.fantasystock.Helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by wilsonsu on 3/23/16.
 */
public class FSScrollView extends ScrollView {
    public FSScrollView(Context context) {
        super(context);
    }

    public FSScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FSScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return (getScrollY() + getHeight()) < getChildAt(0).getHeight();
        }
        return super.onInterceptTouchEvent(ev);
    }
}
