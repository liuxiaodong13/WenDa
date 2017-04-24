package liuxiaodong.neusoft.edu.cn.wenda.model;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by DONG on 2016/10/20.
 */

public class Question extends BmobObject {
    private MyUser author;
    private Category category;
    private String content;
    private List<BmobFile> pics;


    private BmobRelation concerned;

    private BmobFile record;
    private String recordTime;

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public BmobFile getRecord() {
        return record;
    }

    public void setRecord(BmobFile record) {
        this.record = record;
    }

    public BmobRelation getConcerned() {

        return concerned;
    }

    public void setConcerned(BmobRelation concerned) {
        this.concerned = concerned;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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


}
