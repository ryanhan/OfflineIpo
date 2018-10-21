package cn.ryanman.app.offlineipo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.listener.OnViewReloadListener;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryan on 2016/11/26.
 */

public class IpoTodayListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<String> groupList;
    private List<List<IpoItem>> childList;
    private OnViewReloadListener onViewReloadListener;

    public void setOnViewReloadListener(OnViewReloadListener onViewReloadListener) {
        this.onViewReloadListener = onViewReloadListener;
    }

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
        public LinearLayout ipoCurrentLayout;
        public LinearLayout actionLayout;
        public ImageView actionImage;
        public TextView actionText;
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
            holder.ipoName = convertView.findViewById(R.id.adapter_ipo_name);
            holder.ipoCode = convertView.findViewById(R.id.adapter_ipo_code);
            holder.layout = convertView.findViewById(R.id.adapter_ipo_child_layout);
            holder.priceLayout = convertView.findViewById(R.id.adapter_ipo_price_layout);
            holder.ipoCurrentLayout = convertView.findViewById(R.id.adapter_ipo_current_layout);
            holder.issuePrice = convertView.findViewById(R.id.adapter_ipo_price);
            holder.actionLayout = convertView.findViewById(R.id.adapter_action_layout);
            holder.actionImage = convertView.findViewById(R.id.adapter_action_image);
            holder.actionText = convertView.findViewById(R.id.adapter_action_text);
            convertView.setTag(holder);
        } else {
            holder = (IpoViewHolder) convertView.getTag();
        }
        final IpoItem ipoItem = getChild(groupPosition, childPosition);

        holder.ipoName.setText(ipoItem.getName());
        holder.ipoCode.setText(ipoItem.getCode());

        if (ipoItem.getOfflineDate() == null) {
            holder.ipoCurrentLayout.setVisibility(View.VISIBLE);
            holder.priceLayout.setVisibility(View.GONE);
            holder.actionLayout.setVisibility(View.INVISIBLE);
        } else {

            if (ipoItem.getIssuePrice() != 0) {
                DecimalFormat df = new DecimalFormat("#.00");
                holder.issuePrice.setText(context.getString(R.string.issue_price) + context.getString(R.string.space)+ "￥" + df.format(ipoItem.getIssuePrice()));
            } else {
                holder.issuePrice.setText(context.getString(R.string.issue_price) + R.string.none);
            }

            holder.ipoCurrentLayout.setVisibility(View.GONE);
            holder.actionLayout.setVisibility(View.VISIBLE);
            if (!ipoItem.isApplied()) {
                holder.actionImage.setImageResource(R.drawable.ic_add);
                holder.actionImage.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
                holder.actionText.setText(R.string.has_submit);
                holder.actionText.setTextColor(context.getResources().getColor(R.color.colorAccent));

                holder.actionLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View view = layoutInflater.inflate(R.layout.dialog_number_picker, null);
                        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.dialog_number);
                        numberPicker.setMinValue(1);
                        numberPicker.setMaxValue(10);
                        numberPicker.setValue(2);
                        numberPicker.setWrapSelectorWheel(false);

                        new AlertDialog.Builder(context).setTitle(R.string.select_number).setView(view).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseUtils.subscribe(context, ipoItem.getCode(), numberPicker.getValue());
                                if (onViewReloadListener != null)
                                    onViewReloadListener.reload(null);
                            }
                        }).setNegativeButton(R.string.no, null).show();

                    }
                });
            } else {
                holder.actionImage.setImageResource(R.drawable.ic_correct);
                holder.actionImage.setColorFilter(ContextCompat.getColor(context, R.color.green));
                holder.actionText.setTextColor(context.getResources().getColor(R.color.green));
                holder.actionLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(context).setTitle(R.string.cancel_applied).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseUtils.unsubscribe(context, ipoItem.getCode());
                                if (onViewReloadListener != null)
                                    onViewReloadListener.reload(null);
                            }
                        }).setNegativeButton(R.string.no, null).show();

                    }
                });

            }
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
