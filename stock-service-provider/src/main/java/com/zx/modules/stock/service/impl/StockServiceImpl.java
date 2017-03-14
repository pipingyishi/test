package com.zx.modules.stock.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.sql.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.zx.modules.stock.service.StockService;
import com.zx.stock.datamodel.StockModelInfo;
import com.zx.stock.datamodel.StockOrigin;
import com.zx.stock.mybatis.dao.StockinfoMapper;
import com.zx.stock.mybatis.dao.ZxStockInfoMapper;
import com.zx.stock.mybatis.entity.Stockinfo;
import com.zx.stock.mybatis.entity.StockinfoExample;
import com.zx.stock.mybatis.entity.StockinfoExample.Criteria;
import com.zx.stock.mybatis.entity.ZxStockInfo;
import com.zx.stock.mybatis.entity.ZxStockInfoExample;

@Repository
@Service("stockService")
public class StockServiceImpl implements StockService {
    @Autowired
    private StockinfoMapper stockInfoMapper;
    @Autowired
    private ZxStockInfoMapper zxStockInfoMapper;
    @Autowired
    private JdbcTemplate template;
    private static final String BATCH_INSERT = "insert into zx_stock_info (market, stock_code, "
            + "is_del, update_date, stock_description) "
            + " values (?, ?, ?, ?, ?) " + " ON DUPLICATE KEY UPDATE market = VALUES(market), "
            + " stock_code = VALUES(stock_code),"
            + " is_del = VALUES(is_del), update_date = VALUES(update_date), "
            + " stock_description = VALUES(stock_description) ";

    @Override
    public List<StockOrigin> selectStock(int market) {
        StockinfoExample example = new StockinfoExample();
        Criteria criteria = example.createCriteria();
        criteria.andMarketEqualTo(market);
        List<Stockinfo> stockinfos = stockInfoMapper.selectByExample(example);
        List<StockOrigin> resule = Lists.newArrayListWithExpectedSize(stockinfos.size());
        StockOrigin so = null;
        for (Stockinfo rs : stockinfos) {
            so = new StockOrigin();
            BeanUtils.copyProperties(rs, so);
            resule.add(so);
        }
        return resule;
    }

    @Override
    public void batchInsertUpdate(List<StockModelInfo> dtos) {
        template.batchUpdate(BATCH_INSERT, dtos, 100, new ParameterizedPreparedStatementSetter<StockModelInfo>() {
            @Override
            public void setValues(PreparedStatement ps, StockModelInfo dto) throws SQLException {
                ps.setInt(1, dto.getMarket());
                ps.setString(2, dto.getStockCode());
                ps.setBoolean(3, dto.getIsDel());
                Date updateDate = new Date(dto.getUpdateDate().getTime());
                ps.setDate(4, updateDate);
                ps.setString(5, dto.getStockDescription());
            }
        });
    }

    @Override
    public List<StockModelInfo> selectMassage(int maketId, String stockCode) {
        ZxStockInfoExample example = new ZxStockInfoExample();
        com.zx.stock.mybatis.entity.ZxStockInfoExample.Criteria criteria = example.createCriteria();
        criteria.andMarketEqualTo(maketId);
        criteria.andStockCodeEqualTo(stockCode);
        criteria.andIsDelEqualTo(false);
        List<ZxStockInfo> result = zxStockInfoMapper.selectByExampleWithBLOBs(example);
        if (result.size() == 0) {
            return null;
        }
        List<StockModelInfo> vos = Lists.newArrayListWithExpectedSize(result.size());
        StockModelInfo rs = null;
        for (ZxStockInfo zs : result) {
            rs = new StockModelInfo();
            BeanUtils.copyProperties(zs, rs);
            vos.add(rs);
        }
        return vos;
    }

    @Override
    public List<StockOrigin> selectStockMassage(int market, String stockCode) {
        StockinfoExample example = new StockinfoExample();
        Criteria criteria = example.createCriteria();
        criteria.andMarketEqualTo(market);
        criteria.andStockcodeEqualTo(stockCode);
        List<Stockinfo> stockinfos = stockInfoMapper.selectByExample(example);
        List<StockOrigin> resule = Lists.newArrayListWithExpectedSize(stockinfos.size());
        StockOrigin so = null;
        for (Stockinfo rs : stockinfos) {
            so = new StockOrigin();
            BeanUtils.copyProperties(rs, so);
            resule.add(so);
        }
        return resule;
    }
}
