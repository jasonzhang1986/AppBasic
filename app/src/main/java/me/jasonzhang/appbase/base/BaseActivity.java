package me.jasonzhang.appbase.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by JifengZhang on 2017/4/12.
 */

public abstract class BaseActivity<T extends BasePresenter> extends Activity{
    protected T presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presenter!=null) {
            presenter.subscribe();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (presenter!=null) {
            presenter.unsubscribe();
        }
    }
}
