package cn.ryanman.app.offlineipo.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.adapter.IpoTodayListAdapter;
import cn.ryanman.app.offlineipo.listener.OnDataLoadCompletedListener;
import cn.ryanman.app.offlineipo.listener.OnViewReloadListener;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoTodayFull;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.IpoTodayAsyncTask;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryanh on 2016/11/25.
 */

public class IpoTodayFragment extends Fragment {

    private ExpandableListView ipoTodayListView;
    private IpoTodayListAdapter ipoTodayListAdapter;
    private List<String> eventList;
    private List<List<IpoItem>> ipoNameList;
    private boolean isShowRelated;
    private String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ipo_today, container, false);
        ipoTodayListView = (ExpandableListView) view.findViewById(R.id.ipo_today_list);

        isShowRelated = false;
        setHasOptionsMenu(true);

        eventList = new ArrayList<>();
        ipoNameList = new ArrayList<>(Value.eventMap.size());

        ipoTodayListAdapter = new IpoTodayListAdapter(this.getActivity(), eventList, ipoNameList);
        ipoTodayListView.setAdapter(ipoTodayListAdapter);

        ipoTodayListAdapter.setOnViewReloadListener(new OnViewReloadListener() {
            @Override
            public void reload(Object object) {
                updateList(date, false);
            }
        });

        ipoTodayListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent();
                intent.setClass(IpoTodayFragment.this.getActivity(),
                        IpoDetailActivity.class);
                intent.putExtra(Value.IPO_CODE, ipoNameList.get(groupPosition).get(childPosition).getCode());
                startActivity(intent);
                return false;
            }
        });

        updateList(date, false);
        return view;
    }

    public void updateList(String date, boolean isFull) {
        if (isFull) {
            IpoTodayAsyncTask ipoTodayAsyncTask = new IpoTodayAsyncTask(IpoTodayFragment.this.getActivity());
            ipoTodayAsyncTask.setOnDataLoadCompletedListener(new OnDataLoadCompletedListener() {
                @Override
                public void onDataSuccessfully(Object object) {
                    IpoTodayDBAsyncTask task = new IpoTodayDBAsyncTask(IpoTodayFragment.this.getActivity(), isShowRelated);
                    task.execute();
                }

                @Override
                public void onDataFailed() {
                }
            });
            ipoTodayAsyncTask.execute(date);
        } else {
            IpoTodayDBAsyncTask task = new IpoTodayDBAsyncTask(IpoTodayFragment.this.getActivity(), isShowRelated);
            task.execute();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            date = bundle.getString(Value.DATE);
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            date = df.format(new Date());
        }
    }

    private class IpoTodayDBAsyncTask extends AsyncTask<Void, Integer, List<IpoTodayFull>> {

        private Context context;
        private boolean isShowRelated;

        public IpoTodayDBAsyncTask(Context context, boolean isShowRelated) {
            this.context = context;
            this.isShowRelated = isShowRelated;
        }

        @Override
        protected List<IpoTodayFull> doInBackground(Void... params) {
            return DatabaseUtils.getIpoTodayList(context);
        }

        @Override
        protected void onPostExecute(List<IpoTodayFull> result) {

            eventList.clear();
            ipoNameList.clear();

            for (int i = 0; i < Value.eventMap.size(); i++) {
                ipoNameList.add(new ArrayList<IpoItem>());
            }

            Value.ipoTodayMap.clear();

            Set<Integer> eventSet = new TreeSet<>();

            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getEvent().equals(cn.ryanman.app.offlineipo.model.Status.NOTICE) ||
                        result.get(i).getEvent().equals(cn.ryanman.app.offlineipo.model.Status.INQUIRY) ||
                        result.get(i).getEvent().equals(cn.ryanman.app.offlineipo.model.Status.OFFLINE) ||
                        result.get(i).getEvent().equals(cn.ryanman.app.offlineipo.model.Status.PAYMENT) ||
                        result.get(i).getEvent().equals(cn.ryanman.app.offlineipo.model.Status.LISTED)
                        ) {
                    Value.ipoTodayMap.put(result.get(i).getIpo().getName(), result.get(i).getEvent());
                    eventSet.add(Value.eventMap.get(result.get(i).getEvent().toString()));
                    ipoNameList.get(Value.eventMap.get(result.get(i).getEvent().toString())).add(result.get(i).getIpo());
                } else if (!isShowRelated) {
                    eventSet.add(Value.eventMap.get(result.get(i).getEvent().toString()));
                    ipoNameList.get(Value.eventMap.get(result.get(i).getEvent().toString())).add(result.get(i).getIpo());
                }
            }

            Iterator<Integer> iter = eventSet.iterator();
            while (iter.hasNext()) {
                eventList.add(Value.eventArray[iter.next()]);
            }
            for (int i = Value.eventArray.length; i > 0; i--) {
                if (!eventSet.contains(i - 1)) {
                    ipoNameList.remove(i - 1);
                }
            }

            for (int i = 0; i < ipoNameList.size(); i++) {
                ipoTodayListView.expandGroup(i);
            }

            ipoTodayListAdapter.notifyDataSetChanged();

        }
    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.fragment_ipo_today, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.show_related:
//                if (!isShowRelated) {
//                    isShowRelated = true;
//                    item.setTitle(R.string.show_all);
//                } else {
//                    isShowRelated = false;
//                    item.setTitle(R.string.show_only_related);
//                }
//                updateList();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
