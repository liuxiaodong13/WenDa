package liuxiaodong.neusoft.edu.cn.wenda.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.activity.MainActivity;
import liuxiaodong.neusoft.edu.cn.wenda.adapter.NotificationFragmentAdapter;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseFragment;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.model.Notification;
import liuxiaodong.neusoft.edu.cn.wenda.utils.AppUtils;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

/**
 * Created by DONG on 2016/10/19.
 */

public class NotificationFragment extends BaseFragment implements XRecyclerView.LoadingListener {
    public static final String TAG = "NotificationFragment";
    @BindView(R.id.recyclerview)
    XRecyclerView recyclerView;
    private NotificationFragmentAdapter adapter;
    private int pageIndex = 1;


    private MainActivity mainActivity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) mActivity;


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return buildRecyclerViewFragment(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mainActivity.hideFab();
        EventManager.register(this);
    }

    @Override
    public void onStop() {
        EventManager.unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true)
    public void onEvent(Event event) {
        if (event.message.equals(Event.REFRESH_NOTIFICATIONS)) {
            Logger.d("收到事件：更新通知列表" + event);
            recyclerView.setRefreshing(true);
            onRefresh();
            EventManager.removeAllStickyEvents();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mainActivity.hideFab();

        }
    }

    @Override
    protected void initView() {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingListener(this);
        adapter = new NotificationFragmentAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setRefreshing(true);
    }

    @Override
    protected void initData() {

        BmobConn.queryAll(Notification.class, new FindListener<Notification>() {
            @Override
            public void done(List<Notification> list, BmobException e) {
                recyclerView.refreshComplete();
                if (e == null) {
                    if (list != null) {
                        adapter.setData(list);
                        Log.d(TAG, "done: " + list);
                    }

                } else {
                    Log.e(TAG, "done: " + e);
                }
            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                query.setLimit(listItems);
                query.order("-createdAt");
                query.addWhereEqualTo("user", new BmobPointer(AppUtils.getCurrentUser()));

            }
        });

    }

    @Override
    public void onRefresh() {
        pageIndex = 1;
        initData();

    }

    @Override
    public void onLoadMore() {

        BmobConn.queryAll(Notification.class, new FindListener<Notification>() {
            @Override
            public void done(List<Notification> list, BmobException e) {
                recyclerView.loadMoreComplete();
                if (e == null) {
                    pageIndex++;
                    adapter.addData(list);
                } else {
                    Log.e(TAG, "done: " + e);
                }

            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                query.setSkip(pageIndex * listItems);
                query.setLimit(listItems);
                query.addWhereEqualTo("user", new BmobPointer(AppUtils.getCurrentUser()));
            }
        });

    }


}
