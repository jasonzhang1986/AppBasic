package me.jasonzhang.appbase.base;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by JifengZhang on 2017/4/12.
 */

public abstract class BaseFragment<T extends BasePresenter> extends Fragment {
    protected T presenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        presenter = createPresenter();
        if (presenter!=null) {
            //noinspection unchecked
            presenter.attachView((BaseView) this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter!=null) {
            presenter.detachView();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (presenter!=null) {
            presenter.unSubscribe();
        }
    }

    protected abstract T createPresenter();
}
