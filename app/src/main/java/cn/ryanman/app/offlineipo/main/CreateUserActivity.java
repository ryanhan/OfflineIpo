package cn.ryanman.app.offlineipo.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.adapter.MarketSpinnerAdapter;
import cn.ryanman.app.offlineipo.model.User;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;

import static cn.ryanman.app.offlineipo.R.drawable.user;

/**
 * Created by hanyan on 12/26/2016.
 */

public class CreateUserActivity extends AppCompatActivity {

    private EditText userName;
    private EditText userCode;
    private Spinner userMarket;
    private Button saveButton;

    private MarketSpinnerAdapter adapter;

    private int id = -1;
    private String[] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        userName = (EditText) findViewById(R.id.user_name_text);
        userCode = (EditText) findViewById(R.id.user_sh_code_text);
        userMarket = (Spinner) findViewById(R.id.user_sh_market_spinner);
        saveButton = (Button) findViewById(R.id.adapter_create_user_save_button);

        values = getResources().getStringArray(R.array.market_value);

        List<String> valueList = new ArrayList<>();
        for (int i = 0; i < values.length; i++){
            valueList.add(values[i]);
        }

        adapter = new MarketSpinnerAdapter(CreateUserActivity.this, valueList);
        userMarket.setAdapter(adapter);

        getBundle();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userName.getText().toString();
                String code = userCode.getText().toString();
                String market = (String) userMarket.getSelectedItem();
                User user = new User();
                user.setId(id);
                user.setName(name);
                user.setCode(code);
                user.setMarket(market);
                SaveUserAsyncTask saveUserAsyncTask = new SaveUserAsyncTask(CreateUserActivity.this);
                saveUserAsyncTask.execute(user);
            }
        });
    }

    private void getBundle(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt(Value.USER_ID);
            String name = bundle.getString(Value.USER_NAME);
            String code = bundle.getString(Value.USER_SH_CODE);
            String market = bundle.getString(Value.USER_MARKET);

            if (name != null && !name.equals("")) {
                userName.setText(name);
            }

            if (code != null && !code.equals("")) {
                userCode.setText(code);
            }

            if (market != null && !market.equals("")) {
                System.out.println("market adapter count = " + userMarket.getAdapter().getCount());
                for (int i = 0; i < values.length; i++){
                    if (market.equals(values[i])){
                        userMarket.setSelection(i, true);
                        break;
                    }
                }
            }
        }
    }

    private class SaveUserAsyncTask extends AsyncTask<User, Integer, Integer> {

        private Context context;

        public SaveUserAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Integer doInBackground(User... params) {
            DatabaseUtils.saveUser(context, params[0]);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Intent intent = new Intent();
            intent.setClass(CreateUserActivity.this, UserManagementActivity.class);
            setResult(Value.CREATE_USER, intent);
            CreateUserActivity.this.finish();
        }
    }

}
