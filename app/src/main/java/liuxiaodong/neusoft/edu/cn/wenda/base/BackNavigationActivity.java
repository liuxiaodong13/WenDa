package liuxiaodong.neusoft.edu.cn.wenda.base;

/**
 * Created by DONG on 2016/10/19.
 */

public class BackNavigationActivity extends BaseActivity {
    @Override
    protected void buildActivity(int layout) {
        super.buildActivity(layout);
        initBackNavigation();
    }

    private void initBackNavigation() {
        setSupportActionBar(toolbar);
        setNavigationOnClick();
    }





}
