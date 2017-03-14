package com.zx.user.massage.entity;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zx.modules.trade.service.UserUrlService;

public class GrabUserMassage {
    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        UserUrlService userUrlService = (UserUrlService) context.getBean("userUrl");
        AbsUserMassage userMassage = new UserMassage();
        userMassage.execute(userUrlService);
    }
}
