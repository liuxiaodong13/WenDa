package liuxiaodong.neusoft.edu.cn.wenda.other;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;

/**
 * Created by DONG on 10/31/2016.
 */

public class QuestionDataLoader {
    public BmobQuery<Question> bmobQuery;
    private int pageIndex = 1;
    private int homeItems;

    private Init init;

    public interface Init {
        void init(BmobQuery query);
    }

    public QuestionDataLoader(Init init, int pageIndex, int homeItems) {
        bmobQuery = new BmobQuery<>();
        this.init = init;
        this.pageIndex = pageIndex;
        this.homeItems = homeItems;

    }

    public QuestionDataLoader(Init init) {
        bmobQuery = new BmobQuery<>();
        this.init = init;


    }


    public void getQuestionData(FindListener<Question> findListener) {
        if (init != null)
            init.init(bmobQuery);
        pageIndex = 1;
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        bmobQuery.setLimit(AppSettings.getListItemCounts());
        bmobQuery.findObjects(findListener);
    }

    public void loadingMore(FindListener<Question> findListener) {
        pageIndex++;
        BmobQuery<Question> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setSkip(pageIndex * homeItems);
        query.include("author,category");
        query.order("-createdAt"); //按照创建时间 将序排列
        query.findObjects(findListener);
    }

}
