package com.rschina.heilongjiang.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rschina.heilongjiang.R;

import java.util.List;

/**
 * Created by Administrator on 2017/9/22.
 */

public class ImageAdapter extends BaseAdapter {
    private List<Integer> list;
    private List<String> listDes;
    private Context mContext;

    public ImageAdapter(FragmentActivity activity, List<Integer> list,List<String> listDes) {
        mContext = activity;
        this.list = list;
        this.listDes = listDes;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = View.inflate(mContext, R.layout.image_item_adapter, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_ditu);
        textView.setText(listDes.get(i));
        return view;
    }
}
