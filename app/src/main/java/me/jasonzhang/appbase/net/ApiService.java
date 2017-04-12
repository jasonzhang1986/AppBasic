package me.jasonzhang.appbase.net;

import java.util.List;

import io.reactivex.Observable;
import me.jasonzhang.appbase.net.core.BaseResponse;
import me.jasonzhang.appbase.net.model.GankBean;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by JifengZhang on 2017/4/6.
 */

public interface ApiService {
    @GET(API.URL_ANDROID_DATA)
    Observable<BaseResponse<List<GankBean>>> getAndroidData(@Path("pageSize") int pageSize, @Path("pageNo") int pageNo);

    @GET(API.URL_IOS_DATA)
    Observable<BaseResponse<List<GankBean>>> getIOSData(@Path("pageSize") int pageSize, @Path("pageNo") int pageNo);
}
