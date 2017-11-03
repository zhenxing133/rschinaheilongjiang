package com.rschina.heilongjiang.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rschina.heilongjiang.MainActivity;
import com.rschina.heilongjiang.R;
import com.rschina.heilongjiang.db.MarkInfo;
import com.rschina.heilongjiang.fragment.MapFragment;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

/**
 * Created by Administrator on 2017/9/21.
 */

public class MarkActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView marker_back;
    private EditText et_name;
    private EditText et_beizhu;
    private TextView marker_ok;
    private TextView marker_cance;
    private double latitude;
    private double longitude;
    private MarkInfo info;
    private int id;
    //private RelativeLayout marker_show;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        //latitude = getIntent().getDoubleExtra("latitude", 0);
        //longitude = getIntent().getDoubleExtra("longitude", 0);
        //id = getIntent().getIntExtra("id", 0);
        //Log.e("yzx", latitude+"------" + longitude);
        initView();
        initListener();
    }

    private void initView() {
        //marker_show = (RelativeLayout) findViewById(R.id.marker_show);
        marker_back = (ImageView) findViewById(R.id.marker_back);
        et_name = (EditText) findViewById(R.id.et_name);
        et_beizhu = (EditText) findViewById(R.id.et_beizhu);
        marker_ok = (TextView) findViewById(R.id.marker_ok);
        marker_cance = (TextView) findViewById(R.id.marker_cance);

    }

    private void initListener() {
        marker_back.setOnClickListener(this);
        marker_ok.setOnClickListener(this);
        marker_cance.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.marker_back:
                finish();
                break;
            case R.id.marker_cance:
                finish();
                break;
            case R.id.marker_ok:
                String name = et_name.getText().toString().trim();
                String des = et_beizhu.getText().toString().trim();
                if (TextUtils.isEmpty(name)||TextUtils.isEmpty(des)) {
                    Toast.makeText(getApplicationContext(),"标题或备注不能为空",Toast.LENGTH_SHORT).show();
                } else if (name.length() > 15 || des.length() > 20) {
                    Toast.makeText(getApplicationContext(), "标题或备注字数超过限制", Toast.LENGTH_SHORT).show();
                } else {

                    //创建数据库
                    Connector.getDatabase();
                    //info = new MarkInfo();
                    //info.setName(name);
                    //info.setDes(des);
                    //info.setLongitude(longitude);
                    //info.setLatitude(latitude);
                    //info.setNumber(id);
                    //int number = info.getNumber();
                    //Log.e("yzx", "number=" + number);
                    //info.save();

                    Intent intent1 = new Intent(MarkActivity.this, MainActivity.class);
                    //intent1.putExtra("id", id);
                    //Log.e("yzx", "id=" + id);
                    intent1.putExtra("name", name);
                    intent1.putExtra("des", des);
                    setResult(RESULT_OK,intent1);
                    //startActivity(intent1);
                    finish();

                    //Toast.makeText(getApplicationContext(),"信息保存成功",Toast.LENGTH_SHORT).show();
                }


                //Log.e("yzx", latitude + "----" + longitude);
                //Toast.makeText(getApplicationContext(),"当前纬度为:"+latitude+",  "+"当前经度为："+longitude,Toast.LENGTH_SHORT).show();
                break;
        }
    }

/*
    public  void dismiss() {
        marker_show.setVisibility(View.GONE);
    }

    public void showMarker() {
        marker_show.setVisibility(View.VISIBLE);
    }*/
}
