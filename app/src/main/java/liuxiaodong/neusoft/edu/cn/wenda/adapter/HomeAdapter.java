package liuxiaodong.neusoft.edu.cn.wenda.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseActivity;
import liuxiaodong.neusoft.edu.cn.wenda.listener.base.onQuestionItemClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.CategoryTag;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.other.MediaManager;
import liuxiaodong.neusoft.edu.cn.wenda.ui.MyRecyclerView;
import liuxiaodong.neusoft.edu.cn.wenda.ui.RecordView;
import liuxiaodong.neusoft.edu.cn.wenda.ui.UserIconView;
import liuxiaodong.neusoft.edu.cn.wenda.utils.DefaultValue;

/**
 * Created by DONG on 2016/11/1.
 */

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int NO_DATA = -1;
    BaseActivity activity;
    List<Question> questions = new ArrayList<>();
    private Context context;
    private RecordView mCurrentRecordView;

    @Override
    public int getItemViewType(int position) {
        if (questions.size() == 0) {
            return NO_DATA;
        }
        return super.getItemViewType(position);

    }

    public HomeAdapter(BaseActivity activity) {
        this.activity = activity;

    }

    public void setData(List<Question> data) {
        if (data.size() > 0) {
            questions.clear();
            questions.addAll(data);
            notifyDataSetChanged();
        }

    }

    public void addData(List<Question> data) {
        if (data.size() > 0) {
            questions.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        if (viewType == NO_DATA) {
            TextView textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            parent.addView(textView);
            return new EmptyViewHolder(textView);
        }

        return new MyViewHolder(LayoutInflater.from(activity).inflate(R.layout.question_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyViewHolder) {
            final MyViewHolder viewHolder = (MyViewHolder) holder;


            if (questions.get(position).getAuthor().getUserIcon() != null) {

                Glide.with(activity).load(questions.get(position).getAuthor().getUserIcon().getFileUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(viewHolder.icvUserIcon.getCivUserIcon());
                viewHolder.icvUserIcon.setGender(questions.get(position).getAuthor().getGender());
            }

            viewHolder.tvContent.setText(questions.get(position).getContent());
            viewHolder.tvCreateTime.setText(questions.get(position).getCreatedAt());
            viewHolder.tvNickName.setText(questions.get(position).getAuthor().getNick());

            viewHolder.tvCategory.setText(questions.get(position).getCategory().getName());
            viewHolder.ivCategory.setImageResource(new CategoryTag(questions.get(position).getCategory()).getCategoryImageResId());
            viewHolder.cvItem.setOnClickListener(new onQuestionItemClickListener(questions.get(position).getObjectId(), activity));

            viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
//            viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
            viewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());

            List<BmobFile> pics = questions.get(position).getPics();
            viewHolder.tvCategory.setText(questions.get(position).getCategory().getName());
            viewHolder.ivCategory.setImageResource(new CategoryTag(questions.get(position).getCategory()).getCategoryImageResId());
            viewHolder.cvItem.setOnClickListener(new onQuestionItemClickListener(questions.get(position).getObjectId(), activity));
            if (pics != null && pics.size() > 0) {
                List<String> photos = new ArrayList<>();
                for (BmobFile pic : pics) {
                    photos.add(pic.getFileUrl());
                }
                viewHolder.recyclerView.setAdapter(new HomeFragmentPhotoAdapter(activity, photos));

            }

            //设置语音图标显示
            final Question question = questions.get(position);
            if (question.getRecord() != null) {
                viewHolder.mRecordView.setVisibility(View.VISIBLE);
                viewHolder.mRecordView.setTime(question.getRecordTime());
                viewHolder.mRecordView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCurrentRecordView != null) {
                            mCurrentRecordView.stopAnim();
                            mCurrentRecordView = null;
                        }
                        mCurrentRecordView = viewHolder.mRecordView;
                        viewHolder.mRecordView.startAnim();
                        MediaManager.playNetSound(question.getRecord(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                viewHolder.mRecordView.stopAnim();
                                mCurrentRecordView = null;

                            }
                        });


                    }
                });
            }

        } else if (holder instanceof EmptyViewHolder) {
            ((EmptyViewHolder) holder).textView.setText(DefaultValue.NO_DATA);
        }


    }

    @Override
    public int getItemCount() {
        return questions.size() > 0 ? questions.size() : 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.uiv_user_icon)
        UserIconView icvUserIcon;
        @BindView(R.id.tv_nickname)
        TextView tvNickName;
        @BindView(R.id.tv_create_time)
        TextView tvCreateTime;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_category)
        ImageView ivCategory;
        @BindView(R.id.tv_category)
        TextView tvCategory;
        @BindView(R.id.recyclerview)
        MyRecyclerView recyclerView;
        @BindView(R.id.cv_item)
        CardView cvItem;
        @BindView(R.id.rv_Record)
        RecordView mRecordView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

}
