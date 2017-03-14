package com.zx.abnormal.user.massage.entity;

import java.util.List;

import com.zx.modules.trade.service.AbnormalUrlService;

public abstract class AbsYuserMassage {
    public void execute(AbnormalUrlService abnormalUrlService) {
        List<String> urList = selectUrl(abnormalUrlService);
        getUserMassage(urList);
    }

    abstract public List<String> selectUrl(AbnormalUrlService abnormalUrlService);

    abstract public void getUserMassage(List<String> urList);
}
