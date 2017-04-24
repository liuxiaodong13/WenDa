package liuxiaodong.neusoft.edu.cn.wenda.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by DONG on 2016/10/20.
 */

public class Collection  extends BmobObject {
    private MyUser user;
    private BmobRelation collects;

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public BmobRelation getCollects() {
        return collects;
    }

    public void setCollects(BmobRelation collects) {
        this.collects = collects;
    }
}
