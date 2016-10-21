package liuxiaodong.neusoft.edu.cn.wenda.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import liuxiaodong.neusoft.edu.cn.wenda.R;

/**
 * Created by DONG on 2016/10/19.
 */

public class BaseFragment extends Fragment {
    private static final String ARGS = "args";
    protected static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    protected Activity mActivity;

    protected View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            }else {
                ft.show(this);
            }

            ft.commit();
        }
    }

    public static <T> T newInstance(String title, Class<? extends Fragment> clazz) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS, title);
        Fragment fragment = null;
        try {
            fragment =  clazz.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (fragment != null )
            fragment.setArguments(bundle);

        return (T)fragment;

    }

    protected View buildFragment(LayoutInflater inflater, ViewGroup container, int layout, Bundle savedInstanceState) {
        view = inflater.inflate(layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    protected View buildRecyclerViewFragment(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        view = inflater.inflate(R.layout.fragment_base_recyclerview, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    protected void initData() {

    }

    protected void initView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }
}
