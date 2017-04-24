package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.adapter.HomeFragmentPhotoAdapter;
import liuxiaodong.neusoft.edu.cn.wenda.base.BackNavigationActivity;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.model.Answer;
import liuxiaodong.neusoft.edu.cn.wenda.other.MediaManager;
import liuxiaodong.neusoft.edu.cn.wenda.ui.RecordView;
import liuxiaodong.neusoft.edu.cn.wenda.ui.UserIconView;

import static liuxiaodong.neusoft.edu.cn.wenda.fragment.tabpagerfragment.AnswersPagerFragment.ANSWER_ID;

/**
 * Created by DONG on 2016/11/7.
 */

public class AnswerInfoActivity extends BackNavigationActivity {
    public static final String TAG = "AnswerInfoActivity";
    private String answerId;

    @BindView(R.id.uiv_user_icon)
    UserIconView uivUserIcon;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_create_time)
    TextView tvCreateTime;
    @BindView(R.id.iv_category)
    ImageView ivCategory;
    @BindView(R.id.tv_category)
    TextView tvCategory;
    @BindView(R.id.ll_profile)
    LinearLayout llProfile;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.cv_item)
    CardView cvItem;
    @BindView(R.id.rv_Record)
    RecordView mRecordView;
    private HomeFragmentPhotoAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildActivity(R.layout.activity_answer_info);
    }

    @Override
    protected void getExtraData() {
        answerId = getIntent().getStringExtra(ANSWER_ID);
    }

    @Override
    protected void initToolbar() {
       initTitle("回答详情");
    }



    @Override
    protected void initView() {
        ivCategory.setVisibility(View.GONE);
        tvCategory.setVisibility(View.GONE);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        adapter = new HomeFragmentPhotoAdapter(this);
        recyclerview.setAdapter(adapter);
    }

    private void bindData(Answer answer) {
        setRecordView(answer);

        tvContent.setText(answer.getContent());
        tvCreateTime.setText(answer.getCreatedAt());
        tvNickname.setText(answer.getAuthor().getNick());
        Glide.with(this).load(answer.getAuthor().getUserIcon().getFileUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(uivUserIcon.getCivUserIcon());

        Log.i(TAG, "bindData: 共有" + answer.getPics().size() + "张图片" );

        if (answer.getPics() != null && answer.getPics().size() > 0) {
            List<String> photos = new ArrayList<>();
            for (BmobFile pic : answer.getPics()) {
                photos.add(pic.getFileUrl());
            }

            adapter.setData(photos);
        }

    }

    private void setRecordView(final Answer answer) {
        if (answer.getRecord() != null) {
            mRecordView.setVisibility(View.VISIBLE);
            mRecordView.setTime(answer.getRecordTime());
            mRecordView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecordView.startAnim();
                    MediaManager.playSound(answer.getRecord().getFileUrl(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mRecordView.stopAnim();
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void initData() {
        //拿到answer详细数据
        BmobConn.queryObject(Answer.class, answerId, new QueryListener<Answer>() {
            @Override
            public void done(Answer answer, BmobException e) {
                if (e == null) {
                    bindData(answer);

                }else {
                    Log.i(TAG, "done: " + e);
                }

            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                query.include("author");
            }
        });

    }
}
