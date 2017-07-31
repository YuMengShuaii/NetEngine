package com.enation.javashop.net;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by LDD on 17/7/28.
 */

public interface ApiService {

    @GET("api/mobile/member/islogin.do")
    Call<ResponseBody> islogin();

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);

    @GET("goods-info-service/goods-info/admin/category")
    Call<List<CatModel>> getCat(@Query("parentid") String parentid);
}
