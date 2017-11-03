package com.rschina.heilongjiang.activity;

import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.rschina.heilongjiang.R;

/**
 * Created by Administrator on 2017/9/27.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView settint_back;
    private ImageView direction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        intiListener();

    }


    private void initView() {
        settint_back = (ImageView) findViewById(R.id.setting_back);
        direction = (ImageView) findViewById(R.id.direction);

    }

    private void intiListener() {
        settint_back.setOnClickListener(this);
        direction.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_back:
                finish();
                break;
            case R.id.direction:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                direction.setImageResource(R.drawable.open);
                break;
        }

    }
}
