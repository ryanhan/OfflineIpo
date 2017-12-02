package cn.ryanman.app.offlineipo.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import cn.ryanman.app.offlineipo.listener.OnDataLoadCompletedListener;
import cn.ryanman.app.offlineipo.model.IpoToday;

/**
 * Created by ryan on 2016/11/25.
 */

public class IpoTodayAsyncTask extends AsyncTask<String, Integer, List<IpoToday>> {

    private Context context;
    private OnDataLoadCompletedListener onDataLoadCompletedListener;

    public IpoTodayAsyncTask(Context context) {
        this.context = context;
    }

    public void setOnDataLoadCompletedListener(OnDataLoadCompletedListener onDataLoadCompletedListener) {
        this.onDataLoadCompletedListener = onDataLoadCompletedListener;
    }

    @Override
    protected List<IpoToday> doInBackground(String... params) {
        try {
            return WebUtils.getIpoToday(params[0]);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<IpoToday> result) {
        if (result != null) {
            DatabaseUtils.insertIpoTodayList(context, result);
            if (onDataLoadCompletedListener != null)
                onDataLoadCompletedListener.onDataSuccessfully(null);
        } else {
            if (onDataLoadCompletedListener != null)
                onDataLoadCompletedListener.onDataFailed();
        }
    }

}