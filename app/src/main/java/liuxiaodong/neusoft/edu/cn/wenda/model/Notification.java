package liuxiaodong.neusoft.edu.cn.wenda.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by DONG on 2016/12/28.
 */

public class Notification extends BmobObject {
    private Integer type;
    private MyUser user;
    private String title;  //通知的标题 如  “新通知”
    private String content;  //通知的描述详情  如 “您的提问有一条新回答”
    private String typeDesc;   //通知的类别信息  如 “系统消息”
    private String extra;  //通知附带的内容 如 回答的ID 问题的 Id等等
    private Boolean read;  //


    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "type=" + type +
                ", user=" + user +
                ", message='" + title + '\'' +
                ", content='" + content + '\'' +
                ", typeDesc='" + typeDesc + '\'' +
                ", extra='" + extra + '\'' +
                ", read=" + read +
                '}';
    }
}

