package liuxiaodong.neusoft.edu.cn.wenda.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.activity.MainActivity;
import liuxiaodong.neusoft.edu.cn.wenda.adapter.HomeFragmentPhotoAdapter;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseFragment;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.listener.base.onQuestionItemClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.Category;
import liuxiaodong.neusoft.edu.cn.wenda.model.CategoryTag;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.other.AppSettings;
import liuxiaodong.neusoft.edu.cn.wenda.other.MediaManager;
import liuxiaodong.neusoft.edu.cn.wenda.other.QuestionDataLoader;
import liuxiaodong.neusoft.edu.cn.wenda.ui.MyRecyclerView;
import liuxiaodong.neusoft.edu.cn.wenda.ui.RecordView;
import liuxiaodong.neusoft.edu.cn.wenda.ui.UserIconView;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

/**
 * Created by DONG on 2016/10/19.
 */

public class HomeFragment extends BaseFragment implements XRecyclerView.LoadingListener {
    public static final String TAG = "HomeFragment";
    @BindView(R.id.recyclerview)
    XRecyclerView recyclerView;
    private int homePageAmount = AppSettings.getListItemCounts();
    private int pageIndex = 1;
    private HomeAdapter adapter;
    private MainActivity mainActivity;
    private Toolbar toolbar;
    private QuestionDataLoader.Init init = new QuestionDataLoader.Init() {
        @Override
        public void init(BmobQuery query) {
            query.include("author,category");
            query.order("-createdAt"); //按照创建时间 将序排列
            query.setLimit(homePageAmount);
        }
    };
    private QuestionDataLoader questionDataLoader = new QuestionDataLoader(init);


    private List<Category> categories = new ArrayList<>();
    private int currentCategoryOrderId = 0;

