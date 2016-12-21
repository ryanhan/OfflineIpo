package cn.ryanman.app.offlineipo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoStatus;
import cn.ryanman.app.offlineipo.utils.AppUtils;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryanh on 2016/12/17.
 */

public class IpoListAdapter extends ArrayAdapter<IpoItem> {

    private Context context;
    private LayoutInflater mInflater;

    public final class ViewHolder {
        public TextView ipoName;
        public TextView ipoCode;
        public LinearLayout currentLayout;
        public LinearLayout nextLayout;
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
            holder.currentLayout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_current_layout);
            holder.nextLayout = (LinearLayout) convertView.findViewById(R.id.adapter_ipo_next_layout);
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

        if (getItem(position).getOfflineDate() == null){
            holder.currentLayout.setVisibility(View.VISIBLE);
            holder.nextLayout.setVisibility(View.GONE);
            holder.currentTitle.setText(context.getString(R.string.no_offline));
            holder.current.setVisibility(View.GONE);
            holder.joinButton.setVisibility(View.INVISIBLE);
        }
        else {
            holder.joinButton.setVisibility(View.VISIBLE);
            try {
                IpoStatus status = AppUtils.getIpoStatus(getItem(position));
                if (status.getCurrent() == null) {
                    holder.currentLayout.setVisibility(View.GONE);
                }
                else if (status.getCurrent().equals(Value.LISTED)){
                    holder.currentLayout.setVisibility(View.VISIBLE);
                    holder.currentTitle.setText(context.getString(R.string.have_listed));
                    holder.current.setVisibility(View.GONE);
                }
                else{
                    holder.currentLayout.setVisibility(View.VISIBLE);
                    holder.currentTitle.setText(context.getString(R.string.today_task));
                    int resId = context.getResources().getIdentifier(status.getCurrent(), "string", Value.PACKAGENAME);
                    holder.current.setText(context.getString(resId));
                    holder.current.setVisibility(View.VISIBLE);
                }
                if (status.getNext() == null) {
                    holder.nextLayout.setVisibility(View.GONE);
                }
                else{
                    holder.nextLayout.setVisibility(View.VISIBLE);
                    int resId = context.getResources().getIdentifier(status.getNext(), "string", Value.PACKAGENAME);
                    holder.next.setText(context.getString(resId));
                    if (status.getNextDate() != null){
                        holder.nextDate.setText(status.getNextDate());
                        holder.nextDate.setVisibility(View.VISIBLE);
                    }
                    else{
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
