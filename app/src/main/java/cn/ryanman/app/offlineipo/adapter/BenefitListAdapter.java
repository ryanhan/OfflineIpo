package cn.ryanman.app.offlineipo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.model.MyIpo;

/**
 * Created by ryanh on 2016/12/17.
 */

public class BenefitListAdapter extends ArrayAdapter<MyIpo> {

    private LayoutInflater mInflater;

    public final class ViewHolder {
        public TextView ipoName;
        public TextView soldDate;
        public TextView benefit;
    }

    public BenefitListAdapter(Context context, List<MyIpo> objects) {
        super(context, 0, objects);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.adapter_benefit_list, null);
            holder.ipoName = convertView.findViewById(R.id.adapter_ipo_name);
            holder.soldDate = convertView.findViewById(R.id.adapter_ipo_date);
            holder.benefit = convertView.findViewById(R.id.adapter_ipo_benefit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ipoName.setText(getItem(position).getIpoItem().getName() + " " + getItem(position).getIpoItem().getCode());

        if (getItem(position).getSoldDate() == null || getItem(position).getSoldDate().equals("")) {
            holder.soldDate.setText("-");
        } else {
            holder.soldDate.setText(getItem(position).getSoldDate());
        }

        DecimalFormat df = new DecimalFormat("#,###.00");
        holder.benefit.setText("￥" + df.format(getItem(position).getEarnAmount()));

        return convertView;
    }

}
