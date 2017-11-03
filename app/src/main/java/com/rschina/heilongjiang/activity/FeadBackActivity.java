package com.rschina.heilongjiang.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rschina.heilongjiang.R;
import com.rschina.heilongjiang.adapter.LoadingDialog;

/**
 * Created by Administrator on 2017/9/27.
 */

public class FeadBackActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_back;
    private TextView feedback_submit_suggest;
    private Dialog dialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialog.dismiss();
            Toast.makeText(getApplicationContext(),"意见已经反馈成功,非常感谢您宝贵的意见",Toast.LENGTH_LONG).show();
            finish();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
        initListener();

    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.feedback_back_btn);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        feedback_submit_suggest = (TextView) findViewById(R.id.feedback_submit_suggest);
    }

    private void initListener() {
       // iv_back.setOnClickListener(this);
        feedback_submit_suggest.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
           // case R.id.iv_back:
               /* finish();
                Log.e("yzx", "111000");*/
           //     break;
            case R.id.feedback_submit_suggest:
                dialog = LoadingDialog.createLoadingDialog(FeadBackActivity.this, "正在发送反馈意见");
                dialog.show();
                handler.sendEmptyMessageDelayed(0, 1000);
                break;
        }
    }
}
