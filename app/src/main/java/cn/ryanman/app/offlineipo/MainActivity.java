package cn.ryanman.app.offlineipo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import cn.ryanman.app.offlineipo.utils.AppUtils;

public class MainActivity extends AppCompatActivity {

    private TextView webText;
    private BottomNavigationBar bottomNavigationBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webText = (TextView) findViewById(R.id.webtext);
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.list_128px, getString(R.string.ipo_today)))
                .addItem(new BottomNavigationItem(R.drawable.list_128px, getString(R.string.ipo_list)))
                .addItem(new BottomNavigationItem(R.drawable.list_128px, getString(R.string.my_ipo)))
                .initialise();

        IpoListAsyncTask task = new IpoListAsyncTask();
        task.execute();
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
