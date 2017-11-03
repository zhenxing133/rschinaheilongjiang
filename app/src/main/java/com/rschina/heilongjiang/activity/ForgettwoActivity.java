package com.rschina.heilongjiang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ForgettwoActivity extends AppCompatActivity {

    private EditText forgettwo_pwd;
    private EditText forgettwo_confim_pwd;
    private Button forgettwo_over;
    private String modifiedcode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgettwo);
        modifiedcode = getIntent().getStringExtra("modifiedcode");
        Log.e("yzx", "第一次："+modifiedcode);
        initView();

    }

    private void initView() {
        forgettwo_pwd = (EditText) findViewById(R.id.forgettwo_pwd);
        forgettwo_confim_pwd = (EditText) findViewById(R.id.forgettwo_confim_pwd);
        forgettwo_over = (Button) findViewById(R.id.forgettwo_over);
        forgettwo_over.setOnClickListener(new View.OnClickListener() {

            private String pwd;
            private String confim_pwd;

            @Override
            public void onClick(View view) {
                pwd = forgettwo_pwd.getText().toString().trim();
                confim_pwd = forgettwo_confim_pwd.getText().toString().trim();
                if (pwd.equals("") || confim_pwd.equals("")) {
                    Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                } else if (pwd.length()<6|| pwd.length()>16) {
                    Toast.makeText(getApplicationContext(),"密码为6-16位",Toast.LENGTH_SHORT).show();
                } else if (!pwd.equals(confim_pwd)) {
                    Toast.makeText(getApplicationContext(), "两次密码不一致", Toast.LENGTH_SHORT).show();
                } else {
                    String url = Const.FORMAL_HOST+"forgetLoginPassword_resetPassword?modifiedcode="+modifiedcode+"&newPassword="+ confim_pwd;
                    Log.e("yzx", "第二次："+modifiedcode);
                    Log.e("yzx", "密码" + confim_pwd);
                    OkHttpClient client2 = new OkHttpClient();
                    Request request2 = new Request.Builder().url(url).build();
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
                                    //Intent intent1 = new Intent(ForgettwoActivity.this,UserActivity.class);
                                    //startActivity(intent1);
                                    finish();
                                    Looper.prepare();
                                    Toast.makeText(getApplicationContext(), "密码修改成功", Toast.LENGTH_SHORT).show();
                                    Looper.loop();

                                }
                                //13652485174
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }

            }
        });
    }


}
