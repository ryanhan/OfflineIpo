package cn.ryanman.app.offlineipo.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.listener.OnDateChangeListener;
import cn.ryanman.app.offlineipo.utils.AppUtils;
import cn.ryanman.app.offlineipo.utils.Value;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationBar bottomNavigationBar;
    private Fragment[] fragments;
    private ActionBar actionBar;
    private String date;
    private TextView todayText;
    private OnDateChangeListener onDateChangeListener;

    public void setOnDateChangeListener(OnDateChangeListener onDateChangeListener) {
        this.onDateChangeListener = onDateChangeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
        setDefaultFragment();

    }

    private void setViews() {

        actionBar = getSupportActionBar();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        date = df.format(new Date());

        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar.setActiveColor(R.color.colorPrimary)
                .setInActiveColor("#979797");

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_today_24dp, getString(R.string.ipo_today)))
                .addItem(new BottomNavigationItem(R.drawable.ic_list_24dp, getString(R.string.ipo_list)))
                .addItem(new BottomNavigationItem(R.drawable.ic_user_24dp, getString(R.string.my_ipo)))
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment, fragments[position]);
                ft.commit();
                switch (position) {
                    case 0:
                        customizeActionBar();
                        break;
                    default:
                        normalActionBar();
                        break;
                }
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });
    }

    private void customizeActionBar() {

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View view = LayoutInflater.from(this).inflate(
                R.layout.actionbar_ipo_today, null);
        actionBar.setCustomView(view, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        todayText = (TextView) view.findViewById(R.id.actionbar_today);
        ImageView left = (ImageView) view.findViewById(R.id.actionbar_left);
        ImageView right = (ImageView) view.findViewById(R.id.actionbar_right);
        TextView back = (TextView) view.findViewById(R.id.actionbar_back);
        todayText.setText(date);
        left.setOnClickListener(new DateChangeListener(-1));
        right.setOnClickListener(new DateChangeListener(1));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                date = df.format(new Date());
                todayText.setText(date);
                IpoTodayFragment fragment = (IpoTodayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                fragment.updateList(date, true);
            }
        });

        actionBar.setCustomView(view);

    }

    private void normalActionBar() {
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
    }

    private void setDefaultFragment() {

        fragments = new Fragment[3];
        fragments[0] = new IpoTodayFragment();
        fragments[1] = new IpoListFragment();
        fragments[2] = new MyIpoFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Value.DATE, date);  //被传递的对象一定要实现Serializable接口
        fragments[0].setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment, fragments[0]);
        ft.commit();
        customizeActionBar();
    }

    private class DateChangeListener implements View.OnClickListener {

        private int amount;

        public DateChangeListener(int amount) {
            this.amount = amount;
        }

        @Override
        public void onClick(View v) {
            try {
                Date d = AppUtils.parseDate(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                cal.add(Calendar.DATE, amount);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                date = df.format(cal.getTime());
                todayText.setText(date);

                IpoTodayFragment fragment = (IpoTodayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                fragment.updateList(date, true);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}
