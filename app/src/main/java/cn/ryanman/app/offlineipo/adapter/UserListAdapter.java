package cn.ryanman.app.offlineipo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.model.User;

/**
 * Created by ryanh on 2016/12/17.
 */

public class UserListAdapter extends ArrayAdapter<User> {

    private Context context;
    private LayoutInflater mInflater;

    public final class ViewHolder {
        public TextView userName;
        public TextView userShCode;
        public TextView userMarket;
    }

    public UserListAdapter(Context context, List<User> objects) {
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
                    R.layout.adapter_user_list, null);
            holder.userName = (TextView) convertView.findViewById(R.id.adapter_user_name);
            holder.userShCode = (TextView) convertView.findViewById(R.id.adapter_user_sh_code);
            holder.userMarket = (TextView) convertView.findViewById(R.id.adapter_user_market_value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.userName.setText(getItem(position).getName());
        holder.userShCode.setText(getItem(position).getCode());
        holder.userMarket.setText(getItem(position).getMarket());

        return convertView;
    }

}
