package org.arxing.pagemanager;

import android.support.v4.app.Fragment;

import org.arxing.apiconnector.RequestChain;
import org.arxing.apiconnector.RequestInfo;
import org.arxing.apiconnector.ResponseBodyInfo;
import org.arxing.apiconnector.ResponseMap;
import org.arxing.pagemanager.protocol.IPageFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述了一個頁面所該具備的屬性
 */
public class PageInfo<T extends Fragment & IPageFragment> {
    private Class<T> fragmentClass;
    private String fragmentTag;
    private String layoutTag;
    private RequestChain request;
    private ResponseMap responseMap;
    private Map<String, Object> transferData = new HashMap<>();
    private boolean addToHistory;
    private boolean isSetToHistoryRoot;
    private boolean canReshow;
    private boolean animEnter;
    private boolean animExit;

    private PageInfo() {
    }

    public Class<T> getFragmentClass() {
        return fragmentClass;
    }

    public String getFragmentTag() {
        return fragmentTag;
    }

    public String getLayoutTag() {
        return layoutTag;
    }

    public RequestChain getRequest() {
        return request;
    }

    public ResponseMap getResponseMap() {
        return responseMap;
    }

    public void setResponseMap(ResponseMap responseMap) {
        this.responseMap = responseMap;
    }

    public void putResponseExtra(String key, ResponseBodyInfo data) {
        responseMap.putBody(key, data);
    }

    public Map<String, Object> getTransferData() {
        return transferData;
    }

    public boolean isAddToHistory() {
        return addToHistory;
    }

    public boolean isSetToHistoryRoot() {
        return isSetToHistoryRoot;
    }

    public boolean canReshow() {
        return canReshow;
    }

    public boolean isAnimEnter() {
        return animEnter;
    }

    public boolean isAnimExit() {
        return animExit;
    }

    public static class Builder {
        private PageInfo ins;

        public Builder() {
            ins = new PageInfo();
            ins.addToHistory = true;
            ins.canReshow = false;
            ins.isSetToHistoryRoot = false;
            ins.animEnter = true;
            ins.animExit = true;
        }

        /**
         * 設置頁面class
         */
        public Builder setFragmentClass(Class<? extends Fragment> fragmentClass) {
            ins.fragmentClass = fragmentClass;
            return this;
        }

        /**
         * 設置頁面tag
         */
        public Builder setFragmentTag(String fragmentTag) {
            ins.fragmentTag = fragmentTag;
            return this;
        }

        /**
         * 設置頁面容器tag
         */
        public Builder setLayoutTag(String layoutTag) {
            ins.layoutTag = layoutTag;
            return this;
        }

        /**
         * 設置網路請求
         */
        public Builder setRequest(RequestChain requestChain) {
            ins.request = requestChain;
            return this;
        }

        /**
         * 設置網路請求
         */
        public Builder setRequest(RequestInfo request) {
            return setRequest(RequestChain.build(request));
        }

        /**
         * 存放請求回應資料
         */
        public Builder putBundleExtra(String key, ResponseBodyInfo value) {
            ins.putResponseExtra(key, value);
            return this;
        }

        /**
         * 存放請求回應資料
         */
        public Builder putResponseExtra(ResponseMap responseMap) {
            ins.responseMap = responseMap;
            return this;
        }

        /**
         * 存放轉場資料
         */
        public Builder putTransferData(String key, Object value) {
            ins.transferData.put(key, value);
            return this;
        }

        /**
         * 存放轉場資料
         */
        public Builder putTransferData(Map<String, Object> transferData) {
            ins.transferData = transferData;
            return this;
        }

        /**
         * 設定是否加入歷史紀錄
         *
         * @param addToHistory 預設值true
         */
        public Builder setAddToHistory(boolean addToHistory) {
            ins.addToHistory = addToHistory;
            return this;
        }

        /**
         * 設定是否為根頁面
         *
         * @param toHistoryRoot 預設值false
         */
        public Builder setToHistoryRoot(boolean toHistoryRoot) {
            ins.isSetToHistoryRoot = toHistoryRoot;
            return this;
        }

        /**
         * 設定是否可重複顯示
         *
         * @param canReshow 預設值false
         */
        public Builder setCanReshow(boolean canReshow) {
            ins.canReshow = canReshow;
            return this;
        }

        /**
         * 設定是否進場動畫
         *
         * @param enabled 預設值true
         */
        public Builder setAnimEnterEnabled(boolean enabled) {
            ins.animEnter = enabled;
            return this;
        }

        /**
         * 設定是否退場動畫
         *
         * @param enabled 預設值true
         */
        public Builder setAnimExitEnabled(boolean enabled) {
            ins.animExit = enabled;
            return this;
        }

        public PageInfo build() {
            return ins;
        }
    }
}
