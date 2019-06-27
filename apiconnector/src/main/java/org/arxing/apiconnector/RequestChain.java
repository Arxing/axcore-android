package org.arxing.apiconnector;

import android.util.SparseArray;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.arxing.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestChain {
    private String tag;
    private List<RequestSet> list = new ArrayList<>();
    private SparseArray<RequestInfo> requestIdTable = new SparseArray<>();
    private Map<String, RequestInfo> requestTagTable = new HashMap<>();
    private long costTimeMills;
    private final static Object lock = new Object();

    private RequestChain() {
    }

    private boolean checkRequestValid(RequestInfo info) {
        return !requestTagTable.containsKey(info.getTag());
    }

    private void saveRequest(RequestInfo info) {
        if (!checkRequestValid(info))
            throw new AssertionError("duplicate request is not valid.");
        String tag = info.getTag();
        int id = requestIdTable.size();
        info.setId(id);
        requestIdTable.append(id, info);
        requestTagTable.put(tag, info);
    }

    public static RequestChain build() {
        return new RequestChain();
    }

    public static RequestChain build(RequestInfo info) {
        return build().addRequest(info);
    }

    public static RequestChain buildSet(RequestInfo... infos) {
        return build().addSet(RequestSet.builds(infos));
    }

    public RequestChain addRequests(List<RequestInfo> infos) {
        for (RequestInfo info : infos) {
            addRequest(info);
        }
        return this;
    }

    public RequestChain addRequest(RequestInfo info) {
        list.add(RequestSet.build(info));
        saveRequest(info);
        return this;
    }

    public RequestChain addSet(RequestInfo... requests) {
        return addSet(RequestSet.builds(requests));
    }

    public RequestChain addSet(RequestSet set) {
        list.add(set);
        for (RequestInfo info : set.getRequests()) {
            saveRequest(info);
        }
        return this;
    }

    public RequestChain clear() {
        list.clear();
        requestIdTable.clear();
        requestTagTable.clear();
        return this;
    }

    public List<RequestSet> getSets() {
        return list;
    }

    public RequestSet getSet(int level) {
        if (level < list.size())
            return list.get(level);
        return null;
    }

    public int depth() {
        return list.size();
    }

    public int size() {
        return requestIdTable.size();
    }

    public int size(int level) {
        return level < list.size() ? list.get(level).size() : 0;
    }

    public RequestInfo getRequestWithTag(String tag) {
        return requestTagTable.get(tag);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void printTree(Logger logger) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n↓\n========== Tree ==========\n");
        builder.append(Stream.of(list)
                             .map(set -> Stream.of(set.getRequests())
                                               .map(requestInfo -> String.format("[%s]", requestInfo.getTag()))
                                               .collect(Collectors.joining("-")))
                             .collect(Collectors.joining("\n")));
        builder.append("\n==========================\n");
        logger.d(builder.toString());
    }

    RequestInfo getRequestWithId(int id) {
        return requestIdTable.get(id);
    }

    void visitAllRequest(Visitor<RequestInfo> visitor) {
        for (int id = 0; id < requestIdTable.size(); id++) {
            visitor.onVisit(requestIdTable.get(id));
        }
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