    private RecordView mCurrentRecordView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mainActivity = (MainActivity) mActivity;
        toolbar = mainActivity.getToolbar();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return buildRecyclerViewFragment(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d("HomeFragment: onStart()");
        EventManager.register(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    public void onStop() {
        Logger.d("homefragment OnStop（）！");
        EventManager.unregister(this);
        super.onStop();
        MediaManager.stop();
    }

    @Override
    public void onDestroy() {
        Logger.d("homefragment onDestroy() ！");
        super.onDestroy();
        MediaManager.release();

    }


    @Subscribe(sticky = true)
    public void onEvent(Event event) {
        if (event.message.equals(Event.REFRESH_QUESTION_LIST)) {
            Logger.d("onEvent:REFRESH_QUESTION_LIST");
            recyclerView.setRefreshing(true);
            initData();
            EventManager.removeAllStickyEvents();
        }

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) { //当切换为显示状态
            mainActivity.showFab();
        }
    }

    @Override
    protected void initView() {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingListener(this);


        adapter = new HomeAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setRefreshing(true);

    }

    private void loadingMore() {
        questionDataLoader.bmobQuery.setSkip(homePageAmount * pageIndex);
        questionDataLoader.getQuestionData(new FindListener<Question>() {
            @Override
            public void done(List<Question> list, BmobException e) {
                recyclerView.loadMoreComplete();
                if (e == null) {
                    if (list.size() > 0)
                        pageIndex++;
                    adapter.addData(list);
                }

            }
        });

    }

    @Override
    protected void initData() {
        Logger.d("HomeFragment:initData()");
        initCategoryFilter();
        pageIndex = 1;
        questionDataLoader.bmobQuery.setSkip(0);
        questionDataLoader.getQuestionData(new FindListener<Question>() {
            @Override
            public void done(List<Question> list, BmobException e) {

                recyclerView.refreshComplete();
                if (e == null) { //这里 e为null 即 拉取数据成功 可以直接使用list
                    adapter.setData(list);
                } else {
                    Log.i(TAG, "done: " + e);
                }
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        View menuItemView = toolbar.findViewById(id);
        View menuItemView = toolbar.findViewById(id);
        switch (id) {
            case R.id.action_exit:
                APP.destroyActivity();
                break;
            case R.id.action_filter:
                showFilterMenu(menuItemView);
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void switchTheme() {
        mActivity.setTheme(R.style.BaseIndigo);
        mActivity.reload();
    }


    private void onFilterChanged(Category category) {
        if (category == null) {
            Log.i(TAG, "onFilterChanged: category:" + "所有");
            questionDataLoader = new QuestionDataLoader(init);
            initData();
        } else {

            Log.i(TAG, "onFilterChanged: category:" + category.getName());
            pageIndex = 1;
            questionDataLoader = new QuestionDataLoader(init);
            questionDataLoader.bmobQuery.addWhereEqualTo("category", category.getObjectId());

            questionDataLoader.getQuestionData(new FindListener<Question>() {
                @Override
                public void done(List<Question> list, BmobException e) {
                    recyclerView.refreshComplete();
                    if (e == null) { //这里 e为null 即 拉取数据成功 可以直接使用list
                        adapter.setData(list);
                    }
                }
            });
        }
    }


    private void showFilterMenu(View actionView) {
        PopupMenu popupMenu = new PopupMenu(getContext(), actionView);
        //popupMenu.getMenu().add(Menu.NONE, Menu.FIRST + 1, 0, "");
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "所有问题");
        for (int i = 0; i < categories.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, Menu.FIRST + i + 1, i + 1, categories.get(i).getName());

        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() != currentCategoryOrderId) {
                    if (item.getOrder() == 0) {
                        onFilterChanged(null);
                        currentCategoryOrderId = 0;
                        toolbar.setTitle("所有问题");
                    } else {
                        onFilterChanged(categories.get(item.getOrder() - 1));
                        currentCategoryOrderId = item.getOrder();
                        toolbar.setTitle(item.getTitle());
                    }

                } else {
                    //do nothing
                }
                return true;
            }
        });
        popupMenu.show();


    }

    private void initCategoryFilter() {

        BmobConn.queryAll(Category.class, new FindListener<Category>() {

            @Override
            public void done(List<Category> list, BmobException e) {
                if (e == null) {
                    categories.clear();
                    categories.addAll(list);
                } else {
                    BmobConn.parseBmobException(e, mActivity);
                }
            }
        }, null);

    }

    @Override
    public void onRefresh() {
        initData();


    }

    @Override
    public void onLoadMore() {
        loadingMore();


    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        List<Question> questions = new ArrayList<>();

        public HomeAdapter() {
        }


        public HomeAdapter(List<Question> questions) {
            this.setData(questions);
        }

        public void setData(List<Question> data) {
            questions.clear();
            questions.addAll(data);
            notifyDataSetChanged();

        }

        public void addData(List<Question> data) {
            if (data.size() > 0) {
                questions.addAll(data);
                notifyDataSetChanged();
            }
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.question_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.mRecordView.setVisibility(View.GONE);
            Log.i(TAG, "onBindViewHolder: + myUser" + questions.get(position).getAuthor());
            if (questions.get(position).getAuthor().getUserIcon() != null) {

                Glide.with(HomeFragment.this).load(questions.get(position).getAuthor().getUserIcon().getFileUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.icvUserIcon.getCivUserIcon());
            }


            holder.icvUserIcon.setGender(questions.get(position).getAuthor().getGender());

            holder.tvContent.setText(questions.get(position).getContent());
            holder.tvCreateTime.setText(questions.get(position).getCreatedAt());
            holder.tvNickName.setText(questions.get(position).getAuthor().getNick());

            // Logger.i(TAG, "onBindViewHolder: category:" + questions.get(position).getCategory().getName());
            holder.tvCategory.setText(questions.get(position).getCategory().getName());
            holder.ivCategory.setImageResource(new CategoryTag(questions.get(position).getCategory()).getCategoryImageResId());
            holder.cvItem.setOnClickListener(new onQuestionItemClickListener(questions.get(position).getObjectId(), mActivity));

            holder.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//            holder.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
            holder.recyclerView.setItemAnimator(new DefaultItemAnimator());

            List<BmobFile> pics = questions.get(position).getPics();
            holder.recyclerView.setAdapter(null);
            if (pics != null && pics.size() > 0) {
                List<String> photos = new ArrayList<>();
                for (BmobFile pic : pics) {
                    photos.add(pic.getFileUrl());
                }
                holder.recyclerView.setAdapter(new HomeFragmentPhotoAdapter(mActivity, photos));
                holder.recyclerView.setClickBlankAreaListener(new onQuestionItemClickListener(questions.get(position).getObjectId(), mActivity));
            }

            //设置语音图标显示
            final Question question = questions.get(position);
            if (question.getRecord() != null) {
                holder.mRecordView.setVisibility(View.VISIBLE);
                holder.mRecordView.setTime(question.getRecordTime());
                holder.mRecordView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCurrentRecordView != null) {
                            mCurrentRecordView.stopAnim();
                            mCurrentRecordView = null;
                        }
                        mCurrentRecordView = holder.mRecordView;
                        holder.mRecordView.startAnim();
                        MediaManager.playNetSound(question.getRecord(), new MediaPlayer.OnCompletionListener() {
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

        @Override
        public int getItemCount() {
            return questions.size();
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


    }


}
