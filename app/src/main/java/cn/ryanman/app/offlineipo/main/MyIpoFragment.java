package cn.ryanman.app.offlineipo.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.ryanman.app.offlineipo.R;

/**
 * Created by ryanh on 2016/11/25.
 */

public class MyIpoFragment extends Fragment {

    private LinearLayout userLayout;
    private LinearLayout myIpoLayout;
    private LinearLayout profitLayout;
    private LinearLayout checkUpdateLayout;
    private LinearLayout aboutLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_ipo, container, false);
        userLayout = (LinearLayout) view.findViewById(R.id.my_ipo_user_layout);
        myIpoLayout = (LinearLayout) view.findViewById(R.id.my_ipo_layout);
        profitLayout = (LinearLayout) view.findViewById(R.id.my_ipo_profit_layout);
        checkUpdateLayout = (LinearLayout) view.findViewById(R.id.my_ipo_check_update_layout);
        aboutLayout = (LinearLayout) view.findViewById(R.id.my_ipo_about_layout);

        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MyIpoFragment.this.getActivity(),
                        UserManagementActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

}
