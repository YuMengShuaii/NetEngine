package com.enation.javashop.net.engine.plugin.exception;

import android.util.Log;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * Created by LDD on 2017/12/14.
 */

public class ExceptionHandle {
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;
    private static final int NOT_ACCEPTABLE = 406;
    public static ResponeThrowable handleException(Throwable e) {
        ResponeThrowable ex;
        Log.i("NetEngineError", "Error.toString = " + e.toString());
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponeThrowable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED:
                    ex.message = "没有权限";
                    break;
                case FORBIDDEN:
                    ex.message = "服务器拒绝请求";
                    break;
                case NOT_FOUND:
                    ex.message = "找不到指定请求路径";
                    break;
                case REQUEST_TIMEOUT:
                    ex.message = "资源请求超时";
                    break;
                case GATEWAY_TIMEOUT:
                    ex.message = "网关请求超时";
                    break;
                case INTERNAL_SERVER_ERROR:
                    ex.message = "错误网关";
                    break;
                case BAD_GATEWAY:
                    ex.message = "错误网关";
                    break;
                case SERVICE_UNAVAILABLE:
                    ex.message = "服务不可用";
                    break;
                case NOT_ACCEPTABLE:
                    ex.message = "无法接受该请求";
                    break;
                default:
                    ex.message = "网络错误";
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ResponeThrowable(resultException, resultException.code);
            ex.message = resultException.message;
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                /*|| e instanceof ParseException*/) {
            ex = new ResponeThrowable(e, ERROR.PARSE_ERROR);
            ex.message = "解析错误";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ResponeThrowable(e, ERROR.NETWORD_ERROR);
            ex.message = "连接失败";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ResponeThrowable(e, ERROR.SSL_ERROR);
            ex.message = "证书验证失败";
            return ex;
        }else if (e instanceof SocketTimeoutException) {
            ex = new ResponeThrowable(e, ERROR.HTTP_TIMEOUT);
            ex.message = "连接超时";
            return ex;
        }else if(e instanceof RestfulException){
            ex = new ResponeThrowable(e, ERROR.HTTP_TIMEOUT);
            ex.message = ((RestfulException) e).getErrorBody().getError_message();
            return ex;
        }else if(e instanceof UnknownHostException){
            ex = new ResponeThrowable(e, ERROR.UNKNOWN_HOST);
            ex.message ="未识别的服务器地址";
            return ex;
        }
        else {
            ex = new ResponeThrowable(e, ERROR.UNKNOWN);
            ex.message = "未知错误";
            return ex;
        }
    }


    /**
     * 约定异常
     */
    class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int HTTP_TIMEOUT = 1006;

        /**
         * 未识别host
         */
        public static final int UNKNOWN_HOST = 1007;
    }

    public static class ResponeThrowable extends Exception {
        public int code;
        public String message;

        public ResponeThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;
        }
    }

    /**
     * ServerException发生后，将自动转换为ResponeThrowable返回
     */
    class ServerException extends RuntimeException {
        int code;
        String message;
    }

}