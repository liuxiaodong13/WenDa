package liuxiaodong.neusoft.edu.cn.wenda.base;

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
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.activity.LoginActivity;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Constants;

import static cn.bmob.v3.BmobUser.getCurrentUser;

/**
 * Created by DONG on 2016/10/19.
 */

public class BaseActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    protected MyUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalInit();

    }

    private void globalInit() {
        Bmob.initialize(this, Constants.APP_KEY);

    }

    protected void showAcitvity(Class<? extends BaseActivity> acClass) {
        startActivity(new Intent(this, acClass));

    }

    protected void buildActivity(int layout) {
        setContentView(layout);
        ButterKnife.bind(this);
        initToolbar();
        initView();
        initData();
    }

    protected void initData() {

    }

    protected void initView() {

    }

    protected void initToolbar() {

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
                    BaseActivity.this.finish();

                }
            });
        }

    }
}
