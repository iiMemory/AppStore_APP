package com.yj.appstore.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yj.appstore.Contract;
import com.yj.appstore.R;
import com.yj.appstore.adapter.AppListAdapter;
import com.yj.appstore.fragment.SettingFragment;
import com.yj.appstore.fragment.TitleFragment;
import com.yj.appstore.model.bean.App;
import com.yj.appstore.presenter.AppListPresenterImpl;
import com.yj.appstore.util.ToastUtil;
import com.yj.appstore.v.recycle.YJRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseTitleActivity implements Contract.AppListView, SwipeRefreshLayout.OnRefreshListener, YJRecyclerView.LoadMoreListener {
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    Contract.AppListPresenter appListPresenter;
    AppListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // init
        setTitle("应用列表", false);
        setSetting();
        srl.setOnRefreshListener(this);
        appListPresenter = new AppListPresenterImpl(this);
        adapter = new AppListAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        // listener
        setListener();

        appListPresenter.refresh();
    }

    private void setSetting() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transition = manager.beginTransaction();
        transition.replace(R.id.fl_setting, SettingFragment.newInstance());
        transition.commitAllowingStateLoss();
    }


    private void setListener() {
        adapter.setClickListenerImpl(new AppListAdapter.IClickListener() {
            @Override
            public void onClick(String packageId) {
                AppInfoActivity.startAppInfoActivity(MainActivity.this, packageId
                );
            }
        });
    }

    @Override
    public void onRefresh() {
        appListPresenter.refresh();
    }

    @Override
    public void showLoading() {
        srl.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        srl.setRefreshing(false);
    }

    @Override
    public void onRefreshSuccess(List<App> data) {
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshFailure(String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void onLoadMoreSuccess(List<App> data) {
        adapter.addData(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMoreFailure(String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void onLoadMore() {
        appListPresenter.loadMore();
    }
}
