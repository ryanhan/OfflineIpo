package cn.ryanman.app.offlineipo.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.adapter.MarketSpinnerAdapter;
import cn.ryanman.app.offlineipo.model.MyIpo;
import cn.ryanman.app.offlineipo.model.User;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by hanyan on 12/26/2016.
 */

public class IpoDetailActivity extends AppCompatActivity {

    private String ipoCode;
    private TextView nameText;
    private TextView codeText;
    private LinearLayout priceLayout;
    private TextView priceText;
    private LinearLayout subscribeLayout;
    private TextView personText;
    private TextView progressText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipo_detail);

        getBundle();

        nameText = (TextView) findViewById(R.id.ipo_detail_name);
        codeText = (TextView) findViewById(R.id.ipo_detail_code);
        priceLayout = (LinearLayout) findViewById(R.id.ipo_detail_price_layout);
        priceText = (TextView) findViewById(R.id.ipo_detail_price);
        subscribeLayout = (LinearLayout) findViewById(R.id.ipo_detail_subscribe_layout);
        personText = (TextView) findViewById(R.id.ipo_detail_person);
        progressText = (TextView) findViewById(R.id.ipo_detail_progress);

        GetIpoDetailAsyncTask getIpoDetailAsyncTask = new GetIpoDetailAsyncTask(this);
        getIpoDetailAsyncTask.execute(ipoCode);
    }


    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        ipoCode = bundle.getString(Value.IPO_CODE);
    }

    private class GetIpoDetailAsyncTask extends AsyncTask<String, Integer, MyIpo> {

        private Context context;

        public GetIpoDetailAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected MyIpo doInBackground(String... params) {
            return DatabaseUtils.getIpoDetail(context, params[0]);
        }

        @Override
        protected void onPostExecute(MyIpo result) {
            nameText.setText(result.getIpoItem().getName());
            codeText.setText(result.getIpoItem().getCode());
            priceText.setText(String.valueOf(result.getIpoItem().getIssuePrice()));
            progressText.setText(result.getIpoItem().getProgress().toString());
            List<String> persons = result.getUserName();
            StringBuffer sb = new StringBuffer();
            if (persons.size() > 0){
                for (String name: persons) {
                    sb.append(name).append(" ");
                }
                personText.setText(sb.toString());
            }
        }
    }
}
