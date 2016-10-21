package liuxiaodong.neusoft.edu.cn.wenda.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import liuxiaodong.neusoft.edu.cn.wenda.base.BaseFragment;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

/**
 * Created by DONG on 2016/10/19.
 */

public class CollectionFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return buildRecyclerViewFragment(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView() {
        T.makeS(mActivity, "收藏");
    }
}
