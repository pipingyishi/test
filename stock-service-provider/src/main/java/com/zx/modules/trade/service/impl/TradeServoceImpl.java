package com.zx.modules.trade.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.zx.modules.trade.service.TradeService;
import com.zx.stock.mybatis.dao.ZxAbnormalUrlMapper;
import com.zx.stock.mybatis.dao.ZxMassageMapper;
import com.zx.stock.mybatis.dao.ZxStockTradeInfoMapper;
import com.zx.stock.mybatis.dao.ZxStockTradeMapper;
import com.zx.stock.mybatis.dao.ZxStockTradeMassageMapper;
import com.zx.stock.mybatis.entity.ZxAbnormalUrl;
import com.zx.stock.mybatis.entity.ZxAbnormalUrlExample;
import com.zx.stock.mybatis.entity.ZxMassage;
import com.zx.stock.mybatis.entity.ZxMassageExample;
import com.zx.stock.mybatis.entity.ZxStockTrade;
import com.zx.stock.mybatis.entity.ZxStockTradeExample;
import com.zx.stock.mybatis.entity.ZxStockTradeInfo;
import com.zx.stock.mybatis.entity.ZxStockTradeInfoExample;
import com.zx.stock.mybatis.entity.ZxStockTradeInfoExample.Criteria;
import com.zx.stock.mybatis.entity.ZxStockTradeMassage;
import com.zx.stock.mybatis.entity.ZxStockTradeMassageExample;
import com.zx.trade.datamodel.AbnormalUrl;
import com.zx.trade.datamodel.StockTrade;
import com.zx.trade.datamodel.StockTradeMassage;
import com.zx.trade.datamodel.TradeMassage;
import com.zx.trade.datamodel.TradeResult;
import com.zx.trade.datamodel.TradeSrtockResult;
import com.zx.trade.datamodel.TradeStock;
import com.zx.trade.datamodel.ZxQueryResult;

@Service("tradeService")
@Transactional(value = "serviceTxManager")
public class TradeServoceImpl implements TradeService {
    @Autowired
    private ZxMassageMapper zxMassageMapper;
    @Autowired
    private ZxStockTradeMapper zxStockTradeMapper;
    @Autowired
    private ZxAbnormalUrlMapper zxAbnormalUrlMapper;
    @Autowired
    private ZxStockTradeInfoMapper zxStockTradeInfoMapper;
    @Autowired
    private ZxStockTradeMassageMapper zxStockTradeMassageMapper;

