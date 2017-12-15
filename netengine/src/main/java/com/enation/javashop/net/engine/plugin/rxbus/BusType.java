package com.enation.javashop.net.engine.plugin.rxbus;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 线程控制
 */

public class BusType {
    public static final int MAIN = 0;
    public static final int BACKGROUND = 1;
    @IntDef({MAIN, BACKGROUND})
    @Retention(RetentionPolicy.SOURCE)

    public @interface Type {
    }
}
