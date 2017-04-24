package liuxiaodong.neusoft.edu.cn.wenda.fragment;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import liuxiaodong.neusoft.edu.cn.wenda.adapter.UserCollectListAdapter;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.base.ListBaseFragment;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

/**
 * Created by DONG on 2016/10/19.
 */

public class FavoriteFragment extends ListBaseFragment {
    private UserCollectListAdapter adapter;

    private int pageIndex = 1;

    @Override
    public void onStart() {
        super.onStart();
        EventManager.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventManager.unregister(this);
    }

    @Subscribe(sticky = true)
    public void onEvent(Event event) {
        if (event.message.equals(Event.REFRESH_USER_RELEVANT_LIST))
        {
            initData();
            EventManager.removeAllStickyEvents();
        }

    }

    @Override
    public void setAdapter() {
        recyclerView.setAdapter(adapter = new UserCollectListAdapter(activity));

    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onLoadMore() {
        BmobConn.queryAllFavoriteWithSkip(APP.getUserCollectId(), new FindListener<Question>() {
            @Override
            public void done(List<Question> list, BmobException e) {
                recyclerView.loadMoreComplete();
                if (e == null) {
                    if (list.size() > 0)
                        pageIndex++;
                    adapter.addData(list);
                } else {
                    Logger.e(e.toString());
                }

            }
        }, pageIndex * pageItems);
    }

    @Override
    protected void initData() {
        pageIndex = 1;
        Logger.d("FavoriteFragment:initData()");
        FindListener<Question> listener = new FindListener<Question>() {
            @Override
            public void done(List<Question> list, BmobException e) {
                recyclerView.refreshComplete();
                if (e == null) {
                    Logger.d("收藏夹数量：" + list.size());
                    adapter.setData(list);
                } else {
                    Logger.e(e.toString());
                }

            }
        };

        BmobConn.queryAllFavorite(APP.getUserCollectId(), listener);
    }
}
