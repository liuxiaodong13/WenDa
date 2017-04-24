package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.adapter.HomeFragmentPhotoAdapter;
import liuxiaodong.neusoft.edu.cn.wenda.base.BackNavigationActivity;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.fragment.tabpagerfragment.AnswersPagerFragment;
import liuxiaodong.neusoft.edu.cn.wenda.fragment.tabpagerfragment.ConcernPagerFragment;
import liuxiaodong.neusoft.edu.cn.wenda.listener.onUserAreaClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.CategoryTag;
import liuxiaodong.neusoft.edu.cn.wenda.model.Collection;
import liuxiaodong.neusoft.edu.cn.wenda.model.Concern;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.model.Notification;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.other.MediaManager;
import liuxiaodong.neusoft.edu.cn.wenda.ui.RecordView;
import liuxiaodong.neusoft.edu.cn.wenda.ui.UserIconView;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

/**
 * Created by DONG on 10/31/2016.
 */

public class QuestionInfoActivity extends BackNavigationActivity {
//    static {
//        AppCompatDelegate.setCompatVectorFromSourcesEnabled(true);
//    }
    public static final String TAG = "QuestionInfoActivity";

    public static final String QUESTION_ID = "questionId";
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String CONCERN_ID = "concern_Id";
    public static final String ACTION_UPDATE_STATE = "context.notification.update";
    private String questionId;
    private String concernId;
    private String collectId;

    private String notificationId;

    private Question question;

