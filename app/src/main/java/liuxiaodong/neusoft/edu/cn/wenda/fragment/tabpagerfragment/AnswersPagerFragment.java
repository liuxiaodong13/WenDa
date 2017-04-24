package liuxiaodong.neusoft.edu.cn.wenda.fragment.tabpagerfragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;

import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.listener.onAnswerClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.listener.onUserAreaClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.Answer;
import liuxiaodong.neusoft.edu.cn.wenda.model.AnswerWrapper;
import liuxiaodong.neusoft.edu.cn.wenda.other.AnswerWrapperLoader;
import liuxiaodong.neusoft.edu.cn.wenda.other.MediaManager;
import liuxiaodong.neusoft.edu.cn.wenda.ui.LikesBar;
import liuxiaodong.neusoft.edu.cn.wenda.ui.RecordView;
import liuxiaodong.neusoft.edu.cn.wenda.ui.UserIconView;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

/**
 * Created by DONG on 2016/11/1.
 */

public class AnswersPagerFragment extends PagerFragment implements XRecyclerView.LoadingListener {
    public static final String TAG = "AnswersPagerFragment";

    public static final String ANSWER_ID = "answerId";
    @BindView(R.id.recyclerview)
    XRecyclerView recyclerView;
    private SimpleAnswerAdapter adapter;
    private int pageIndex = 1;
    private AnswerWrapperLoader answerWrapperLoader;

