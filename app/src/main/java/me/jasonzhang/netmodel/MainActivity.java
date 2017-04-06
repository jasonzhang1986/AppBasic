package me.jasonzhang.netmodel;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jasonzhang.netmodel.net.core.BaseResponse;
import me.jasonzhang.netmodel.net.core.NetManager;
import me.jasonzhang.netmodel.net.model.InstallNeceModel;
import me.jasonzhang.netmodel.net.model.UpgradeModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                test6();
                break;
        }
    }

    private void test1() {
        NetManager.get().checkUpgradeAsync(5800, "LETV_X443", new Callback<BaseResponse<UpgradeModel>>() {
            @Override
            public void onResponse(Call<BaseResponse<UpgradeModel>> call, Response<BaseResponse<UpgradeModel>> response) {
                Timber.i("checkUpgradeAsync onResponse");
            }

            @Override
            public void onFailure(Call<BaseResponse<UpgradeModel>> call, Throwable t) {

            }
        });
    }
    public void test2() {
        NetManager.get().getInstallDeceDetailAsync(callback);
    }
    private Callback<BaseResponse<List<InstallNeceModel>>> callback = new Callback<BaseResponse<List<InstallNeceModel>>>() {
        @Override
        public void onResponse(Call<BaseResponse<List<InstallNeceModel>>> call, Response<BaseResponse<List<InstallNeceModel>>> response) {
            Timber.i("getInstallDeceDetailAsync onResponse：%s", response.body().entity);
        }

        @Override
        public void onFailure(Call<BaseResponse<List<InstallNeceModel>>> call, Throwable t) {

        }
    };

    private void test3() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    NetManager.get().checkUpgrade(5800, "LETV_X443");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void test4() {
        NetManager.get().checkUpgradeRx(5800, "LETV_X443")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<UpgradeModel>>() {
                    @Override
                    public void accept(@NonNull BaseResponse<UpgradeModel> upgradeModelBaseResponse) throws Exception {
                        Timber.d("checkUpgradeRx onNext %s", upgradeModelBaseResponse.entity.url);
                    }
                });
    }
    private void test5() {
        NetManager.get().getInstallDeceDetailRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<List<InstallNeceModel>>>() {
                    @Override
                    public void accept(@NonNull BaseResponse<List<InstallNeceModel>> listBaseResponse) throws Exception {
                        Timber.d("getInstallDeceDetail onNext size = %d", listBaseResponse.entity.size());
                    }
                });
    }

    private void test6() {
        NetManager.get().checkUpgradeRx(5800, "LETV_X443")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<BaseResponse<UpgradeModel>, ObservableSource<BaseResponse<List<InstallNeceModel>>>>() {
                    @Override
                    public ObservableSource<BaseResponse<List<InstallNeceModel>>> apply(
                            @NonNull BaseResponse<UpgradeModel> upgradeModelBaseResponse) throws Exception {
                        return NetManager.get().getInstallDeceDetailRx();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<List<InstallNeceModel>>>() {
                    @Override
                    public void accept(@NonNull BaseResponse<List<InstallNeceModel>> listBaseResponse) throws Exception {
                        Timber.d("getInstallDeceDetail onNext size = %d", listBaseResponse.entity.size());
                    }
                });
    }
}
