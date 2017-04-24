package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.adapter.HomeAdapter;
import liuxiaodong.neusoft.edu.cn.wenda.adapter.UserConcernListAdapter;
import liuxiaodong.neusoft.edu.cn.wenda.adapter.UserQuestionListAdapter;
import liuxiaodong.neusoft.edu.cn.wenda.base.OnEventBackNavigationActivity;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.other.AppSettings;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

import static liuxiaodong.neusoft.edu.cn.wenda.event.Event.REFRESH_USER_RELEVANT_LIST;

/**
 * Created by DONG on 2016/11/10.
 */

public class RelevantListActivity extends OnEventBackNavigationActivity implements XRecyclerView.LoadingListener {
    private String[] titles = {"提问", "关注"};
    //    private String[] titles = {"提问", "关注", "收藏"};
    public static final String TITLE_INDEX = "title_index";
    public static final String OBJECT_ID = "objectId";
    public static final String USER_NICK = "usernick";
    private int index;
    private String userId;
    private String userNick;
    @BindView(R.id.recyclerview)
    XRecyclerView recyclerview;
    private HomeAdapter adapter;
    private int pageIndex = 1;
    private int listCounts = AppSettings.getListItemCounts();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildActivity(R.layout.activity_relevant_list);
    }

    @Override
    public void onEvent(Event event) {
        if (event.message.equals(REFRESH_USER_RELEVANT_LIST)) {

            Logger.d("收到事件:" + event);
            initData();
        }
    }

    @Override
    protected void getExtraData() {
        index = getIntent().getIntExtra(TITLE_INDEX, 0);
        userId = getIntent().getStringExtra(OBJECT_ID);
        userNick = getIntent().getStringExtra(USER_NICK);
    }

    @Override
    protected void initData() {
        FindListener<Question> listener = new FindListener<Question>() {
            @Override
            public void done(List<Question> list, BmobException e) {
                if (e == null) {
                    adapter.setData(list);
                    setAdapter();
                } else {
                    Log.i(TAG, "done: " + e);
                }

            }
        };
        switch (index) {
            //提问
            case 0:
                BmobConn.queryAllQuestions(userId, listener, 0);
                break;
            //关注
            case 1:
                BmobConn.queryConcernedQuestions(listener, 0);
                break;
//            //收藏
//            case 2:
//                BmobConn.queryCollectsFromUserId(userId, listener);
//                break;
        }

    }

    private void setAdapter() {
        recyclerview.setAdapter(adapter);
    }

    @Override
    protected void initToolbar() {
        initTitle(userNick + " 的" + titles[index]);
    }

    @Override
    protected void initView() {

        recyclerview.setLoadingMoreEnabled(true);
        recyclerview.setLoadingListener(this);
        recyclerview.setPullRefreshEnabled(false);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        switch (index) {
            //提问
            case 0:
//                recyclerview.setAdapter(adapter = new UserQuestionListAdapter(this));
                adapter = new UserQuestionListAdapter(this);
                break;
            //关注
            case 1:
//                recyclerview.setAdapter(adapter = new UserConcernListAdapter(this));
                adapter = new UserConcernListAdapter(this);
                break;

        }


    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

        FindListener<Question> listener = new FindListener<Question>() {
            @Override
            public void done(List<Question> list, BmobException e) {
                recyclerview.loadMoreComplete();
                if (e == null) {
                    if (list.size() > 0)
                        pageIndex++;
                    adapter.addData(list);

                } else {
                    Log.i(TAG, "done: " + e);
                }

            }
        };
        switch (index) {
            //提问
            case 0:
////                recyclerview.setAdapter(adapter = new UserQuestionListAdapter(this));
//                adapter = new UserQuestionListAdapter(this);

                BmobConn.queryAllQuestions(userId, listener, listCounts * pageIndex);
                break;
            //关注
            case 1:

                BmobConn.queryConcernedQuestions(listener, listCounts * pageIndex);

                break;

        }

    }
}
