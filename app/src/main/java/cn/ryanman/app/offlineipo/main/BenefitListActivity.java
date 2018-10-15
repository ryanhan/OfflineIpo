package cn.ryanman.app.offlineipo.main;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.adapter.BenefitListAdapter;
import cn.ryanman.app.offlineipo.model.MyIpo;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;

/**
 * Created by ryanh on 2016/12/27.
 */

public class BenefitListActivity extends AppCompatActivity {

    private List<MyIpo> myIpoList;
    private ListView benefitListView;
    private BenefitListAdapter benefitListAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefit_list);
        setActionBar();

        myIpoList = DatabaseUtils.getAllSubscription(this);

        for (int i = 0; i < myIpoList.size(); i++){
            if (myIpoList.get(i).getEarnAmount() == 0){
                myIpoList.remove(i);
            }
        }

        benefitListView = findViewById(R.id.my_benefit_list);
        benefitListAdapter = new BenefitListAdapter(this, myIpoList);
        benefitListView.setAdapter(benefitListAdapter);

    }

    private void setActionBar() {
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle(R.string.my_benefit);
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

}
