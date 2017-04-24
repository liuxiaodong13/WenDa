package liuxiaodong.neusoft.edu.cn.wenda.model;

/**
 * Created by DONG on 2016/11/5.
 */

public class AnswerWrapper {
    private Answer answer;
    private boolean isLike;


    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public AnswerWrapper(Answer answer, boolean isLike) {
        this.answer = answer;
        this.isLike = isLike;
    }

    @Override
    public String toString() {
        return "AnswerWrapper{" +
                "answer=" + answer.getContent() +
                ", isLike=" + isLike +
                '}';
    }
}
