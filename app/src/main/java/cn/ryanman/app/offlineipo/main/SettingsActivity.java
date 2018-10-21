package cn.ryanman.app.offlineipo.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.utils.CheckUpdateAsyncTask;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;

/**
 * Created by hanyan on 12/26/2016.
 */

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout exportDB;
    private LinearLayout importDB;
    private LinearLayout checkUpdate;
    private final int EXPORT = 1;
    private final int IMPORT = 2;

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
                if (checkPermissions(EXPORT)) {
                    exportAndImport(true);
                }
            }
        });
        importDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissions(IMPORT)) {
                    exportAndImport(false);
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
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            if (requestCode == EXPORT) {
                exportAndImport(true);
            } else if (requestCode == IMPORT) {
                exportAndImport(false);
            }
        }
        //这里实现用户操作，或同意或拒绝的逻辑
        /*grantResults会传进android.content.pm.PackageManager.PERMISSION_GRANTED 或 android.content.pm.PackageManager.PERMISSION_DENIED两个常，前者代表用户同意程序获取系统权限，后者代表用户拒绝程序获取系统权限*/
    }

    private boolean checkPermissions(int requestCode) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            return false;
        } else {
            return true;
        }
    }

    private void exportAndImport(boolean isExport) {
        if (isExport) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.export_database)).setMessage(getString(R.string.export_warning))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DatabaseUtils.exportMyIpo(SettingsActivity.this)) {
                                Toast.makeText(SettingsActivity.this, getString(R.string.export_suc) + "/OfflineIpo/myipo.db\"!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, getString(R.string.export_fail), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }).create();
            alertDialog.show();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.import_database)).setMessage(getString(R.string.import_warning))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DatabaseUtils.importMyIpo(SettingsActivity.this)) {
                                Toast.makeText(SettingsActivity.this, getString(R.string.import_suc), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, getString(R.string.import_fail) + "/OfflineIpo/myipo.db\"!", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }).create();
            alertDialog.show();
        }
    }
}
