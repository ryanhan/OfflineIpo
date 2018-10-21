package cn.ryanman.app.offlineipo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.listener.OnViewReloadListener;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.utils.AppUtils;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.MyNumberPicker;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryanh on 2016/12/17.
 */

public class IpoListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private LayoutInflater mInflater;
    private IpoListFilter mFilter;
    private OnViewReloadListener onViewReloadListener;
    private List<IpoItem> items1;
    private List<IpoItem> items2;

    public void setOnViewReloadListener(OnViewReloadListener onViewReloadListener) {
        this.onViewReloadListener = onViewReloadListener;
    }

    public final class ViewHolder {
        public TextView ipoName;
        public TextView ipoCode;
        public LinearLayout priceLayout;
        public LinearLayout currentLayout;
        public TextView issuePrice;
        public TextView current;
        public LinearLayout actionLayout;
        public ImageView actionImage;
        public TextView actionText;
    }

    public IpoListAdapter(Context context, List<IpoItem> items) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.items1 = items;
        this.items2 = items;
    }

    @Override
    public int getCount() {
        return items2.size();
    }

    @Override
    public Object getItem(int position) {
        return items2.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.adapter_all_ipo_list, null);
            holder.ipoName = convertView.findViewById(R.id.adapter_ipo_name);
            holder.ipoCode = convertView.findViewById(R.id.adapter_ipo_code);
            holder.priceLayout = convertView.findViewById(R.id.adapter_ipo_price_layout);
            holder.currentLayout = convertView.findViewById(R.id.adapter_ipo_current_layout);
            holder.issuePrice = convertView.findViewById(R.id.adapter_ipo_price);
            holder.current = convertView.findViewById(R.id.adapter_ipo_current);
            holder.actionLayout = convertView.findViewById(R.id.adapter_action_layout);
            holder.actionImage = convertView.findViewById(R.id.adapter_action_image);
            holder.actionText = convertView.findViewById(R.id.adapter_action_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ipoName.setText(items2.get(position).getName());
        holder.ipoCode.setText(items2.get(position).getCode());

        //显示发行价
        if (items2.get(position).getIssuePrice() != 0) {
            DecimalFormat df = new DecimalFormat("#.00");
            holder.issuePrice.setText(context.getString(R.string.issue_price) + context.getString(R.string.space)+ "￥" + df.format(items2.get(position).getIssuePrice()));
        } else {
            holder.issuePrice.setText(context.getString(R.string.issue_price) + R.string.none);
        }


        holder.actionLayout.setVisibility(View.VISIBLE);
        if (AppUtils.isListed(items2.get(position))) {
            holder.current.setText(context.getString(R.string.have_listed) + context.getString(R.string.space) + items2.get(position).getListedDate());
        } else if (Value.ipoTodayMap.containsKey(items2.get(position).getName())) {
            int resId = context.getResources().getIdentifier(Value.ipoTodayMap.get(items2.get(position).getName()).toString(), "string", Value.PACKAGENAME);
            holder.current.setText(resId);
        } else {
            holder.current.setText(R.string.no_work_today);
        }

        if (!items2.get(position).isApplied()) {
            holder.actionImage.setImageResource(R.drawable.ic_add);
            holder.actionImage.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
            holder.actionText.setText(R.string.has_submit);
            holder.actionText.setTextColor(context.getColor(R.color.colorAccent));
            holder.actionLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    View view = layoutInflater.inflate(R.layout.dialog_number_picker, null);
                    final MyNumberPicker numberPicker = view.findViewById(R.id.dialog_number);
                    numberPicker.setMinValue(1);
                    numberPicker.setMaxValue(10);
                    numberPicker.setValue(2);
                    numberPicker.setWrapSelectorWheel(false);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.select_number).setView(view).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseUtils.subscribe(context, items2.get(position).getCode(), numberPicker.getValue());
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
                            DatabaseUtils.unsubscribe(context, items2.get(position).getCode());
                            if (onViewReloadListener != null)
                                onViewReloadListener.reload(null);
                        }
                    }).setNegativeButton(R.string.no, null).show();

                }
            });
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new IpoListFilter();
        }
        return mFilter;
    }

    class IpoListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<IpoItem> list;
            if (TextUtils.isEmpty(constraint)) {
                list = items1;
            } else {
                list = new ArrayList<>(); //新建ArrayList用于存放符合过滤条件的项
                for (IpoItem item : items1) {
                    if (item.getName().contains(constraint) || item.getCode().contains(constraint)) {
                        list.add(item);
                    }
                }
            }
            results.values = list;
            results.count = list.size();
            return results; //返回results，publishResults(CharSequence constraint, FilterResults results)接受此返回值
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            items2 = (List<IpoItem>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();//通知数据发生了改变
            } else {
                notifyDataSetInvalidated();//通知数据失效
            }
        }

    }
}