    private int answers = 0;
    private int concerns = 0;
    private boolean isConcerned;
    private boolean isCollected;


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
    RecyclerView recyclerView;
    @BindView(R.id.cv_item)
    CardView cvItem;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.fam)
    FloatingActionMenu floatingActionMenu;
    @BindView(R.id.menu_add_answer)
    FloatingActionButton fabAnswer;
    @BindView(R.id.menu_add_collect)
    FloatingActionButton fabCollect;
    @BindView(R.id.menu_add_concern)
    FloatingActionButton fabConcern;
    @BindView(R.id.rv_Record)
    RecordView mRecordView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildActivity(R.layout.activity_question_info);
        parseIntent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    @OnClick(R.id.ll_userArea)
    public void onClick(View view) {
        new onUserAreaClickListener(activity, question.getAuthor().getObjectId(), question.getAuthor().getNick()).onClick(view);
    }

    private MyTabPagerAdapter tabPagerAdapter;


    private void parseIntent() {
        if (getIntent() != null) {
            if (getIntent().getAction() != null) {
                switch (getIntent().getAction()) {
                    case ACTION_UPDATE_STATE:
                        updateNotificationState(notificationId);
                        break;
                }
            }

        }


    }

    private void updateNotificationState(String notificationId) {
        Log.d(TAG, "updateNotificationState: 更新通知为已读notificationId为" + notificationId);
        Notification notification = new Notification();
        notification.setRead(true);
        notification.update(notificationId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                sendEvent();
                if (e == null) {
                    Log.d(TAG, "done: 更新通知成功");


                } else {
                    Log.e(TAG, "done: 更新通知失败" + e);
                }

            }
        });

    }

    private void sendEvent() {

        EventManager.postSticky(new Event(Event.REFRESH_NOTIFICATIONS));


    }


    @OnClick(R.id.menu_add_answer)
    public void onAnswerFabClick() {
        if (loginCheck()) return;

        Log.i(TAG, "onAnswerFabClick: ");
        Intent intent = new Intent(activity, NewAnswerActivity.class);
        intent.putExtra(NewAnswerActivity.QUESTION_ID, questionId);
        startActivity(intent);
        floatingActionMenu.close(true);

    }

    @OnClick(R.id.menu_add_concern)
    public void onConcernClick() {
        if (loginCheck()) return;

        doConcern();
        floatingActionMenu.close(true);
    }

    @OnClick(R.id.menu_add_collect)
    public void onCollectClick() {
        if (loginCheck()) return;

        doCollect();
        floatingActionMenu.close(true);
    }

    private void doCollect() {
        if (!isCollected) {
            //添加收藏
            Collection collection = new Collection();
            BmobRelation relation = new BmobRelation();
            Question question = new Question();
            question.setObjectId(questionId);
            relation.add(question);
            collection.setCollects(relation);
            collection.update(collectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.i(TAG, "done: 更新collection表 添加收藏成功！");
                        isCollected = true;
                        initFabMenuState();
                        T.makeS(activity, "收藏成功！");
                    } else {
                        Log.i(TAG, "done: 收藏失败" + e);
                        T.makeS(activity, "收藏失败！");
                    }

                }
            });


        } else {
            //取消收藏
            Collection collection = new Collection();
            BmobRelation relation = new BmobRelation();
            Question question1 = new Question();
            question1.setObjectId(questionId);

            relation.remove(question1);
            collection.setCollects(relation);
            collection.update(collectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        isCollected = false;
                        initFabMenuState();
                        Log.i(TAG, "done: 取消收藏成功！");
                        T.makeS(activity, "取消收藏成功！");
                    } else {
                        Log.i(TAG, "done: 取消收藏失败" + e);
                        T.makeS(activity, "取消收藏失败");
                    }

                }
            });
        }

    }

    private void doConcern() {

        MyUser user = getCurrentUser();
        user.setObjectId(getCurrentUser().getObjectId());

        Question question = new Question();
        question.setObjectId(questionId);
        BmobRelation relation = new BmobRelation();

        if (!isConcerned) {

            //更新question表 concerned字段
            relation.add(getCurrentUser());
            question.setConcerned(relation);
            question.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        T.makeS(activity, "关注成功！");
                        isConcerned = true;
                        initFabMenuState();
                        EventManager.postSticky(new Event(Event.REFRESH_CONCERN_LIST));
                    } else {
                        T.makeS(activity, "关注失败！");
                    }
                }
            });
            //更新concern表 方便 直接拉取用户关注的所有问题
            Question question1 = new Question();
            question1.setObjectId(questionId);
            BmobRelation relation1 = new BmobRelation();
            relation1.add(question1);

            Concern concern = new Concern();
            concern.setUser(getCurrentUser());
            concern.setConcerns(relation1);
            concern.update(concernId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.i(TAG, "done: 更新concern表 添加关注成功！");
                    } else {
                        Log.i(TAG, "done: " + e);
                    }
                }
            });


        } else {
            //取消关注！
            //更新question表 concerned字段
            relation.remove(getCurrentUser());
            question.setConcerned(relation);
            question.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        isConcerned = false;
                        initFabMenuState();
                        T.makeS(activity, "取消关注成功！");
                        EventManager.postSticky(new Event(Event.REFRESH_CONCERN_LIST));
                    } else {
                        T.makeS(activity, "取消关注失败！");
                    }
                }
            });

            //更新concern表 方便 直接拉取用户关注的所有问题
            Question question1 = new Question();
            question1.setObjectId(questionId);
            BmobRelation relation1 = new BmobRelation();
            relation1.remove(question1);
            Concern concern = new Concern();
            concern.setConcerns(relation1);
            concern.setUser(getCurrentUser());
            concern.update(concernId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.i(TAG, "done: 更新concern表成功！取消关注成功！");
                    } else {
                        Log.i(TAG, "done: " + e);
                    }

                }
            });

        }
    }


    @Override
    protected void initToolbar() {
        initTitle("问题详情");
    }

    @Override
    protected void getExtraData() {
        questionId = getIntent().getStringExtra(QUESTION_ID);
        notificationId = getIntent().getStringExtra(NOTIFICATION_ID);
    }

    @Override
    protected void initView() {
        floatingActionMenu.setClosedOnTouchOutside(true);
        tabPagerAdapter = new MyTabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

    }

    @Override
    protected void initData() {
        queryConcernId();
        queryCollectId();
        BmobConn.queryObject(Question.class, questionId, new QueryListener<Question>() {
            @Override
            public void done(Question question, BmobException e) {
                if (e == null) {
                    QuestionInfoActivity.this.question = question;
                    bindData(question);

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

        //queryAnswersCount();


    }

    /**
     * 查询当前问题回答数目:
     */
    private void queryAnswersCount() {

        BmobConn.queryRelatedAnswersCountFromQuestionId(questionId, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    Logger.d("done: 当前问题回答数目count:" + integer);
                    answers = integer;
                    tabPagerAdapter.initTitleCounts(answers, concerns);
                } else {
                    Logger.e(e.toString());
                }
            }
        });


    }

    private void queryCollectId() {
        BmobConn.queryAll(Collection.class, new FindListener<Collection>() {
            @Override
            public void done(List<Collection> list, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: 查询collectId成功！");
                    collectId = list.get(0).getObjectId();
                    queryIsCollected();
                } else {
                    Log.i(TAG, "done: " + e);
                }

            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                query.addWhereEqualTo("user", getCurrentUser());
            }
        });
    }

    private void queryIsCollected() {

        BmobConn.queryAll(Question.class, new FindListener<Question>() {
            @Override
            public void done(List<Question> list, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: 当前用户收藏数量:" + list.size());
                    isCollected = isCollected(list);
                    initFabMenuState();
                } else {
                    Log.i(TAG, "done: " + e);
                }

            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Collection collection = new Collection();
                collection.setObjectId(collectId);
                query.addWhereRelatedTo("collects", new BmobPointer(collection));
            }
        });

    }

    private boolean isCollected(List<Question> list) {
        for (Question question : list) {
            if (question.getObjectId().equals(questionId)) {
                return true;
            }
        }
        return false;
    }

    private void queryConcernId() {
        BmobQuery<Concern> query = new BmobQuery<>();
        query.addWhereEqualTo("user", getCurrentUser());
        query.findObjects(new FindListener<Concern>() {
            @Override
            public void done(List<Concern> list, BmobException e) {
                if (e == null) {
                    queryIsConcerned();
                    concernId = list.get(0).getObjectId();
                    //isConcerned = isConcerned(list.get(0).getConcerns().getObjects());
                    Log.i(TAG, "done: 获取concernId成功！");

                } else {
                    Log.i(TAG, "done: 获取concernId失败！" + e);
                }

            }
        });
    }

    private void queryIsConcerned() {
        BmobConn.queryAll(MyUser.class, new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: 查询关注了该问题的所有用户：" + list.size());
                    concerns = list.size();
                    queryAnswersCount();
                    isConcerned = isConcerned(list);
                    initFabMenuState();
                }
            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Question question = new Question();
                question.setObjectId(questionId);
                query.addWhereRelatedTo("concerned", new BmobPointer(question));
            }
        });
    }

    private void initFabMenuState() {
        if (isConcerned) {
            fabConcern.setLabelText("已关注");

        } else {
            fabConcern.setLabelText("关注");
        }

        if (isCollected) {
            fabCollect.setLabelText("已收藏");
        } else {
            fabCollect.setLabelText("收藏");
        }
    }

    private boolean isConcerned(List<MyUser> objects) {
        for (MyUser object : objects) {
            if (object.getObjectId().equals(getCurrentUser().getObjectId())) {
                return true;
            }
        }
        return false;
    }

    private void bindData(final Question question) {
        if (question.getAuthor().getUserIcon() != null) {

            Glide.with(this).load(question.getAuthor().getUserIcon().getFileUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(icvUserIcon.getCivUserIcon());
        }
        icvUserIcon.setGender(question.getAuthor().getGender());
        tvContent.setText(question.getContent());
        tvCreateTime.setText(question.getCreatedAt());
        tvNickName.setText(question.getAuthor().getNick());

        //(TAG, "onBindViewHolder: category:" + question.getCategory().getName());
        tvCategory.setText(question.getCategory().getName());
        ivCategory.setImageResource(new CategoryTag(question.getCategory()).getCategoryImageResId());
        //cvItem.setOnClickListener(new HomeAdapter.onQuestionItemOnClickListener(question.getObjectId()));

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        List<BmobFile> pics = question.getPics();
        if (pics != null && pics.size() > 0) {
            List<String> photos = new ArrayList<>();
            for (BmobFile pic : pics) {
                photos.add(pic.getFileUrl());
            }
            recyclerView.setAdapter(new HomeFragmentPhotoAdapter(this, photos));

        }

        if (question.getRecord() != null) {
            mRecordView.setVisibility(View.VISIBLE);
            mRecordView.setTime(question.getRecordTime());
            mRecordView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecordView.startAnim();
                    MediaManager.playNetSound(question.getRecord(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mRecordView.stopAnim();
                        }
                    });
                }
            });
        }

    }

    class MyTabPagerAdapter extends FragmentPagerAdapter {
        private Fragment[] fragments = {AnswersPagerFragment.newInstance(questionId), ConcernPagerFragment.newInstance(questionId)};
        private String[] titles = {"回答", "关注"};


        public MyTabPagerAdapter(FragmentManager fm) {
            super(fm);
            //initTitleCounts(x, y);
        }

        public void initTitleCounts(int x, int y) {
            titles[0] = titles[0] + " " + (x + "");
            titles[1] = titles[1] + " " + (y + "");
            notifyDataSetChanged();
        }


        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
