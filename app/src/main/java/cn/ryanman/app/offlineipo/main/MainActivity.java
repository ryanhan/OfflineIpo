package cn.ryanman.app.offlineipo.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.util.ArrayList;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.utils.AppUtils;

public class MainActivity extends AppCompatActivity {

    private TextView webText;
    private BottomNavigationBar bottomNavigationBar;
    private Fragment[] fragments;
    //private IpoTodayFragment ipoTodayFragment;
    //private IpoListFragment ipoListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
        setDefaultFragment();

        //IpoListAsyncTask task = new IpoListAsyncTask();
        //task.execute();
    }

    private void setViews(){
        //webText = (TextView) findViewById(R.id.webtext);
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.list_128px, getString(R.string.ipo_today)))
                .addItem(new BottomNavigationItem(R.drawable.list_128px, getString(R.string.ipo_list)))
                .addItem(new BottomNavigationItem(R.drawable.list_128px, getString(R.string.my_ipo)))
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment, fragments[position]);
                ft.commit();
            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
            }
        });
    }

    private void setDefaultFragment(){

        fragments = new Fragment[3];
        fragments[0] = new IpoTodayFragment();
        fragments[1] = new IpoListFragment();
        fragments[2] = new MyIpoFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment, fragments[0]);
        ft.commit();

    }

    private class IpoListAsyncTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                return AppUtils.getIpoItems().size() + "";
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null)
                webText.setText(result);
        }
    }

}
