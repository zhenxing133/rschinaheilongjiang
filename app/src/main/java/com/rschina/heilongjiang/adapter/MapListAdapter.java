package com.rschina.heilongjiang.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.rschina.heilongjiang.model.MapInfo;
import com.rschina.heilongjiang.R;
import java.util.List;

/**
 * Created by Administrator on 2016/9/12.
 */
public class MapListAdapter extends  CommonAdapter<MapInfo> {
    private  int selectedIndex=0;
    public MapListAdapter(Context context, List<MapInfo> mData) {
        super(context, mData, R.layout.item_map_choose);
    }

    @Override
    public void convert(ViewHolder viewHolder, MapInfo mapInfo) {
        TextView textView=viewHolder.getView(R.id.tv_describe);
        textView.setText(mapInfo.name);
        /*if(mapInfo.describe.contains("201")){
            textView.setText(mapInfo.describe);
        }else {

            textView.setText(mapInfo.describe+" "+mapInfo.timeString);
        }*/
        if(viewHolder.getPosition()==selectedIndex){
            viewHolder.getView(R.id.iv_has_choose).setVisibility(View.VISIBLE);
            textView.setTextColor(0xff000000);
        }else {
            viewHolder.getView(R.id.iv_has_choose).setVisibility(View.INVISIBLE);
            textView.setTextColor(0xff666666);
        }
    }

    public void setSelectedIndex(int selectedIndex){
        this.selectedIndex=selectedIndex;
    }

    public int getSelectedIndex(){
        return  selectedIndex;
    }


}
