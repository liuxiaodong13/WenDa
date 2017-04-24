package liuxiaodong.neusoft.edu.cn.wenda.event;

import android.util.SparseArray;

import org.greenrobot.eventbus.EventBus;

/**
 *  枚举工厂
 * EventBusFactory.START.getBus();
 * Created by DONG on 2017/3/11.
 */

public enum EventBusFactory {
    CREATE(0),
    START(1);

    private int mType;

    EventBusFactory(int type) {
        mType = type;
    }

    public EventBus getBus() {
        return mBusSparseArray.get(mType);
    }

    private static SparseArray<EventBus> mBusSparseArray = new SparseArray<>(2);

    static {
        mBusSparseArray.put(CREATE.mType, EventBus.builder().build());
        mBusSparseArray.put(START.mType, EventBus.getDefault());
    }
}
