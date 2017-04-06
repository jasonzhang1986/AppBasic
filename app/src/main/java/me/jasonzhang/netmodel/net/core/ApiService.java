package me.jasonzhang.netmodel.net.core;

import java.util.List;

import me.jasonzhang.netmodel.net.model.InstallNeceModel;
import me.jasonzhang.netmodel.net.model.UpgradeModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by JifengZhang on 2017/4/5.
 */

interface ApiService {
    @GET(API.URL_STORE_UPGRADE)
    Call<BaseResponse<UpgradeModel>> checkUpgrade(@Query("versionCode") int versionCode, @Query("upChannel") String channel);

    @GET(API.URL_INSTALLNECE_DETAIL)
    Call<BaseResponse<List<InstallNeceModel>>> getInstallNeceDetail();
}
