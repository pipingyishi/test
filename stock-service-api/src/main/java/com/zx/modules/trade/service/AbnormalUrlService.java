package com.zx.modules.trade.service;

import java.util.List;

import com.zx.trade.datamodel.AbnormalUrl;

public interface AbnormalUrlService {
    public List<AbnormalUrl> selectAbnormalUrl(String tradePlatform,int pageum,int pageSize);
}
