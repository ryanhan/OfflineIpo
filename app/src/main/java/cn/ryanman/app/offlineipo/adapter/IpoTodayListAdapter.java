package cn.ryanman.app.offlineipo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.listener.OnDataLoadCompletedListener;
import cn.ryanman.app.offlineipo.listener.OnViewReloadListener;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoStatus;
import cn.ryanman.app.offlineipo.model.Status;
import cn.ryanman.app.offlineipo.model.User;
import cn.ryanman.app.offlineipo.utils.AppUtils;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.GetUserAsyncTask;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryan on 2016/11/26.
 */

public class IpoTodayListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<String> groupList;
    private List<List<IpoItem>> childList;
    private int selectCount;
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
        public LinearLayout ipoNextLayout;
        public TextView ipoNextDate;
        public TextView ipoNext;
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
            holder.ipoName = (TextView) convertView.findViewById(R.id.adapter_ipo_name);
            holder.ipoCode = (TextView) convertView.findViewById(R.id.adapter_ipo_code);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_child_layout);
            holder.priceLayout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_price_layout);
            holder.ipoNextLayout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_next_layout);
            holder.ipoNext = (TextView) convertView.findViewById(R.id.adapter_ipo_next);
            holder.ipoNextDate = (TextView) convertView.findViewById(R.id.adapter_ipo_next_date);
            holder.issuePrice = (TextView) convertView.findViewById(R.id.adapter_ipo_price);
            holder.actionLayout = (LinearLayout) convertView.findViewById(R.id.adpater_action_layout);
            holder.actionImage = (ImageView) convertView.findViewById(R.id.adpater_action_image);
            holder.actionText = (TextView) convertView.findViewById(R.id.adapter_action_text);
            convertView.setTag(holder);
        } else {
            holder = (IpoViewHolder) convertView.getTag();
        }
        final IpoItem ipoItem = getChild(groupPosition, childPosition);

        holder.ipoName.setText(ipoItem.getName());
        holder.ipoCode.setText(ipoItem.getCode());
        if (ipoItem.getIssuePrice() != 0) {
            holder.priceLayout.setVisibility(View.VISIBLE);
            DecimalFormat df = new DecimalFormat("#.00");
            holder.issuePrice.setText(df.format(ipoItem.getIssuePrice()));
        } else {
            holder.priceLayout.setVisibility(View.GONE);
        }

        if (ipoItem.getOfflineDate() == null) {
            holder.ipoNextLayout.setVisibility(View.VISIBLE);
            holder.ipoNext.setText(R.string.no_offline);
            holder.ipoNextDate.setVisibility(View.GONE);
            holder.actionLayout.setVisibility(View.INVISIBLE);
        } else {
            IpoStatus ipoStatus = null;
            try {
                ipoStatus = AppUtils.getIpoStatus(ipoItem);
                if (ipoStatus.getNext() == null) {
                    holder.ipoNextLayout.setVisibility(View.GONE);
                } else {
                    holder.ipoNextLayout.setVisibility(View.VISIBLE);
                    int resId = context.getResources().getIdentifier(ipoStatus.getNext().toString(), "string", Value.PACKAGENAME);
                    holder.ipoNext.setText(context.getString(resId));
                    if (ipoStatus.getNextDate() != null) {
                        holder.ipoNextDate.setText(ipoStatus.getNextDate());
                    } else {
                        holder.ipoNextDate.setText(context.getResources().getString(R.string.next_step));
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (ipoStatus.getCurrent() != null && ipoStatus.getCurrent().compareTo(Status.PAYMENT) > 0) {
                holder.actionLayout.setVisibility(View.INVISIBLE);
            } else {
                holder.actionLayout.setVisibility(View.VISIBLE);
                if (ipoItem.getProgress() == Status.NONE) {

                    holder.actionImage.setImageResource(R.drawable.ic_add);
                    holder.actionImage.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                    holder.actionText.setText(context.getString(R.string.has_submit));
                    holder.actionText.setTextColor(context.getResources().getColor(R.color.colorPrimary));

                    holder.actionLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GetUserAsyncTask getUserAsyncTask = new GetUserAsyncTask(context);
                            getUserAsyncTask.setOnDataLoadCompletedListener(new OnDataLoadCompletedListener() {
                                @Override
                                public void onDataSuccessfully(Object object) {
                                    final List<User> userList = (List<User>) object;
                                    if (userList.size() > 0) {
                                        String[] nameArray = new String[userList.size()];
                                        for (int i = 0; i < userList.size(); i++) {
                                            nameArray[i] = userList.get(i).getName();
                                        }

                                        final boolean[] selected = new boolean[userList.size()];
                                        for (int i = 0; i < userList.size(); i++) {
                                            selected[i] = true;
                                        }

                                        new AlertDialog.Builder(context).setMultiChoiceItems(nameArray, selected, new DialogInterface.OnMultiChoiceClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                                selected[which] = isChecked;
                                            }
                                        }).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                for (int i = 0; i < selected.length; i++) {
                                                    if (selected[i] == true) {
                                                        selectCount++;
                                                        DatabaseUtils.subscribe(context, userList.get(i).getName(), ipoItem.getCode());
                                                    }
                                                }
                                                if (selectCount == 0) {
                                                    Toast.makeText(context, R.string.no_selected_user, Toast.LENGTH_LONG).show();
                                                } else {
                                                    if (onViewReloadListener != null)
                                                        onViewReloadListener.reload(null);
                                                }
                                            }
                                        }).setNegativeButton(R.string.no, null).show();
                                    } else if (userList.size() == 0) {
                                        Toast.makeText(context, R.string.no_user, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onDataFailed() {

                                }
                            });
                            getUserAsyncTask.execute();
                        }
                    });
                } else {
                    if (ipoStatus != null) {
                        Status current = null;
                        if (ipoStatus.getCurrent() != null) {
                            current = ipoStatus.getCurrent();
                        } else if (ipoStatus.getNext() != null) {
                            current = ipoStatus.getNext().before();
                        }
                        if (current != null) {
                            final Status _current = current;
                            if (_current.equals(Status.NOTICE)) {
                                holder.actionText.setText(R.string.has_submit);
                            } else if (_current.equals(Status.INQUIRY)) {
                                holder.actionText.setText(R.string.has_inquiry);
                            } else if (_current.equals(Status.OFFLINE)) {
                                holder.actionText.setText(R.string.has_apply);
                            } else if (_current.equals(Status.PAYMENT)) {
                                holder.actionText.setText(R.string.has_pay);
                            }
                            if (ipoItem.getProgress().compareTo(_current) == 0) { //相同进度，显示打钩完成状态
                                holder.actionImage.setImageResource(R.drawable.ic_correct);
                                holder.actionImage.setColorFilter(ContextCompat.getColor(context, R.color.green));
                                holder.actionText.setTextColor(context.getResources().getColor(R.color.green));
                            } else if (ipoItem.getProgress().compareTo(_current) < 0) { //落后进度，显示TODO状态
                                holder.actionImage.setImageResource(R.drawable.ic_flag);
                                holder.actionImage.setColorFilter(ContextCompat.getColor(context, R.color.red));
                                holder.actionText.setTextColor(context.getResources().getColor(R.color.red));
                                holder.actionLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DatabaseUtils.updateProgress(context, ipoItem.getCode(), _current);
                                        if (onViewReloadListener != null)
                                            onViewReloadListener.reload(null);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
