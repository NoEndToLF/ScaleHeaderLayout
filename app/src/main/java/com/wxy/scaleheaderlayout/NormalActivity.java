package com.wxy.scaleheaderlayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wxy.scaleheaderlayoutlibrary.callback.OnReadyScaleListener;
import com.wxy.scaleheaderlayoutlibrary.view.ScaleHeaderLayout;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.ielse.view.SwitchView;

public class NormalActivity extends AppCompatActivity {
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.scale_layout)
    ScaleHeaderLayout scaleLayout;
    @BindView(R.id.switch_fling)
    SwitchView switchFling;
    @BindView(R.id.tv_fling)
    TextView tvFling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        ButterKnife.bind(this);
        scaleLayout.setHeadImageView(ivHead);
        scaleLayout.setRatio(0.5f);
        scaleLayout.setMaxScale(2f);
        scaleLayout.setRecoverTime(400);
        scaleLayout.setOnReadyScaleListener(new OnReadyScaleListener() {
            @Override
            public boolean isReadyScale() {
                return true;
            }
        });
        switchFling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchFling.isOpened()) {
                    scaleLayout.setEnableFlingScale(true);
                    tvFling.setText("是否允许Fling：是");
                } else {
                    scaleLayout.setEnableFlingScale(false);
                    tvFling.setText("是否允许Fling：否");
                }
            }
        });
    }

    @OnClick(R.id.iv_head)
    public void onViewClicked() {
        Toast.makeText(this, "嘿嘿", Toast.LENGTH_SHORT).show();
    }
}
