package com.enation.javashop.net.engine.utils;

import com.google.gson.Gson;

/**
 * Created by LDD on 2017/12/14.
 */

public class GsonHelper {
    private static Gson instance;


    public static Gson prepare(){
        if (instance == null){
            instance = new Gson();
        }
        return instance;
    }

    public static String toJson(Object o){
        return prepare().toJson(o);
    }

    public  static <T> T toInstance (String json,Class<T> _class){
        return prepare().fromJson(json,_class);
    }
}
