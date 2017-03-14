package com.zx.modules.promotion.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.zx.modules.promotion.service.TradeService;
import com.zx.promotion.datamodel.TradeTable;
import com.zx.promotion.mybatis.dao.ZxTradeTableMapper;
import com.zx.promotion.mybatis.entity.ZxTradeTable;
import com.zx.promotion.mybatis.entity.ZxTradeTableExample;
import com.zx.promotion.mybatis.entity.ZxTradeTableExample.Criteria;
import com.zx.promotion.tool.ZxQueryResult;

@Service("tradeService") // 对应controller下面的配置文件 test-service-provider.xml
public class TradeServiceImpl implements TradeService {
    private static final String condition = "全部";
    private static final boolean isDel = true;
//    @Autowired
//    private ZxTestTableMapper testTableMapper;
    @Autowired
    private ZxTradeTableMapper testTradeMapper;

//    @Override
//    public List<TestTable> findAllData() {
//        ZxTestTableExample example = new ZxTestTableExample();
//        example.setOrderByClause("test_id ASC");
//        List<ZxTestTable> dtos = testTableMapper.selectByExample(example);
//        List<TestTable> vos = Lists.newArrayListWithExpectedSize(dtos.size());
//        TestTable vo = null;
//        // dtos:数据库查询结果集
//        // vos:最终结果集集合
//        for (ZxTestTable dto : dtos) {
//            vo = new TestTable();
//            BeanUtils.copyProperties(dto, vo); // 将每一次遍历的结果放入到vo中
//            vos.add(vo);
//        }
//        return vos; // 结果放回controller
//    }

    @Override
    public int addTradeData(TradeTable trade) {
        ZxTradeTable zt = new ZxTradeTable();
        zt.setTradeAmount(trade.getTradeAmount());
        zt.setTradeCname(trade.getTradeCname());
        if (trade.getTradePname().equals("请输入")) {
            zt.setTradePname(" ");
        } else {
            zt.setTradePname(trade.getTradePname());
        }
        zt.setTradePrice(trade.getTradePrice());
        zt.setTradeTelphone(trade.getTradeTelphone());
        zt.setTradeUnit(trade.getTradeUnit());
        Date date = new Date();
        zt.setTradeTime(date);
        zt.setTradeMarketValue(trade.getTradeMarketValue());
        zt.setTradeType(trade.getTradeType());
        zt.setTradeVarieties(trade.getTradeVarieties());
        zt.setTradeFlag(trade.getTradeFlag());
        int flag = testTradeMapper.insertSelective(zt);
        return flag;
    }

    @Override
    public List<TradeTable> selectTradeData(String tradeType, int pageNo, int pageSize) {
        ZxTradeTableExample example = new ZxTradeTableExample();
        example.setOrderByClause("trade_time DESC");
        Criteria criteria = example.createCriteria();
        criteria.andTradeTypeEqualTo(tradeType);
        criteria.andIsDelEqualTo(!isDel);
        RowBounds rowBounds = new RowBounds(pageNo, pageSize);
        List<ZxTradeTable> listPage = testTradeMapper.selectByExampleWithRowbounds(example, rowBounds);
        List<TradeTable> vos = Lists.newArrayListWithExpectedSize(listPage.size());
        TradeTable rs = null;
        for (ZxTradeTable zt : listPage) {
            rs = new TradeTable();
            BeanUtils.copyProperties(zt, rs);
            vos.add(rs);
        }
        return vos;
    }

    @Override
    public ZxQueryResult backstagePage(String tradeType, String tradeVarieties, int pageNo, int pageSize) {
        ZxTradeTableExample example = new ZxTradeTableExample();
        example.setOrderByClause("trade_time DESC");
        Criteria criteria = example.createCriteria();
        if (tradeType.equals(condition) && tradeVarieties.equals(condition)) {
            criteria.andTradeIdIsNotNull();
            criteria.andIsDelEqualTo(!isDel);
        } else {
            if (!tradeType.equals(condition)) {
                criteria.andTradeTypeEqualTo(tradeType);
            }
            if (!tradeVarieties.equals(condition)) {
                criteria.andTradeVarietiesEqualTo(tradeVarieties);
            }
            criteria.andIsDelEqualTo(!isDel);
        }
        RowBounds rowBounds = new RowBounds(pageNo, pageSize);
        List<ZxTradeTable> listPage = testTradeMapper.selectByExampleWithRowbounds(example, rowBounds);
        List<TradeTable> vos = Lists.newArrayListWithExpectedSize(listPage.size());
        TradeTable rs = null;
        for (ZxTradeTable zt : listPage) {
            rs = new TradeTable();
            BeanUtils.copyProperties(zt, rs);
            vos.add(rs);
        }
        int count = testTradeMapper.countByExample(example);
        int totalPages = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        ZxQueryResult result = new ZxQueryResult();
        result.setPageList(vos);
        result.setTotalPages(totalPages);
        return result;
    }

    @Override
    public boolean deleteRecord(List<Integer> idList) {
        for (int i = 0; i < idList.size(); i++) {
            ZxTradeTable zt = new ZxTradeTable();
            ZxTradeTableExample example = new ZxTradeTableExample();
            Criteria criteria = example.createCriteria();
            criteria.andTradeIdEqualTo(idList.get(i));
            zt.setIsDel(isDel);
            if (testTradeMapper.updateByExampleSelective(zt, example) == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean saveRecord(List<TradeTable> saveRecord) {
        for (int i = 0; i < saveRecord.size(); i++) {
            ZxTradeTable zt = new ZxTradeTable();
            ZxTradeTableExample example = new ZxTradeTableExample();
            Criteria criteria = example.createCriteria();
            criteria.andTradeIdEqualTo(saveRecord.get(i).getTradeId());
            zt.setTradeFlag(saveRecord.get(i).getTradeFlag());
            zt.setTradeRemakes(saveRecord.get(i).getTradeRemakes());
            zt.setTradeType(saveRecord.get(i).getTradeType());
            zt.setTradeCname(saveRecord.get(i).getTradeCname());
            zt.setTradeVarieties(saveRecord.get(i).getTradeVarieties());
            zt.setTradePrice(saveRecord.get(i).getTradePrice());
            zt.setTradeAmount(saveRecord.get(i).getTradeAmount());
            zt.setTradeMarketValue(Math.round(saveRecord.get(i).getTradePrice()*saveRecord.get(i).getTradeAmount()/10000 * 100 ) / 100.0);
            zt.setTradeUnit(saveRecord.get(i).getTradeUnit());
            zt.setTradePname(saveRecord.get(i).getTradePname());
            zt.setTradeTelphone(saveRecord.get(i).getTradeTelphone());
            zt.setTradeTime(saveRecord.get(i).getTradeTime());
            int flag = testTradeMapper.updateByExampleSelective(zt, example);
            if (flag == 0) {
                return false;
            }
        }
        return true;
    }
}
