package com.wxy.scaleheaderlayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.wxy.scaleheaderlayout.adapter.MainPagerAdapter;
import com.wxy.scaleheaderlayout.fragment.FragmentOne;
import com.wxy.scaleheaderlayout.listener.AppBarLayoutStateChangeListener;
import com.wxy.scaleheaderlayout.view.NoScrollViewPager;
import com.wxy.scaleheaderlayoutlibrary.callback.OnReadyScaleListener;
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
    NoScrollViewPager viewpager;
    @BindView(R.id.scale_layout)
    ScaleHeaderLayout scaleLayout;
    private volatile boolean isScale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beautiful);
        ButterKnife.bind(this);
        initViewPager();
        scaleLayout.setHeadView(ivHead);
        scaleLayout.setRatio(0.5f);
        scaleLayout.setMaxScale(2f);
        scaleLayout.setRecoverTime(400);
        scaleLayout.setOnReadyScaleListener(new OnReadyScaleListener() {
            @Override
            public boolean isReadyScale() {
//                Log.v("heihei=",""+isScale);
                return isScale;
            }
        });
        appbarLayout.addOnOffsetChangedListener(new AppBarLayoutStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, AppBarLayoutStateChangeListener.State state) {
                switch (state){
                    case EXPANDED:
                        isScale=true;
                        viewpager.setNoScroll(false);
                        break;
                    case COLLAPSED:
                        isScale=false;
                        viewpager.setNoScroll(false);
                        break;
                    case INTERMEDIATE:
                        isScale=false;
                        viewpager.setNoScroll(true);
                        break;
                }
            }
        });
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
