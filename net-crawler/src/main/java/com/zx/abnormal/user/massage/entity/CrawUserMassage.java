package com.zx.abnormal.user.massage.entity;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.zx.modules.trade.service.AbnormalUrlService;

public class CrawUserMassage {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        AbnormalUrlService abnormalUrlService = (AbnormalUrlService) context.getBean("abnormalUrl");
        AbsUserMassage userMassage = new UserMassage();
        userMassage.execute(abnormalUrlService);
    }
}
