package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.adapter.HomeFragmentPhotoAdapter;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.base.OnEventBackNavigationActivity;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.listener.onAnswerLongClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.listener.base.onQuestionItemClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.listener.onUserAreaClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.Answer;
import liuxiaodong.neusoft.edu.cn.wenda.other.AppSettings;
import liuxiaodong.neusoft.edu.cn.wenda.ui.MyRecyclerView;
import liuxiaodong.neusoft.edu.cn.wenda.ui.UserIconView;
import liuxiaodong.neusoft.edu.cn.wenda.utils.DefaultValue;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

/**
 * Created by DONG on 2016/11/10.
 */

public class AnswerListActivity extends OnEventBackNavigationActivity implements XRecyclerView.LoadingListener {
    public static final String TAG = "AnswerListActivity";
    @BindView(R.id.recyclerview)
    XRecyclerView recyclerview;
    private AnswerAdapter adapter;

    private int pageIndex = 1;
    private int listCounts = AppSettings.getListItemCounts();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildActivity(R.layout.activity_relevant_list);
    }

    @Override
    public void onEvent(Event event) {
        if (event.message.equals(Event.REFRESH_USER_ANSWER_LIST)) {
            Logger.d("收到事件:" + event);
            initData();

        }


    }

    @Override
    protected void initToolbar() {
        initTitle(APP.getUserName() + "的回答");

    }

    @Override
    protected void initData() {
        BmobConn.queryAllUserAnswers(getCurrentUser().getObjectId(), new FindListener<Answer>() {
            @Override
            public void done(List<Answer> list, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: answerListSize:" + list.size());
                    adapter.setData(list);

                } else {
                    Log.i(TAG, "done: " + e);
                }
            }
        }, 0);

    }

    @Override
    protected void initView() {
        recyclerview.setLoadingMoreEnabled(true);
        recyclerview.setPullRefreshEnabled(false);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setLoadingListener(this);
        adapter = new AnswerAdapter();
        recyclerview.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        Logger.i("listcounts:" + listCounts);
        Logger.i("pageindex:" + pageIndex);
        Logger.i("skipCounts:" + pageIndex * listCounts);
        BmobConn.queryAllUserAnswers(getCurrentUser().getObjectId(), new FindListener<Answer>() {
            @Override
            public void done(List<Answer> list, BmobException e) {
                recyclerview.loadMoreComplete();
                if (e == null) {
                    if (list.size() > 0)
                        pageIndex++;
                    Log.i(TAG, "done: answerListSize:" + list.size());
                    adapter.addData(list);

                } else {
                    Log.i(TAG, "done: " + e);
                }
            }
        }, pageIndex * listCounts);

    }

    class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {


        private ArrayList<Answer> answers = new ArrayList<>();

        public void setData(List<Answer> data) {
            answers.clear();
            answers.addAll(data);
            notifyDataSetChanged();

        }

        public void addData(List<Answer> data) {
            answers.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AnswerViewHolder(LayoutInflater.from(activity).inflate(R.layout.answer_item, parent, false));
        }

        @Override
        public void onBindViewHolder(AnswerViewHolder holder, int position) {
            holder.tvRecordInfo.setVisibility(View.GONE);
            holder.tvQuestionPicsInfo.setVisibility(View.GONE);


            Answer answer = answers.get(position);
            Glide.with(activity).load(answer.getAuthor().getUserIcon().getFileUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.civUserIcon.getCivUserIcon());
            holder.civUserIcon.setGender(answer.getAuthor().getGender());
            holder.tvNickname.setText(answer.getAuthor().getNick());
            holder.tvCreateTime.setText(answer.getCreatedAt());
            holder.tvContent.setText(answer.getContent());
            initNestedRecyclerView(holder.recyclerview);

            if (answer.getPics() != null && answer.getPics().size() > 0) {
                List<String> photos = new ArrayList<>();
                for (BmobFile pic : answer.getPics()) {
                    photos.add(pic.getFileUrl());
                }


                holder.recyclerview.setAdapter(new HomeFragmentPhotoAdapter(activity, photos));
            }


            holder.llProfile.setOnClickListener(new onUserAreaClickListener(activity, answers.get(position).getAuthor().getObjectId(),
                    answers.get(position).getAuthor().getNick()));

            //设置长按监听弹出菜单
            holder.cv_item.setOnLongClickListener(new onAnswerLongClickListener(answers.get(position), activity));


            //下面加载原提问
            if (answer.getQuestion() == null) {
                holder.tvQuestionContent.setText(DefaultValue.DELETED_QUESTION);
                return;
            }


            Glide.with(activity).load(answer.getQuestion().getAuthor().getUserIcon().getFileUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.civQuestionAuthorIcon.getCivUserIcon());
            holder.civQuestionAuthorIcon.setGender(answer.getQuestion().getAuthor().getGender());

            holder.tvQuestionAuthorNick.setText(answer.getQuestion().getAuthor().getNick());
            holder.tvQuestionContent.setText(answer.getQuestion().getContent());
            if (answer.getQuestion().getPics() != null) {
                holder.tvQuestionPicsInfo.setVisibility(View.VISIBLE);
                holder.tvQuestionPicsInfo.setText("有" + answer.getQuestion().getPics().size() + "张图片");
            }

            if (answer.getQuestion().getRecord() != null) {

                holder.tvRecordInfo.setVisibility(View.VISIBLE);
            }


//            holder.uivUserIcon.setOnClickListener(new onQuestionItemClickListener(answers.get(position).getQuestion().getObjectId(), activity));
            holder.llQuestionAuthorArea.setOnClickListener(new onUserAreaClickListener(activity, answers.get(position).getQuestion().getAuthor().getObjectId(),
                    answers.get(position).getQuestion().getAuthor().getNick()));

            //设置问题区域 监听
            holder.llQuestionArea.setOnClickListener(new onQuestionItemClickListener(answer.getQuestion().getObjectId(), activity));


        }

        private void initNestedRecyclerView(RecyclerView recyclerview) {
            recyclerview.setLayoutManager(new GridLayoutManager(activity, 3));
            recyclerview.setItemAnimator(new DefaultItemAnimator());
            recyclerview.setAdapter(null);
        }

        @Override
        public int getItemCount() {
            return answers.size();
        }


        class AnswerViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.uiv_user_icon)
            UserIconView civUserIcon;
            @BindView(R.id.tv_nickname)
            TextView tvNickname;
            @BindView(R.id.tv_create_time)
            TextView tvCreateTime;
            @BindView(R.id.ll_profile)
            LinearLayout llProfile;
            @BindView(R.id.tv_content)
            TextView tvContent;
            @BindView(R.id.recyclerview)
            MyRecyclerView recyclerview;
            @BindView(R.id.civ_question_author_icon)
            UserIconView civQuestionAuthorIcon;
            @BindView(R.id.tv_question_author_nick)
            TextView tvQuestionAuthorNick;
            @BindView(R.id.ll_question_author_area)
            LinearLayout llQuestionAuthorArea;
            @BindView(R.id.tv_question_content)
            TextView tvQuestionContent;
            @BindView(R.id.tv_question_pics_info)
            TextView tvQuestionPicsInfo;
            @BindView(R.id.ll_question_area)
            LinearLayout llQuestionArea;
            @BindView(R.id.cv_item)
            CardView cv_item;
            @BindView(R.id.tv_record_info)
            TextView tvRecordInfo;


            public AnswerViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
