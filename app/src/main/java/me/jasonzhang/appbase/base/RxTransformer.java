package me.jasonzhang.appbase.base;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by JifengZhang on 2017/4/26.
 */

public class RxTransformer {
    public static<T> ObservableTransformer<T,T> schedulersTransformer() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
