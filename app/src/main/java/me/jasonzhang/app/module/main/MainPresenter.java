package me.jasonzhang.app.module.main;


import java.util.ArrayList;
import java.util.List;

import com.leplay.android.utils.LogUtils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jasonzhang.app.base.BasePresenter;
import me.jasonzhang.app.net.API;
import me.jasonzhang.app.net.ApiService;
import me.jasonzhang.app.net.BaseResponse;
import me.jasonzhang.app.net.core.NetManager;
import me.jasonzhang.app.base.RxTransformer;
import me.jasonzhang.app.net.model.GankBean;

/**
 * Created by JifengZhang on 2017/4/12.
 */

class MainPresenter extends BasePresenter<MainView>{
    @NonNull
    private CompositeDisposable mSubscriptions;
    private ApiService mApiService;
    MainPresenter() {
        mSubscriptions = new CompositeDisposable();
        mApiService = NetManager.get(API.BASE_URL).getApiService(ApiService.class);
    }

    void testRxJava() {
        mSubscriptions.add(mApiService.getAndroidData(5, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<List<GankBean>>>() {
                    @Override
                    public void accept(@NonNull BaseResponse<List<GankBean>> gankBean) throws Exception {
                        LogUtils.d("checkUpgradeRx Android Data[0].desc %s", gankBean.results.get(0).desc);
                        getView().setResultText("testRxJava Android Data[0].desc = " + gankBean.results.get(0).desc);
                    }
                }));
    }

