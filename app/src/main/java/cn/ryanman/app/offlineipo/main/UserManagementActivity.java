package cn.ryanman.app.offlineipo.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.adapter.UserListAdapter;
import cn.ryanman.app.offlineipo.model.User;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by hanyan on 12/26/2016.
 */

public class UserManagementActivity extends AppCompatActivity {

    private ListView userListView;
    private UserListAdapter adapter;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        users = new ArrayList<>();

        adapter = new UserListAdapter(this, users);
        userListView = (ListView) findViewById(R.id.user_list);
        userListView.setAdapter(adapter);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.adapter_user_footer, null);
        userListView.addFooterView(view);

        LinearLayout createUserLayout = (LinearLayout) view.findViewById(R.id.adapter_create_user);

        createUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(UserManagementActivity.this,
                        CreateUserActivity.class);
                startActivityForResult(intent, Value.CREATE_USER);
            }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                final int index = (int) id;
                String items[] = {getResources().getString(R.string.change_user), getResources().getString(R.string.delete_user)};
                new AlertDialog.Builder(UserManagementActivity.this).setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Intent intent = new Intent();
                                intent.setClass(UserManagementActivity.this,
                                        CreateUserActivity.class);
                                intent.putExtra(Value.USER_ID, users.get(index).getId());
                                intent.putExtra(Value.USER_NAME, users.get(index).getName());
                                intent.putExtra(Value.USER_SH_CODE, users.get(index).getCode());
                                intent.putExtra(Value.USER_MARKET, users.get(index).getMarket());
                                startActivityForResult(intent, Value.CREATE_USER);
                                break;
                            case 1:
                                break;
                        }
                    }
                }).show();
            }
        });

        GetUserAsyncTask getUserAsyncTask = new GetUserAsyncTask(UserManagementActivity.this);
        getUserAsyncTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Value.CREATE_USER){
            GetUserAsyncTask getUserAsyncTask = new GetUserAsyncTask(UserManagementActivity.this);
            getUserAsyncTask.execute();
        }
    }

    private class GetUserAsyncTask extends AsyncTask<Void, Integer, List<User>> {

        private Context context;

        public GetUserAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<User> doInBackground(Void... voids) {
            return DatabaseUtils.getUserList(context);
        }

        @Override
        protected void onPostExecute(List<User> result) {
            users.clear();
            users.addAll(result);
            adapter.notifyDataSetChanged();
        }
    }
}