    private RecordView mCurrentRecordView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return buildRecyclerViewFragment(inflater, container, savedInstanceState);
    }

    public static AnswersPagerFragment newInstance(String questionId) {
        Bundle bundle = new Bundle();
        bundle.putString(QUESTION_ID, questionId);
        AnswersPagerFragment fragment = new AnswersPagerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected void initData() {
        //查出该条question的所有回答
        answerWrapperLoader = new AnswerWrapperLoader(questionId, listItems, new AnswerWrapperLoader.onCompleteListener() {
            @Override
            public void onLoadComplete(ArrayList<AnswerWrapper> wrappers) {
                recyclerView.refreshComplete();
                adapter.setData(wrappers);
            }

            @Override
            public void onLoadingMoreComplete(ArrayList<AnswerWrapper> wrappers) {
                recyclerView.loadMoreComplete();
                adapter.addData(wrappers);
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView.setLoadingListener(this);
        adapter = new SimpleAnswerAdapter();
        recyclerView.setAdapter(adapter);
    }


    @Subscribe(sticky = true)
    public void onEvent(Event event) {
        if (event.message.equals(Event.REFRESH_ANSWER_LIST)) {
            Logger.d("收到事件！" + event);
            recyclerView.setRefreshing(true);
            initData();
            EventManager.removeAllStickyEvents();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d("AnswersPagerFragment:onStart()");
        EventManager.register(this);
    }

    @Override
    public void onStop() {
        Logger.d("AnswersPagerFragment:onStop()");
        EventManager.unregister(this);
        super.onStop();
    }

    @Override
    public void onRefresh() {
        answerWrapperLoader.reFresh();

    }

    @Override
    public void onLoadMore() {
        answerWrapperLoader.loadMore();

    }

    private void doCancelLike(String objectId) {
        Answer answer = new Answer();
        answer.increment("likescount", -1);
        BmobRelation relation = new BmobRelation();
        relation.remove(getCurrentUser());
        answer.setLikes(relation);
        answer.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                Log.i(TAG, "done: objectId 取消点赞成功");
                //textView.setText(isLike? currentCount + 1 +"" : currentCount - 1 + "");
            }
        });
    }

    private void doLike(String objectId) {
        Answer answer = new Answer();
        answer.increment("likescount");
        BmobRelation relation = new BmobRelation();
        relation.add(getCurrentUser());
        answer.setLikes(relation);
        answer.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                Log.i(TAG, "done: objectId 点赞成功");

            }
        });
    }

    class SimpleAnswerAdapter extends RecyclerView.Adapter<SimpleAnswerAdapter.AnswerViewHolder> {
        private ArrayList<AnswerWrapper> answerWrappers = new ArrayList<>();

        public SimpleAnswerAdapter() {

        }

        public void setData(List<AnswerWrapper> datas) {
            answerWrappers.clear();
            answerWrappers.addAll(datas);
            notifyDataSetChanged();

        }

        public void addData(List<AnswerWrapper> datas) {
            if (datas.size() > 0) {
                answerWrappers.addAll(datas);
                notifyDataSetChanged();
            }
        }

        @Override
        public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AnswerViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.pager_answer_item, parent, false));
        }

        @Override
        public int getItemCount() {
            return answerWrappers.size();
        }

        @Override
        public void onBindViewHolder(final AnswerViewHolder holder, final int position) {
            holder.mRecordView.setVisibility(View.GONE);
            final Answer answer = answerWrappers.get(position).getAnswer();
            Log.i(TAG, "onBindViewHolder: answer" + answer);

            Glide.with(AnswersPagerFragment.this).load(answer.getAuthor().getUserIcon().getFileUrl()).into(holder.civUserIcon.getCivUserIcon());
            holder.civUserIcon.setGender(answer.getAuthor().getGender());

            if (answer.getPics() != null) {

                holder.tvPicsInfo.setText("有" + answer.getPics().size() + "张图片");

            }
            holder.tvNickname.setText(answer.getAuthor().getNick());
            holder.tvCreateTime.setText(answer.getCreatedAt());
            if (answer.getLikescount() != null) {
                holder.likesBar.setTvLikesCount(answerWrappers.get(position).getAnswer().getLikescount().toString());

            }
            //设置点赞图标状态
            holder.likesBar.setState(answer.getLikescount(), answerWrappers.get(position).isLike());
            //点赞
            holder.likesBar.setOnLikeStateChangedListener(new LikesBar.onLikeStateChangedListener() {
                @Override
                public void onLike() {
                    doLike(answer.getObjectId());
                }

                @Override
                public void onCancelLike() {
                    doCancelLike(answer.getObjectId());
                }
            });
            //
            holder.tvAnswerContent.setText(answer.getContent());
            holder.cvAnswerItem.setOnClickListener(new onAnswerClickListener(answerWrappers.get(position).getAnswer().getObjectId(), mActivity));
            holder.civUserIcon.setOnClickListener(new onUserAreaClickListener(mActivity, answerWrappers.get(position).getAnswer().getAuthor().getObjectId(), answerWrappers.get(position).getAnswer().getAuthor().getNick()));

            //如果有录音 设置图标显示
            if (answer.getRecord() != null) {
                holder.mRecordView.setVisibility(View.VISIBLE);
                holder.mRecordView.setTime(answer.getRecordTime());
                holder.mRecordView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCurrentRecordView != null) {
                            mCurrentRecordView.stopAnim();
                            mCurrentRecordView = null;
                        }
                        mCurrentRecordView = holder.mRecordView;
                        holder.mRecordView.startAnim();
                        MediaManager.playNetSound(answer.getRecord(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                holder.mRecordView.stopAnim();
                                mCurrentRecordView = null;

                            }
                        });
                    }
                });
            }
        }

        class AnswerViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.uiv_user_icon)
            UserIconView civUserIcon;
            @BindView(R.id.tv_nickname)
            TextView tvNickname;
            @BindView(R.id.tv_create_time)
            TextView tvCreateTime;
            @BindView(R.id.ll_user)
            LinearLayout llUser;
            @BindView(R.id.tv_answer_content)
            TextView tvAnswerContent;
            @BindView(R.id.tv_pics_info)
            TextView tvPicsInfo;
            @BindView(R.id.cv_answer_item)
            CardView cvAnswerItem;
            @BindView(R.id.lb_likesbar)
            LikesBar likesBar;
            @BindView(R.id.rv_Record)
            RecordView mRecordView;

            public AnswerViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

        }
    }


}
