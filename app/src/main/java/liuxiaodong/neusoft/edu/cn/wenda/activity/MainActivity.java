package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseActivity;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseFragment;
import liuxiaodong.neusoft.edu.cn.wenda.fragment.CollectionFragment;
import liuxiaodong.neusoft.edu.cn.wenda.fragment.ConcernFragment;
import liuxiaodong.neusoft.edu.cn.wenda.fragment.HomeFragment;
import liuxiaodong.neusoft.edu.cn.wenda.fragment.SessionFragment;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    private FragmentManager fm;
    private HomeFragment homeFragment;
    private CollectionFragment collectionFragment;
    private SessionFragment sessionFragment;
    private ConcernFragment concernFragment;

    @OnClick(R.id.fab)
    public void onClickFab() {
        T.makeS(this, "test");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLogin();


        buildActivity(R.layout.activity_main);
        initFragment(savedInstanceState);

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
        switch (index)
        {
            case 0:
                if (homeFragment == null) {
                    homeFragment = BaseFragment.newInstance(getString(R.string.home), HomeFragment.class);
                    transaction.add(R.id.content_main, homeFragment);
                } else {
                    transaction.show(homeFragment);
                }
                break;
            case 1:
                if (sessionFragment == null) {
                    sessionFragment = BaseFragment.newInstance(getString(R.string.Session), SessionFragment.class);
                    transaction.add(R.id.content_main, sessionFragment);
                } else {
                    transaction.show(sessionFragment);
                }
                break;
            case 2:
                if (concernFragment == null) {
                    concernFragment = BaseFragment.newInstance(getString(R.string.concern), ConcernFragment.class);
                    transaction.add(R.id.content_main, concernFragment);
                } else {
                    transaction.show(concernFragment);
                }
                break;
            case 3:
                if (collectionFragment == null) {
                    collectionFragment = BaseFragment.newInstance(getString(R.string.collection), CollectionFragment.class);
                    transaction.add(R.id.content_main, collectionFragment);
                } else {
                    transaction.show(collectionFragment);
                }
                break;
        }

        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (collectionFragment != null) {
            transaction.hide(collectionFragment);
        }
        if (concernFragment != null) {
            transaction.hide(concernFragment);
        }
        if (sessionFragment != null) {
            transaction.hide(sessionFragment);
        }

    }

    @Override
    protected void initView() {
        super.initView();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    protected void initToolbar() {
        toolbar.setTitle(R.string.home);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            switchFragment(0);

        } else if (id == R.id.nav_session) {
            switchFragment(1);
        } else if (id == R.id.nav_concern) {
            switchFragment(2);
        } else if (id == R.id.nav_collection) {
            switchFragment(3);
        }
        else if (id == R.id.nav_draft) {

        } else if (id == R.id.nav_settings) {

        }
        invalidateOptionsMenu();

        toolbar.setTitle(item.getTitle());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void isLogin() {
        //判断是否登录
        currentUser =  BmobUser.getCurrentUser(MyUser.class);
        if (currentUser == null) {
            //登录
            showAcitvity(LoginActivity.class);

        }


    }


}
