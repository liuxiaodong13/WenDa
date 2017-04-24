package liuxiaodong.neusoft.edu.cn.wenda.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;

import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;

/**
 * Created by DONG on 2016/10/19.
 */

public abstract class OnEventBackNavigationActivity extends BaseActivity {
    @Override
    protected void buildActivity(int layout) {
        super.buildActivity(layout);
        initBackNavigation();
    }

    private void initBackNavigation() {
        setSupportActionBar(toolbar);
        setNavigationOnClick();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventManager.register(this);

    }

    @Subscribe(sticky = true)
    public void onReceive(Event event) {
        onEvent(event);
        EventManager.removeAllStickyEvents();

    }

    @Override
    protected void onDestroy() {
        EventManager.unregister(this);
        super.onDestroy();

    }

    public abstract void onEvent(Event event);


}
