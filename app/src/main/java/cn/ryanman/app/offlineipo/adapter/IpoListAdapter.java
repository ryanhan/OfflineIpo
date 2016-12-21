package cn.ryanman.app.offlineipo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoStatus;
import cn.ryanman.app.offlineipo.utils.AppUtils;

/**
 * Created by ryanh on 2016/12/17.
 */

public class IpoListAdapter extends ArrayAdapter<IpoItem> {

    private Context context;
    private LayoutInflater mInflater;

    public final class ViewHolder {
        public TextView ipoName;
        public TextView ipoCode;
        public TextView current;
        public TextView next;
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
            holder.current = (TextView) convertView.findViewById(R.id.adapter_ipo_current);
            holder.next = (TextView) convertView.findViewById(R.id.adapter_ipo_next);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ipoName.setText(getItem(position).getName());
        holder.ipoCode.setText(getItem(position).getCode());

        if (getItem(position).getOfflineDate() == null){
            holder.current.setText("无网下申购");
        }
        else {
            try {
                IpoStatus status = AppUtils.getNextStep(getItem(position));
                if (status.getCurrent() != null) {
                    holder.current.setText(status.getCurrent());
                }
                else{
                    holder.current.setText("不知道");
                }
                if (status.getNextDate() != null) {
                    holder.next.setText(status.getNext() + "  " + status.getNextDate());
                }
                else{
                    holder.next.setText(status.getNext() + "  " + "不知道");
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

//        try {
//            IpoStatus status = AppUtils.getNextStep(getItem(position));
//            if (status.getNext() != null) {
//                holder.next.setText(status.getNext());
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        return convertView;
    }

}
