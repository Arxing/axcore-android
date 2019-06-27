package org.arxing.lazyrecyclerview.protocol;

public interface LoadMoreNotifier {
    /**
     * 通知本次加載更多完成 下次再滑動至底部時還會繼續加載
     * @param pageTag
     */
    void notifyLoadCompleted(Object pageTag);

    /**
     * 通知本次加載更多失敗
     */
    void notifyLoadFailed();

    /**
     * 通知沒有更多可以加載了
     */
    void notifyLoadExhausted();

    /**
     * 通知加載中
     */
    void notifyLoading();

    /**
     * 通知重置狀態
     */
    void notifyReset();
}
