package liuxiaodong.neusoft.edu.cn.wenda;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DONG on 2016/10/19.
 */

public class BaseActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    protected void buildActivity(int layout) {
        setContentView(layout);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    protected void initData() {

    }

    protected void initView() {
        initToolbar();
    }

    protected void initToolbar() {

    }

}
