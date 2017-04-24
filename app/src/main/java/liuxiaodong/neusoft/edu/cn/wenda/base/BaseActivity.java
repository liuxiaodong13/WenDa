package liuxiaodong.neusoft.edu.cn.wenda.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.activity.LoginActivity;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.other.AppSettings;
import liuxiaodong.neusoft.edu.cn.wenda.utils.DefaultValue;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;
import liuxiaodong.neusoft.edu.cn.wenda.utils.ThemeUtils;

/**
 * Created by DONG on 2016/10/19.
 */

public class BaseActivity extends AppCompatActivity implements IReload {
    public static final String TAG = "BaseActivity";
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    protected MyUser currentUser;
    protected BaseActivity activity = this;
    private int theme = DefaultValue.DEFAULT_THEME;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            this.theme = this.configTheme();
        } else {
            this.theme = savedInstanceState.getInt("theme");
        }

        if(this.theme > 0) {
            this.setTheme(this.theme);
        }
        super.onCreate(savedInstanceState);
        //APP.getInstance().setTheme();

        getExtraData();
        globalInit();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        int i = ThemeUtils.themeArr[AppSettings.getThemeColor()];
        if (i != theme) {
            //主题更改
            reload();

        }

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
        super.onDestroy();
        APP.removeActivity(this.getClass().getName());

    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("theme", this.theme);
    }

    private int configTheme() {
        int theme = ThemeUtils.themeArr[AppSettings.getThemeColor()];
       return theme;
    }


    protected void getExtraData() {

    }

    protected void globalInit() {
        //Bmob.initialize(this, Consts.APP_KEY);

        addToDestroyMap(this, this.getClass().getName());

    }

    protected void showActivity(Class<? extends Activity> acClass) {
        startActivity(new Intent(this, acClass));

    }

    protected void buildActivity(int layout) {
        setContentView(layout);
        ButterKnife.bind(this);
        initView();
        initToolbar();
        initListener();
        initData();

    }
    protected void initData() {

    }

    protected void initListener() {

    }

    protected void initView() {

    }

    protected boolean loginCheck() {

        if (!APP.isLogin) {
            T.makeS(this, "尚未登录!");
            showActivity(LoginActivity.class);
            return true;
        }
        return false;
    }

    protected void initToolbar() {


    }

    protected  void initTitle(String title) {
        toolbar.setTitle(title);
    }



    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    protected void setNavigationOnClick() {
        if (toolbar != null) {
            Log.i("tag", "setNavigationOnClick:  true");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNavigationOnClick();
                }
            });
        }

    }

    protected void onNavigationOnClick() {
        BaseActivity.this.finish();

    }

    public MyUser getCurrentUser() {
        return  currentUser = MyUser.getCurrentUser(MyUser.class);
    }

    protected void addToDestroyMap(Activity activity, String activityName) {
        APP.addActivity(activity, activityName);

    }


    @Override
    public void reload() {
        Intent intent = this.getIntent();
        this.overridePendingTransition(0, 0);
        intent.addFlags(65536);
        this.finish();
        this.overridePendingTransition(0, 0);
        this.startActivity(intent);
    }
}
