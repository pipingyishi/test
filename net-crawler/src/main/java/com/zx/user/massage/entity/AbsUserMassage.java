package com.zx.user.massage.entity;

import java.util.List;

import com.zx.modules.trade.service.UserUrlService;

public abstract class AbsUserMassage {
    public void execute(UserUrlService userUrlService) {
        selectId(userUrlService);
    }

    abstract public void selectId(UserUrlService userUrlService);
}
