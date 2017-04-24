package liuxiaodong.neusoft.edu.cn.wenda.model;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by DONG on 2016/10/20.
 */

public class MyUser extends BmobUser {
    private String nick;
    private BmobFile userIcon;
    private String desc;
    private String location;
    private Boolean gender;
    private BmobRelation good_at;
    private BmobFile background;
    private BmobRelation concerns;

    public BmobRelation getGood_at() {
        return good_at;
    }

    public void setGood_at(BmobRelation good_at) {
        this.good_at = good_at;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public BmobFile getBackground() {
        return background;
    }

    public void setBackground(BmobFile background) {
        this.background = background;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BmobRelation getConcerns() {
        return concerns;
    }

    public void setConcerns(BmobRelation concerns) {
        this.concerns = concerns;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public BmobFile getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(BmobFile userIcon) {
        this.userIcon = userIcon;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "MyUser{" +
                "nick='" + nick + '\'' +
                ", userIcon=" + userIcon +
                ", desc='" + desc + '\'' +
                '}';
    }
}
