package com.enation.javashop.net.engine.core;


/**
 *   文件进度传输类
 */

public class DownloadProgressEvent {

    /**
     * 文件总大小
     */
    private long mTotal;
    /**
     * 当前下载进度
     */
    private long mProgress;

    /**
     * 构造方法
     * @param total     总大小
     * @param progress  已下载大小
     */
    public DownloadProgressEvent(long total, long progress) {
        mTotal = total;
        mProgress = progress;
    }

    /**
     * 获取进度百分比
     * @return  百分比
     */
    public int getProgressPercent(){
        float pressent = (float) mProgress / mTotal * 100;
        return (int) pressent;
    }

    /**
     * 获取文件总大小
     *
     * @return  文件大小
     */
    public long getTotal() {
        return mTotal;
    }

    /**
     * 获取已下载大小
     * @return  已下载文件大小
     */
    public long getProgress() {
        return mProgress;
    }

    /**
     * 是否还没有下载完成
     *
     * @return   是否完成
     */
    public boolean isNotDownloadFinished() {
        return mTotal != mProgress;
    }

}
