package com.wxy.scaleheaderlayout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.wxy.scaleheaderlayout.adapter.MainPagerAdapter;
import com.wxy.scaleheaderlayout.fragment.FragmentOne;
import com.wxy.scaleheaderlayout.listener.AppBarLayoutStateChangeListener;
import com.wxy.scaleheaderlayoutlibrary.callback.OnReadyScaleListener;
import com.wxy.scaleheaderlayoutlibrary.callback.OnRefreshListener;
import com.wxy.scaleheaderlayoutlibrary.view.ScaleHeaderLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BeautifulActivity extends AppCompatActivity {

    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_fragment_one)
    TextView tvFragmentOne;
    @BindView(R.id.tv_fragment_two)
    TextView tvFragmentTwo;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.scale_layout)
    ScaleHeaderLayout scaleLayout;
    @BindView(R.id.iv_refresh)
    ImageView ivRefresh;
    private boolean isScale;
    private float maxRefreshHeight;
    private boolean isFreshing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beautiful);
        ButterKnife.bind(this);
        initViewPager();
        scaleLayout.setHeadView(ivHead);
        scaleLayout.setRatio(0.2f);
        scaleLayout.setMaxScale(2f);
        scaleLayout.setRecoverTime(400);
        scaleLayout.setEnableFlingScale(true);
        scaleLayout.setOnReadyScaleListener(new OnReadyScaleListener() {
            @Override
            public boolean isReadyScale() {
                return isScale;
            }
        });
        scaleLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshPrepare() {
                if (isFreshing)return;
                //先让ivRefresh隐藏掉
                ivRefresh.setTranslationY(-ivRefresh.getHeight());
            }
            @Override
            public void onRefreshPulldown(float dy, float headViewHeight) {
                if (isFreshing)return;
                //自定义下拉的最大位置
                 maxRefreshHeight=headViewHeight/3;
                //随着下拉距离变换
                ivRefresh.setTranslationY(Math.min(dy-ivRefresh.getHeight(),maxRefreshHeight));
                Log.v("xixi", dy-ivRefresh.getHeight()/maxRefreshHeight*360f+"");
                ivRefresh.setRotation(dy-ivRefresh.getHeight()/maxRefreshHeight*360);
            }
            @Override
            public void onRefreshReady() {
                if (ivRefresh.getTranslationY()<maxRefreshHeight){
                    //达不到最大下拉位置，则认为不加载，直接recover
                    recover();
                }else {
                    //达到最大下拉位置，则认为加载,执行加载动画
                    isFreshing=true;
                    Toast.makeText(BeautifulActivity.this, "开始加载", Toast.LENGTH_SHORT).show();
                float rotation = ivRefresh.getRotation();
                ValueAnimator valueAnimator= ObjectAnimator.ofFloat(ivRefresh, "rotation", rotation, rotation + 360);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.setDuration(1000);
                valueAnimator.setRepeatMode(ValueAnimator.RESTART);
                valueAnimator.setRepeatCount(-1);
                valueAnimator.start();
                //模拟耗时操作
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BeautifulActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
                        valueAnimator.cancel();
                        recover();
                    }
                },2000);
            }
            }
        });
        appbarLayout.addOnOffsetChangedListener(new AppBarLayoutStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                switch (state) {
                    case EXPANDED:
                        isScale = true;
                        break;
                    case COLLAPSED:
                        isScale = false;
                        break;
                    case INTERMEDIATE:
                        isScale = false;
                        break;
                }
            }
        });
    }

    private void recover() {
        float rotation = ivRefresh.getRotation();
        AnimatorSet animatorSet = new AnimatorSet();
        if (!isFreshing){
        animatorSet.play( ObjectAnimator.ofFloat(ivRefresh, "translationY", ivRefresh.getTranslationY(), -ivRefresh.getHeight()))
        .with(ObjectAnimator.ofFloat(ivRefresh, "rotation", rotation, rotation - 360));
        }else {
            animatorSet.play( ObjectAnimator.ofFloat(ivRefresh, "translationY", ivRefresh.getTranslationY(), -ivRefresh.getHeight()))
                    .with(ObjectAnimator.ofFloat(ivRefresh, "rotation", rotation, rotation + 360));
        }
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.setDuration(300);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                isFreshing=false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();

    }

    private void initViewPager() {
        List<Fragment> list = new ArrayList<>();
        list.add(new FragmentOne());
        list.add(new FragmentOne());
        list.add(new FragmentOne());
        list.add(new FragmentOne());
        viewpager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), list));
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tvFragmentOne.setTextColor(getResources().getColor(R.color.colorAccent));
                        tvFragmentTwo.setTextColor(getResources().getColor(R.color.black));
                        break;
                    case 1:
                        tvFragmentOne.setTextColor(getResources().getColor(R.color.black));
                        tvFragmentTwo.setTextColor(getResources().getColor(R.color.colorAccent));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.tv_fragment_one, R.id.tv_fragment_two})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_fragment_one:
                viewpager.setCurrentItem(0);
                tvFragmentOne.setTextColor(getResources().getColor(R.color.colorAccent));
                tvFragmentTwo.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.tv_fragment_two:
                viewpager.setCurrentItem(1);
                tvFragmentOne.setTextColor(getResources().getColor(R.color.black));
                tvFragmentTwo.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
        }
    }
}
