package liuxiaodong.neusoft.edu.cn.wenda.fragment.tabpagerfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import butterknife.BindView;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseFragment;
import liuxiaodong.neusoft.edu.cn.wenda.other.AppSettings;

/**
 * Created by DONG on 2016/11/3.
 */

public class PagerFragment extends BaseFragment {
    protected int listItems = AppSettings.getListItemCounts();
    public static final String QUESTION_ID = "questionId";
    protected String questionId;
    @BindView(R.id.recyclerview)
    protected XRecyclerView recyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionId = getArguments().getString(QUESTION_ID);
    }

    @Override
    protected void initView() {
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

    }
}
