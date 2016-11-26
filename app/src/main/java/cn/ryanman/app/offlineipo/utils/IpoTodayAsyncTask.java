package cn.ryanman.app.offlineipo.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import cn.ryanman.app.offlineipo.listener.OnDataLoadCompletedListener;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoToday;

/**
 * Created by ryan on 2016/11/25.
 */

public class IpoTodayAsyncTask extends AsyncTask<Void, Integer, IpoToday> {

    private Context context;
    private OnDataLoadCompletedListener onDataLoadCompletedListener;

    public IpoTodayAsyncTask(Context context) {
        this.context = context;
    }

    public void setOnDataLoadCompletedListener(OnDataLoadCompletedListener onDataLoadCompletedListener) {
        this.onDataLoadCompletedListener = onDataLoadCompletedListener;
    }

    @Override
    protected IpoToday doInBackground(Void... params) {
        try {
            return WebUtils.getIpoToday();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(IpoToday result) {
        if (result != null) {
            DatabaseUtils.insertIpoToday(context, result);
            if (onDataLoadCompletedListener != null)
                onDataLoadCompletedListener.onDataSuccessfully();
        } else {
            if (onDataLoadCompletedListener != null)
                onDataLoadCompletedListener.onDataFailed();
        }
    }

}