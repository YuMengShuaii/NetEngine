package com.enation.javashop.net.engine.plugin.exception;

import com.enation.javashop.net.engine.utils.ErrorBody;
import com.enation.javashop.net.engine.utils.GsonHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Restful异常拦截器，用来拦截Restful异常
 * Created by LDD on 2017/12/14.
 */

public class RestfulExceptionInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        //从数据链中获取Respose
        JSONObject jsonArray = null;
        String jsonstring = null;
        RestfulException exception = null;
        Response originalResponse = chain.proceed(chain.request());
        MediaType mediaType = originalResponse.body().contentType();
        if (mediaType!=null &&(mediaType.toString().contains("json")||mediaType.toString().contains("text")) && !originalResponse.isSuccessful()){
            try {
                jsonstring = String.valueOf(originalResponse.body().string());
                jsonArray = new JSONObject(jsonstring);
                exception = new RestfulException(jsonArray.getString("error_message"));
            }catch (JSONException e){/**用来防止报错跳出，但不作任何处理；*/}
            if (exception!= null && jsonArray!=null &&jsonArray.has("error_message")) {
                exception.setErrorBody(GsonHelper.toInstance(jsonstring,ErrorBody.class));
                throw exception;
            }
            //二次生成Respone，防止二次读取Body抛出close异常；
            return originalResponse.newBuilder()
                    .body(ResponseBody.create(mediaType, jsonstring))
                    .build();
        }
        return  originalResponse;
    }
}
