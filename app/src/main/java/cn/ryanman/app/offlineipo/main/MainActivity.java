package cn.ryanman.app.offlineipo.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import cn.ryanman.app.offlineipo.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationBar bottomNavigationBar;
    private Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
        setDefaultFragment();

    }

    private void setViews(){

        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.list_128px, getString(R.string.ipo_today)))
                .addItem(new BottomNavigationItem(R.drawable.list_128px, getString(R.string.ipo_list)))
                .addItem(new BottomNavigationItem(R.drawable.user, getString(R.string.my_ipo)))
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

}
