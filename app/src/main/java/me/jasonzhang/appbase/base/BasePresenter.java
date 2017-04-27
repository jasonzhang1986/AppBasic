package me.jasonzhang.appbase.base;

import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import timber.log.Timber;

/**
 * Created by JifengZhang on 2017/4/12.
 */

public abstract class BasePresenter<T extends BaseView> {
    private WeakReference<T> mViewRef;
    private T nullView;
    protected BasePresenter() {
        try {
            nullView = NullView.of(internalGetViewInterfaceClass());
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("unchecked")
    private Class<T> internalGetViewInterfaceClass() {
        Class clazz = getClass();
        Type genericSuperclass;
        for (; ; ) {
            genericSuperclass = clazz.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                break;
            }
            clazz = clazz.getSuperclass();
        }
        return (Class<T>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    }

    void attachView(T view) {
        Timber.d("attachView");
        mViewRef = new WeakReference<>(view);
    }
    void detachView() {
        Timber.d("detachView");
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }
    public T getView() {
        Timber.d("getView mViewRef = " + mViewRef);
        if (mViewRef != null) {
            T view = mViewRef.get();
            if (view != null) {
                return view;
            }
        }
        return nullView;
    }

    protected abstract void unSubscribe();

}
