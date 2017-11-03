package com.rschina.heilongjiang.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rschina.heilongjiang.R;
import com.rschina.heilongjiang.adapter.LoadingDialog;
import com.rschina.heilongjiang.model.Const;
import com.rschina.heilongjiang.model.LoginBean;
import com.rschina.heilongjiang.utils.GsonUtils;
import com.rschina.heilongjiang.utils.MD5Util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/17.
 */

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText login_username_et;
    private EditText login_password_et;
    private Button btn_submit_login;
    private TextView warning;
    private TextView login_regist_enter;
    private String login_name;
    private String login_password;
    private Dialog loadingDialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            warning.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(),"登入失败",Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
        initData();
    }



    private void initView() {
        login_username_et = (EditText)findViewById(R.id.login_username_et);
        login_password_et = (EditText)findViewById(R.id.login_password_et);
        btn_submit_login = (Button)findViewById(R.id.btn_submit_login);
        warning = (TextView)findViewById(R.id.warning);
        login_regist_enter = (TextView)findViewById(R.id.login_regist_enter);

    }

    private void initData() {
        btn_submit_login.setOnClickListener(this);
        login_regist_enter.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_regist_enter:
                Intent intent = new Intent(UserActivity.this, RegistActivity.class);
                startActivity(intent);
                //Toast.makeText(getActivity(),"---------",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_submit_login:
                login_name = login_username_et.getText().toString().trim();
                login_password = login_password_et.getText().toString().trim();
                if (login_name.equals("") || login_password.equals("")) {
                    warning.setVisibility(View.VISIBLE);
                    warning.setText("帐号密码不能为空");
                    handler.sendEmptyMessageDelayed(0, 3500);
                    return;
                } else if (login_name.length() < 6 || login_name.length() > 18 && login_password.length() < 6 || login_password.length() > 18) {
                    warning.setVisibility(View.VISIBLE);
                    warning.setText("请输入正确格式的的手机号或邮箱,6-16位密码");
                    handler.sendEmptyMessageDelayed(0, 3500);
                } else if (!isMobile(login_name)) {
                    warning.setVisibility(View.VISIBLE);
                    warning.setText("请输入正确的手机号或邮箱");
                    handler.sendEmptyMessageDelayed(0, 3500);
                } else {

                   /* loadingDialog = LoadingDialog.createLoadingDialog(this, "玩命登入中...");
                    loadingDialog.show();*/
                    String pwd = MD5Util.encrypt(login_password);
                    //Log.e("yzx", pwd);
                    OkHttpClient client = new OkHttpClient();
                    //http://localhost:8189/userLogin?contact=1&password=84d5f8ec365b42a04d2b0dcb585ed1ed&type=2
                    String url = Const.LOGIN_URL + "contact=" + login_name + "&password=" + pwd;
                    Request request = new Request.Builder().url(url).build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //失败
                            //Log.e("yzx", "-----");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //成功
                            String str = response.body().string();
                            //Log.e("yzx", str);
                            LoginBean loginBean = GsonUtils.java2Bean(str, LoginBean.class);

                            if (loginBean.getCode().equals("1001")) {
                                //保存相应数据
                               /* String userName = loginBean.getUserName();
                                Intent intent = new Intent(getApplicationContext(), UserCenter.class);
                                intent.putExtra("userName", userName);
                                startActivity(intent);
                                finish();
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), "登入成功", Toast.LENGTH_SHORT).show();
                                Looper.loop();*/
                                //loadingDialog.dismiss();

                            } else {

                                handler.sendEmptyMessageDelayed(0, 2500);

                            }


                        }
                    });
                }
                break;
            case R.id.forget_password_enter:
                Intent intent1 = new Intent(getApplicationContext(), ForgetActivity.class);
                startActivity(intent1);
                break;

        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobile(String mobiles) {
		/*
		移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		联通：130、131、132、152、155、156、185、186
		电信：133、153、180、189、（1349卫通）
		170
		总结起来就是第一位必定为1，第二位必定为3或5或7或8，其他位置的可以为0-9
		*/
        String telRegex = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * 是否是邮箱
     * @param strEmail
     * @return
     */
    private static boolean isEmail(String strEmail) {

        String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

        Pattern p = Pattern.compile(strPattern);

        Matcher m = p.matcher(strEmail);

        return m.matches();
    }
}
