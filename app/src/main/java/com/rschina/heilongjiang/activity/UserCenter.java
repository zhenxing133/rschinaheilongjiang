/*
package com.rschina.heilongjiang.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.rschina.heilongjiang.MainActivity;
import com.rschina.heilongjiang.R;
import com.rschina.heilongjiang.adapter.LoadingDialog;
import com.rschina.heilongjiang.fragment.MapFragment;
import com.rschina.heilongjiang.model.Const;
import com.rschina.heilongjiang.model.FileCache;
import com.rschina.heilongjiang.views.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

*/
/**
 * Created by Administrator on 2017/9/25.
 *//*


public class UserCenter extends AppCompatActivity implements View.OnClickListener {

    private String user;
    private Context mContext;
    private TextView tv_user;
    private TextView tv_clean;
    private String file;
    private FileCache filecache;
    //private ImageView iv_back;
    private Dialog dialog;
    private TextView fead_back;
    private TextView setting;
    private TextView tv_logout;
    private TextView set_pwd;
    private Dialog auto_dislog;
    private CircleImageView iv_user_head;
    public int screenWidth,screenHight;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "缓存清理成功", Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter);
        mContext=this;
        user = getIntent().getStringExtra("contact");
        initView();
        tv_user.setText(user);
        initListener();


    }

    private void initView() {
        filecache = new FileCache(this);
        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_clean = (TextView) findViewById(R.id.tv_clean);
        fead_back = (TextView) findViewById(R.id.tv_feadback);
        setting = (TextView) findViewById(R.id.setting);
        tv_logout = (TextView) findViewById(R.id.tv_logout);
        //iv_back = (ImageView) findViewById(R.id.iv_back);
        set_pwd = (TextView) findViewById(R.id.set_pwd);
        iv_user_head = (CircleImageView) findViewById(R.id.personal_head_icon);
        iv_user_head.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //自定义dailog
                auto_dislog = new Dialog(UserCenter.this, R.style.avatar_dialog);
                auto_dislog.setCancelable(true);
                auto_dislog.setCanceledOnTouchOutside(true);
                auto_dislog.setContentView(R.layout.dialog_photo_select);
                TextView take_camner = (TextView) auto_dislog.findViewById(R.id.take_photo);
                TextView take_from_phonecamner = (TextView) auto_dislog.findViewById(R.id.take_from_phone);
                TextView cancle_select_photo = (TextView) auto_dislog.findViewById(R.id.cancle_select_photo);
                //拍照
                take_camner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //auto_dislog.dismiss();
                        file = filecache.getImageCacheDir().getAbsolutePath() + File.separator + "avatar.jpg";
                        int TAKE_PHOTO = 3022;
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(file)));
                        startActivityForResult(intent, TAKE_PHOTO);
                    }
                });
                //从手机相册
                take_from_phonecamner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //auto_dislog.dismiss();
                        int PICK_PHOTO = 3021;
                        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//					intent.setType("image*/
/*");
//					intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, PICK_PHOTO);
                    }
                });
                //取消
                cancle_select_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        auto_dislog.dismiss();
                    }
                });
                WindowManager.LayoutParams params = auto_dislog.getWindow().getAttributes();
                WindowManager wm = (WindowManager)UserCenter.this.getSystemService(Context.WINDOW_SERVICE);
                screenWidth = wm.getDefaultDisplay().getWidth();// 屏幕宽度
                screenHight = wm.getDefaultDisplay().getHeight();
                params.width = (int) (screenWidth * 0.99); // 宽度设置为屏幕的0.8
                params.height = (int) (screenHight * 0.28); // 宽度设置为屏幕的0.8
                Window window = auto_dislog.getWindow();
                window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
                auto_dislog.show();

            }
        });
    }
    private void initListener() {
        tv_clean.setOnClickListener(this);
        fead_back.setOnClickListener(this);
        setting.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        //iv_back.setOnClickListener(this);
        set_pwd.setOnClickListener(this);

    }


    */
/**
     * 监听返回键
     *//*

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //点击完返回键，执行的动作
            Intent intent = new Intent(UserCenter.this, MainActivity.class);
            intent.putExtra("id", 1);
            startActivity(intent);
            finish();
            //Log.e("yzx", "返回执行了");
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_pwd:
                Intent intent1 = new Intent(UserCenter.this, ForgetActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_clean:
                dialog = LoadingDialog.createLoadingDialog(UserCenter.this, "缓存清理中...");
                dialog.show();
                handler.sendEmptyMessageDelayed(0, 4000);
                break;
            case R.id.tv_feadback:
                final Intent intent = new Intent(UserCenter.this, FeadBackActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                Intent intent2 = new Intent(UserCenter.this, SettingActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_logout:
                final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("是否注销登入?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OkHttpClient okhttp = new OkHttpClient();
                        Request request = new Request.Builder().url(Const.LOG_OUT).build();
                        Call call = okhttp.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), "注销失败", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String s = response.body().string();
                                try {
                                    JSONObject obj = new JSONObject(s);
                                    if (obj.getInt("code") == 1) {

                                        Intent intent5 = new Intent(UserCenter.this, MainActivity.class);
                                        intent5.putExtra("out",1);
                                        startActivity(intent5);
                                        //finish();

                                        Looper.prepare();
                                        Toast.makeText(getApplicationContext(), "退出成功", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();

                break;
        }

    }

}
*/
