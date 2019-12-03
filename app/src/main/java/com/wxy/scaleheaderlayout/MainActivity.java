package com.wxy.scaleheaderlayout;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.wxy.scaleheaderlayoutlibrary.callback.OnReadyScaleListener;
import com.wxy.scaleheaderlayoutlibrary.view.ScaleHeaderLayout;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.scale_layout)
    ScaleHeaderLayout scaleLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        scaleLayout.setHeadView(ivHead);
        scaleLayout.setRatio(0.5f);
        scaleLayout.setMaxScale(2f);
        scaleLayout.setRecoverTime(400);
        scaleLayout.setOnReadyScaleListener(new OnReadyScaleListener() {
            @Override
            public boolean isReadyScale() {
                return true;
            }
        });
    }

    @OnClick(R.id.iv_head)
    public void onViewClicked() {
        Toast.makeText(this, "嘿嘿", Toast.LENGTH_SHORT).show();
    }
}
