package cn.ryanman.app.offlineipo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
 * Created by ryanh on 2016/12/17.
 */

public class IpoListAdapter extends ArrayAdapter<IpoItem> {

    private Context context;
    private LayoutInflater mInflater;
    private int selectCount;
    private OnViewReloadListener onViewReloadListener;

    public void setOnViewReloadListener(OnViewReloadListener onViewReloadListener) {
        this.onViewReloadListener = onViewReloadListener;
    }

    public final class ViewHolder {
        public TextView ipoName;
        public TextView ipoCode;
        public LinearLayout priceLayout;
        public LinearLayout currentLayout;
        public LinearLayout nextLayout;
        public TextView issuePrice;
        public TextView currentTitle;
        public TextView current;
        public TextView next;
        public TextView nextDate;
        public LinearLayout actionLayout;
        public ImageView actionImage;
        public TextView actionText;
    }

    public IpoListAdapter(Context context, List<IpoItem> objects) {
        super(context, 0, objects);
        mInflater = LayoutInflater.from(context);
        this.context = context;
        selectCount = 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.adapter_all_ipo_list, null);
            holder.ipoName = (TextView) convertView.findViewById(R.id.adapter_ipo_name);
            holder.ipoCode = (TextView) convertView.findViewById(R.id.adapter_ipo_code);
            holder.priceLayout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_price_layout);
            holder.currentLayout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_current_layout);
            holder.nextLayout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_next_layout);
            holder.issuePrice = (TextView) convertView.findViewById(R.id.adapter_ipo_price);
            holder.currentTitle = (TextView) convertView.findViewById(R.id.adapter_ipo_current_title);
            holder.current = (TextView) convertView.findViewById(R.id.adapter_ipo_current);
            holder.next = (TextView) convertView.findViewById(R.id.adapter_ipo_next);
            holder.nextDate = (TextView) convertView.findViewById(R.id.adapter_ipo_next_date);
            holder.actionLayout = (LinearLayout) convertView.findViewById(R.id.adpater_action_layout);
            holder.actionImage = (ImageView) convertView.findViewById(R.id.adpater_action_image);
            holder.actionText = (TextView) convertView.findViewById(R.id.adapter_action_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ipoName.setText(getItem(position).getName());
        holder.ipoCode.setText(getItem(position).getCode());

        if (getItem(position).getIssuePrice() != 0) {
            holder.priceLayout.setVisibility(View.VISIBLE);
            DecimalFormat df = new DecimalFormat("#.00");
            holder.issuePrice.setText(df.format(getItem(position).getIssuePrice()));
        } else {
            holder.priceLayout.setVisibility(View.GONE);
        }

        if (getItem(position).getOfflineDate() == null) {
            holder.currentLayout.setVisibility(View.VISIBLE);
            holder.nextLayout.setVisibility(View.GONE);
            holder.currentTitle.setText(R.string.no_offline);
            holder.current.setVisibility(View.GONE);
            holder.actionLayout.setVisibility(View.INVISIBLE);
        } else {
            IpoStatus ipoStatus = null;
            holder.actionLayout.setVisibility(View.VISIBLE);
            try {
                ipoStatus = AppUtils.getIpoStatus(getItem(position));
                if (ipoStatus.getCurrent() == null) {
                    holder.currentLayout.setVisibility(View.GONE);
                } else if (ipoStatus.getCurrent().equals(Status.LISTED)) {
                    holder.actionLayout.setVisibility(View.INVISIBLE);
                    holder.currentLayout.setVisibility(View.VISIBLE);
                    holder.currentTitle.setText(R.string.have_listed);
                    holder.current.setVisibility(View.GONE);
                } else {
                    holder.currentLayout.setVisibility(View.VISIBLE);
                    holder.currentTitle.setText(R.string.today_task);
                    int resId = context.getResources().getIdentifier(ipoStatus.getCurrent().toString(), "string", Value.PACKAGENAME);
                    holder.current.setText(resId);
                    holder.current.setVisibility(View.VISIBLE);
                }
                if (ipoStatus.getNext() == null) {
                    holder.nextLayout.setVisibility(View.GONE);
                } else {
                    holder.nextLayout.setVisibility(View.VISIBLE);
                    if (ipoStatus.getNext().equals(Status.LISTED)) {
                        holder.next.setText(R.string.wait_listed);
                        if (ipoStatus.getCurrent() == null){
                            holder.actionLayout.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        int resId = context.getResources().getIdentifier(ipoStatus.getNext().toString(), "string", Value.PACKAGENAME);
                        holder.next.setText(resId);
                    }
                    if (ipoStatus.getNextDate() != null) {
                        holder.nextDate.setText(ipoStatus.getNextDate());
                        holder.nextDate.setVisibility(View.VISIBLE);
                    } else {
                        holder.nextDate.setVisibility(View.GONE);
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (getItem(position).getProgress() == Status.NONE) {

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
                                                    DatabaseUtils.subscribe(context, userList.get(i).getName(), getItem(position).getCode());
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
                        if (getItem(position).getProgress().equals(Status.PAYMENT)){ //完成所有进度
                            holder.actionImage.setImageResource(R.drawable.ic_correct);
                            holder.actionImage.setColorFilter(ContextCompat.getColor(context, R.color.green));
                            holder.actionText.setTextColor(context.getResources().getColor(R.color.green));
                        }
                        else if (getItem(position).getProgress().compareTo(_current) == 0) { //相同进度，显示打钩完成状态
                            holder.actionImage.setImageResource(R.drawable.ic_correct);
                            holder.actionImage.setColorFilter(ContextCompat.getColor(context, R.color.green));
                            holder.actionText.setTextColor(context.getResources().getColor(R.color.green));
                        } else if (getItem(position).getProgress().compareTo(_current) < 0) { //落后进度，显示TODO状态
                            holder.actionImage.setImageResource(R.drawable.ic_flag);
                            holder.actionImage.setColorFilter(ContextCompat.getColor(context, R.color.red));
                            holder.actionText.setTextColor(context.getResources().getColor(R.color.red));
                            holder.actionLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DatabaseUtils.updateProgress(context, getItem(position).getCode(), _current);
                                    if (onViewReloadListener != null)
                                        onViewReloadListener.reload(null);
                                }
                            });
                        }
                    }
                }
            }
        }
        return convertView;
    }

}
