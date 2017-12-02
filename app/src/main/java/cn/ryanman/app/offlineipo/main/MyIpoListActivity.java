package cn.ryanman.app.offlineipo.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.model.MyIpo;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;

/**
 * Created by ryanh on 2016/12/27.
 */

public class MyIpoListActivity extends AppCompatActivity {

    private TextView tv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ipo_list);

        tv = (TextView) findViewById(R.id.my_ipo_list_size);
        GetSubscribeAsyncTask getSubscribeAsyncTask = new GetSubscribeAsyncTask(this);
        getSubscribeAsyncTask.execute();
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
            tv.setText("MyIpo size = " + result.size());
        }
    }

}
