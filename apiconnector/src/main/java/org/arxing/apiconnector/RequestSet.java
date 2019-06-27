package org.arxing.apiconnector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestSet {
    private List<RequestInfo> list = new ArrayList<>();
    private int completion;
    private boolean isAnyRequestFailed;
    private long costTimeMills;

    private RequestSet() {

    }

    public static RequestSet build() {
        return new RequestSet();
    }

    public static RequestSet build(RequestInfo info) {
        return builds(info);
    }

    public static RequestSet builds(RequestInfo... infos) {
        return builds(Arrays.asList(infos));
    }

    public static RequestSet builds(List<RequestInfo> infos) {
        return new RequestSet().addRequests(infos);
    }

    public RequestSet addRequest(RequestInfo info) {
        return addRequests(info);
    }

    public RequestSet addRequests(RequestInfo... infos) {
        return addRequests(Arrays.asList(infos));
    }

    public RequestSet addRequests(List<RequestInfo> infos) {
        list.addAll(infos);
        return this;
    }

    public RequestSet clear() {
        list.clear();
        return this;
    }

    public List<RequestInfo> getRequests() {
        return list;
    }

    public int size() {
        return list.size();
    }

    public boolean isSingleRequest() {
        return size() == 1;
    }

    public RequestInfo singleRequestInfo() {
        return list.get(0);
    }

    void resetCompletion() {
        completion = 0;
        isAnyRequestFailed = false;
    }

    void adjustCompletion() {
        completion++;
    }

    int getCompletion() {
        return completion;
    }

    boolean isAllRequestCompleted() {
        return completion == list.size();
    }

    boolean isAnyRequestFailed() {
        return isAnyRequestFailed;
    }

    void notifyRequestFailed() {
        isAnyRequestFailed = true;
    }

    void startTiming() {
        costTimeMills = System.currentTimeMillis();
    }

    void endTiming() {
        costTimeMills = System.currentTimeMillis() - costTimeMills;
    }

    long getCostTimeMills() {
        return costTimeMills;
    }
}
