package cn.ryanman.app.offlineipo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryan on 2016/11/26.
 */

public class IpoTodayListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<String> groupList;
    private List<List<IpoItem>> childList;

    public final class EventViewHolder {
        public TextView eventName;
    }

    public final class IpoViewHolder {
        public TextView ipoName;
        public TextView ipoCode;
    }

    public IpoTodayListAdapter(Context context, List<String> groupList, List<List<IpoItem>> childList) {
        this.context = context;
        this.groupList = groupList;
        this.childList = childList;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public IpoItem getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        long id = 0;
        for (int i = 0; i < groupPosition; i++) {
            id += childList.get(i).size();
        }
        id += childPosition;
        return id;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        EventViewHolder holder = null;
        if (convertView == null) {
            holder = new EventViewHolder();
            convertView = mInflater.inflate(
                    R.layout.adapter_ipo_today_group, null);
            holder.eventName = (TextView) convertView.findViewById(R.id.adapter_event_name);
            convertView.setTag(holder);
        } else {
            holder = (EventViewHolder) convertView.getTag();
        }

        int resId = context.getResources().getIdentifier(getGroup(groupPosition), "string", Value.PACKAGENAME);
        holder.eventName.setText(context.getString(resId));

        if (getGroup(groupPosition).equals(Value.NOTICE)) {
            holder.eventName.setTextColor(context.getResources().getColor(R.color.green));
        } else if (getGroup(groupPosition).equals(Value.INQUIRY)) {
            holder.eventName.setTextColor(context.getResources().getColor(R.color.yellow));
        } else if (getGroup(groupPosition).equals(Value.OFFLINE)) {
            holder.eventName.setTextColor(context.getResources().getColor(R.color.blue));
        } else if (getGroup(groupPosition).equals(Value.PAYMENT)) {
            holder.eventName.setTextColor(context.getResources().getColor(R.color.red));
        }
        else{
            holder.eventName.setTextColor(context.getResources().getColor(R.color.dark_grey));
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        IpoViewHolder holder = null;
        if (convertView == null) {
            holder = new IpoViewHolder();
            convertView = mInflater.inflate(
                    R.layout.adapter_ipo_today_child, null);
            holder.ipoName = (TextView) convertView.findViewById(R.id.adapter_ipo_name);
            holder.ipoCode = (TextView) convertView.findViewById(R.id.adapter_ipo_code);
            convertView.setTag(holder);
        } else {
            holder = (IpoViewHolder) convertView.getTag();
        }
        holder.ipoName.setText(getChild(groupPosition, childPosition).getName());
        holder.ipoCode.setText(getChild(groupPosition, childPosition).getCode());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
