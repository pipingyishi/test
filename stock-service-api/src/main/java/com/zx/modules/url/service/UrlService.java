package com.zx.modules.url.service;

import java.util.List;

import com.zx.stock.datamodel.UserUrlInfo;

public interface UrlService {
    public boolean  addUrl(List<UserUrlInfo> UserUrlINfo);
    
    public List<UserUrlInfo> selectUrl(int pageNum,int pageSize);
    
    public boolean updatePosition(int position,boolean positionValue);
    
    public List<UserUrlInfo> selectUpdatedUrlId();
    
}
