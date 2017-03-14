package com.zx.modules.user.massage.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zx.modules.user.massage.service.UserMassageService;
import com.zx.stock.mybatis.dao.ZxCrawlerUserInfoMapper;
import com.zx.stock.mybatis.entity.ZxCrawlerUserInfo;
import com.zx.trade.datamodel.UserMassage;

@Service("userMassage")
public class UserMassageServiceImpl implements UserMassageService {
    @Autowired
    private ZxCrawlerUserInfoMapper zxCrawlerUserInfoMapper;
    
    @Override
    public boolean addUserMassage(List<UserMassage> userMassages) {
        for(int i=0;i<userMassages.size();i++){
            ZxCrawlerUserInfo zu=new ZxCrawlerUserInfo();
            zu.setUserId(userMassages.get(i).getUserId());
            zu.setUserName(userMassages.get(i).getUserName());
            zu.setRealName(userMassages.get(i).getRealName());
            zu.setTitle(userMassages.get(i).getTitle());
            zu.setTradeGrade(userMassages.get(i).getTradeGrade());
            zu.setCreditScore(userMassages.get(i).getCreditScore());
            zu.setScoreTimes(userMassages.get(i).getScoreTimes());
            zu.setPostTimes(userMassages.get(i).getPostTimes());
            zu.setPostScore(userMassages.get(i).getPostScore());
            zu.setRegisterTime(userMassages.get(i).getRegisterTime());
            zu.setEffectiveDate(userMassages.get(i).getEffectiveDate());
            zu.setTradeTimes(userMassages.get(i).getTradeTimes());
            zu.setDealValue(userMassages.get(i).getDealValue());
            zu.setPraiseRate(userMassages.get(i).getPraiseRate());
            zu.setEvaluateGrand(userMassages.get(i).getEvaluateGrand());
            zu.setAddress(userMassages.get(i).getAddress());
            zu.setQq(userMassages.get(i).getQq());
            zu.setUrl(userMassages.get(i).getUrl());
            zu.setAddDate(new Date());
            zu.setPlatform(userMassages.get(i).getPlatform());
            zu.setIsDel(userMassages.get(i).getIsDel());
            zu.setTelephone1(userMassages.get(i).getTelephone1());
            zu.setTelephone2(userMassages.get(i).getTelephone2());
            zu.setTelephone3(userMassages.get(i).getTelephone3());
            zu.setTelephone4(userMassages.get(i).getTelephone4());
            int flag=zxCrawlerUserInfoMapper.insertSelective(zu);
            if(flag==0){
                return false;
            }
        }
        return true;
    }

}
