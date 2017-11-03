package com.rschina.heilongjiang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rschina.heilongjiang.R;
import com.rschina.heilongjiang.model.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/27.
 */

public class ForgetActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView forgetone_back;
    private EditText forgetone_name;
    private EditText forgetone_edit;
    private TextView forgetone_yzm;
    private Button forget_next;
    private String yzm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        initView();
        intiListener();

    }


    private void initView() {
        forgetone_back = (ImageView) findViewById(R.id.forgetone_back);
        forgetone_name = (EditText) findViewById(R.id.forgetone_name);
        forgetone_edit = (EditText) findViewById(R.id.forgetone_edit);
        forgetone_yzm = (TextView) findViewById(R.id.forgetone_yzm);
        forget_next = (Button) findViewById(R.id.forget_next);
    }

    private void intiListener() {
        forgetone_back.setOnClickListener(this);
        forgetone_yzm.setOnClickListener(this);
        forget_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forgetone_back:
                finish();
                break;
            case R.id.forgetone_yzm:
                forgetone_yzm.setText("已发送");
                String phone = forgetone_name.getText().toString().trim();
                String url = Const.FORMAL_HOST+"forgetLoginPassword_sendMessage?phone="+phone;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String str = response.body().string();
                        try {
                            JSONObject obj = new JSONObject(str);
                            if (obj.getInt("code") == 1) {
                                Log.e("yzx", str);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                break;
            case R.id.forget_next:
                yzm = forgetone_edit.getText().toString().trim();
                if (yzm.equals("")) {
                    return;
                }

                String url2 =Const.FORMAL_HOST+"forgetLoginPassword_checkCode?scode="+yzm;
                OkHttpClient client2 = new OkHttpClient();
                Request request2 = new Request.Builder().url(url2).build();
                Call call2 = client2.newCall(request2);
                call2.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String str = response.body().string();
                        Log.e("yzx", str);
                        try {
                            JSONObject obj = new JSONObject(str);
                            if (obj.getInt("code") == 1) {
                                String modifiedcode = obj.getString("modifiedcode");
                                Intent intent = new Intent(ForgetActivity.this, ForgettwoActivity.class);
                                intent.putExtra("modifiedcode", modifiedcode);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                break;
        }
    }
}
