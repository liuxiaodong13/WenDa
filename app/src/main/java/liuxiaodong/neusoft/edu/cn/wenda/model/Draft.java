package liuxiaodong.neusoft.edu.cn.wenda.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by DONG on 2017/1/16.
 */

public class Draft implements Parcelable {
    public enum Type {
        QUESTION, ANSWER
    }

    private Type mType;
    private Integer id;
    private String content;
    private String categoryName;
    private String time;
    private ArrayList<String> picUrls;
    private String recordPath;
    private String recordTime;

    private String questionId;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public Draft(Type type, String content, String categoryName, String time, ArrayList<String> picUrls, String recordPath, String recordTime, String questionId) {
        this.content = content;
        this.categoryName = categoryName;
        this.time = time;
        this.picUrls = picUrls;
        this.mType = type;
        this.recordPath = recordPath;
        this.recordTime = recordTime;
        this.questionId = questionId;

    }

    public Draft(Type type, Integer id, String content, String categoryName, String time, ArrayList<String> picUrls, String recordPath, String recordTime, String questionId) {
        this.id = id;
        this.content = content;
        this.categoryName = categoryName;
        this.time = time;
        this.picUrls = picUrls;
        this.mType = type;
        this.recordPath = recordPath;
        this.recordTime = recordTime;
        this.questionId = questionId;
    }

    public Draft(int type, Integer id, String content, String categoryName, String time, ArrayList<String> picUrls, String recordPath, String recordTime,String questionId) {
        this.id = id;
        this.content = content;
        this.categoryName = categoryName;
        this.time = time;
        this.picUrls = picUrls;
        this.mType = Type.values()[type];
        this.recordPath = recordPath;
        this.recordTime = recordTime;
        this.questionId = questionId;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ArrayList<String> getPicUrls() {
        return picUrls;
    }

    public void setPicUrls(ArrayList<String> picUrls) {
        this.picUrls = picUrls;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Draft{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", time='" + time + '\'' +
                ", picUrls=" + picUrls +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Draft draft = (Draft) o;

        if (mType != draft.mType) return false;
        if (!id.equals(draft.id)) return false;
        if (content != null ? !content.equals(draft.content) : draft.content != null) return false;
        if (categoryName != null ? !categoryName.equals(draft.categoryName) : draft.categoryName != null) return false;
        if (picUrls != null ? !picUrls.equals(draft.picUrls) : draft.picUrls != null) return false;
        if (recordPath != null ? !recordPath.equals(draft.recordPath) : draft.recordPath != null) return false;
        if (recordTime != null ? !recordTime.equals(draft.recordTime) : draft.recordTime != null) return false;
        return questionId != null ? questionId.equals(draft.questionId) : draft.questionId == null;

    }

    @Override
    public int hashCode() {
        int result = mType != null ? mType.hashCode() : 0;
        result = 31 * result + id.hashCode();
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (categoryName != null ? categoryName.hashCode() : 0);
        result = 31 * result + (picUrls != null ? picUrls.hashCode() : 0);
        result = 31 * result + (recordPath != null ? recordPath.hashCode() : 0);
        result = 31 * result + (recordTime != null ? recordTime.hashCode() : 0);
        result = 31 * result + (questionId != null ? questionId.hashCode() : 0);
        return result;
    }

    //
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Draft draft = (Draft) o;
//
//        if (id != null ? !id.equals(draft.id) : draft.id != null) return false;
//        if (content != null ? !content.equals(draft.content) : draft.content != null) return false;
//        if (categoryName != null ? !categoryName.equals(draft.categoryName) : draft.categoryName != null) return false;
////        if (time != null ? !time.equals(draft.time) : draft.time != null) return false;
//        return picUrls != null ? picUrls.equals(draft.picUrls) : draft.picUrls == null;
//
//    }

//    @Override
//    public int hashCode() {
//        int result = id != null ? id.hashCode() : 0;
//        result = 31 * result + (content != null ? content.hashCode() : 0);
//        result = 31 * result + (categoryName != null ? categoryName.hashCode() : 0);
////        result = 31 * result + (time != null ? time.hashCode() : 0);
//        result = 31 * result + (picUrls != null ? picUrls.hashCode() : 0);
//        return result;
//    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
        dest.writeValue(this.id);
        dest.writeString(this.content);
        dest.writeString(this.categoryName);
        dest.writeString(this.time);
        dest.writeStringList(this.picUrls);
        dest.writeString(this.recordPath);
        dest.writeString(this.recordTime);
        dest.writeString(this.questionId);
    }

    protected Draft(Parcel in) {
        int tmpMType = in.readInt();
        this.mType = tmpMType == -1 ? null : Type.values()[tmpMType];
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.content = in.readString();
        this.categoryName = in.readString();
        this.time = in.readString();
        this.picUrls = in.createStringArrayList();
        this.recordPath = in.readString();
        this.recordTime = in.readString();
        this.questionId = in.readString();
    }

    public static final Creator<Draft> CREATOR = new Creator<Draft>() {
        @Override
        public Draft createFromParcel(Parcel source) {
            return new Draft(source);
        }

        @Override
        public Draft[] newArray(int size) {
            return new Draft[size];
        }
    };
}
