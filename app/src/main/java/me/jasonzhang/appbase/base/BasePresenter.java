package me.jasonzhang.appbase.base;

import java.lang.ref.WeakReference;

/**
 * Created by JifengZhang on 2017/4/12.
 */

public abstract class BasePresenter<T extends BaseView> {
    private WeakReference<T> mViewRef;

    public void attachView(T view) {
        mViewRef = new WeakReference<>(view);
    }
    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }
    public T getView() {
        return mViewRef.get();
    }

    protected abstract void unSubscribe();

}
