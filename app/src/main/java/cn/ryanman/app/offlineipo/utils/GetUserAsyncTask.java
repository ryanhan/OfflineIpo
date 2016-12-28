package cn.ryanman.app.offlineipo.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import cn.ryanman.app.offlineipo.listener.OnDataLoadCompletedListener;
import cn.ryanman.app.offlineipo.model.User;


/**
 * Created by ryanh on 2016/12/28.
 */


public class GetUserAsyncTask extends AsyncTask<Void, Integer, List<User>> {

    private Context context;
    private OnDataLoadCompletedListener onDataLoadCompletedListener;


    public GetUserAsyncTask(Context context) {
        this.context = context;
    }

    public void setOnDataLoadCompletedListener(OnDataLoadCompletedListener onDataLoadCompletedListener) {
        this.onDataLoadCompletedListener = onDataLoadCompletedListener;
    }

    @Override
    protected List<User> doInBackground(Void... voids) {
        return DatabaseUtils.getUserList(context);
    }

    @Override
    protected void onPostExecute(List<User> result) {

        if (onDataLoadCompletedListener != null) {
            if (result == null) {
                onDataLoadCompletedListener.onDataFailed();
            } else {
                onDataLoadCompletedListener.onDataSuccessfully(result);
            }

        }
    }
}