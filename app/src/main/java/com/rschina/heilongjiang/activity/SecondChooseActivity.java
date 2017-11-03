package com.rschina.heilongjiang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.rschina.heilongjiang.R;
import com.rschina.heilongjiang.model.MapInfo;
import com.rschina.heilongjiang.adapter.MapListAdapter;
import com.rschina.heilongjiang.db.RsService;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class SecondChooseActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "SecondChooseActivity";
    @ViewInject(R.id.tv_btn_left)
    TextView tv_btn_left;
    @ViewInject(R.id.tv_btn_right)
    TextView tv_btn_right;
    @ViewInject(R.id.tv_baseMap_selected)
    TextView tv_baseMap_selected;
    @ViewInject(R.id.iv_back)
    ImageView iv_back;
    @ViewInject(R.id.map_list)
    ListView map_list;
    @ViewInject(R.id.choose_confirm)
    TextView choose_confirm;

    Intent intent;
    MapListAdapter adapter;
    List<MapInfo> data;
    int baseIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_second_choose);
        x.view().inject(this);
        intent=getIntent();
        baseIndex=intent.getIntExtra("baseIndex",0);
        data=new ArrayList<>();
        List<MapInfo> allMapInfos= RsService.getInstance().getImageMaps();
        for(int i=0;i<allMapInfos.size();i++){
            /*if(i==baseIndex){
                tv_baseMap_selected.setText(getString(R.string.base_map_selected,
                        allMapInfos.get(i).describe + "("+allMapInfos.get(i).timeString+")"));
                continue;
            }*/
            data.add(allMapInfos.get(i));
        }
        adapter=new MapListAdapter(this,data);
        int index=getIntent().getIntExtra("selectedIndex",0);
        adapter.setSelectedIndex(index<baseIndex?index:index-1);
        map_list.setAdapter(adapter);
        setView();
        setListeners();
    }


    private void setView(){
        iv_back.setVisibility(View.VISIBLE);
        tv_btn_right.setVisibility(View.GONE);
        tv_btn_left.setText("请选择对比图层");
    }

    private  void setListeners(){
        iv_back.setOnClickListener(this);
        map_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedIndex(position);
                adapter.notifyDataSetChanged();
            }
        });
        choose_confirm.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: "+FristChooseActivity.class.getName());
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.choose_confirm:
                Intent intent=new Intent();
                int select=adapter.getSelectedIndex();
                if(select<0){
                    Toast.makeText(SecondChooseActivity.this, "请先选择", Toast.LENGTH_SHORT).show();
                    break;
                }
                intent.putExtra("selectedIndex",select<baseIndex?select:select+1);
                setResult(RESULT_OK,intent);
                finish();
                break;
            default:
                break;
        }
    }
}
