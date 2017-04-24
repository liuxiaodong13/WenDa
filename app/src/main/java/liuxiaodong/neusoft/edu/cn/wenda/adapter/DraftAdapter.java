package liuxiaodong.neusoft.edu.cn.wenda.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseActivity;
import liuxiaodong.neusoft.edu.cn.wenda.listener.base.onQuestionItemClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.listener.onDraftDeleteOnClick;
import liuxiaodong.neusoft.edu.cn.wenda.listener.onDraftSendOnClick;
import liuxiaodong.neusoft.edu.cn.wenda.listener.onUserAreaClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.Category;
import liuxiaodong.neusoft.edu.cn.wenda.model.CategoryTag;
import liuxiaodong.neusoft.edu.cn.wenda.model.Draft;
import liuxiaodong.neusoft.edu.cn.wenda.model.DraftWrapper;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.other.MediaManager;
import liuxiaodong.neusoft.edu.cn.wenda.ui.RecordView;
import liuxiaodong.neusoft.edu.cn.wenda.ui.UserIconView;
import liuxiaodong.neusoft.edu.cn.wenda.utils.DefaultValue;

/**
 * Created by DONG on 2017/1/15.
 */

public class DraftAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int NO_DATA = -1;


    private ArrayList<DraftWrapper> drafts = new ArrayList<>();
    private BaseActivity activity;

    private boolean isDraftEntire = false;


    public boolean isDraftEntire() {
        return isDraftEntire;
    }

    public void setDraftEntire(boolean draftEntire) {
        isDraftEntire = draftEntire;
    }

    public void setData(List<DraftWrapper> data) {
        drafts.clear();
        drafts.addAll(data);
        notifyDataSetChanged();
    }

    public DraftAdapter(BaseActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        if (drafts.size() == 0) {
            return NO_DATA;
        }

        //默认返回viewType为0
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == NO_DATA) {
            TextView textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            parent.addView(textView);
            return new EmptyViewHolder(textView);
        }

        return new DraftViewHolder(LayoutInflater.from(context).inflate(R.layout.draft_item, parent, false));


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmptyViewHolder) {
            ((EmptyViewHolder) holder).textView.setText(DefaultValue.NO_DATA_DRAFT);

        } else if (holder instanceof DraftViewHolder) {

            final Draft draft = drafts.get(position).getDraft();
            final DraftViewHolder draftViewHolder = (DraftViewHolder) holder;

            //将 view设置默认隐藏


            draftViewHolder.ivCategory.setVisibility(View.GONE);
            draftViewHolder.llOriginalQsRoot.setVisibility(View.GONE);
            draftViewHolder.tvQuestionPicsInfo.setVisibility(View.GONE);
            draftViewHolder.tvRecordInfo.setVisibility(View.GONE);
            draftViewHolder.mRecordView.setVisibility(View.GONE);

            //先设置共同数据
            Draft.Type type = draft.getType();

            draftViewHolder.mTvContentType.setText(getTypeDesc(type));
            draftViewHolder.tvContent.setText(draft.getContent());
            draftViewHolder.tvCreateTime.setText(draft.getTime());

            initRecyclerView(draftViewHolder.recyclerView);
            draftViewHolder.recyclerView.setAdapter(null);
            draftViewHolder.recyclerView.setAdapter(new HomeFragmentPhotoAdapter(activity, draft.getPicUrls()));

            //设置录音 View的状态
            setRecordView(draft, draftViewHolder);


            draftViewHolder.btnRemove.setOnClickListener(
                    new onDraftDeleteOnClick(activity.getString(R.string.draftDeleteWarning)
                            , activity
                            , draft.getId()));
            draftViewHolder.btnSend.setOnClickListener(new onDraftSendOnClick(activity, draft));


            if (draft.getType() == Draft.Type.QUESTION) {
                draftViewHolder.ivCategory.setVisibility(View.VISIBLE);
                draftViewHolder.ivCategory.setImageResource(new CategoryTag(new Category(draft.getCategoryName())).getCategoryImageResId());
            } else if (draft.getType() == Draft.Type.ANSWER) {
                //设置 原提问区域
                draftViewHolder.ivCategory.setVisibility(View.GONE);

                setOriginalQsArea(draftViewHolder, drafts.get(position));

            }


        }

    }

    private void setRecordView(final Draft draft, final DraftViewHolder draftViewHolder) {
        if (!draft.getRecordPath().equals(""))
        {
            draftViewHolder.mRecordView.setVisibility(View.VISIBLE);
            draftViewHolder.mRecordView.setTime(draft.getRecordTime());
            draftViewHolder.mRecordView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    draftViewHolder.mRecordView.startAnim();
                    MediaManager.playSound(draft.getRecordPath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            draftViewHolder.mRecordView.stopAnim();

                        }
                    });

                }
            });
        }
    }

    private void setOriginalQsArea(DraftViewHolder draftViewHolder, DraftWrapper wrapper) {
        //先判断 当前adapter状态是否已更新
        Draft draft = wrapper.getDraft();
        Question question = wrapper.getQuestion();
        if (isDraftEntire) {

            draftViewHolder.llOriginalQsRoot.setVisibility(View.VISIBLE);

            //下面加载原提问
            if (question == null) {
                draftViewHolder.tvQuestionContent.setText(DefaultValue.DELETED_QUESTION);
                return;
            }


            //先设置监听
            draftViewHolder.llQuestionArea.setOnClickListener(new onQuestionItemClickListener(question.getObjectId(), activity));
            Glide.with(activity).load(question.getAuthor().getUserIcon().getFileUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(draftViewHolder.civQuestionAuthorIcon.getCivUserIcon());
            draftViewHolder.civQuestionAuthorIcon.setGender(question.getAuthor().getGender());
            draftViewHolder.tvQuestionAuthorNick.setText(question.getAuthor().getNick());
            draftViewHolder.tvQuestionContent.setText(question.getContent());
            if (question.getPics() != null) {
                draftViewHolder.tvQuestionPicsInfo.setVisibility(View.VISIBLE);
                draftViewHolder.tvQuestionPicsInfo.setText("有" + question.getPics().size() +   "张 图片");
            }
            if (question.getRecord() != null) {
                draftViewHolder.tvRecordInfo.setVisibility(View.VISIBLE);
            }
            draftViewHolder.llQuestionArea.setOnClickListener(new onQuestionItemClickListener(question.getObjectId(), activity));
            draftViewHolder.llQuestionAuthorArea.setOnClickListener(new onUserAreaClickListener(activity, question.getAuthor().getObjectId(), question.getAuthor().getNick()));


        }

    }

    private String getTypeDesc(Draft.Type type) {
        String str = "";
        switch (type) {
            case QUESTION:
                str = "新提问";
                break;
            case ANSWER:
                str = "新回答";
                break;
        }
        return str;
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public int getItemCount() {
        return drafts.size() > 0 ? drafts.size() : 1;
//        return drafts.size();
    }

    public void updateData(List<DraftWrapper> wrappers) {
        drafts.clear();
        drafts.addAll(wrappers);
        setDraftEntire(true);
        notifyDataSetChanged();

    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    class DraftViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_category)
        ImageView ivCategory;
        @BindView(R.id.tv_create_time)
        TextView tvCreateTime;
        @BindView(R.id.recyclerview)
        RecyclerView recyclerView;
        @BindView(R.id.btn_remove)
        Button btnRemove;
        @BindView(R.id.btn_send)
        Button btnSend;
        @BindView(R.id.tv_contentType)
        TextView mTvContentType;

        @BindView(R.id.rv_Record)
        RecordView mRecordView;


        /**
         * 原提问
         */
        @BindView(R.id.civ_question_author_icon)
        UserIconView civQuestionAuthorIcon;
        @BindView(R.id.tv_question_author_nick)
        TextView tvQuestionAuthorNick;
        @BindView(R.id.ll_question_author_area)
        LinearLayout llQuestionAuthorArea;
        @BindView(R.id.tv_question_content)
        TextView tvQuestionContent;
        @BindView(R.id.tv_record_info)
        TextView tvRecordInfo;
        @BindView(R.id.tv_question_pics_info)
        TextView tvQuestionPicsInfo;
        @BindView(R.id.ll_question_area)
        LinearLayout llQuestionArea;
        @BindView(R.id.ll_original_qs_root)
        LinearLayout llOriginalQsRoot;

        public DraftViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
