package cn.ryanman.app.offlineipo.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.adapter.IpoListAdapter;
import cn.ryanman.app.offlineipo.listener.OnViewReloadListener;
import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryanh on 2016/11/25.
 */

public class IpoListFragment extends Fragment {

    private ListView ipoListView;
    private IpoListAdapter ipoListAdapter;
    private List<IpoItem> ipoList;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ipo_list, container, false);
        setHasOptionsMenu(true);

        ipoListView = view.findViewById(R.id.all_ipo_list);
        ipoList = new ArrayList<>();

        ipoListAdapter = new IpoListAdapter(this.getActivity(), ipoList);
        ipoListView.setAdapter(ipoListAdapter);
        ipoListView.setTextFilterEnabled(true);

        ipoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(IpoListFragment.this.getActivity(),
                        IpoDetailActivity.class);
                IpoItem ipoItem = (IpoItem) ipoListAdapter.getItem(position);
                intent.putExtra(Value.IPO_NAME, ipoItem.getName());
                intent.putExtra(Value.IPO_CODE, ipoItem.getCode());
                startActivity(intent);
            }
        });

        ipoListAdapter.setOnViewReloadListener(new OnViewReloadListener() {
            @Override
            public void reload(Object object) {
                updateList();
            }
        });

        return view;
    }

    private void updateList(){
        ipoList.clear();
        ipoList.addAll(DatabaseUtils.getIpoList(this.getActivity()));
        ipoListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search_view, menu);

        //找到searchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.hint_white));//设置提示文字颜色
        searchAutoComplete.setTextColor(getResources().getColor(android.R.color.white));//设置内容文字颜色

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ipoListAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
        getFocus();
    }

    private void getFocus() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!searchView.isIconified()) {
                        searchView.onActionViewCollapsed();
                    } else {
                        IpoListFragment.this.getActivity().finish();
                    }
                    return true;
                }
                return false;
            }
        });
    }

}
