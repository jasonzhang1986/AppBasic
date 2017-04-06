package me.jasonzhang.netmodel;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.List;

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
                test2();
                break;
        }
    }

    private void test() {
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
    int count = 3;
    public void test2() {
        NetManager.get().getInstallDeceDetailAsync(callback);
    }
    private Callback<BaseResponse<List<InstallNeceModel>>> callback = new Callback<BaseResponse<List<InstallNeceModel>>>() {
        @Override
        public void onResponse(Call<BaseResponse<List<InstallNeceModel>>> call, Response<BaseResponse<List<InstallNeceModel>>> response) {
            Timber.i("getInstallDeceDetailAsync onResponse：%s", response.body().entity);
            if (--count>0) {
                call.clone().enqueue(callback);
            }
        }

        @Override
        public void onFailure(Call<BaseResponse<List<InstallNeceModel>>> call, Throwable t) {

        }
    };
}
