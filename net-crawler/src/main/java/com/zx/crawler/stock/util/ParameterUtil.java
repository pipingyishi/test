package com.zx.crawler.stock.util;

import java.util.List;

public class ParameterUtil {
    private String userName;
    private ListUtil userInfomation;
    private String url;
    private String referenceTelephone;
    private String bankType;
    private List<String> Telephones;
    private String name;
    private List<String> cardNo;
    private String link;
    private int page;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ListUtil getUserInfomation() {
        return userInfomation;
    }

    public void setUserInfomation(ListUtil userInfomation) {
        this.userInfomation = userInfomation;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getReferenceTelephone() {
        return referenceTelephone;
    }

    public void setReferenceTelephone(String referenceTelephone) {
        this.referenceTelephone = referenceTelephone;
    }

    public List<String> getTelephones() {
        return Telephones;
    }

    public void setTelephones(List<String> telephones) {
        Telephones = telephones;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCardNo() {
        return cardNo;
    }

    public void setCardNo(List<String> cardNo) {
        this.cardNo = cardNo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
