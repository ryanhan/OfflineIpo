package cn.ryanman.app.offlineipo.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.listener.OnDataLoadCompletedListener;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.IpoListAsyncTask;
import cn.ryanman.app.offlineipo.utils.IpoTodayAsyncTask;

/**
 * Created by ryan on 2016/11/25.
 */

public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGHT = 2000;
    private boolean time_out = false;
    private boolean ipo_list_completed = false;
    private boolean ipo_today_completed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseUtils.createDatabase(this);

//        if (AppUtils.isWifiConnected(this)) {
//            //Toast.makeText(this, "Wifi Connected", Toast.LENGTH_SHORT).show();
//            CheckUpdateAsyncTask checkUpdateAysncTask = new CheckUpdateAsyncTask(this, false);
//            checkUpdateAysncTask.execute();
//        }

        loadNewData();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (ipo_list_completed && ipo_today_completed && !time_out) {
                    completeSplash();
                } else {
                    time_out = true;
                }
            }

        }, SPLASH_DISPLAY_LENGHT);
    }

    private void loadNewData() {
        IpoListAsyncTask ipoListAsyncTask = new IpoListAsyncTask(this);
        ipoListAsyncTask.setOnDataLoadCompletedListener(new OnDataLoadCompletedListener() {
            @Override
            public void onDataSuccessfully(Object object) {
                if (ipo_today_completed && time_out) {
                    time_out = true;
                    completeSplash();
                } else {
                    ipo_list_completed = true;
                }
            }

            @Override
            public void onDataFailed() {
                if (ipo_today_completed && time_out) {
                    time_out = true;
                    completeSplash();
                } else {
                    ipo_list_completed = true;
                }
            }
        });
        ipoListAsyncTask.execute();

        IpoTodayAsyncTask ipoTodayAsyncTask = new IpoTodayAsyncTask(this);
        ipoTodayAsyncTask.setOnDataLoadCompletedListener(new OnDataLoadCompletedListener() {
            @Override
            public void onDataSuccessfully(Object object) {
                if (ipo_list_completed && time_out) {
                    time_out = true;
                    completeSplash();
                } else {
                    ipo_today_completed = true;
                }
            }

            @Override
            public void onDataFailed() {
                if (ipo_list_completed && time_out) {
                    time_out = true;
                    completeSplash();
                } else {
                    ipo_today_completed = true;
                }
            }
        });

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ipoTodayAsyncTask.execute(df.format(new Date()));
    }

    private void completeSplash() {
        Intent intent = new Intent(SplashActivity.this,
                MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
