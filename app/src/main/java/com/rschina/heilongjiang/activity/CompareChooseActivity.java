package com.rschina.heilongjiang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rschina.heilongjiang.adapter.MapCompareListAdapter;
import com.rschina.heilongjiang.R;
import com.rschina.heilongjiang.db.RsService;
import com.rschina.heilongjiang.model.MonitorInfo;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class CompareChooseActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CompareChooseActivity";
    @ViewInject(R.id.tv_btn_left)
    TextView tv_btn_left;
    @ViewInject(R.id.tv_btn_right)
    TextView tv_btn_right;
    @ViewInject(R.id.tv_baseMap_selected)
    TextView tv_baseMap_selected;
    @ViewInject(R.id.iv_back)
    ImageView iv_back;
    @ViewInject(R.id.map_list)
    ExpandableListView map_list;
    @ViewInject(R.id.choose_confirm)
    TextView choose_confirm;
    Intent intent;
    List<MonitorInfo> monitorInfos;
    List<MonitorInfo>[] subList = new List[4];
    MapCompareListAdapter adapter;
    List<List<MonitorInfo>> listData;
    List<String> ids;
    MonitorInfo selectMap;
    int baseIndex;
    /**
     * 应用样式
     */
    String[] groupNames = {"国土监测","环保监测","规划监测", "水利监测"};//组名
    String[] groupType = {"ddfee11d-ea71-4974-aac3-451dd3378ac3","ff8170be-210f-4793-b158-28ee958881c0",
            "7681aaea-ae29-456f-9727-c86cd2f95af9", "6981ad25-edf5-4e1a-b677-0dcc28793982"};//组（子类型）
    List<String> groupName = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_compare_choose);
        x.view().inject(this);
        listData = new ArrayList<>();
        intent = getIntent();
        baseIndex = intent.getIntExtra("baseIndex", 0);
        for (int i = 0; i < groupType.length; i++) {
            subList[i] = new ArrayList<>();
        }

       ids = RsService.getInstance().getMonitorsId();

        monitorInfos = RsService.getInstance().getMonitorInfos();
        if (ids == null || monitorInfos == null) {
            //RsService.getInstance().fetchInfoType();
        }


        for (int i = 0; i < monitorInfos.size(); i++) {
            MonitorInfo moni = monitorInfos.get(i);
            if (ids.get(i).equals("ddfee11d-ea71-4974-aac3-451dd3378ac3")) {
                subList[0].add(moni);
            } else if (ids.get(i).equals("ff8170be-210f-4793-b158-28ee958881c0")) {
                subList[1].add(moni);
            } else if (ids.get(i).equals("7681aaea-ae29-456f-9727-c86cd2f95af9")) {
                subList[2].add(moni);
            } else if (ids.get(i).equals("6981ad25-edf5-4e1a-b677-0dcc28793982")) {
                subList[3].add(moni);
            }

        }

        List<String> groupName = new ArrayList<>();
        for (int i = 0; i < groupType.length; i++) {
            if (subList[i].size() > 0) {
                groupName.add(groupNames[i]);
                listData.add(subList[i]);
            }
        }
        adapter = new MapCompareListAdapter(this, listData, groupName);
        int index = getIntent().getIntExtra("selectedIndex", 0), groupIndex = 0;
        if (index >= 0) {
            selectMap = monitorInfos.get(index);
            for (int groupPosition = 0; groupPosition < groupName.size(); groupPosition++) {
                for (int childPosition = 0; childPosition < listData.get(groupPosition).size(); childPosition++) {
                    if (selectMap.name.equals(listData.get(groupPosition).get(childPosition).name)) {
                        adapter.setSelectedGroup(groupPosition);
                        adapter.setSelectedChild(childPosition);
                        groupIndex = groupPosition;
                    }
                }
            }
        } else {
            adapter.setSelectedGroup(-1);
            adapter.setSelectedChild(-1);
        }
        map_list.setAdapter(adapter);
        map_list.expandGroup(groupIndex);//////////////////////
        map_list.setGroupIndicator(null);
        setView();
        setListeners();
    }


    private void setView() {
        iv_back.setVisibility(View.VISIBLE);
        tv_btn_right.setVisibility(View.GONE);
        tv_btn_left.setText("请选择应用");
    }

    private void setListeners() {
        iv_back.setOnClickListener(this);

        map_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                selectMap = listData.get(groupPosition).get(childPosition);
                adapter.setSelectedGroup(groupPosition);
                adapter.setSelectedChild(childPosition);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        choose_confirm.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: " + FristChooseActivity.class.getName());
        super.onDestroy();
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
                int select = monitorInfos.indexOf(selectMap);
                if (select < 0) {
                    Toast.makeText(CompareChooseActivity.this, "请先选择", Toast.LENGTH_SHORT).show();
                    break;
                }

                intent.putExtra("selectedIndex", select);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
}
