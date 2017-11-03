package com.rschina.heilongjiang.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.rschina.heilongjiang.MainActivity;
import com.rschina.heilongjiang.R;
import com.rschina.heilongjiang.adapter.CollectionAdapter;
import com.rschina.heilongjiang.db.MarkInfo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */

public class CollectionFragment extends Fragment {

    private List<String> nameList = new ArrayList<>();
    private List<String> desList = new ArrayList<>();
    private List<Double> latiList = new ArrayList<>();
    private List<Double> longiList = new ArrayList<>();
    private CollectionAdapter adapter;
    private ImageView collection_back;
    private List<MarkInfo> all;
    private ListView mark_list;
    private MarkInfo markInfo1;
    private int index = 0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.activity_collection, null);
        mark_list = (ListView) view.findViewById(R.id.mark_list);
        collection_back = (ImageView) view.findViewById(R.id.collection_back);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        collection_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        //查询数据库内容
        all = DataSupport.findAll(MarkInfo.class);
        //Log.e("yzx", all.size() + "=================");
        for (MarkInfo markInfo : all) {
            nameList.add(markInfo.getName());
            desList.add(markInfo.getDes());
            latiList.add(markInfo.getLatitude());
            longiList.add(markInfo.getLongitude());
        }
        if (adapter == null) {
            adapter = new CollectionAdapter(getActivity(), nameList, desList);
            mark_list.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        mark_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //int delete = DataSupport.delete(MarkInfo.class, i);
                nameList.clear();
                desList.clear();
                List<MarkInfo> all2 = DataSupport.findAll(MarkInfo.class);
                markInfo1 = all2.get(i);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("确定删除该地点?");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int delete = markInfo1.delete();
                        //Log.e("yzx", "delete" + delete);
                        if (delete == 0) {
                            Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
                        }
                        List<MarkInfo> all = DataSupport.findAll(MarkInfo.class);
                        for (MarkInfo markInfo : all) {
                            nameList.add(markInfo.getName());
                            desList.add(markInfo.getDes());
                        }
                        adapter.notifyDataSetChanged();


                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();

                return false;
            }
        });
        mark_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity)getActivity()).gotoMapFragment(i);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

}
