package com.enation.javashop.net;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by LDD on 17/7/28.
 */

public interface ApiService {

    @GET("login11?username=yumengshuai&password=59421ldd111")
    Observable<Map> islogin();

    @Streaming
    @GET
    Observable<Response<ResponseBody>> download(@Url String url);

    @GET("goods-info-service/goods-info/admin/category")
    Call<List<CatModel>> getCat(@Query("parentid") String parentid);
}
