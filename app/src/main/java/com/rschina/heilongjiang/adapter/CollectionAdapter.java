package com.rschina.heilongjiang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rschina.heilongjiang.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/17.
 */

public class CollectionAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> nameList = new ArrayList<>();
    private List<String> desList = new ArrayList<>();
    public CollectionAdapter(Context mContext, List<String> nameList, List<String> desList) {
        this.mContext = mContext;
        this.nameList = nameList;
        this.desList = desList;


    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int i) {
        return nameList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_marklist, null);
            viewHolder = new ViewHolder();
            viewHolder.mark_name = (TextView) convertView.findViewById(R.id.mark_name);
            viewHolder.mark_info = (TextView) convertView.findViewById(R.id.mark_info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mark_name.setText(nameList.get(i));
        viewHolder.mark_info.setText(desList.get(i));
        return convertView;
    }

     static class ViewHolder{
         TextView mark_name,mark_info;
     }
}
