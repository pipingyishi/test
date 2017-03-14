package com.zx.datamodels.notice.bean;

import java.io.Serializable;
import java.util.List;

import com.zx.datamodels.content.bean.BizFeed;
import com.zx.datamodels.content.bean.entity.Dict;
import com.zx.datamodels.content.bean.entity.Feed;
import com.zx.datamodels.content.bean.entity.Notice;
import com.zx.datamodels.market.bean.entity.Exchange;
import com.zx.datamodels.user.bean.entity.User;

public class NoticeModel implements Serializable {
    private Notice notice;

    private Exchange exchange;

    private List<BizFeed> bizFeeds;

    private String openAccount;

    private String openAccountUrl;

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public List<BizFeed> getBizFeeds() {
        return bizFeeds;
    }

    public void setBizFeeds(List<BizFeed> bizFeeds) {
        this.bizFeeds = bizFeeds;
    }

    public String getOpenAccount() {
        return openAccount;
    }

    public void setOpenAccount(String openAccount) {
        this.openAccount = openAccount;
    }

    public String getOpenAccountUrl() {
        return openAccountUrl;
    }

    public void setOpenAccountUrl(String openAccountUrl) {
        this.openAccountUrl = openAccountUrl;
    }
}
