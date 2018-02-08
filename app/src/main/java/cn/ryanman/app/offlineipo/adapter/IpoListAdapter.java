package cn.ryanman.app.offlineipo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.listener.OnViewReloadListener;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoStatus;
import cn.ryanman.app.offlineipo.model.Status;
import cn.ryanman.app.offlineipo.utils.AppUtils;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
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
                        if (ipoStatus.getCurrent() == null) {
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

            if (!getItem(position).isApplied()) {

                holder.actionImage.setImageResource(R.drawable.ic_add);
                holder.actionImage.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                holder.actionText.setText(R.string.has_submit);
                holder.actionText.setTextColor(context.getResources().getColor(R.color.colorPrimary));
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.select_number).setView(view).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseUtils.subscribe(context, getItem(position).getCode(), numberPicker.getValue());
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
                                DatabaseUtils.unsubscribe(context, getItem(position).getCode());
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

}
