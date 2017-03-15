package cn.ryanman.app.offlineipo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoStatus;
import cn.ryanman.app.offlineipo.utils.AppUtils;
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
        public ImageView arrow;
        public LinearLayout layout;
    }

    public final class IpoViewHolder {
        public TextView ipoName;
        public TextView ipoCode;
        public LinearLayout layout;
        public LinearLayout priceLayout;
        public TextView issuePrice;
        public LinearLayout ipoNextLayout;
        public TextView ipoNextDate;
        public TextView ipoNext;
        public Button joinButton;
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
            holder.arrow = (ImageView) convertView.findViewById(R.id.adapter_arrow);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.ipo_group_layout);
            convertView.setTag(holder);
        } else {
            holder = (EventViewHolder) convertView.getTag();
        }

        int resId = context.getResources().getIdentifier(getGroup(groupPosition), "string", Value.PACKAGENAME);
        holder.eventName.setText(context.getString(resId));

        if (isExpanded) {
            holder.arrow.setBackgroundResource(R.drawable.ic_down_arrow_15dp);
        } else {
            holder.arrow.setBackgroundResource(R.drawable.ic_right_arrow_15dp);
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
            holder.layout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_child_layout);
            holder.priceLayout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_price_layout);
            holder.ipoNextLayout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_next_layout);
            holder.ipoNext = (TextView) convertView.findViewById(R.id.adapter_ipo_next);
            holder.ipoNextDate = (TextView) convertView.findViewById(R.id.adapter_ipo_next_date);
            holder.issuePrice = (TextView) convertView.findViewById(R.id.adapter_ipo_price);
            holder.joinButton = (Button) convertView.findViewById(R.id.adapter_join_button);
            convertView.setTag(holder);
        } else {
            holder = (IpoViewHolder) convertView.getTag();
        }
        holder.ipoName.setText(getChild(groupPosition, childPosition).getName());
        holder.ipoCode.setText(getChild(groupPosition, childPosition).getCode());
        if (getChild(groupPosition, childPosition).getIssuePrice() != 0) {
            holder.priceLayout.setVisibility(View.VISIBLE);
            DecimalFormat df = new DecimalFormat("#.00");
            holder.issuePrice.setText(df.format(getChild(groupPosition, childPosition).getIssuePrice()));
        } else {
            holder.priceLayout.setVisibility(View.GONE);
        }

        try {
            IpoStatus status = AppUtils.getIpoStatus(getChild(groupPosition, childPosition));
            if (status.getNext() == null) {
                holder.ipoNextLayout.setVisibility(View.GONE);
            } else {
                holder.ipoNextLayout.setVisibility(View.VISIBLE);
                int resId = context.getResources().getIdentifier(status.getNext().toString(), "string", Value.PACKAGENAME);
                holder.ipoNext.setText(context.getString(resId));
                if (status.getNextDate() != null) {
                    holder.ipoNextDate.setText(status.getNextDate());
                } else {
                    holder.ipoNextDate.setText(context.getResources().getString(R.string.next_step));
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (getChild(groupPosition, childPosition).getOfflineDate() == null) {
            holder.joinButton.setVisibility(View.INVISIBLE);
        }
        else{
            holder.joinButton.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
