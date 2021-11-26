package com.example.uu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class NoSwipeViewPager extends ViewPager {

    private boolean enabled;

    public NoSwipeViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoSwipeViewPager(@NonNull Context context) {
        super(context);
        this.enabled=false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return enabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return enabled;
    }
}
