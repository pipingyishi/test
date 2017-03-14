package com.zx.repeat.crawler.bean;

import java.util.List;

public class MultiThreadParameter {
    private int totalCount;
    private int thread;
    private int offset;
    private int limit;
    private List<String> urList;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<String> getUrList() {
        return urList;
    }

    public void setUrList(List<String> urList) {
        this.urList = urList;
    }

}
