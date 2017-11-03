//package com.rschina.heilongjiang.activity;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.rschina.heilongjiang.R;
//import com.rschina.heilongjiang.adapter.CollectionAdapter;
//import com.rschina.heilongjiang.db.MarkInfo;
//import com.rschina.heilongjiang.fragment.MapFragment;
//import com.rschina.heilongjiang.model.MapInfo;
//
//import org.litepal.crud.DataSupport;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Administrator on 2017/10/11.
// */
//
//public class CollectionActivity extends AppCompatActivity {
//    private List<String> nameList = new ArrayList<>();
//    private List<String> desList = new ArrayList<>();
//    private List<Double> latiList = new ArrayList<>();
//    private List<Double> longiList = new ArrayList<>();
//    private CollectionAdapter adapter;
//    private int index;//长按删除索引
//    private ImageView collection_back;
//    private List<MarkInfo> all;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_collection);
//        nameList.clear();
//        desList.clear();
//        latiList.clear();
//        longiList.clear();
//        final ListView mark_list = (ListView) findViewById(R.id.mark_list);
//        collection_back = (ImageView) findViewById(R.id.collection_back);
//        collection_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//        //查询数据库内容
//        all = DataSupport.findAll(MarkInfo.class);
//        Log.e("yzx", all.size() + "=================");
//        for (MarkInfo markInfo : all) {
//            nameList.add(markInfo.getName());
//            desList.add(markInfo.getDes());
//        }
//        adapter = new CollectionAdapter(getApplicationContext(), nameList, desList);
//        mark_list.setAdapter(adapter);
//
//        //长按事件
//        mark_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
//                nameList.clear();
//                desList.clear();
//                adapter.notifyDataSetChanged();
//                //int delete = DataSupport.delete(MarkInfo.class,l);
//                Log.e("yzx", l + "=====----");
//                MarkInfo markInfo1 = all.get(position);
//                //Log.e("yzx", delete + "delete");
//                markInfo1.delete();
//                List<MarkInfo> all = DataSupport.findAll(MarkInfo.class);
//
//                for (MarkInfo markInfo : all) {
//                    nameList.add(markInfo.getName());
//                    desList.add(markInfo.getDes());
//                }
//                adapter.notifyDataSetChanged();
//                //Log.e("yzx", position+1 + "--------");
//                return true;
//            }
//        });
//        //条目单击事件
//        mark_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                //MapInfo mapInfo = new MapInfo();
//
//                //Toast.makeText(getApplicationContext(),"我被点中"+position,Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    private void deleteInfo(int index) {
//
//    }
//
//
//}
