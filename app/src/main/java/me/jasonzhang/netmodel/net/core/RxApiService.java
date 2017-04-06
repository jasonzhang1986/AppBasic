package me.jasonzhang.netmodel.net.core;

import java.util.List;

import io.reactivex.Observable;
import me.jasonzhang.netmodel.net.model.InstallNeceModel;
import me.jasonzhang.netmodel.net.model.UpgradeModel;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by JifengZhang on 2017/4/6.
 */

public interface RxApiService {
    @GET(API.URL_STORE_UPGRADE)
    Observable<BaseResponse<UpgradeModel>> checkUpgrade(@Query("versionCode") int versionCode, @Query("upChannel") String channel);

    @GET(API.URL_INSTALLNECE_DETAIL)
    Observable<BaseResponse<List<InstallNeceModel>>> getInstallNeceDetail();
}