    void testZip() {
        mSubscriptions.add(Observable.zip(
                mApiService.getAndroidData(5, 1),
                mApiService.getIOSData(4,1),
                new BiFunction<BaseResponse<List<GankBean>>, BaseResponse<List<GankBean>>, List<GankBean>>() {
                    @Override
                    public List<GankBean> apply(@NonNull BaseResponse<List<GankBean>> gankBean1,
                                        @NonNull BaseResponse<List<GankBean>> gankBean2) throws Exception {
                        List<GankBean> list = new ArrayList<GankBean>();
                        list.addAll(gankBean1.results);
                        list.addAll(gankBean2.results);
                        LogUtils.d("zip apply size = %d | %s", list.size(), Thread.currentThread().getName());
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<GankBean>>() {
                               @Override
                               public void accept(@NonNull List<GankBean> list) throws Exception {
                                   getView().setResultText("testZip result = " + list.toString());
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(@NonNull Throwable throwable) throws Exception {
                                    getView().showError("testZip onError");
                               }
                           }, new Action() {
                               @Override
                               public void run() throws Exception {
                                    getView().showEnd("testZip Complete");
                               }
                           }
                ));
    }

    void testZipWith() {
        mSubscriptions.add(mApiService.getAndroidData(5, 1).zipWith(mApiService.getIOSData(3,1),
                new BiFunction<BaseResponse<List<GankBean>>, BaseResponse<List<GankBean>>, List<GankBean>>() {
                    @Override
                    public List<GankBean> apply(@NonNull BaseResponse<List<GankBean>> listBaseResponse1,
                                                @NonNull BaseResponse<List<GankBean>> listBaseResponse2) throws Exception {
                        List<GankBean> list = new ArrayList<GankBean>();
                        list.addAll(listBaseResponse1.results);
                        list.addAll(listBaseResponse2.results);
                        LogUtils.d("zipWith size = %d | %s", list.size(), Thread.currentThread().getName());
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<GankBean>>() {
                               @Override
                               public void accept(@NonNull List<GankBean> list) throws Exception {
                                   getView().setResultText("testZipWith onNext content = " + list.toString());
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(@NonNull Throwable throwable) throws Exception {
                                   getView().showError("testZipWith onError");
                               }
                           }, new Action() {
                               @Override
                               public void run() throws Exception {
                                   getView().showEnd("testZipWith Complete");
                               }
                           }
                ));
    }

    void testZipUseLambda() {
        mSubscriptions.add(Observable.zip(
                mApiService.getAndroidData(5, 1),
                mApiService.getIOSData(4,1),
                (gankBean1, gankBean2) -> {
                    List<GankBean> list = new ArrayList<>();
                    list.addAll(gankBean1.results);
                    list.addAll(gankBean2.results);
                    LogUtils.d("zip apply size = %d | %s", list.size(), Thread.currentThread().getName());
                    return list;
                })
                .compose(RxTransformer.schedulersTransformer())
                .subscribe(list -> getView().setResultText("testZip result = " + list.toString()),
                        throwable -> getView().showError("testZip onError"),
                        () -> getView().showEnd("testZip Complete")
                ));
    }

    void testZipWithUseLambda() {
        mSubscriptions.add(mApiService.getAndroidData(5, 1).zipWith(mApiService.getIOSData(3,1),
                (listBaseResponse1, listBaseResponse) -> {
                    List<GankBean> list = new ArrayList<>();
                    list.addAll(listBaseResponse1.results);
                    list.addAll(listBaseResponse.results);
                    LogUtils.d("testZipWithUseLambda apply size = %d | %s", list.size(), Thread.currentThread().getName());
                    return list;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( list -> {
                            LogUtils.d("onNext size = %s | %s", list.toString(), Thread.currentThread().getName());
                            getView().setResultText("testZipWithUseLambda size = " + list.toString());
                        },
                        throwable -> LogUtils.d(throwable, "testZipWithUseLambda"),
                        () -> LogUtils.d("onComplete")
                ));
    }

    void testComplex() {
        /**
         * request1结束后使用request1的结果请求request2，request2结果是list，对list进行分解输出
         */
        mSubscriptions.add(mApiService.getAndroidData(5, 1)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                         getView().showBegin("Complex invoke Begin!!!");
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())//IO线程，由observeOn()指定
                .flatMap(new Function<BaseResponse<List<GankBean>>, Observable<BaseResponse<List<GankBean>>>>() {
                    @Override
                    public Observable<BaseResponse<List<GankBean>>> apply(
                            @NonNull BaseResponse<List<GankBean>> upgradeModelBaseResponse) throws Exception {
                        return mApiService.getIOSData(3,1);
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<BaseResponse<List<GankBean>>, Observable<GankBean>>() {
                    @Override
                    public Observable<GankBean> apply(@NonNull BaseResponse<List<GankBean>> listBaseResponse) throws Exception {
                        return Observable.fromIterable(listBaseResponse.results);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GankBean>() {
                    @Override
                    public void accept(@NonNull GankBean gankBean) throws Exception {
                        LogUtils.d("testComplex onNext gankBean.desc = %s", gankBean.desc);
                        getView().setResultText("testComplex onNext "+ gankBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        LogUtils.d("Error!");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                         getView().showBegin("Complex invoke Complete!!!");
                    }
                }));
    }

    void testComplexUseLambda() {
        /**
         * request1结束后使用request1的结果请求request2，request2结果是list，对list进行分解输出
         * 使用lambda, 添加在开始执行的时候显示begin的提示(可以是showProgressBar)，在结束(Complete)的时候显示End的提示(隐藏ProgressBar)
         */
        mSubscriptions.add(mApiService.getAndroidData(5, 1)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe((@NonNull Disposable disposable) -> getView().showBegin("ComplexUseLambda invoke Begin!!!"))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .flatMap((@NonNull BaseResponse<List<GankBean>> upgradeModelBaseResponse) -> mApiService.getIOSData(3, 1))
                .observeOn(Schedulers.io())
                .flatMap((@NonNull BaseResponse<List<GankBean>> listBaseResponse) -> Observable.fromIterable(listBaseResponse.results))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gankBean -> {
                            LogUtils.d("testComplexUseLambda onNext bean = %s", gankBean);
                            getView().setResultText("testComplexUseLambda onNext bean = " + gankBean);
                        },
                        (Throwable throwable) -> LogUtils.d("test8 error %s", throwable.getMessage()),
                        () ->  getView().showEnd("ComplexUseLambda invoke Complete!!!")));
    }


    @Override
    protected void unSubscribe() {
        mSubscriptions.clear();
    }
}
