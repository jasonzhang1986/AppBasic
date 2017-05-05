package me.jasonzhang.app.net;

import java.util.List;

import io.reactivex.Observable;
import me.jasonzhang.app.net.model.GankBean;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by JifengZhang on 2017/4/6.
 */

public interface ApiService {
    @GET(API.URL_ANDROID_DATA)
    Observable<BaseResponse<List<GankBean>>> getAndroidData(@Path("pageSize") int pageSize, @Path("pageNo") int pageNo);

    @GET(API.URL_IOS_DATA)
    Observable<BaseResponse<List<GankBean>>> getIOSData(@Path("pageSize") int pageSize, @Path("pageNo") int pageNo);
}