    @Override
    @Transactional(readOnly = false)
    public boolean addStockTrade(TradeResult dots) {
        TradeStock tradeStock = dots.getTradeStock();
        List<TradeMassage> tradeMassage = dots.getTradeMassage();
        ZxStockTrade zs = new ZxStockTrade();
        zs.setAddress(tradeStock.getAddress());
        zs.setCreditScore(tradeStock.getCreditScore());
        zs.setIsDel(tradeStock.getIsDel());
        zs.setUserName(tradeStock.getUserName());
        zs.setOnlineName(tradeStock.getOnlineName());
        zs.setPostIntegral(tradeStock.getPostIntegral());
        zs.setPostTimes(tradeStock.getPostTimes());
        zs.setRegistTime(tradeStock.getRegistTime());
        zs.setScoreTimes(tradeStock.getScoreTimes());
        zs.setTitel(tradeStock.getTitel());
        zs.setTradeGrade(tradeStock.getTradeGrade());
        zs.setUrlName(tradeStock.getUrlName());
        zs.setQq(tradeStock.getQq());
        int flag = zxStockTradeMapper.insertSelective(zs);
        if (flag == 0) {
            return false;
        }
        for (int j = 0; j < tradeMassage.size(); j++) {
            ZxMassage zm = new ZxMassage();
            zm.setUserId(zs.getId());
            zm.setCardNumber(tradeMassage.get(j).getCardNumber());
            zm.setDepositBank(tradeMassage.get(j).getDepositBank());
            zm.setIsDel(tradeMassage.get(j).getIsDel());
            zm.setTelephone(tradeMassage.get(j).getTelephone());
            zm.setBankType(tradeMassage.get(j).getBankType());
            flag = zxMassageMapper.insertSelective(zm);
            if (flag == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ZxQueryResult selectUrl(String tradePlatform) {
        ZxStockTradeExample example = new ZxStockTradeExample();
        example.setOrderByClause("id ASC");
        List<ZxStockTrade> dtos = zxStockTradeMapper.selectByExample(example);
        List<TradeStock> vos = Lists.newArrayListWithExpectedSize(dtos.size());
        TradeStock vo = null;
        for (ZxStockTrade dto : dtos) {
            vo = new TradeStock();
            BeanUtils.copyProperties(dto, vo);
            vos.add(vo);
        }
        int count = zxStockTradeMapper.countByExample(example);
        List<AbnormalUrl> abnormalUrls = selectExisteAbnormalUrl(tradePlatform);
        ZxQueryResult result = new ZxQueryResult();
        result.setTotalRecords(count);
        result.setTradeStocks(vos);
        result.setAbnormalUrls(abnormalUrls);
        return result;
    }

    @Override
    public List<TradeMassage> selectMassage() {
        ZxMassageExample example = new ZxMassageExample();
        example.setOrderByClause("id ASC");
        List<ZxMassage> dtos = zxMassageMapper.selectByExample(example);
        List<TradeMassage> vos = Lists.newArrayListWithExpectedSize(dtos.size());
        TradeMassage vo = null;
        for (ZxMassage dto : dtos) {
            vo = new TradeMassage();
            BeanUtils.copyProperties(dto, vo);
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public boolean saveAbnormalUrl(AbnormalUrl abnormalUrl) {
        ZxAbnormalUrl za = new ZxAbnormalUrl();
        za.setAbnormalUrl(abnormalUrl.getAbnormalUrl());
        za.setIsDel(abnormalUrl.getIsDel());
        za.setTradePlatform(abnormalUrl.getTradePlatform());
        za.setUrlGrand(abnormalUrl.getUrlGrand());
        zxAbnormalUrlMapper.insertSelective(za);
        return false;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean addUserMassage(TradeSrtockResult dots) {
        StockTrade stockTrade = dots.getStockTrade();
        List<StockTradeMassage> stockTradeMassage = dots.getStockTradeMassages();
        ZxStockTradeInfo zt = new ZxStockTradeInfo();
        zt.setUserName(stockTrade.getUserName());
        zt.setRealName(stockTrade.getRealName());
        zt.setRegistTime(stockTrade.getRegistTime());
        zt.setTermValidity(stockTrade.getTermValidity());
        zt.setMemberLevel(stockTrade.getMemberLevel());
        zt.setTradeTimes(stockTrade.getTradeTimes());
        zt.setTradeAmount(stockTrade.getTradeAmount());
        zt.setPraiseRate(stockTrade.getPraiseRate());
        zt.setEvaluateGrand(stockTrade.getEvaluateGrand());
        zt.setAddress(stockTrade.getAddress());
        zt.setUrlName(stockTrade.getUrlName());
        zt.setIsDel(stockTrade.getIsDel());
        int flag = zxStockTradeInfoMapper.insertSelective(zt);
        if (flag == 0) {
            return false;
        }
        for (int i = 0; i < stockTradeMassage.size(); i++) {
            ZxStockTradeMassage zm = new ZxStockTradeMassage();
            zm.setUserId(zt.getId());
            zm.setTelphone(stockTradeMassage.get(i).getTelphone());
            zm.setCardNo(stockTradeMassage.get(i).getCardNo());
            zm.setDepositBank(stockTradeMassage.get(i).getDepositBank());
            zm.setIsDel(stockTradeMassage.get(i).getIsDel());
            zm.setBankType(stockTradeMassage.get(i).getBankType());
            flag = zxStockTradeMassageMapper.insertSelective(zm);
            if (flag == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ZxQueryResult queryUrl(String tradePlatform) {
        ZxStockTradeInfoExample example = new ZxStockTradeInfoExample();
        example.setOrderByClause("id ASC");
        List<ZxStockTradeInfo> dtos = zxStockTradeInfoMapper.selectByExample(example);
        List<StockTrade> vos = Lists.newArrayListWithExpectedSize(dtos.size());
        StockTrade vo = null;
        for (ZxStockTradeInfo dto : dtos) {
            vo = new StockTrade();
            BeanUtils.copyProperties(dto, vo);
            vos.add(vo);
        }
        ZxStockTradeMassageExample exp = new ZxStockTradeMassageExample();
        exp.setOrderByClause("id ASC");
        List<ZxStockTradeMassage> dos = zxStockTradeMassageMapper.selectByExample(exp);
        List<StockTradeMassage> svos = Lists.newArrayListWithExpectedSize(dos.size());
        StockTradeMassage svo = null;
        for (ZxStockTradeMassage sdo : dos) {
            svo = new StockTradeMassage();
            BeanUtils.copyProperties(sdo, svo);
            svos.add(svo);
        }
        List<AbnormalUrl> abnormalUrls = selectExisteAbnormalUrl(tradePlatform);
        ZxQueryResult result = new ZxQueryResult();
        result.setStockTradeMassage(svos);
        result.setStockTrades(vos);
        result.setAbnormalUrls(abnormalUrls);
        return result;
    }

    @Override
    public List<AbnormalUrl> selectExisteAbnormalUrl(String tradePlatform) {
        ZxAbnormalUrlExample example = new ZxAbnormalUrlExample();
        com.zx.stock.mybatis.entity.ZxAbnormalUrlExample.Criteria criteria = example.createCriteria();
        criteria.andTradePlatformEqualTo(tradePlatform);
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

    @Override
    public List<TradeStock> selectUserMassage(int pageNum, int pageSize) {
        ZxStockTradeExample example = new ZxStockTradeExample();
        example.setOrderByClause("id ASC");
        PageHelper.startPage(pageNum, pageSize, false);
        List<ZxStockTrade> dots=zxStockTradeMapper.selectByExample(example);
        List<TradeStock> vos=Lists.newArrayListWithExpectedSize(dots.size());
        TradeStock vo=null;
        for(ZxStockTrade dot:dots){
            vo=new TradeStock();
            BeanUtils.copyProperties(dot, vo);
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<StockTrade> queryUserMassage(int pageNum, int pageSize) {
        ZxStockTradeInfoExample example = new ZxStockTradeInfoExample();
        example.setOrderByClause("id ASC");
        //
        Criteria criteria=example.createCriteria();
        criteria.andIdGreaterThan(2212);
        //
        PageHelper.startPage(pageNum, pageSize, false);
        List<ZxStockTradeInfo> dtos = zxStockTradeInfoMapper.selectByExample(example);
        List<StockTrade> vos = Lists.newArrayListWithExpectedSize(dtos.size());
        StockTrade vo = null;
        for (ZxStockTradeInfo dto : dtos) {
            vo = new StockTrade();
            BeanUtils.copyProperties(dto, vo);
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<StockTradeMassage> queryUserInfo(int pageNum, int pageSize) {
        ZxStockTradeMassageExample exp = new ZxStockTradeMassageExample();
        exp.setOrderByClause("id ASC");
        //
        ZxStockTradeMassageExample.Criteria criteria=exp.createCriteria();
        criteria.andIdGreaterThan(2904);
        //
        PageHelper.startPage(pageNum, pageSize, false);
        List<ZxStockTradeMassage> dos = zxStockTradeMassageMapper.selectByExample(exp);
        List<StockTradeMassage> svos = Lists.newArrayListWithExpectedSize(dos.size());
        StockTradeMassage svo = null;
        for (ZxStockTradeMassage sdo : dos) {
            svo = new StockTradeMassage();
            BeanUtils.copyProperties(sdo, svo);
            svos.add(svo);
        }
        return svos;
    }
}
