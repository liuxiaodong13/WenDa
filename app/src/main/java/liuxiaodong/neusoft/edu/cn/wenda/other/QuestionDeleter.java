package liuxiaodong.neusoft.edu.cn.wenda.other;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;

/**
 * Created by DONG on 2017/3/29.
 */

public class QuestionDeleter extends Deleter {
    private Question mQuestion;

    public QuestionDeleter(Question question, onDeleteListener listener) {
        super(Question.class, listener);
        this.mQuestion = question;
    }

    @Override
    protected void prepareData() {
        objectId = mQuestion.getObjectId();
        ArrayList<String> files = new ArrayList<>();

        //准备  要删除的 图片 语音
        if (mQuestion.getPics() != null) {
            //有图片
            List<BmobFile> pics = mQuestion.getPics();
            for (int i = 0; i < pics.size(); i++) {
                files.add(pics.get(i).getUrl());
            }

        }

        if (mQuestion.getRecord() != null) {
            //有语音
            BmobFile record = mQuestion.getRecord();
            files.add(record.getUrl());

        }

        urls = files.toArray(new String[files.size()]);

    }
}
