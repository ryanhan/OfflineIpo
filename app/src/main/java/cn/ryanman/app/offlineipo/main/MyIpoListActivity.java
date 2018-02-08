package cn.ryanman.app.offlineipo.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.adapter.IpoListAdapter;
import cn.ryanman.app.offlineipo.listener.OnViewReloadListener;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.MyIpo;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryanh on 2016/12/27.
 */

public class MyIpoListActivity extends AppCompatActivity {

    private ListView ipoListView;
    private IpoListAdapter ipoListAdapter;
    private List<IpoItem> ipoList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ipo_list);

        ipoListView = (ListView) findViewById(R.id.my_ipo_list);
        ipoList = new ArrayList<>();

        ipoListAdapter = new IpoListAdapter(this, ipoList);
        ipoListView.setAdapter(ipoListAdapter);

        ipoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MyIpoListActivity.this,
                        IpoDetailActivity.class);
                intent.putExtra(Value.IPO_CODE, ipoList.get(position).getCode());
                startActivity(intent);
            }
        });

        ipoListAdapter.setOnViewReloadListener(new OnViewReloadListener() {
            @Override
            public void reload(Object object) {
                GetSubscribeAsyncTask task = new GetSubscribeAsyncTask(MyIpoListActivity.this);
                task.execute();
            }
        });

        GetSubscribeAsyncTask task = new GetSubscribeAsyncTask(this);
        task.execute();
    }

    private class GetSubscribeAsyncTask extends AsyncTask<Void, Integer, List<MyIpo>> {

        private Context context;

        public GetSubscribeAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<MyIpo> doInBackground(Void... voids) {
            return DatabaseUtils.getAllSubscription(context);
        }

        @Override
        protected void onPostExecute(List<MyIpo> result) {
            if (result != null) {
                List<IpoItem> ipoItems = new ArrayList<>();
                for (MyIpo myIpo : result){
                    ipoItems.add(myIpo.getIpoItem());
                }
                ipoList.clear();
                ipoList.addAll(ipoItems);
                ipoListAdapter.notifyDataSetChanged();
            }

        }
    }

}
