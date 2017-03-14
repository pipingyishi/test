package com.zx.modules.url.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.zx.modules.url.service.UrlService;
import com.zx.stock.datamodel.UserUrlInfo;
import com.zx.stock.mybatis.dao.ZxUserUrlInfoMapper;
import com.zx.stock.mybatis.entity.ZxUserUrlInfo;
import com.zx.stock.mybatis.entity.ZxUserUrlInfoExample;
import com.zx.stock.mybatis.entity.ZxUserUrlInfoExample.Criteria;

@Service("userUrlInfo")
public class UrlServiceImpl implements UrlService {
    @Autowired
    private ZxUserUrlInfoMapper zxUserUrlInfoMapper;
    @Override
    public boolean addUrl(List<UserUrlInfo> userUrlInfos) {
        int flag=0;
        for(int i=0;i<userUrlInfos.size();i++){
            ZxUserUrlInfo zu=new ZxUserUrlInfo();
            zu.setDate(new Date());
            UserUrlInfo userUrlInfo=userUrlInfos.get(i);
            zu.setUrl(userUrlInfo.getUrl());
            flag=zxUserUrlInfoMapper.insertSelective(zu);
            if(flag==0){
                return false;
            }
        }
        return true;
    }
    
    /**
     * 分页查询
     * @param pageNum  页码
     * @param pageSize 每页显示数量
     */
    @Override
    public List<UserUrlInfo> selectUrl(int pageNum, int pageSize) {
        ZxUserUrlInfoExample example=new ZxUserUrlInfoExample();
        example.setOrderByClause("id ASC");
        PageHelper.startPage(pageNum, pageSize, false);
        List<ZxUserUrlInfo> dtos=zxUserUrlInfoMapper.selectByExample(example);
        List<UserUrlInfo> vos=Lists.newArrayListWithExpectedSize(dtos.size());
        UserUrlInfo vo=null;
        for(ZxUserUrlInfo dto:dtos){
            vo=new UserUrlInfo();
            BeanUtils.copyProperties(dto,vo);
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public boolean updatePosition(int position,boolean positionValue) {
        ZxUserUrlInfoExample example=new ZxUserUrlInfoExample();
        ZxUserUrlInfo zu=new ZxUserUrlInfo();
        Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(position);
        zu.setPosition(positionValue);
        int flag=zxUserUrlInfoMapper.updateByExampleSelective(zu, example);
        if(flag==0){
            return false;
        }
        return true;
    }

    @Override
    public List<UserUrlInfo> selectUpdatedUrlId() {
        ZxUserUrlInfoExample example=new ZxUserUrlInfoExample();
        Criteria criteria = example.createCriteria();
        criteria.andPositionEqualTo(true);
        List<ZxUserUrlInfo> dtos=zxUserUrlInfoMapper.selectByExample(example);
        List<UserUrlInfo> vos=Lists.newArrayListWithExpectedSize(dtos.size());
        UserUrlInfo vo=null;
        for(ZxUserUrlInfo dto:dtos){
            vo=new UserUrlInfo();
            BeanUtils.copyProperties(dto,vo);
            vos.add(vo);
        }
        return vos;
    }
}
