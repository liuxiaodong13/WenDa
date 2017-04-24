package liuxiaodong.neusoft.edu.cn.wenda.model;

/**
 * Created by DONG on 2017/3/29.
 */

public class DraftWrapper {
    private Draft mDraft;
    private Question mQuestion;

    public DraftWrapper(Draft draft, Question question) {
        mDraft = draft;
        mQuestion = question;
    }

    public Draft getDraft() {
        return mDraft;
    }

    public void setDraft(Draft draft) {
        mDraft = draft;
    }

    public Question getQuestion() {
        return mQuestion;
    }

    public void setQuestion(Question question) {
        mQuestion = question;
    }
}
