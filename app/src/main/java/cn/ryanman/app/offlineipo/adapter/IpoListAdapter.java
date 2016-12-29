package cn.ryanman.app.offlineipo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.listener.OnDataLoadCompletedListener;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoStatus;
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
        public Button joinButton;
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
            holder.joinButton = (Button) convertView.findViewById(R.id.adapter_join_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ipoName.setText(getItem(position).getName());
        holder.ipoCode.setText(getItem(position).getCode());
        if (DatabaseUtils.isSubscribed(context, getItem(position).getCode())) {
            //holder.joinButton.setClickable(false);
        }
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
            holder.joinButton.setVisibility(View.INVISIBLE);
        } else {
            holder.joinButton.setVisibility(View.VISIBLE);
            holder.joinButton.setOnClickListener(new View.OnClickListener() {
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
                                            //holder.joinButton.setClickable(false);
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

            try {
                IpoStatus status = AppUtils.getIpoStatus(getItem(position));
                if (status.getCurrent() == null) {
                    holder.currentLayout.setVisibility(View.GONE);
                } else if (status.getCurrent().equals(Value.LISTED)) {
                    holder.currentLayout.setVisibility(View.VISIBLE);
                    holder.currentTitle.setText(R.string.have_listed);
                    holder.current.setVisibility(View.GONE);
                } else {
                    holder.currentLayout.setVisibility(View.VISIBLE);
                    holder.currentTitle.setText(R.string.today_task);
                    int resId = context.getResources().getIdentifier(status.getCurrent(), "string", Value.PACKAGENAME);
                    holder.current.setText(resId);
                    holder.current.setVisibility(View.VISIBLE);
                }
                if (status.getNext() == null) {
                    holder.nextLayout.setVisibility(View.GONE);
                } else {
                    holder.nextLayout.setVisibility(View.VISIBLE);
                    if (status.getNext().equals(Value.LISTED)) {
                        holder.next.setText(R.string.wait_listed);
                    } else {
                        int resId = context.getResources().getIdentifier(status.getNext(), "string", Value.PACKAGENAME);
                        holder.next.setText(resId);
                    }
                    if (status.getNextDate() != null) {
                        holder.nextDate.setText(status.getNextDate());
                        holder.nextDate.setVisibility(View.VISIBLE);
                    } else {
                        holder.nextDate.setVisibility(View.GONE);
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

}
