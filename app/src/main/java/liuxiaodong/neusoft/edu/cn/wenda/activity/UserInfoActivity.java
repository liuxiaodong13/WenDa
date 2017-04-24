package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import de.hdodenhof.circleimageview.CircleImageView;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.base.BackNavigationActivity;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.model.Answer;
import liuxiaodong.neusoft.edu.cn.wenda.model.Category;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;

/**
 * Created by DONG on 2016/10/25.
 */

public class UserInfoActivity extends BackNavigationActivity {
    public static final String TAG = "UserInfoActivity";
    public static final String OBJECT_ID = "objectId";
    public static final String USER_NICK = "usernick";

    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_user_desc)
    TextView tvUserDesc;
    @BindView(R.id.tv_good_at)
    TextView tvGoodAt;
    @BindView(R.id.tv_answer_count)
    TextView tvAnswerCount;
    @BindView(R.id.tv_concern_count)
    TextView tvConcernCount;
    @BindView(R.id.tv_question_count)
    TextView tvQuestionCount;

    @BindView(R.id.civ_userIcon)
    CircleImageView civUserIcon;
    @BindView(R.id.iv_sex)
    ImageView ivSex;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.btn_logout)
    Button btnLogout;


    @BindView(R.id.iv_user_background)
    ImageView ivUserBackground;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.ll_answer)
    LinearLayout llAnswer;
    @BindView(R.id.ll_question)
    LinearLayout llQuestion;
    @BindView(R.id.ll_concern)
    LinearLayout llConcern;
//    @BindView(R.id.f)
//    LinearLayout llCollection;
    private String objectId;
    private String userNick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        buildActivity(R.layout.activity_user_info);
    }

    @Override
    protected void getExtraData() {
        objectId = getIntent().getStringExtra(OBJECT_ID);
        //Logger.i(TAG, "onCreate: objectId:" + objectId);
        userNick = getIntent().getStringExtra(USER_NICK);

    }

    @Override
    protected void globalInit() {
        addToDestroyMap(this, this.getClass().getName());

    }

    @Override
    protected void initView() {
        initUserType();

    }

    private void initUserType() {
        if (!objectId.equals(getCurrentUser().getObjectId())) {
            fab.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            //llCollection.setVisibility(View.GONE);

        }
    }


    @Override
    protected void initData() {
        BmobConn.queryObject(MyUser.class, objectId, new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e == null) {
                    setUserData(myUser);
                } else {
                    BmobConn.parseBmobException(e, activity);
                }
            }
        });

        //查询该用户所有回答
        BmobConn.queryAll(Answer.class, new FindListener<Answer>() {
            @Override
            public void done(List<Answer> list, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: 该用户所有回答:" + list.size());
                    tvAnswerCount.setText(list.size() + "");
                } else {
                    Log.i(TAG, "done: " + e);
                }

            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                MyUser user = new MyUser();
                user.setObjectId(objectId);
                query.addWhereEqualTo("author", new BmobPointer(user));
            }
        });

        //查询该用户所有提问

        BmobConn.queryAll(Question.class, new FindListener<Question>() {
            @Override
            public void done(List<Question> list, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: 该用户所有提问" + list.size());
                    tvQuestionCount.setText(list.size() + "");
                } else {
                    Log.i(TAG, "done: " + e);
                }

            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                MyUser user = new MyUser();
                user.setObjectId(objectId);
                query.addWhereEqualTo("author", new BmobPointer(user));
            }
        });

        //查询该用户所有关注

//        BmobConn.queryAllConcernedQuestions(getCurrentUser().getObjectId(), new FindListener<Question>() {
//            @Override
//            public void done(List<Question> list, BmobException e) {
//                if (e == null) {
//                    Log.i(TAG, "done: 查询该用户所有关注:" + list.size());
//                    tvConcernCount.setText(list.size() + "");
//                } else {
//                    Log.i(TAG, "done: " + e);
//                }
//            }
//        });

//        BmobConn.queryConcernedQuestions(new FindListener<Question>() {
//            @Override
//            public void done(List<Question> list, BmobException e) {
//                if (e == null) {
//                    Log.i(TAG, "done: 查询该用户所有关注:" + list.size());
//                    tvConcernCount.setText(list.size() + "");
//                } else {
//                    Log.i(TAG, "done: " + e);
//                }
//            }
//        });

        BmobConn.queryAllConcernedQuestionCounts(new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: 查询该用户所有关注:" + integer);
                    tvConcernCount.setText(integer + "");
                } else {
                    Log.i(TAG, "done: " + e);
                }
            }
        });

        //查询该用户 所有收藏

//        BmobConn.queryCollectsFromUserId(getCurrentUser().getObjectId(), new FindListener<Question>() {
//            @Override
//            public void done(List<Question> list, BmobException e) {
//                if (e == null) {
//                    tvCollectionCount.setText(list.size() + "");
//                    Log.i(TAG, "done: 查询该用户 所有收藏" + list.size());
//                } else {
//                    Log.i(TAG, "done: " + e);
//                }
//            }
//        });

        //查询当前用户 的擅长
        BmobConn.queryGoodAt(objectId, new FindListener<Category>() {
            @Override
            public void done(List<Category> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        StringBuilder builder = new StringBuilder();
                        for (Category category : list) {
                            builder.append(category.getName());
                            builder.append("\n");
                        }
                        tvGoodAt.setText(builder.toString());
                    } else {
                        Log.i(TAG, "done: " + e);
                    }

                }
            }
        });

    }

    private void setUserData(MyUser myUser) {
        tvUserDesc.setText(myUser.getDesc());
        Glide.with(this).load(myUser.getUserIcon().getFileUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(civUserIcon);
        Log.i(TAG, "setUserData:" + myUser.getUserIcon().getFileUrl() + "  \n" + myUser.getUserIcon().getFilename() + "  \n" + myUser.getUserIcon().getGroup() + "  \n" + myUser.getUserIcon().getUrl());
        if (myUser.getBackground() != null) {
            Glide.with(this).load(myUser.getBackground().getFileUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivUserBackground);
        }
    }

    @Override
    protected void initToolbar() {
        initTitle(userNick);
    }

    @OnClick({R.id.fab, R.id.btn_logout, R.id.ll_answer, R.id.ll_question, R.id.ll_concern})
    public void onClick(View view) {
        Intent intent = new Intent(activity, RelevantListActivity.class);
        intent.putExtra(OBJECT_ID, objectId);
        intent.putExtra(USER_NICK, userNick);
        switch (view.getId()) {
            case R.id.fab:
                finish();
                showActivity(EditUserInfoActivity.class);
                break;
            case R.id.btn_logout:
                MyUser.logOut();
                APP.getInstance().initLoginState();

//                EventBusFactory.START.getBus().postSticky(new Event(Event.LOGOUT_SUCCESS));

                EventManager.postSticky(new Event(Event.LOGOUT_SUCCESS));
                showActivity(MainActivity.class);
                break;
            case R.id.ll_answer:
                Intent intent1 = new Intent(activity, AnswerListActivity.class);
                startActivity(intent1);

                break;
            case R.id.ll_question:
                intent.putExtra(RelevantListActivity.TITLE_INDEX, 0);

                startActivity(intent);

                break;
            case R.id.ll_concern:
                intent.putExtra(RelevantListActivity.TITLE_INDEX, 1);
                startActivity(intent);

                break;

        }
    }


}
