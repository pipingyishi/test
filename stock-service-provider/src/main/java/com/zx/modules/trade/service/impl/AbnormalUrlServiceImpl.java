package com.zx.modules.trade.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.zx.modules.trade.service.AbnormalUrlService;
import com.zx.stock.mybatis.dao.ZxAbnormalUrlMapper;
import com.zx.stock.mybatis.entity.ZxAbnormalUrl;
import com.zx.stock.mybatis.entity.ZxAbnormalUrlExample;
import com.zx.trade.datamodel.AbnormalUrl;

@Service("abnormalUrl")
public class AbnormalUrlServiceImpl implements AbnormalUrlService {
    @Autowired
    private ZxAbnormalUrlMapper zxAbnormalUrlMapper;
    
    @Override
    public List<AbnormalUrl> selectAbnormalUrl(String tradePlatform,int pageNum,int pageSize) {
        ZxAbnormalUrlExample example = new ZxAbnormalUrlExample();
        ZxAbnormalUrlExample.Criteria criteria = example.createCriteria();
        criteria.andTradePlatformEqualTo(tradePlatform);
        PageHelper.startPage(pageNum, pageSize, false);
        List<ZxAbnormalUrl> dots = zxAbnormalUrlMapper.selectByExample(example);
        List<AbnormalUrl> vos = Lists.newArrayListWithExpectedSize(dots.size());
        AbnormalUrl vo = null;
        for (ZxAbnormalUrl dot : dots) {
            vo = new AbnormalUrl();
            BeanUtils.copyProperties(dot, vo);
            vos.add(vo);
        }
        return vos;
    }

}
