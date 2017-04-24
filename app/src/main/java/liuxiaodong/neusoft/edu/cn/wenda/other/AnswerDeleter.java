package liuxiaodong.neusoft.edu.cn.wenda.other;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import liuxiaodong.neusoft.edu.cn.wenda.model.Answer;

/**
 * Created by DONG on 2017/3/29.
 */

public class AnswerDeleter extends Deleter {
    private Answer mAnswer;

    public AnswerDeleter(Answer answer, onDeleteListener listener) {
        super(Answer.class, listener);
        this.mAnswer = answer;
    }

    @Override
    protected void prepareData() {
        objectId = mAnswer.getObjectId();
        ArrayList<String> files = new ArrayList<>();

        //准备  要删除的 图片 语音
        if (mAnswer.getPics() != null) {
            //有图片
            List<BmobFile> pics = mAnswer.getPics();
            for (int i = 0; i < pics.size(); i++) {
                files.add(pics.get(i).getUrl());
            }


        }

        if (mAnswer.getRecord() != null) {
            //有语音
            BmobFile record = mAnswer.getRecord();
            files.add(record.getUrl());

        }

        urls = files.toArray(new String[files.size()]);

    }
}
