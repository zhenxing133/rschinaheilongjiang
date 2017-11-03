package com.rschina.heilongjiang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.rschina.heilongjiang.R;
import com.rschina.heilongjiang.model.MonitorInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/9/12.
 */
public class MapCompareListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<List<MonitorInfo>> infoList;
    private List<String> groupName;
    LayoutInflater inflater;
    private  int selectedIndex=0,selectedGroup=0,selectedChild=0;

    public  MapCompareListAdapter (Context context, List<List<MonitorInfo>> infoList, List<String> groupName){
        mContext=context;
        this.infoList=infoList;
        this.groupName=groupName;
        inflater = LayoutInflater.from(context);
    }

    // 获得组的数量
    @Override
    public int getGroupCount() {
        return groupName.size();
    }

    //  获得某组的子类数量
    @Override
    public int getChildrenCount(int groupPosition) {
        return infoList.get(groupPosition).size();//////////////////////////
        //return 0;
    }


    //获取哪个组
    @Override
    public Object getGroup(int groupPosition) {
        return groupName.get(groupPosition);
    }

    //获取某组类中的某个子数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return infoList.get(groupPosition).get(childPosition);
        //return null;
    }

    //获得某个父项的id
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //  获得某个父项的某个子项的id
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
        //return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.item_map_class,null);
            groupHolder = new GroupHolder();
            groupHolder.groupText =(TextView) convertView.findViewById(R.id.tv_describe);
            groupHolder.groupImage =(ImageView) convertView.findViewById(R.id.iv_hide);
            convertView.setTag(groupHolder);
        }else{
            groupHolder = (GroupHolder)convertView.getTag();
        }
        groupHolder.groupText.setText(groupName.get(groupPosition));
        if(isExpanded){
            groupHolder.groupImage.setVisibility(View.VISIBLE);
        }else{
            groupHolder.groupImage.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    @Override
    public View getChildView(int  groupPosition, int  childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ItemHolder itemHolder = null;
        MonitorInfo mapInfo=infoList.get(groupPosition).get(childPosition);

        if(convertView==null){
            convertView = (View) inflater.inflate(R.layout.item_map_choose,null);
            itemHolder = new ItemHolder();
            itemHolder.itemText = (TextView) convertView.findViewById(R.id.tv_describe);
            itemHolder.itemImage = (ImageView) convertView.findViewById(R.id.iv_has_choose);
            convertView.setTag(itemHolder);
        }else{
            itemHolder = (ItemHolder)convertView.getTag();
        }

        /*if(mapInfo.describe.contains("201")){
            itemHolder.itemText.setText(mapInfo.describe);
        }else {
            itemHolder.itemText.setText(mapInfo.describe+" "+mapInfo.timeString);
        }*/

        if(groupPosition==selectedGroup&&childPosition==selectedChild){
            itemHolder.itemImage.setVisibility(View.VISIBLE);
            itemHolder.itemText.setTextColor(0xff000000);
        }else {
            itemHolder.itemImage.setVisibility(View.INVISIBLE);
            itemHolder.itemText.setTextColor(0xff666666);
        }

        itemHolder.itemText.setText(mapInfo.name);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder{
        private TextView groupText;
        private ImageView groupImage;
    }

    private class ItemHolder{
        private TextView itemText;
        private ImageView itemImage;
    }

    public void setSelectedGroup(int groupPosition){
        this.selectedGroup=groupPosition;
    }

    public void setSelectedChild(int childPosition){
        this.selectedChild=childPosition;
    }


}
