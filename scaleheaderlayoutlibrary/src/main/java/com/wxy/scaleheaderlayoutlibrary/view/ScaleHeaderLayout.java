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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.wxy.scaleheaderlayoutlibrary.callback.OnReadyScaleListener;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

public class ScaleHeaderLayout extends LinearLayout {
    private boolean isEnable = true;//是否允许下拉放大
    private boolean isRefreshable = false;//是否允许下拉刷新
    private int headHeight,headWidth;//头部的高度和宽度
    private OnReadyScaleListener onReadyScaleListener;//是否可以滑动放大
    private boolean isFlingScale;//是否可以惯性放大
    private View headView;//可放大的View
    private boolean isBeginScale;//开始缩放
    private float downX,downY;
    private float maxScale=2;//最大缩放倍数
    private float mLastY ;
    private float mTotalDy;//滑动的距离
    private float mLastScale;//缩放比例
    private int mLastFlingY;
    private float ratio; //阻尼系数
    private ValueAnimator anim;//回弹动画
    private int recoverTime=200;
    private boolean isFirstInitAnim;
    public void setRecoverTime(int recoverTime) {
        this.recoverTime = recoverTime<200?200:recoverTime;
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

    public void setHeadView(final View headView) {
        this.headView = headView;
        headView.post(new Runnable() {
            @Override
            public void run() {
                if (headHeight==0){
                headHeight=headView.getHeight();
                headWidth=headView.getWidth();
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
        initAnim();
        ViewConfiguration vc = ViewConfiguration.get(context);
        mMaxFlingVelocity = vc.getMaximumFlingVelocity();
    }
    private void initAnim() {
        isFirstInitAnim=true;
        anim = new ValueAnimator();
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(recoverTime);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!isFirstInitAnim){
                    float currentValue = (float) animation.getAnimatedValue();
                    Log.v("heihei=",currentValue+"");
                    setHeadViewPosition(currentValue);
            }
            }
        });

    }
    private void setHeadViewPosition(float scale){
        ViewGroup.LayoutParams layoutParams=headView.getLayoutParams();
        layoutParams.height= (int) (headHeight+headHeight*(scale-1)/2);
        headView.setLayoutParams(layoutParams);
//        headView.requestLayout();
//        Log.v("heihei=",headHeight+"");

//        headView.setScaleX(scale);
//        headView.setScaleY(scale);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (scroller==null){
            scroller=new Scroller(getContext());
        }
        if (isHasHeadView()&&isReadyScale()&&scroller.isFinished()){
            //添加速度检测器，用于处理fling效果

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
                if (moveY > 0 && moveY / Math.abs(moveX) > 2) {
                    isBeginScale = true;
                    return true;
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
        if (isHasHeadView()&&isReadyScale()&&scroller.isFinished()){
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(ev);
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (isBeginScale) {
                        float moveY = ev.getY() - mLastY;
                        //向下滑动
                        scale((int) moveY);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (isBeginScale){
                        mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                        int yvel = (int) mVelocityTracker.getYVelocity();
                        mLastFlingY=(int) mTotalDy;

                        scroller.fling( 0, (int) mTotalDy,0,-yvel,-headWidth*20,headWidth*20,-headHeight*10,headHeight*10);
                        invalidate();
                        if (mVelocityTracker != null) {
                            mVelocityTracker.clear();
                        }
                        return true;
                    }
                    break;
            }
            mLastY= ev.getY();
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller==null)return;
        if (scroller.computeScrollOffset()){
            int y = scroller.getCurrY();
                int dy= mLastFlingY-y;
                mLastFlingY=y;
                scale(dy);
            invalidate();
        }else {
            recoverScale();
        }
    }
    private void recoverScale() {
        isFirstInitAnim=false;
        anim.setFloatValues(headView.getScaleX(),1f);
        anim.setDuration(recoverTime);
        anim.start();
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
