package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import de.hdodenhof.circleimageview.CircleImageView;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseActivity;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseFragment;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.fragment.HomeFragment;
import liuxiaodong.neusoft.edu.cn.wenda.fragment.NotificationFragment;
import liuxiaodong.neusoft.edu.cn.wenda.fragment.FavoriteFragment;
import liuxiaodong.neusoft.edu.cn.wenda.model.Category;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "MainActivity";
    public static final String ACTION_NOTIFICATION = "activity.notification";
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    private FragmentManager fm;
    private HomeFragment homeFragment;
    private FavoriteFragment favoriteFragment;
    private NotificationFragment notificationFragment;
    private NavigationHeaderHolder headerViewHolder;
    private List<Category> categories = new ArrayList<>();
    private int currentCategoryOrderId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        isLogin();
        initCategoryFilter();


        buildActivity(R.layout.activity_main);
        initFragment(savedInstanceState);

        switchFragmentByIntent();
        //初始化app设置
        PreferenceManager.setDefaultValues(this, R.xml.app_preference, false);
        EventManager.register(this);
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


    @Subscribe(sticky = true)
    public void onEvent(Event event) {
        Logger.d("收到事件!" + event.message);
        switch (event.message) {
            case Event.LOGIN_SUCCESS:

            case Event.LOGOUT_SUCCESS:
                initData();
                EventManager.removeAllStickyEvents();
                break;
        }

    }

    //    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent: ");
        setIntent(intent);
        switchFragmentByIntent();

    }

    private void switchFragmentByIntent() {
        String action = getIntent().getAction();
        if (action != null) {
            Log.d(TAG, "action" + action);
            switch (action) {
                case ACTION_NOTIFICATION:
                    Log.d(TAG, "switchFragmentByIntent: 切换fragment");
                    navigationView.setCheckedItem(R.id.nav_notification);
                    switchFragment(1);
                    break;
            }
        }
    }

    private void initCategoryFilter() {

        BmobConn.queryAll(Category.class, new FindListener<Category>() {

            @Override
            public void done(List<Category> list, BmobException e) {
                if (e == null) {
                    categories.clear();
                    categories.addAll(list);
                } else {
                    BmobConn.parseBmobException(e, activity);
                }
            }
        }, null);

    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        if (loginCheck())
            return;
        showActivity(NewQuestionActivity.class);
    }

    private void initFragment(Bundle savedInstanceState) {
        fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            switchFragment(0);
        }
    }

    private void switchFragment(int index) {
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:

                if (homeFragment == null) {
                    homeFragment = BaseFragment.newInstance(getString(R.string.home), HomeFragment.class);
                    transaction.add(R.id.content_main, homeFragment);
                } else {
                    transaction.show(homeFragment);
                }
                break;
            case 1:

                if (notificationFragment == null) {
                    notificationFragment = BaseFragment.newInstance(getString(R.string.notification), NotificationFragment.class);
                    transaction.add(R.id.content_main, notificationFragment);
                } else {
                    transaction.show(notificationFragment);
                }
                break;
            case 2:


                if (favoriteFragment == null) {
                    favoriteFragment = BaseFragment.newInstance(getString(R.string.favorite), FavoriteFragment.class);
                    transaction.add(R.id.content_main, favoriteFragment);
                } else {
                    transaction.show(favoriteFragment);
                }
                break;
        }

        transaction.commit();
    }


    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (notificationFragment != null) {
            transaction.hide(notificationFragment);
        }
        if (favoriteFragment != null) {
            transaction.hide(favoriteFragment);
        }

    }

    @Override
    protected void initView() {
        super.initView();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        View header = navigationView.getHeaderView(0);
        headerViewHolder = new NavigationHeaderHolder(header);
        if (!APP.isLogin) {
            setDefaultUserInfo();
        }
    }


    @Override
    protected void initToolbar() {
        toolbar.setTitle("首页");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        //先判断是否登录  只要点击的不是首页 或者设置 都需要判断
        if (id != R.id.nav_home && id != R.id.nav_settings) {
            if (loginCheck())
                return false;
        }

        if (id == R.id.nav_home) {
            switchFragment(0);
            toolbar.setTitle(item.getTitle());

        } else if (id == R.id.nav_favorite) {

            switchFragment(2);
            toolbar.setTitle(item.getTitle());
        } else if (id == R.id.nav_notification) {

            switchFragment(1);
            toolbar.setTitle(item.getTitle());
        } else if (id == R.id.nav_draft) {

            showActivity(DraftActivity.class);

        } else if (id == R.id.nav_settings) {
            showActivity(SettingActivity.class);

        }
//        invalidateOptionsMenu();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void isLogin() {
        //判断是否登录
        currentUser = MyUser.getCurrentUser(MyUser.class);
        if (!APP.isLogin) {
            //登录
            showActivity(LoginActivity.class);
        }
    }

    @Override
    protected void initData() {
        if (APP.isLogin)
            initUserInfo();
        else {
            setDefaultUserInfo();


        }

    }

    private void setDefaultUserInfo() {
        headerViewHolder.tvUserNick.setText("尚未登录");
        headerViewHolder.civUserIcon.setImageResource(R.drawable.default_usericon);

    }

    private void initUserInfo() {
        BmobConn.queryObject(MyUser.class, getCurrentUser().getObjectId(), new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e == null) {
                    setUserInfoData(myUser);
                } else {
                    BmobConn.parseBmobException(e, activity);
                }

            }
        });
    }


    private void setUserInfoData(MyUser myUser) {
        headerViewHolder.tvUserNick.setText(myUser.getNick());
        Glide.with(this).load(myUser.getUserIcon().getFileUrl())
                .diskCacheStrategy(DiskCacheStrategy.RESULT).centerCrop().into(headerViewHolder.civUserIcon);

    }

   /* public interface onFilterCategoryChangedListener {
        void onFilterChanged(Category category);
    }*/

    public void showFab() {
        floatingActionButton.show();
    }

    public void hideFab() {
        floatingActionButton.hide();
    }

    protected class NavigationHeaderHolder {
        @BindView(R.id.tv_usernickname)
        TextView tvUserNick;

        @BindView(R.id.civ_userIcon)
        CircleImageView civUserIcon;

        public NavigationHeaderHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.ll_userArea)
        public void onUserAreaClick() {
            if (!APP.isLogin) {
                T.makeS(activity, "请先登录!");
                showActivity(LoginActivity.class);
            } else {
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.OBJECT_ID, getCurrentUser().getObjectId());
                intent.putExtra(UserInfoActivity.USER_NICK, getCurrentUser().getNick());
                startActivity(intent);
            }

        }
    }

}
