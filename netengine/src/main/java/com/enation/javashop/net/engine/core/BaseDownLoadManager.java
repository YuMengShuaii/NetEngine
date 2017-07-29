package com.enation.javashop.net.engine.core;

import android.content.Context;

import java.io.File;

import okhttp3.ResponseBody;

/**
 * 本地序列化接口
 */

public interface BaseDownLoadManager {

    /**
     * 写入本地方法
     * @param context 上下文
     * @param body    返回体
     * @return        写入完成后的File对象
     */
    File writeResponseBodyToDisk(Context context, ResponseBody body);


}
