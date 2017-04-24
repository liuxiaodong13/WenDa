package liuxiaodong.neusoft.edu.cn.wenda.model;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by DONG on 2016/10/20.
 */

public class Answer extends BmobObject {
    private Question question;
    private MyUser author;
    private String content;
    private List<BmobFile> pics;
    private Integer likescount;
    private BmobRelation likes;

    private BmobFile record;
    private String recordTime;

    public BmobFile getRecord() {
        return record;
    }

    public void setRecord(BmobFile record) {
        this.record = record;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public Integer getLikescount() {
        return likescount;
    }

    public void setLikescount(Integer likescount) {
        this.likescount = likescount;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<BmobFile> getPics() {
        return pics;
    }

    public void setPics(List<BmobFile> pics) {
        this.pics = pics;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "question=" + question +
                ", author=" + author +
                ", content='" + content + '\'' +
                ", pics=" + pics +
                ", likescount=" + likescount +
                ", likes=" + likes +
                '}';
    }
}
