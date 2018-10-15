package cn.ryanman.app.offlineipo.main;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.utils.AppUtils;
import cn.ryanman.app.offlineipo.utils.CheckUpdateAsyncTask;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;

/**
 * Created by hanyan on 12/26/2016.
 */

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout exportDB;
    private LinearLayout importDB;
    private LinearLayout checkUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setActionBar();
        exportDB = findViewById(R.id.settings_export_layout);
        importDB = findViewById(R.id.settings_import_layout);
        checkUpdate = findViewById(R.id.settings_check_update_layout);

        exportDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.getPermissions(SettingsActivity.this);
                if (DatabaseUtils.exportMyIpo(SettingsActivity.this)) {
                    Toast.makeText(SettingsActivity.this, getString(R.string.export_suc) + "/OfflineIpo/myipo.db\"!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, getString(R.string.export_fail), Toast.LENGTH_SHORT).show();
                }
            }
        });
        importDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.getPermissions(SettingsActivity.this);
                if (DatabaseUtils.importMyIpo(SettingsActivity.this)) {
                    Toast.makeText(SettingsActivity.this, getString(R.string.import_suc), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, getString(R.string.import_fail) + "/OfflineIpo/myipo.db\"!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckUpdateAsyncTask checkUpdateAsyncTask = new CheckUpdateAsyncTask(SettingsActivity.this);
                checkUpdateAsyncTask.execute();
            }
        });
    }

    private void setActionBar() {
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle(R.string.settings);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
