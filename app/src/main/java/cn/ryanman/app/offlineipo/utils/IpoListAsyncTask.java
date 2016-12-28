package cn.ryanman.app.offlineipo.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import cn.ryanman.app.offlineipo.listener.OnDataLoadCompletedListener;
import cn.ryanman.app.offlineipo.model.IpoItem;

/**
 * Created by ryan on 2016/11/25.
 */

public class IpoListAsyncTask extends AsyncTask<Void, Integer, List<IpoItem>> {

    private Context context;
    private OnDataLoadCompletedListener onDataLoadCompletedListener;

    public IpoListAsyncTask(Context context) {
        this.context = context;
    }

    public void setOnDataLoadCompletedListener(OnDataLoadCompletedListener onDataLoadCompletedListener) {
        this.onDataLoadCompletedListener = onDataLoadCompletedListener;
    }

    @Override
    protected List<IpoItem> doInBackground(Void... params) {
        try {
            return WebUtils.getIpoItems();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<IpoItem> result) {
        if (result != null && result.size() > 0) {
            DatabaseUtils.insertIpoList(context, result);
            IpoScheduleAsyncTask ipoScheduleAsyncTask = new IpoScheduleAsyncTask(context);
            ipoScheduleAsyncTask.setOnDataLoadCompletedListener(new OnDataLoadCompletedListener() {
                @Override
                public void onDataSuccessfully(Object object) {
                    onDataLoadCompletedListener.onDataSuccessfully(null);
                }

                @Override
                public void onDataFailed() {
                    onDataLoadCompletedListener.onDataFailed();
                }
            });
            ipoScheduleAsyncTask.execute();

        } else {
            if (onDataLoadCompletedListener != null) {
                onDataLoadCompletedListener.onDataFailed();
            }
        }
    }

}