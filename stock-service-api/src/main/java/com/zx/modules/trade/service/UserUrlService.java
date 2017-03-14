package com.zx.modules.trade.service;

import java.util.List;

import com.zx.trade.datamodel.UserUrlMassage;

public interface UserUrlService {
    public boolean addUrl(List<UserUrlMassage> url);
    
    public List<UserUrlMassage> selectId(int offset,int limit);
    
    public boolean updateId(int original,int present);
    
    public void batchInsertUpdate(List<UserUrlMassage> dtos);
}
