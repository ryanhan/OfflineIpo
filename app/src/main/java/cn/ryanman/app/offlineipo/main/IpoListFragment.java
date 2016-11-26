package cn.ryanman.app.offlineipo.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.listener.OnDataLoadCompletedListener;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.WebUtils;

/**
 * Created by ryanh on 2016/11/25.
 */

public class IpoListFragment extends Fragment {

    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ipo_list, container, false);
        textView = (TextView) view.findViewById(R.id.IpoListText);
        IpoListAsyncTask task = new IpoListAsyncTask(this.getActivity());
        task.execute();

        return view;
    }

    private class IpoListAsyncTask extends AsyncTask<Void, Integer, List<IpoItem>> {

        private Context context;

        public IpoListAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<IpoItem> doInBackground(Void... params) {
            return DatabaseUtils.getIpoList(context);
        }

        @Override
        protected void onPostExecute(List<IpoItem> result) {
            textView.setText(result.get(0).getName());
        }
    }

}
