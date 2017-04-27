package me.jasonzhang.appbase.base;

import android.support.v4.app.Fragment;

/**
 * Created by JifengZhang on 2017/4/12.
 */

public class BaseFragment<T extends BasePresenter> extends Fragment {
    protected T presenter;
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (presenter!=null) {
            presenter.unSubscribe();
        }
    }
}
