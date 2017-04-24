package liuxiaodong.neusoft.edu.cn.wenda.event;

import org.greenrobot.eventbus.EventBus;

import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

/**
 * Created by DONG on 2017/3/14.
 */

public class EventManager {
    public static void register(Object object) {
        if (!EventBus.getDefault().isRegistered(object)) {
            EventBus.getDefault().register(object);
            Logger.d("注册！");
        }

    }

    public static void unregister(Object object) {
        if (EventBus.getDefault().isRegistered(object)) {
            EventBus.getDefault().unregister(object);
        }
    }

    public static void post(Object object) {
        EventBus.getDefault().post(object);
    }

    public static void postSticky(Object object) {
        EventBus.getDefault().postSticky(object);
    }

    public static void removeAllStickyEvents() {
        EventBus.getDefault().removeAllStickyEvents();
    }
}
