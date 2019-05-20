package de.uni_ulm.ismm.stolperpfad.info_display.history.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Wrapper class to rotate the swipe direction of a normal view pager to be vertical
 */
public class HistoricalTermListPager extends ViewPager {

    public HistoricalTermListPager(Context context) {
        super(context);
        setPageTransformer(true, new RotatedPageTransformer());
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public HistoricalTermListPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPageTransformer(true, new RotatedPageTransformer());
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    private class RotatedPageTransformer implements ViewPager.PageTransformer {
        @Override
        public void transformPage(@NonNull View view, float position) {
            if (position < -1) {
                view.setAlpha(0);
            } else if (position <= 1) {
                view.setAlpha(1);
                view.setTranslationX(view.getWidth() * -position);
                float yPosition = position * view.getHeight();
                view.setTranslationY(yPosition);
            } else {
                view.setAlpha(0);
            }
        }
    }

    private MotionEvent rotate(MotionEvent event) {
        float rotatedX = event.getY() / getHeight() *  getWidth();
        float rotatedY = event.getX() /  getWidth() * getHeight();
        event.setLocation(rotatedX, rotatedY);
        return event;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        boolean intercepted = super.onInterceptTouchEvent(rotate(ev));
        rotate(ev);
        return intercepted;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        performClick();
        return super.onTouchEvent(rotate(ev));
    }

}