package com.zx.crawler.stock.util;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class UserMassage {
    private static List<String> allMassage = null;
    private static Map<String, String> filter = new HashMap<String, String>();
    private static List<String> name = new ArrayList<String>();
    private static Logger LOG = LoggerFactory.getLogger(UserMassage.class);
    private static final int TELPHONE_LENGTH_ONE = 11;
    private static final int TELPHONE_LENGTH_TWO = 7;
    private static final int TELPHONE_LENGTH_THREE = 8;
    private static final int BANK_CARD_LENGTH_MIN = 16;
    private static final int BANK_CARD_LENGTH_MAX = 20;
    private static final int URL_GRAND_THREE = 3;
    private static final int URL_GRAND_FOUR = 4;
    private static final String SITE = "互动";
    private static final String ADDRESS = "地址";

    public ListUtil getUserMassage(String userName, PropertieUtil propertieUtil, ListUtil listUtil) {
        ListUtil userInfomation = new ListUtil();
        String url = "";
        try {
            url = propertieUtil.getHostUser() + userName;
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(20000);
            Document doc = Jsoup.parse(conn.getInputStream(), "gbk", url); // 进入详细页
            String string = doc.text().replace(" ", "").replaceAll("\u00A0", "");
            Elements trNodes = doc.select("table").select("tr");
            LOG.info("" + listUtil.getLink() + " link of " + listUtil.getPage() + " page;" + "leftUrl:" + url
                    + "  from hudong  grand:3");
            List<String> trInfo = new ArrayList<String>();
            List<String> userInfo = new ArrayList<String>();
            for (int i = 0; i < trNodes.size(); i++) {
                String trText = trNodes.get(i).text();
                trText += trNodes.get(i).select("img").attr("alt");
                trInfo.add(trText.replace(" ", "").replaceAll("\u00A0", ""));
            }
            String referInfo[] = propertieUtil.getReferenceInformation().split("、");
            for (int j = 0; j < referInfo.length; j++) {
                for (int k = 0; k < trInfo.size(); k++) {
                    if (trInfo.get(k).contains(referInfo[j])) {
                        userInfo.add(trInfo.get(k));
                        break;
                    }
                }
            }
            if (userInfo.size() == 0) {
                return null;
            }
            userInfo = getUserInfo(userInfo);
            if (userInfo.size() < 2) {
                return null;
            }
            String crawName = userInfo.get(0) + userInfo.get(1);
            if (StockTradeUtil.isCompare(listUtil.getExistName(), crawName)) {
                return null;
            }
            if (StockTradeUtil.isCompare(name, crawName)) {
                return null;
            }
            name.add(crawName);
            userInfomation.setUserInfo(userInfo);
            ParameterUtil parameter = new ParameterUtil();
            parameter.setUserName(userName);
            parameter.setUserInfomation(userInfomation);
            parameter.setUrl(propertieUtil.getHostConnect() + userName);
            parameter.setReferenceTelephone(propertieUtil.getTelphone());
            parameter.setBankType(propertieUtil.getBankType());
            if (listUtil.getPage() != 0) {
                parameter.setTelephones(listUtil.getExistTelphone());
                parameter.setCardNo(listUtil.getExistCardNo());
            }
            parameter.setLink(listUtil.getLink());
            parameter.setPage(listUtil.getPage());
            userInfomation = getUserConnectInformation(parameter, listUtil.getAbnormalUrls());
        } catch (Exception e) {
            try {
                if (!StockTradeUtil.isCompare(listUtil.getAbnormalUrls(), url)) {
                    SaveAbnormalUrl.saveExceptionalUrl(url, URL_GRAND_THREE, SITE);
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                LOG.warn(
                        "An error occurs when the data into the database form hudong :{}  page  Corresponding to url {}  grand:3",
                        listUtil.getPage(), url, ex);
            }
            LOG.warn(
                    "an error occurs when reading a detailed page from hudong : {} page Corresponding to url {} grand:3",
                    listUtil.getPage(), url, e);
            return null;
        }
        return userInfomation;
    }

    public List<String> getUserInfo(List<String> ui) {
        List<String> userInfo = new ArrayList<String>();
        for (int i = 0; i < ui.size(); i++) {
            if (i == ui.size() - 1) {
                userInfo.add(ui.get(i));
                break;
            }
            userInfo.add(StockTradeUtil.getSplitMassage(ui.get(i)));
        }
        return userInfo;
    }

    public ListUtil getUserConnectInformation(ParameterUtil parameter, List<String> abnormalUrl) {
        ListUtil userInfomation = parameter.getUserInfomation();
        String url = parameter.getUrl();
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(20000);
            Document doc = Jsoup.parse(conn.getInputStream(), "gbk", url); // 进入某一页
            LOG.info("see massage:  {}  page Corresponding to url {}  from hudong grand:4", parameter.getPage(), url);
            String docText = doc.text();
            String docHtml = doc.html();
            String textArray[] = docText.split(" ");
            userInfomation.setAddress(getAddress(textArray));
            List<String> telphone = getTelphone(docText);
            List<String> cardNo = new ArrayList<String>();
            cardNo = getCardNo(StringUtils.replaceEach(docText, new String[] { " ", " ", "\u00A0", ",", "，", ";", "；" },
                    new String[] { "", "", "", "", "", "", "" }));
            if (parameter.getPage() != 0) {
                if (RightSplitUtil.foundCompreWithExist(parameter.getTelephones(), telphone)
                        || RightSplitUtil.foundCompreWithExist(parameter.getCardNo(), cardNo)) {
                    return null;
                }
            }
            int size = filter.size();
            for (int i = 0; i < telphone.size(); i++) {
                filter.put(telphone.get(i), "filter");
            }
            for (int j = 0; j < cardNo.size(); j++) {
                filter.put(cardNo.get(j), "filter");
            }
            if (filter.size() != size + telphone.size() + cardNo.size()) {
                return null;
            }
            userInfomation.setTelphone(telphone);
            userInfomation.setCardNo(cardNo);
            String temp = StringUtils.replaceEach(docText, new String[] { " ", " ", "\u00A0", },
                    new String[] { "", "", "" });
            userInfomation.setBankType(getBankType(docHtml, cardNo));
            String personalMsg[] = docText.replaceAll("\u00A0", "").split(" ");
            allMassage = Lists.newArrayList(personalMsg);
            userInfomation.setDepositBank(openAccountMassage(docHtml, cardNo));
        } catch (Exception e) {
            try {
                if (!StockTradeUtil.isCompare(abnormalUrl, parameter.getUrl())) {
                    SaveAbnormalUrl.saveExceptionalUrl(parameter.getUrl(), URL_GRAND_FOUR, SITE);
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                LOG.warn(
                        "An error occurs when the data into the database: {}  page Corresponding to url {} grand:4   from hudong",
                        parameter.getPage(), url, ex);
            }
            LOG.warn(
                    "an error occurs when reading a detailed page: {} page Corresponding to url: {} grand:4  from hudong",
                    parameter.getPage(), url, e);
            return null;
        }
        return userInfomation;
    }

    public List<String> getTelphone(String docText) {
        Pattern p = Pattern.compile("\\d{2,}");
        Matcher m = p.matcher(docText);
        List<String> temp = new ArrayList<String>();
        List<String> telphone = new ArrayList<String>();
        while (m.find()) {
            temp.add(m.group());
        }
        temp = removeListRepeat(temp);
        String tel = "";
        for (int j = 0; j < temp.size(); j++) {
            tel = temp.get(j);
            while (tel.length() % TELPHONE_LENGTH_ONE == 0) {
                telphone.add(tel.substring(0, 10) + tel.charAt(10));
                tel = tel.substring(10);
            }
            if (temp.get(j).length() == TELPHONE_LENGTH_TWO || temp.get(j).length() == TELPHONE_LENGTH_THREE) {
                int index = docText.indexOf(temp.get(j));
                if (docText.charAt(index - 1) == '-' || docText.charAt(index - 1) == '—'
                        || docText.charAt(index - 1) == '－') {
                    telphone.add(temp.get(j - 1) + "-" + temp.get(j));
                }
            }
        }
        return telphone;
    }

    public List<String> removeListRepeat(List<String> list) {
        Set<String> set = new HashSet<String>();
        List<String> newList = new ArrayList<String>();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            if (set.add(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

    public String getAddress(String textArray[]) {
        String address = "";
        for (int i = 0; i < textArray.length; i++) {
            if (textArray[i].contains(ADDRESS)) {
                if (textArray[i].contains("：")) {
                    address = getEffectiveAddress(textArray, textArray[i], i, "：");
                    break;
                } else if (textArray[i].contains(":")) {
                    address = getEffectiveAddress(textArray, textArray[i], i, ":");
                    break;
                } else if (textArray[i].contains(";")) {
                    address = getEffectiveAddress(textArray, textArray[i], i, ";");
                    break;
                } else {
                    if (textArray[i].contains("；")) {
                        address = getEffectiveAddress(textArray, textArray[i], i, "；");
                        break;
                    }
                }
            }
        }
        if (address != null) {
            return address;
        } else {
            return "";
        }
    }

    public String getEffectiveAddress(String textArray[], String currentMsg, int index, String splitSign) {
        if (currentMsg.split(splitSign).length > 1) {
            if ((index == textArray.length - 1)
                    || StringUtils.containsAny(textArray[index + 1], new char[] { ':', '：', ';', '；' })) {
                return currentMsg.split(splitSign)[1];
            }
        } else {
            String temp = currentMsg;
            for (int j = index + 1; j < textArray.length; j++) {
                if (!textArray[j].contains(":") && !textArray[j].contains("：") && !textArray[j].contains("；")
                        && !textArray[j].contains(";")) {
                    temp += textArray[j];
                } else {
                    break;
                }
            }
            if (temp.split(splitSign).length > 1) {
                return temp.split(splitSign)[1];
            } else {
                return temp.split(splitSign)[0];
            }
        }
        return null;
    }

    public List<String> getCardNo(String docText) {
        List<String> temp = new ArrayList<String>();
        List<String> cardNumber = new ArrayList<String>();
        Pattern pt = Pattern.compile("\\d{15,}");
        Matcher mt = pt.matcher(docText);
        while (mt.find()) {
            temp.add(mt.group());
        }
        for (int n = 0; n < temp.size(); n++) {
            if (temp.get(n).length() >= BANK_CARD_LENGTH_MIN && temp.get(n).length() <= BANK_CARD_LENGTH_MAX) {
                int index = docText.indexOf(temp.get(n));
                if (docText.charAt(index - 1) != '-') {
                    cardNumber.add(temp.get(n));
                }
            }
        }
        return cardNumber;
    }

    public List<String> getBankType(String docText, List<String> cardNo) {
        List<String> bankTypes = new ArrayList<String>();
        if (cardNo.size() == 0) {
            return bankTypes;
        }
        bankTypes = StockTradeUtil.getBankType(docText, cardNo);
        return bankTypes;
    }

    public List<String> openAccountMassage(String docText, List<String> cardNumber) {
        List<String> depositMassage = new ArrayList<String>();
        if (cardNumber.size() == 0) {
            return depositMassage;
        }
        depositMassage = RightSplitUtil.openAccountMassage(docText, cardNumber);
        return depositMassage;
    }
}
