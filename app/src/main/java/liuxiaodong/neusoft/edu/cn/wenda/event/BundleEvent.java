package liuxiaodong.neusoft.edu.cn.wenda.event;

import android.os.Bundle;

/**
 * Created by DONG on 2017/3/14.
 */

public class BundleEvent extends Event {
    private Bundle bundle;


    public BundleEvent(String message, Bundle bundle) {
        super(message);
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }



}
