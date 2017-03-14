package com.zx.user.merge.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zx.trade.datamodel.BankCardMassage;
import com.zx.modules.user.massage.service.CardNoInfoService;
import com.zx.modules.user.massage.service.UserMassageService;
import com.zx.trade.datamodel.UserMassage;

public class PresentServiceProvide {
    private static final int PAGE_SIZE = 30;
    private static CardNoInfoService bankCardInfoService = null;
    private static UserMassageService userMassageService = null;
    static {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        bankCardInfoService = (CardNoInfoService) context.getBean("cardNoInfo");
        userMassageService = (UserMassageService) context.getBean("userMassage");
    }

    public static void addUserMassage(List<UserMassage> userMassages) {
        int count = 2, start = 0, end = PAGE_SIZE;
        boolean flag = false;
        for (;; count++) {
            List<UserMassage> msg = new ArrayList<UserMassage>();
            if (end >= userMassages.size()) {
                end = userMassages.size() - 1;
                flag = true;
            }
            for (int i = start; i <= end; i++) {
                msg.add(userMassages.get(i));
            }
            userMassageService.addUserMassage(msg);
            if (flag) {
                break;
            }
            start = PAGE_SIZE * (count - 1) + 1;
            end = PAGE_SIZE * count;
        }
    }

    public static void addBankCardInfo(List<BankCardMassage> bankCardMassage) {
        int count = 2, start = 0, end = PAGE_SIZE;
        boolean flag = false;
        for (;; count++) {
            List<BankCardMassage> msg = new ArrayList<BankCardMassage>();
            if (end >= bankCardMassage.size()) {
                end = bankCardMassage.size() - 1;
                flag = true;
            }
            for (int i = start; i <= end; i++) {
                msg.add(bankCardMassage.get(i));
            }
            bankCardInfoService.addCardNoInfo(msg);
            if (flag) {
                break;
            }
            start = PAGE_SIZE * (count - 1) + 1;
            end = PAGE_SIZE * count;
        }
    }
}
