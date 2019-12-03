package com.wxy.scaleheaderlayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_normal)
    Button btnNormal;
    @BindView(R.id.btn_beautiful)
    Button btnBeautiful;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }
    @OnClick({R.id.btn_normal, R.id.btn_beautiful})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_normal:
                startActivity(new Intent(this,NormalActivity.class));
                break;
            case R.id.btn_beautiful:
                startActivity(new Intent(this,BeautifulActivity.class));
                break;
        }
    }
}
