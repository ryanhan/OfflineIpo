package cn.ryanman.app.offlineipo.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.model.AppInfo;


/**
 * Created by Ryan on 2015/12/29.
 */
public class CheckUpdateAsyncTask extends AsyncTask<Void, Integer, AppInfo> {

    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;

    public CheckUpdateAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected AppInfo doInBackground(Void... params) {
        try {
            AppInfo appInfo = WebUtils.getAppInfo();
            return appInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(AppInfo result) {
        if (result == null) {
            Toast.makeText(context, context.getString(R.string.error_retry), Toast.LENGTH_SHORT)
                    .show();
        } else {
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packInfo = packageManager.getPackageInfo(
                        context.getPackageName(), 0);
                int currentVersion = packInfo.versionCode;
                int latestVersion = Integer.parseInt(result.getVersion());

                Log.d("currentVersion", currentVersion+"");
                Log.d("latestVersion", latestVersion+"");

                if (currentVersion < latestVersion) {
                    confirmDownload(result);
                } else {
                    Toast.makeText(context, context.getString(R.string.already_latest_version), Toast.LENGTH_SHORT)
                            .show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getString(R.string.error_retry), Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }

    private void confirmDownload(final AppInfo info) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.download_new_version) + " v" + info.getVersionShort()).setMessage(info.getChangelog())
                .setNegativeButton(context.getString(R.string.cancel), null)
                .setPositiveButton(context.getString(R.string.update), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDownload(info.getUrl());
                        Toast.makeText(context, context.getString(R.string.start_download), Toast.LENGTH_SHORT)
                                .show();
                    }

                }).create();
        alertDialog.show();
    }

    private void startDownload(String url) {
        AppDownloaderAysncTask appDownloader = new AppDownloaderAysncTask();
        appDownloader.execute(url);
    }

    private class AppDownloaderAysncTask extends
            AsyncTask<String, Integer, File> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel chan1 = new NotificationChannel("PRIMARY_CHANNEL",
                        "Primary Channel", NotificationManager.IMPORTANCE_DEFAULT);
                chan1.setLightColor(Color.GREEN);
                chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                notificationManager.createNotificationChannel(chan1);
                mBuilder = new NotificationCompat.Builder(context, "PRIMARY_CHANNEL");
            } else {
                mBuilder = new NotificationCompat.Builder(context, null);
            }
            mBuilder.setContentText(context.getString(R.string.app_name))
                    .setContentTitle(context.getString(R.string.start_download))
                    .setTicker(context.getString(R.string.start_download))
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setOnlyAlertOnce(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setProgress(100, 0, false);
            Notification mNotification = mBuilder.build();
            notificationManager.notify(1, mNotification);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mBuilder.setContentText(context.getString(R.string.downloading) + ": ( " + values[0] + "% )");
            mBuilder.setProgress(100, values[0], false);
            notificationManager.notify(1, mBuilder.build());
            super.onProgressUpdate(values);
        }

        @Override
        protected File doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                int count;

                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int lenghtOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());

                String fileName = "offlineipo.apk";
                File downloadDir = new File(context.getExternalCacheDir(),
                        "app");
                if (!downloadDir.exists()) {
                    downloadDir.mkdir();
                }

                File filePath = new File(downloadDir, fileName);

                FileOutputStream fos = new FileOutputStream(filePath);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) ((total * 100) / lenghtOfFile));
                    fos.write(data, 0, count);
                }
                fos.flush();
                fos.close();
                input.close();
                return filePath;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        }

        @Override
        protected void onPostExecute(File result) {
            if (result == null) {
                mBuilder.setContentText(context.getString(R.string.download_fail));
                mBuilder.setProgress(0, 0, false);
                mBuilder.setOngoing(false);
                notificationManager.notify(1, mBuilder.build());
                Toast.makeText(context, context.getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
            } else {
                mBuilder.setContentText(context.getString(R.string.download_success));
                mBuilder.setProgress(0, 0, false);
                mBuilder.setOngoing(false);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
                String authority = Value.PACKAGENAME + ".provider";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri contentUri = FileProvider.getUriForFile(context, authority, result);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
                }

                notificationManager.notify(1, mBuilder.build());

                Toast.makeText(context, context.getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }
        }
    }
}