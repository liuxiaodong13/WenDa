package liuxiaodong.neusoft.edu.cn.wenda.fragment.tabpagerfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.listener.onUserAreaClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.ui.UserIconView;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;


/**
 * Created by DONG on 2016/11/1.
 */

public class ConcernPagerFragment extends OnEventPagerFragment implements XRecyclerView.LoadingListener {
    public static final String TAG = "ConcernPagerFragment";
    public static final String USER_ID = "userId";
    private int pageIndex = 1;
    private ConcernAdapter adapter;


    public static ConcernPagerFragment newInstance(String questionId) {
        Bundle bundle = new Bundle();
        bundle.putString(QUESTION_ID, questionId);
        ConcernPagerFragment fragment = new ConcernPagerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return buildRecyclerViewFragment(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        adapter = new ConcernAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadingListener(this);
    }

    @Override
    public boolean onEvent(Event stickyEvent) {
        if (stickyEvent.message.equals(Event.REFRESH_CONCERN_LIST)) {
            Logger.d("收到刷新concern事件:" + stickyEvent);
            recyclerView.setRefreshing(true);
            initData();
            return true;
        }
        return false;
    }

    @Override
    protected void initData() {

        //获取所有关注该问题的用户
        pageIndex = 1;
        BmobConn.queryAll(MyUser.class, new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null) {
                    recyclerView.refreshComplete();
                    adapter.setData(list);
                    Log.i(TAG, "done: concernAdapterSIze:" + list.size());
                } else {
                    Log.i(TAG, "done: " + e);
                }
            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Question question = new Question();

                question.setObjectId(questionId);
                query.setLimit(listItems);
                query.include("author");
                query.addWhereRelatedTo("concerned", new BmobPointer(question));
            }
        });
    }

    @Override
    public void onRefresh() {
        initData();

    }

    @Override
    public void onLoadMore() {
        BmobConn.queryAll(MyUser.class, new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0)
                        pageIndex++;
                    recyclerView.loadMoreComplete();
                    adapter.addData(list);
                } else {
                    Log.i(TAG, "done: " + e);
                }
            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Question question = new Question();
                question.setObjectId(questionId);
                query.setSkip(pageIndex * listItems);
                query.setLimit(listItems);
                query.include("author");
                query.addWhereRelatedTo("concerned", new BmobPointer(question));
            }
        });

    }

    class ConcernAdapter extends RecyclerView.Adapter<ConcernAdapter.ConcernViewHolder> {

        private ArrayList<MyUser> users = new ArrayList<>();

        public void setData(List<MyUser> datas) {
            if (datas.size() > 0) {
                users.clear();
                users.addAll(datas);
                notifyDataSetChanged();
            }
        }

        public void addData(List<MyUser> datas) {
            if (datas.size() > 0) {
                users.addAll(datas);
                notifyDataSetChanged();
            }
        }

        @Override
        public ConcernViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ConcernViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.userinfo_item_bar, parent, false));
        }

        @Override
        public void onBindViewHolder(final ConcernViewHolder holder, final int position) {
            holder.tvCreateTime.setVisibility(View.GONE);

            Glide.with(ConcernPagerFragment.this).load(users.get(position).getUserIcon().getFileUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.civUserIcon.getCivUserIcon());

            holder.civUserIcon.setGender(users.get(position).getGender());
            holder.tvNickname.setText(users.get(position).getNick());
            holder.ll_item.setOnClickListener(new onUserAreaClickListener(mActivity, users.get(position).getObjectId(), users.get(position).getNick()));

        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class ConcernViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.uiv_user_icon)
            UserIconView civUserIcon;
            @BindView(R.id.tv_nickname)
            TextView tvNickname;
            @BindView(R.id.tv_create_time)
            TextView tvCreateTime;
            @BindView(R.id.ll_item)
            LinearLayout ll_item;

            public ConcernViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }


    }


}
