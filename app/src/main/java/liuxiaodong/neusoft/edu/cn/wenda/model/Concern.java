package liuxiaodong.neusoft.edu.cn.wenda.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by DONG on 2016/10/20.
 */

public class Concern extends BmobObject {
    private MyUser user;
    private BmobRelation concerns;

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public BmobRelation getConcerns() {
        return concerns;
    }

    public void setConcerns(BmobRelation concerns) {
        this.concerns = concerns;
    }
}
