package cn.ryanman.app.offlineipo.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.adapter.IpoListAdapter;
import cn.ryanman.app.offlineipo.listener.OnViewReloadListener;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryanh on 2016/11/25.
 */

public class IpoListFragment extends Fragment {

    private ListView ipoListView;
    private IpoListAdapter ipoListAdapter;
    private List<IpoItem> ipoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ipo_list, container, false);
        ipoListView = view.findViewById(R.id.all_ipo_list);
        ipoList = new ArrayList<>();

        ipoListAdapter = new IpoListAdapter(this.getActivity(), ipoList);
        ipoListView.setAdapter(ipoListAdapter);

        ipoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(IpoListFragment.this.getActivity(),
                        IpoDetailActivity.class);
                intent.putExtra(Value.IPO_NAME, ipoList.get(position).getName());
                intent.putExtra(Value.IPO_CODE, ipoList.get(position).getCode());
                startActivity(intent);
            }
        });

        ipoListAdapter.setOnViewReloadListener(new OnViewReloadListener() {
            @Override
            public void reload(Object object) {
                IpoListAsyncTask task = new IpoListAsyncTask(IpoListFragment.this.getActivity());
                task.execute();
            }
        });

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
            if (result != null) {
                ipoList.clear();
                ipoList.addAll(result);
                ipoListAdapter.notifyDataSetChanged();
            }
        }
    }

}
