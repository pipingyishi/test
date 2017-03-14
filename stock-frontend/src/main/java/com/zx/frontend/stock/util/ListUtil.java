package com.zx.frontend.stock.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;

import com.zx.trade.datamodel.AbnormalUrl;
import com.zx.trade.datamodel.TradeMassage;
import com.zx.trade.datamodel.TradeStock;

public class ListUtil {
    private List<String> userInfo;
    private List<String> telphone;
    private List<String> cardNo;
    private List<String> bankType;
    private List<String> depositBank;
    private String address;
    private List<String> listUrl;
    private List<String> listTelphone;
    private List<String> abnormalUrls;
    private boolean flag;
    private boolean status;

    public List<String> getListUrl() {
        return listUrl;
    }

    public void setListUrl(List<String> listUrl) {
        this.listUrl = listUrl;
    }

    public List<String> getListTelphone() {
        return listTelphone;
    }

    public void setListTelphone(List<String> listTelphone) {
        this.listTelphone = listTelphone;
    }

    public List<String> getAbnormalUrls() {
        return abnormalUrls;
    }

    public void setAbnormalUrls(List<String> abnormalUrls) {
        this.abnormalUrls = abnormalUrls;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(List<String> userInfo) {
        this.userInfo = userInfo;
    }

    public List<String> getTelphone() {
        return telphone;
    }

    public void setTelphone(List<String> telphone) {
        this.telphone = telphone;
    }

    public List<String> getCardNo() {
        return cardNo;
    }

    public void setCardNo(List<String> cardNo) {
        this.cardNo = cardNo;
    }

    public List<String> getBankType() {
        return bankType;
    }

    public void setBankType(List<String> bankType) {
        this.bankType = bankType;
    }

    public List<String> getDepositBank() {
        return depositBank;
    }

    public void setDepositBank(List<String> depositBank) {
        this.depositBank = depositBank;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ListUtil getListMassage(List<TradeStock> tradeStocks, List<TradeMassage> tradeMassages,
            List<AbnormalUrl> exceptionalUrl) {
        List<String> listUrl = new ArrayList<String>();
        List<String> listTelphone = new ArrayList<String>();
        List<String> abnormalUrls = new ArrayList<String>();
        Set<String> set = new HashSet<String>();
        for (int m = 0; m < tradeStocks.size(); m++) {
            if (set.add(tradeStocks.get(m).getUrlName())) {
                listUrl.add(tradeStocks.get(m).getUrlName());
            }
        }
        for (int n = 0; n < tradeMassages.size(); n++) {
            listTelphone.add(tradeMassages.get(n).getTelephone());
        }
        for (int i = 0; i < exceptionalUrl.size(); i++) {
            abnormalUrls.add(exceptionalUrl.get(i).getAbnormalUrl());
        }
        setListUrl(listUrl);
        setListTelphone(listTelphone);
        setAbnormalUrls(abnormalUrls);
        return this;
    }
}
