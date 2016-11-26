package cn.ryanman.app.offlineipo.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.adapter.IpoTodayListAdapter;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoToday;
import cn.ryanman.app.offlineipo.model.IpoTodayFull;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryanh on 2016/11/25.
 */

public class IpoTodayFragment extends Fragment {

    private ExpandableListView ipoTodayListView;
    private BaseExpandableListAdapter ipoTodayListAdapter;
    private List<List<IpoItem>> ipoNameList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ipo_today, container, false);
        ipoTodayListView = (ExpandableListView) view.findViewById(R.id.ipo_today_list);

        ipoNameList = new ArrayList<>();
        for (int i = 0; i < Value.eventMap.size(); i++) {
            ipoNameList.add(new ArrayList<IpoItem>());
        }

        ipoTodayListAdapter = new IpoTodayListAdapter(this.getActivity(), Value.eventArray, ipoNameList);
        ipoTodayListView.setAdapter(ipoTodayListAdapter);

        for (int i = 0; i < Value.eventArray.length; i++) {
            ipoTodayListView.expandGroup(i);
        }

        IpoTodayAsyncTask task = new IpoTodayAsyncTask(this.getActivity());
        task.execute();

        return view;
    }

    private class IpoTodayAsyncTask extends AsyncTask<Void, Integer, List<IpoTodayFull>> {

        private Context context;

        public IpoTodayAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<IpoTodayFull> doInBackground(Void... params) {
            return DatabaseUtils.getIpoTodayFullList(context);
        }

        @Override
        protected void onPostExecute(List<IpoTodayFull> result) {

            for (int i = 0; i < Value.eventMap.size(); i++) {
                ipoNameList.get(i).clear();
            }

            for (int i = 0; i < result.size(); i++) {
                System.out.println(result.get(i).getIpo().getName());
                ipoNameList.get(Value.eventMap.get(result.get(i).getEvent())).add(result.get(i).getIpo());
            }

            ipoTodayListAdapter.notifyDataSetInvalidated();

        }
    }

}