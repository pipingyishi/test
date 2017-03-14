package com.zx.user.merge.entity;

import com.zx.bean.entity.BankCardMassage;
import com.zx.bean.entity.UserInformation;

public class OriginUserMassage {
    public static void main(String args[]) {
        UserInformation userInfo = OriginServiceProvide.getUserMassage();
        BankCardMassage bankCardMassage = OriginServiceProvide.getBankCardMassage();
        UserMsg.mergeUserInfo(userInfo, bankCardMassage);
        System.out.println("信息合并完毕");
    }
}
