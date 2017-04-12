package me.jasonzhang.appbase.module.main;

import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jasonzhang.appbase.*;
import me.jasonzhang.appbase.net.API;
import me.jasonzhang.appbase.net.ApiService;
import me.jasonzhang.appbase.net.core.BaseResponse;
import me.jasonzhang.appbase.net.core.NetManager;
import me.jasonzhang.appbase.net.model.InstallNeceModel;
import me.jasonzhang.appbase.net.model.UpgradeModel;
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
        mSubscriptions.add(mApiService.checkUpgrade(5800, "LETV_X443")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<UpgradeModel>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull BaseResponse<UpgradeModel> upgradeModelBaseResponse) throws Exception {
                        LoggerUtils.d("checkUpgradeRx onNext %s", upgradeModelBaseResponse.entity.url);
                        mMainView.setResultText("testRxJava onNext updateUrl = " + upgradeModelBaseResponse.entity.url);
                    }
                }));
    }

    @Override
    public void testZip() {
        mSubscriptions.add(Observable.zip(
                mApiService.checkUpgrade(5800, "LETV_X443"),
                mApiService.getInstallNeceDetail(),
                new BiFunction<BaseResponse<UpgradeModel>, BaseResponse<List<InstallNeceModel>>, String>() {
                    @Override
                    public String apply(@io.reactivex.annotations.NonNull BaseResponse<UpgradeModel> upgradeModelBaseResponse,
                                        @io.reactivex.annotations.NonNull BaseResponse<List<InstallNeceModel>> listBaseResponse) throws Exception {
                        LoggerUtils.d("zip apply size = %d | %s", listBaseResponse.entity.size(), Thread.currentThread().getName());
                        return String.valueOf(listBaseResponse.entity.size());
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                               @Override
                               public void accept(@io.reactivex.annotations.NonNull String s) throws Exception {
                                   mMainView.setResultText("testZip onNext app size = " + s);
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
        mSubscriptions.add(mApiService.checkUpgrade(5800, "LETV_X443").zipWith(mApiService.getInstallNeceDetail(),
                (upgradeModelBaseResponse, listBaseResponse) -> {
                    LoggerUtils.d("zip apply size = %d | %s", listBaseResponse.entity.size(), Thread.currentThread().getName());
                    return String.valueOf(listBaseResponse.entity.size());
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                               @Override
                               public void accept(@io.reactivex.annotations.NonNull String s) throws Exception {
                                   mMainView.setResultText("testZipWith onNext app size = " + s);
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
                mApiService.checkUpgrade(5800, "LETV_X443"),
                mApiService.getInstallNeceDetail(),
                (upgradeModelBaseResponse, listBaseResponse) -> {
                    LoggerUtils.d("zip apply size = %d | %s", listBaseResponse.entity.size(), Thread.currentThread().getName());
                    return String.valueOf(listBaseResponse.entity.size());
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( s -> {
                            LoggerUtils.d("onNext size = %s | %s", s, Thread.currentThread().getName());
                            mMainView.setResultText("testZipUseLambda app size = " + s);
                        },
                        throwable -> LoggerUtils.d(throwable,"onError"),
                        () -> LoggerUtils.d("onComplete")
                ));
    }

    @Override
    public void testZipWithUseLambda() {
        mSubscriptions.add(mApiService.checkUpgrade(5800, "LETV_X443").zipWith(mApiService.getInstallNeceDetail(),
                (upgradeModelBaseResponse, listBaseResponse) -> {
                    LoggerUtils.d("zip apply size = %d | %s", listBaseResponse.entity.size(), Thread.currentThread().getName());
                    return String.valueOf(listBaseResponse.entity.size());
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( s -> {
                            LoggerUtils.d("onNext size = %s | %s", s, Thread.currentThread().getName());
                            mMainView.setResultText("testZipWithUseLambda size = " + s);
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
        mSubscriptions.add(mApiService.checkUpgrade(5800, "LETV_X443")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                         mMainView.showBegin("Complex invoke Begin!!!");
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .flatMap(new Function<BaseResponse<UpgradeModel>, Observable<BaseResponse<List<InstallNeceModel>>>>() {//IO线程，由observeOn()指定
                    @Override
                    public Observable<BaseResponse<List<InstallNeceModel>>> apply(
                            @io.reactivex.annotations.NonNull BaseResponse<UpgradeModel> upgradeModelBaseResponse) throws Exception {
                        return mApiService.getInstallNeceDetail();
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<BaseResponse<List<InstallNeceModel>>, Observable<InstallNeceModel>>() {
                    @Override
                    public Observable<InstallNeceModel> apply(@io.reactivex.annotations.NonNull BaseResponse<List<InstallNeceModel>> listBaseResponse) throws Exception {
                        return Observable.fromIterable(listBaseResponse.entity);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<InstallNeceModel>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull InstallNeceModel installNeceModel) throws Exception {
                        LoggerUtils.d("testComplex onNext model.name = %s", installNeceModel.name);
                        mMainView.setResultText("testComplex onNext "+ installNeceModel.name);
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
        mSubscriptions.add(mApiService.checkUpgrade(5800, "LETV_X443")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe((@io.reactivex.annotations.NonNull Disposable disposable) -> mMainView.showBegin("ComplexUseLambda invoke Begin!!!"))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .flatMap((@io.reactivex.annotations.NonNull BaseResponse<UpgradeModel> upgradeModelBaseResponse) -> mApiService.getInstallNeceDetail())
                .observeOn(Schedulers.io())
                .flatMap((@io.reactivex.annotations.NonNull BaseResponse<List<InstallNeceModel>> listBaseResponse) -> Observable.fromIterable(listBaseResponse.entity))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(installNeceModel -> mMainView.setResultText("testComplexUseLambda onNext model.name = " + installNeceModel.name),
                        (Throwable throwable) -> LoggerUtils.d("test8 error %s", throwable.getMessage()),
                        () ->  mMainView.showEnd("ComplexUseLambda invoke Complete!!!")));
    }
}
