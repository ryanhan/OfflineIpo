package cn.ryanman.app.offlineipo.main;

import android.content.Intent;
import android.os.Bundle;
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

            }
        });
    }

}
