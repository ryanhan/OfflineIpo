package cn.ryanman.app.offlineipo.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.adapter.MyIpoListAdapter;
import cn.ryanman.app.offlineipo.model.MyIpo;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryanh on 2016/12/27.
 */

public class MyIpoListActivity extends AppCompatActivity {

    private ListView ipoListView;
    private MyIpoListAdapter ipoListAdapter;
    private List<MyIpo> ipoList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ipo_list);
        setActionBar();

        ipoListView = findViewById(R.id.my_ipo_list);
        ipoList = new ArrayList<>();

        ipoListAdapter = new MyIpoListAdapter(this, ipoList);
        ipoListView.setAdapter(ipoListAdapter);

        ipoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MyIpoListActivity.this,
                        IpoDetailActivity.class);
                intent.putExtra(Value.IPO_NAME, ipoList.get(position).getIpoItem().getName());
                intent.putExtra(Value.IPO_CODE, ipoList.get(position).getIpoItem().getCode());
                startActivity(intent);
            }
        });

    }

    private void setActionBar(){
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle(R.string.my_ipo);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSubscribeList();
    }

    private void refreshSubscribeList(){
        ipoList.clear();
        ipoList.addAll(DatabaseUtils.getAllSubscription(this));
        ipoListAdapter.notifyDataSetChanged();
    }

}
