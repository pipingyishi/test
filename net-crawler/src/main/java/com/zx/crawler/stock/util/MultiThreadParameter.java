package com.zx.crawler.stock.util;

import java.util.List;

public class MultiThreadParameter {
    private String sectionUrl;
    private String turnPageUrl;
    private String host;
    private List<String> abnormalUrl;
    private ListUtil list;
    private List<String> name;
    private int section;
    private int totalPages;
    private int thread;
    private List<String> url;
    private String flip;
    private int id;
    private int offset;
    private int limit;

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public String getSectionUrl() {
        return sectionUrl;
    }

    public void setSectionUrl(String sectionUrl) {
        this.sectionUrl = sectionUrl;
    }

    public String getTurnPageUrl() {
        return turnPageUrl;
    }

    public void setTurnPageUrl(String turnPageUrl) {
        this.turnPageUrl = turnPageUrl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<String> getAbnormalUrl() {
        return abnormalUrl;
    }

    public void setAbnormalUrl(List<String> abnormalUrl) {
        this.abnormalUrl = abnormalUrl;
    }

    public ListUtil getList() {
        return list;
    }

    public void setList(ListUtil list) {
        this.list = list;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public String getFlip() {
        return flip;
    }

    public void setFlip(String flip) {
        this.flip = flip;
    }

    public int getId() {
        return id;
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

    public void setId(int id) {
        this.id = id;
    }

}
