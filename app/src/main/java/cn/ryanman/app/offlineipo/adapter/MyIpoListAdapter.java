package cn.ryanman.app.offlineipo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.listener.OnViewReloadListener;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.MyIpo;
import cn.ryanman.app.offlineipo.utils.AppUtils;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryanh on 2016/12/17.
 */

public class MyIpoListAdapter extends ArrayAdapter<MyIpo> {

    private Context context;
    private LayoutInflater mInflater;
    private OnViewReloadListener onViewReloadListener;

    public void setOnViewReloadListener(OnViewReloadListener onViewReloadListener) {
        this.onViewReloadListener = onViewReloadListener;
    }

    public final class ViewHolder {
        public TextView ipoName;
        public TextView ipoCode;
        public LinearLayout priceLayout;
        public LinearLayout currentLayout;
        public TextView issuePrice;
        public TextView stockNumber;
        public TextView stockText;
        public TextView benefit;
        public TextView personNumber;
        public TextView current;
        public LinearLayout actionLayout;
        public ImageView actionImage;
        public TextView actionText;
    }

    public MyIpoListAdapter(Context context, List<MyIpo> objects) {
        super(context, 0, objects);
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.adapter_my_ipo_list, null);
            holder.ipoName = (TextView) convertView.findViewById(R.id.adapter_ipo_name);
            holder.ipoCode = (TextView) convertView.findViewById(R.id.adapter_ipo_code);
            holder.priceLayout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_price_layout);
            holder.currentLayout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_current_layout);
            holder.issuePrice = (TextView) convertView.findViewById(R.id.adapter_ipo_price);
            holder.stockNumber = (TextView) convertView.findViewById(R.id.adapter_ipo_stock_text);
            holder.stockText = (TextView) convertView.findViewById(R.id.adapter_ipo_stock);
            holder.benefit = (TextView) convertView.findViewById(R.id.adapter_ipo_benefit);
            holder.personNumber = (TextView) convertView.findViewById(R.id.adapter_ipo_person_number);
            holder.current = (TextView) convertView.findViewById(R.id.adapter_ipo_current);
            holder.actionLayout = (LinearLayout) convertView.findViewById(R.id.adapter_action_layout);
            holder.actionImage = (ImageView) convertView.findViewById(R.id.adapter_action_image);
            holder.actionText = (TextView) convertView.findViewById(R.id.adapter_action_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ipoName.setText(getItem(position).getIpoItem().getName());
        holder.ipoCode.setText(getItem(position).getIpoItem().getCode());

        if (getItem(position).getIpoItem().getIssuePrice() != 0) {
            DecimalFormat df = new DecimalFormat("#.00");
            holder.issuePrice.setText("￥" + df.format(getItem(position).getIpoItem().getIssuePrice()));
        } else {
            holder.issuePrice.setText(R.string.none);
        }

        if (getItem(position).getStockShare() != 0) {
            holder.stockNumber.setText(String.valueOf(getItem(position).getStockShare()));
            holder.stockText.setVisibility(View.VISIBLE);
        } else {
            holder.stockNumber.setText(R.string.none);
            holder.stockText.setVisibility(View.INVISIBLE);
        }

        if (getItem(position).getEarnAmount() != 0) {
            DecimalFormat df = new DecimalFormat("#.00");
            holder.benefit.setText("￥" + df.format(getItem(position).getEarnAmount()));
        } else {
            holder.benefit.setText(R.string.none);
        }

        holder.personNumber.setText(getItem(position).getPersonNumber() + "人");

        if (!getItem(position).getIpoItem().isApplied()) {

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
                            DatabaseUtils.subscribe(context, getItem(position).getIpoItem().getCode(), numberPicker.getValue());
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
                            DatabaseUtils.unsubscribe(context, getItem(position).getIpoItem().getCode());
                            if (onViewReloadListener != null)
                                onViewReloadListener.reload(null);
                        }
                    }).setNegativeButton(R.string.no, null).show();

                }
            });
        }

        return convertView;
    }

}
