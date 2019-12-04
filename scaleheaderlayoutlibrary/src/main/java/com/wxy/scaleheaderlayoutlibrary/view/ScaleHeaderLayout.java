package com.wxy.scaleheaderlayoutlibrary.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.wxy.scaleheaderlayoutlibrary.callback.OnReadyScaleListener;
import com.wxy.scaleheaderlayoutlibrary.callback.OnRefreshListener;

import java.util.concurrent.CountDownLatch;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

public class ScaleHeaderLayout extends FrameLayout {
    private boolean isEnableScale = true;//是否允许下拉放大
    private int headHeight,headWidth;//头部的高度和宽度
    private OnReadyScaleListener onReadyScaleListener;//是否可以滑动放大
    private boolean isEnableFlingScale;//是否可以惯性放大
    private View headView;//可放大的View
    private boolean isBeginScale;//开始缩放
    private float downX,downY;
    private float maxScale=2;//最大缩放倍数
    private float mLastY ;
    private float mTotalDy;//滑动的距离
    private float mLastScale;//缩放比例
    private float mLastFlingY;
    private float ratio; //阻尼系数
    private ValueAnimator anim;//回弹动画
    private int recoverTime=200;
    private boolean isFling;
    public void setEnableScale(boolean enableScale) {
        isEnableScale = enableScale;
    }
    public void setRecoverTime(int recoverTime) {
        this.recoverTime = recoverTime<200?200:recoverTime;
    }
    public void setEnableFlingScale(boolean enableFlingScale) {
        isEnableFlingScale = enableFlingScale;
    }
    protected VelocityTracker mVelocityTracker;
    private int mMaxFlingVelocity;//最大速度
    protected Scroller scroller;
    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }
    public void setRatio(float ratio) {
        this.ratio = ratio<0?0:ratio;
    }
    private OnRefreshListener onRefreshListener;
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;

    }

    public void setHeadView(final View headView) {
        this.headView = headView;
        if (headView!=null){
            if (!(headView.getParent()  instanceof LinearLayout)){
                throw new RuntimeException("headView必须至于LinearLayout中");
            }
        }
        headView.post(new Runnable() {
            @Override
            public void run() {
                if (headHeight==0){
                headHeight=headView.getHeight();
                headWidth=headView.getWidth();
                    if (onRefreshListener!=null){
                        onRefreshListener.onRefreshPrepare();
                    }
                }
            }
        });
    }
    public void setOnReadyScaleListener(OnReadyScaleListener onReadyScaleListener) {
        this.onReadyScaleListener = onReadyScaleListener;
    }

    public ScaleHeaderLayout(Context context) {
        this(context,null);
    }

    public ScaleHeaderLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
    public ScaleHeaderLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration vc = ViewConfiguration.get(context);
        mMaxFlingVelocity = vc.getMaximumFlingVelocity();
            scroller=new Scroller(getContext());
    }
    private void setHeadViewPosition(float scale){
        ViewGroup.LayoutParams layoutParams=headView.getLayoutParams();
        layoutParams.height= (int) ((float)headHeight+(float)headHeight*(scale-1)/2);
        layoutParams.width= (int) ((float)headWidth+(float)headWidth*(scale-1)/2);
        headView.setPadding(0,0, (int) ((float)layoutParams.width-(float)headWidth),0);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isHasHeadView()&&isReadyScale()&&!isFling&&isEnableScale){
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                isBeginScale = false;
                mLastY=ev.getY();
                mTotalDy=0;
                mLastFlingY=0;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX() - downX;
                float moveY = ev.getY() -downY ;
                //向下滑动
                if (isReadyScale()){
                    if (moveY > 0 && moveY / Math.abs(moveX) > 2) {
                        isBeginScale = true;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
        }
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isHasHeadView()&&isReadyScale()&&!isFling&&isEnableScale){
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(ev);
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (isBeginScale&&mLastY!=-1) {
                        float moveY = ev.getY() - mLastY;
                        //向下滑动
                        scale((int) moveY);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (isBeginScale&&mLastY!=-1){
                        if (isEnableFlingScale){
                        mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                        int yvel = (int) mVelocityTracker.getYVelocity();
                        //因为速度过大的话，        headView.requestLayout();会有轻微的卡顿，所以限制了最大速度
                        yvel= Math.min(yvel,4000);
                        if (yvel>1000){
                        mLastFlingY= mTotalDy;
                        isFling=true;
                        scroller.fling( 0, (int) mTotalDy,0,-yvel,-headWidth*20,headWidth*20,-headHeight*10,headHeight*10);
                        invalidate();
                        if (mVelocityTracker != null) {
                            mVelocityTracker.clear();
                        }
                        }else {
                            scroller.abortAnimation();
                            recoverScale();
                        }
                        }else {
                            scroller.abortAnimation();
                            recoverScale();
                        }
                        return true;
                    }
                    break;
            }
            if (mLastY!=-1){
            mLastY= ev.getY();
            }
        }
        return super.onTouchEvent(ev);
    }
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (!isFling)return;
        if (scroller.computeScrollOffset()){
            int y = scroller.getCurrY();
                int dy=(int) mLastFlingY-y;
                mLastFlingY=y;
                mLastY=-1;
            scale(dy);
            invalidate();
        }else {
            recoverScale();
        }
    }
    private void recoverScale() {
        if (anim==null) {
            anim = new ValueAnimator();
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(recoverTime);
            anim.setFloatValues(mLastScale, 1f);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = (float) animation.getAnimatedValue();
                    setHeadViewPosition(currentValue);
                }
            });
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (onRefreshListener!=null){
                        onRefreshListener.onRefreshReady();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isFling = false;
                    mLastY=-1;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            anim.start();
        }else {
            if (!anim.isRunning()){
                anim.setDuration(recoverTime);
                anim.setFloatValues(mLastScale, 1f);
                anim.start();
            }
        }
    }
    private void scale(  int dy) {
        mTotalDy += dy;
        mLastScale = Math.max(1f, 1f + mTotalDy / (headHeight*(1+ratio)));
        if (mLastScale>=maxScale){
            mLastScale=maxScale;
            if (!scroller.isFinished()){
                scroller.abortAnimation();
            }
        }
        if (onRefreshListener!=null){
            onRefreshListener.onRefreshPulldown(mTotalDy-mTotalDy*(mLastScale-1)/2,headHeight);
        }
        setHeadViewPosition(mLastScale);
    }

    //有headView
    private boolean isHasHeadView(){
        return headView!=null;
    }
    //可以放大
    private boolean isReadyScale(){
        return onReadyScaleListener!=null&&onReadyScaleListener.isReadyScale();
    }

}
