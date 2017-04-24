package liuxiaodong.neusoft.edu.cn.wenda.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import butterknife.BindView;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.activity.MainActivity;
import liuxiaodong.neusoft.edu.cn.wenda.other.AppSettings;

/**
 * Created by DONG on 2017/3/20.
 */

public abstract class ListBaseFragment extends BaseFragment implements XRecyclerView.LoadingListener {
    @BindView(R.id.recyclerview)
    protected XRecyclerView recyclerView;
    protected MainActivity activity;
    protected int pageItems = AppSettings.getListItemCounts();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity instanceof MainActivity)
        activity  = (MainActivity) mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return buildRecyclerViewFragment(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.hideFab();
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingListener(this);
        setAdapter();
//        recyclerView.setRefreshing(true);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.hideFab();

        }
    }

    public abstract void setAdapter();

}
