package me.jasonzhang.appbase.module.main;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jasonzhang.appbase.net.API;
import me.jasonzhang.appbase.net.ApiService;
import me.jasonzhang.appbase.net.core.BaseResponse;
import me.jasonzhang.appbase.net.core.NetManager;
import me.jasonzhang.appbase.net.model.GankBean;
import me.jasonzhang.appbase.utils.LoggerUtils;

/**
 * Created by JifengZhang on 2017/4/12.
 */

public class MainPresenter implements MainContract.Presenter {
    @NonNull
    private CompositeDisposable mSubscriptions;
    private ApiService mApiService;
    private MainContract.View mMainView;
    public  MainPresenter(MainContract.View mainView) {
        mSubscriptions = new CompositeDisposable();
        mMainView = mainView;
        mApiService = NetManager.get(API.BASE_URL).getApiService(ApiService.class);
    }

    @Override
    public void subscribe() {
        testZipUseLambda();
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void testRxJava() {
        mSubscriptions.add(mApiService.getAndroidData(5, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<List<GankBean>>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull BaseResponse<List<GankBean>> gankBean) throws Exception {
                        LoggerUtils.d("checkUpgradeRx Android Data[0].desc %s", gankBean.results.get(0).desc);
                        mMainView.setResultText("testRxJava Android Data[0].desc = " + gankBean.results.get(0).desc);
                    }
                }));
    }

    @Override
    public void testZip() {
        mSubscriptions.add(Observable.zip(
                mApiService.getAndroidData(5, 1),
                mApiService.getIOSData(4,1),
                new BiFunction<BaseResponse<List<GankBean>>, BaseResponse<List<GankBean>>, List<GankBean>>() {
                    @Override
                    public List<GankBean> apply(@io.reactivex.annotations.NonNull BaseResponse<List<GankBean>> gankBean1,
                                        @io.reactivex.annotations.NonNull BaseResponse<List<GankBean>> gankBean2) throws Exception {
                        List<GankBean> list = new ArrayList<GankBean>();
                        list.addAll(gankBean1.results);
                        list.addAll(gankBean2.results);
                        LoggerUtils.d("zip apply size = %d | %s", list.size(), Thread.currentThread().getName());
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<GankBean>>() {
                               @Override
                               public void accept(@io.reactivex.annotations.NonNull List<GankBean> list) throws Exception {
                                   mMainView.setResultText("testZip result = " + list.toString());
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                                    mMainView.showError("testZip onError");
                               }
                           }, new Action() {
                               @Override
                               public void run() throws Exception {
                                    mMainView.showEnd("testZip Complete");
                               }
                           }
                ));
    }

    @Override
    public void testZipWith() {
        mSubscriptions.add(mApiService.getAndroidData(5, 1).zipWith(mApiService.getIOSData(3,1),
                (listBaseResponse1, listBaseResponse2) -> {
                    List<GankBean> list = new ArrayList<GankBean>();
                    list.addAll(listBaseResponse1.results);
                    list.addAll(listBaseResponse2.results);
                    LoggerUtils.d("zipWith size = %d | %s", list.size(), Thread.currentThread().getName());
                    return list;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<GankBean>>() {
                               @Override
                               public void accept(@io.reactivex.annotations.NonNull List<GankBean> list) throws Exception {
                                   mMainView.setResultText("testZipWith onNext content = " + list.toString());
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                                   mMainView.showError("testZipWith onError");
                               }
                           }, new Action() {
                               @Override
                               public void run() throws Exception {
                                   mMainView.showEnd("testZipWith Complete");
                               }
                           }
                ));
    }

    @Override
    public void testZipUseLambda() {
        mSubscriptions.add(Observable.zip(
                mApiService.getAndroidData(5, 1),
                mApiService.getIOSData(4,1),
                (gankBean1, gankBean2) -> {
                    List<GankBean> list = new ArrayList<>();
                    list.addAll(gankBean1.results);
                    list.addAll(gankBean2.results);
                    LoggerUtils.d("zip apply size = %d | %s", list.size(), Thread.currentThread().getName());
                    return list;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> mMainView.setResultText("testZip result = " + list.toString()),
                        throwable -> mMainView.showError("testZip onError"),
                        () -> mMainView.showEnd("testZip Complete")
                ));
    }

    @Override
    public void testZipWithUseLambda() {
        mSubscriptions.add(mApiService.getAndroidData(5, 1).zipWith(mApiService.getIOSData(3,1),
                (listBaseResponse1, listBaseResponse) -> {
                    List<GankBean> list = new ArrayList<>();
                    list.addAll(listBaseResponse1.results);
                    list.addAll(listBaseResponse.results);
                    LoggerUtils.d("testZipWithUseLambda apply size = %d | %s", list.size(), Thread.currentThread().getName());
                    return list;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( list -> {
                            LoggerUtils.d("onNext size = %s | %s", list.toString(), Thread.currentThread().getName());
                            mMainView.setResultText("testZipWithUseLambda size = " + list.toString());
                        },
                        throwable -> LoggerUtils.d(throwable,"onError"),
                        () -> LoggerUtils.d("onComplete")
                ));
    }

    @Override
    public void testComplex() {
        /**
         * request1结束后使用request1的结果请求request2，request2结果是list，对list进行分解输出
         */
        mSubscriptions.add(mApiService.getAndroidData(5, 1)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                         mMainView.showBegin("Complex invoke Begin!!!");
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .flatMap(new Function<BaseResponse<List<GankBean>>, Observable<BaseResponse<List<GankBean>>>>() {//IO线程，由observeOn()指定
                    @Override
                    public Observable<BaseResponse<List<GankBean>>> apply(
                            @io.reactivex.annotations.NonNull BaseResponse<List<GankBean>> upgradeModelBaseResponse) throws Exception {
                        return mApiService.getIOSData(3,1);
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<BaseResponse<List<GankBean>>, Observable<GankBean>>() {
                    @Override
                    public Observable<GankBean> apply(@io.reactivex.annotations.NonNull BaseResponse<List<GankBean>> listBaseResponse) throws Exception {
                        return Observable.fromIterable(listBaseResponse.results);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GankBean>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull GankBean gankBean) throws Exception {
                        LoggerUtils.d("testComplex onNext gankBean.desc = %s", gankBean.desc);
                        mMainView.setResultText("testComplex onNext "+ gankBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        LoggerUtils.d("Error!");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                         mMainView.showBegin("Complex invoke Complete!!!");
                    }
                }));
    }

    @Override
    public void testComplexUseLambda() {
        /**
         * request1结束后使用request1的结果请求request2，request2结果是list，对list进行分解输出
         * 使用lambda, 添加在开始执行的时候显示begin的提示(可以是showProgressBar)，在结束(Complete)的时候显示End的提示(隐藏ProgressBar)
         */
        mSubscriptions.add(mApiService.getAndroidData(5, 1)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe((@io.reactivex.annotations.NonNull Disposable disposable) -> mMainView.showBegin("ComplexUseLambda invoke Begin!!!"))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .flatMap((@io.reactivex.annotations.NonNull BaseResponse<List<GankBean>> upgradeModelBaseResponse) -> mApiService.getIOSData(3, 1))
                .observeOn(Schedulers.io())
                .flatMap((@io.reactivex.annotations.NonNull BaseResponse<List<GankBean>> listBaseResponse) -> Observable.fromIterable(listBaseResponse.results))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gankBean -> {
                            LoggerUtils.d("testComplexUseLambda onNext bean = %s", gankBean);
                            mMainView.setResultText("testComplexUseLambda onNext bean = " + gankBean);
                        },
                        (Throwable throwable) -> LoggerUtils.d("test8 error %s", throwable.getMessage()),
                        () ->  mMainView.showEnd("ComplexUseLambda invoke Complete!!!")));
    }
}
