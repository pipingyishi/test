package com.zx.frontend.stock.util;

import java.net.URL;
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

    public ListUtil getUserMassage(String userName, PropertieUtil propertieUtil, List<String> existTelphone,
            List<String> abnormalUrl) {
        ListUtil userInfomation = new ListUtil();
        String url = "";
        try {
            url = propertieUtil.getHostUser() + userName;
            Jsoup.connect(url).timeout(100000);
            Document doc = Jsoup.parse(new URL(url).openStream(), "gbk", url);// 进入详细页
            Thread.sleep(2500);
            String string = doc.text().replace(" ", "").replaceAll("\u00A0", "");
            Elements trNodes = doc.select("table").select("tr");
            LOG.info("see information from hudong" + url + "grand:3");
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
            if (!StockTradeUtil.nameCompare(name, userInfo.get(0) + userInfo.get(1))) {
                return null;
            }
            name.add(userInfo.get(0) + userInfo.get(1));
            userInfomation.setUserInfo(userInfo);
            ParameterUtil parameter = new ParameterUtil();
            parameter.setUserName(userName);
            parameter.setUserInfomation(userInfomation);
            parameter.setUrl(propertieUtil.getHostConnect() + userName);
            parameter.setReferenceTelephone(propertieUtil.getTelphone());
            parameter.setBankType(propertieUtil.getBankType());
            parameter.setTelephones(existTelphone);
            userInfomation = getUserConnectInformation(parameter, abnormalUrl);
        } catch (Exception e) {
            LOG.warn("an error occurs when reading a detailed page from hudong" + url + "grand:3", e);
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
            if (ui.get(i).contains("：")) {
                if (ui.get(i).split("：").length > 1) {
                    userInfo.add(ui.get(i).split("：")[1]);
                } else {
                    userInfo.add("");
                }
            } else if (ui.get(i).contains(":")) {
                if (ui.get(i).split(":").length > 1) {
                    userInfo.add(ui.get(i).split(":")[1]);
                } else {
                    userInfo.add("");
                }
            } else if (ui.get(i).contains(";")) {
                if (ui.get(i).split(";").length > 1) {
                    userInfo.add(ui.get(i).split(";")[1]);
                } else {
                    userInfo.add("");
                }
            } else {
                if (ui.get(i).contains("；")) {
                    if (ui.get(i).split("；").length > 1) {
                        userInfo.add(ui.get(i).split("；")[1]);
                    } else {
                        userInfo.add("");
                    }
                }
            }
        }
        return userInfo;
    }

    public ListUtil getUserConnectInformation(ParameterUtil parameter, List<String> abnormalUrl) {
        ListUtil userInfomation = parameter.getUserInfomation();
        try {
            Jsoup.connect(parameter.getUrl()).timeout(100000);
            Document doc = Jsoup.parse(new URL(parameter.getUrl()).openStream(), "gbk", parameter.getUrl());// 进入某一页
            Thread.sleep(2500);
            LOG.info("see information from hudong" + parameter.getUrl() + "grand:4");
            String docText = doc.text();
            String docHtml = doc.html();
            String textArray[] = docText.split(" ");
            userInfomation.setAddress(getAddress(textArray));
            List<String> telphone = getTelphone(docText);
            for (int n = 0; n < parameter.getTelephones().size(); n++) {
                for (int m = 0; m < telphone.size(); m++) {
                    if (telphone.get(m).equals(parameter.getTelephones().get(n))) {
                        return null;
                    }
                }
            }
            int size = filter.size();
            for (int i = 0; i < telphone.size(); i++) {
                filter.put(telphone.get(i), "filter");
            }
            if (filter.size() != size + telphone.size()) {
                return null;
            }
            userInfomation.setTelphone(telphone);
            List<String> cardNo = new ArrayList<String>();
            cardNo = getCardNo(StringUtils.replaceEach(docText, new String[] { " ", " ", "\u00A0", },
                    new String[] { "", "", "" }));
            userInfomation.setCardNo(cardNo);
            String temp = StringUtils.replaceEach(docText, new String[] { " ", " ", "\u00A0", },
                    new String[] { "", "", "" });
            userInfomation.setBankType(getBankType(docHtml, cardNo));
            String personalMsg[] = docText.replaceAll("\u00A0", "").split(" ");
            allMassage = Lists.newArrayList(personalMsg);
            userInfomation.setDepositBank(openAccountMassage(docHtml, cardNo));
        } catch (Exception e) {
            LOG.warn("an error occurs when reading a detailed page from hudong" + parameter.getUrl() + "grand:4", e);
        }
        return userInfomation;
    }

    public List<String> getTelphone(String docText) {
        docText = StringUtils.replaceEach(docText, new String[] { " ", " ", "\u00A0", }, new String[] { "", "", "" })
                .trim();
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
            while (tel.length() % 11 == 0) {
                telphone.add(tel.substring(0, 10) + tel.charAt(10));
                tel = tel.substring(10);
            }
            if (temp.get(j).length() == 7 || temp.get(j).length() == 8) {
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
            if (textArray[i].contains("地址")) {
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
            if ((index == textArray.length - 1) || textArray[index + 1].contains(":")
                    || textArray[index + 1].contains("：") || textArray[index + 1].contains("；")
                    || textArray[index + 1].contains(";")) {
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
            if (temp.get(n).length() >= 16 && temp.get(n).length() <= 20) {
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

    public boolean isAllDegit(String temp) {
        for (int i = 0; i < temp.length(); i++) {
            if (!Character.isDigit(temp.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
