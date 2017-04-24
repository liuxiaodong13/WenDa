package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.adapter.DraftAdapter;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.base.BackNavigationActivity;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.db.MySQLiteHelper;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.model.Draft;
import liuxiaodong.neusoft.edu.cn.wenda.model.DraftWrapper;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

/**
 * Created by DONG on 2017/1/15.
 */

public class DraftActivity extends BackNavigationActivity {
    @BindView(R.id.recyclerview)
    XRecyclerView recyclerView;
    private DraftAdapter adapter;
    private MySQLiteHelper sqLiteHelper;
    private ArrayList<DraftWrapper> mDraftWrappers;
    private AtomicInteger mCountFlag = new AtomicInteger(0);

    public interface onCompleteListener {
        void onComplete();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildActivity(R.layout.activity_draft);
        EventManager.register(this);


    }

    @Subscribe(sticky = true)
    public void onEvent(Event event) {
        if (event.message.equals(Event.REFRESH_DRAFTS)) {
            Logger.d("收到事件：" + event);
            initData();
            EventManager.removeAllStickyEvents();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EventManager.unregister(this);

        super.onDestroy();

    }

    @Override
    protected void globalInit() {
        sqLiteHelper = APP.getInstance().getSqLiteHelper();
    }

    @Override
    protected void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setIsnomore(false);

        adapter = new DraftAdapter(activity);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        //Logger.d(queryALlDrafts().size() + "");
        mDraftWrappers = convert(queryALlDrafts());
        adapter.setData(mDraftWrappers);
        queryRestData(new onCompleteListener() {
            @Override
            public void onComplete() {
                adapter.updateData(mDraftWrappers);
            }
        });

    }

    /**
     * 查询 draft 类型为 answer 的原提问详情
     *
     * @param onCompleteListener
     * @return
     */
    private void queryRestData(onCompleteListener onCompleteListener) {
        mCountFlag.set(0);
        if (mDraftWrappers.size() > 0)
            for (int i = 0; i < mDraftWrappers.size(); i++) {
                if (mDraftWrappers.get(i).getDraft().getType() == Draft.Type.ANSWER) {
                    doQuery(i, mDraftWrappers.get(i).getDraft().getQuestionId(), onCompleteListener);
                }
            }

    }

    private void doQuery(final int index, String questionId, final onCompleteListener onCompleteListener) {
        BmobConn.queryObject(Question.class, questionId, new QueryListener<Question>() {
            @Override
            public void done(Question question, BmobException e) {
                if (e == null) {
                    DraftWrapper wrapper = mDraftWrappers.get(index);
                    wrapper.setQuestion(question);
                    mDraftWrappers.set(index, wrapper);

                    mCountFlag.incrementAndGet();
                    if (mCountFlag.get() == mDraftWrappers.size()) {
                        if (onCompleteListener != null) {
                            onCompleteListener.onComplete();
                        }

                    }

                } else {
                    BmobConn.parseBmobException(e, activity);
                }


            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                query.include("author,category");
            }
        });

    }

    @Override
    protected void initToolbar() {
        initTitle("草稿箱");
    }

    private ArrayList<Draft> queryALlDrafts() {

        return sqLiteHelper.queryALlDrafts();
    }


    private ArrayList<DraftWrapper> convert(ArrayList<Draft> drafts) {
        ArrayList<DraftWrapper> wrappers = new ArrayList<>(drafts.size());
        for (int i = 0; i < drafts.size(); i++) {
            wrappers.add(i, new DraftWrapper(drafts.get(i), null));
        }


        return wrappers;
    }


}

