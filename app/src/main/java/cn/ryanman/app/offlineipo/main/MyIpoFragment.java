package cn.ryanman.app.offlineipo.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ryanman.app.offlineipo.R;

/**
 * Created by ryanh on 2016/11/25.
 */

public class MyIpoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_ipo, container, false);
    }

}
