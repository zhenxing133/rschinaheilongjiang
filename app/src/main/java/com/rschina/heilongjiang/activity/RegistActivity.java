package com.rschina.heilongjiang.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rschina.heilongjiang.R;
import com.rschina.heilongjiang.adapter.LoadingDialog;
import com.rschina.heilongjiang.model.Const;
import com.rschina.heilongjiang.utils.UserSharedPrefs;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Administrator on 2017/9/24.
 */

public class RegistActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView regist_back;
    private EditText regist_username;
    private EditText regist_password;
    private TextView regist_warning;
    private Button btn_regist;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            regist_warning.setVisibility(View.INVISIBLE);
        }
    };
    private String username;
    private String password;
    private UserSharedPrefs sp;
    private TextView tv_yzm;
    private EditText edit_yzm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_regist);
        initView();
        listener();
        sp = new UserSharedPrefs(getApplicationContext());
    }

    private void initView() {
        regist_back = (ImageView) findViewById(R.id.regist_back);
        regist_username = (EditText) findViewById(R.id.regist_username);
        regist_password = (EditText) findViewById(R.id.regist_password);
        regist_warning = (TextView) findViewById(R.id.regist_warning);
        btn_regist = (Button) findViewById(R.id.btn_regist);
        tv_yzm = (TextView) findViewById(R.id.tv_yzm);
        edit_yzm = (EditText) findViewById(R.id.edit_yzm);
    }

    private void listener() {
        regist_back.setOnClickListener(this);
        btn_regist.setOnClickListener(this);
        tv_yzm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.regist_back:
                finish();
                break;
            case R.id.tv_yzm:
                tv_yzm.setText("已发送");
                //Toast.makeText(getApplicationContext(),"发送中",Toast.LENGTH_SHORT).show();
                String phone = regist_username.getText().toString().trim();
                String yzm_url = Const.FORMAL_HOST+"sendValidateCode?phoneNumber="+phone;
                OkHttpClient ok = new OkHttpClient();
                Request request0 = new Request.Builder().url(yzm_url).build();
                Call call0 = ok.newCall(request0);
                call0.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Log.e("yzx", "erro");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //发送成功
                        //Log.e("yzx", response.toString());
                    }
                });
                break;
            case R.id.btn_regist:
                String phoneNum = edit_yzm.getText().toString().trim();
                username = regist_username.getText().toString().trim();
                password = regist_password.getText().toString().trim();
                if (username.equals("") || password.equals("")) {
                    regist_warning.setVisibility(View.VISIBLE);
                    regist_warning.setText("帐号密码不能为空");
                    handler.sendEmptyMessageDelayed(0, 3500);
                    return;
                } else if (username.length() < 6 || username.length() > 18 && password.length() < 6 || password.length() > 18) {
                    regist_warning.setVisibility(View.VISIBLE);
                    regist_warning.setText("请输入正确格式的手机号或邮箱,6-16位密码");
                    handler.sendEmptyMessageDelayed(0, 3500);
                } else if (!isMobile(username)) {
                    regist_warning.setVisibility(View.VISIBLE);
                    regist_warning.setText("请输入正确的手机号或邮箱");
                    handler.sendEmptyMessageDelayed(0, 3500);
                } else if (phoneNum.equals("")) {
                    regist_warning.setVisibility(View.VISIBLE);
                    regist_warning.setText("请输入正确验证码");
                    handler.sendEmptyMessageDelayed(0, 3500);
                }



                final Dialog dialog = LoadingDialog.createLoadingDialog(RegistActivity.this, "正在注册中....");
                dialog.show();
                OkHttpClient client = new OkHttpClient();
                String url = Const.REGIST_URL + "contact=" + username + "&password=" + password+"&validateCode="+phoneNum;
                Request request = new Request.Builder().url(url).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //失败
                        finish();

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //成功
                        String str = response.body().string();
                        //JSONObject obj = new JSONObject()
                        //Log.e("yzx", str);
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                        Looper.loop();


                    }
                });


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
     *
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
