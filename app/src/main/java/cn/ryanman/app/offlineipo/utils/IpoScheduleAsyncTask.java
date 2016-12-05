package cn.ryanman.app.offlineipo.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import cn.ryanman.app.offlineipo.listener.OnDataLoadCompletedListener;
import cn.ryanman.app.offlineipo.model.IpoItem;

/**
 * Created by hanyan on 12/5/2016.
 */

public class IpoScheduleAsyncTask extends AsyncTask<Void, Integer, List<IpoItem>> {

    private Context context;
    private OnDataLoadCompletedListener onDataLoadCompletedListener;

    public IpoScheduleAsyncTask(Context context) {
        this.context = context;
    }

    public void setOnDataLoadCompletedListener(OnDataLoadCompletedListener onDataLoadCompletedListener) {
        this.onDataLoadCompletedListener = onDataLoadCompletedListener;
    }

    @Override
    protected List<IpoItem> doInBackground(Void... params) {
        try {
            return WebUtils.getIpoSchedule();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<IpoItem> result) {
        if (result != null && result.size() > 0) {
            DatabaseUtils.updateIpoSchedule(context, result);
            if (onDataLoadCompletedListener != null)
                onDataLoadCompletedListener.onDataSuccessfully();
        } else {
            if (onDataLoadCompletedListener != null)
                onDataLoadCompletedListener.onDataFailed();
        }
    }

}
