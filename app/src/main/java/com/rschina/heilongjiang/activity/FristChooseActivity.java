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

import com.rschina.heilongjiang.model.Event;
import com.rschina.heilongjiang.model.MapInfo;
import com.rschina.heilongjiang.adapter.MapListAdapter;
import com.rschina.heilongjiang.db.RsService;
import com.rschina.heilongjiang.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class FristChooseActivity extends Activity implements View.OnClickListener, Event.ActionListener {
    private static final String TAG = "FristChooseActivity";
    @ViewInject(R.id.tv_btn_left)
    TextView tv_btn_left;
    @ViewInject(R.id.tv_btn_right)
    TextView tv_btn_right;
    @ViewInject(R.id.iv_back)
    ImageView iv_back;
    @ViewInject(R.id.map_list)
    ListView map_list;
    @ViewInject(R.id.choose_confirm)
    TextView choose_confirm;

    List<MapInfo> originData;
    List<MapInfo> data;
    MapListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_choose);
        x.view().inject(this);
        initData();
        int index = getIntent().getIntExtra("selectedIndex", 0);
        if (index > 0) {

            adapter.setSelectedIndex(data.indexOf(originData.get(index)));
            setCompare();
        }

        map_list.setAdapter(adapter);
        setView();
        setListeners();
    }

    private void initData() {
        originData = RsService.getInstance().getImageMaps();
        data = new ArrayList<>();
        for (MapInfo mapInfo : originData) {
            data.add(mapInfo);
        }
        adapter = new MapListAdapter(this, data);
        adapter.setSelectedIndex(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onAction(Event.Action action) {
        if (action == Event.Finish) {
            initData();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: " + FristChooseActivity.class.getName());
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    private void setView() {
        iv_back.setVisibility(View.VISIBLE);
        tv_btn_right.setVisibility(View.GONE);
        tv_btn_left.setText("请选择影像时间");
    }

    private void setCompare() {
        choose_confirm.setText("确认选择");

    }

    private void setListeners() {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.choose_confirm:
                Intent intent = new Intent();
                int index = originData.indexOf(data.get(adapter.getSelectedIndex()));
                intent.putExtra("selectedIndex", index);
                setResult(RESULT_OK, intent);
                adapter.notifyDataSetChanged();
                finish();
                break;
            default:
                break;
        }
    }
}
