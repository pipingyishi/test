package com.zx.modules.trade.service.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.zx.modules.trade.service.UserUrlService;
import com.zx.stock.mybatis.dao.ZxUserUrlMapper;
import com.zx.stock.mybatis.entity.ZxUserUrl;
import com.zx.stock.mybatis.entity.ZxUserUrlExample;
import com.zx.stock.mybatis.entity.ZxUserUrlExample.Criteria;
import com.zx.trade.datamodel.UserUrlMassage;

@Service("userUrl")
public class UserUrlServiceImpl implements UserUrlService {
    @Autowired
    private ZxUserUrlMapper zxUserUrlMapper;
    @Autowired
    private JdbcTemplate template;
    
    private static final String BATCH_INSERT ="insert into zx_user_url (board_id, url_id, date, is_del, section, flag)"
            +" values (?, ?, ?, ?, ?,?) " + " ON DUPLICATE KEY UPDATE board_id=VALUES(board_id),"
            +"url_id=VALUES(url_id),date=VALUES(date),is_del=VALUES(is_del),section=VALUES(section),flag=VALUES(flag)";
    
    @Override
    public boolean addUrl(List<UserUrlMassage> url) {
       for(int i=0;i<url.size();i++){
           ZxUserUrl zu=new ZxUserUrl();
           zu.setDate(url.get(i).getDate());
           zu.setIsDel(url.get(i).getIsDel());
           zu.setSection(url.get(i).getSection());
           zu.setBoardId(url.get(i).getBoardId());
           zu.setUrlId(url.get(i).getUrlId());
           int flag=zxUserUrlMapper.insertSelective(zu);
           if(flag==0){
               return false;
           }
       }
        return true;
    }
    @Override
    public List<UserUrlMassage> selectId(int offset,int limit) {
        ZxUserUrlExample example = new ZxUserUrlExample();
        example.setOrderByClause("id ASC");
        RowBounds rowBounds = new RowBounds(offset,limit);
        List<ZxUserUrl> dots=zxUserUrlMapper.selectByExampleWithRowbounds(example, rowBounds);
        List<UserUrlMassage>vos=Lists.newArrayListWithExpectedSize(dots.size());
        UserUrlMassage vo=null;
        for(ZxUserUrl dot:dots){
            vo=new UserUrlMassage();
            BeanUtils.copyProperties(dot, vo);
            vos.add(vo);
        }
        return vos;
    }
    @Override
    public boolean updateId(int original, int present) {
        ZxUserUrl zu=new ZxUserUrl();
        ZxUserUrlExample example = new ZxUserUrlExample();
        Criteria criteria = example.createCriteria();
        int flag=0;
        if(original!=0){
            criteria.andIdEqualTo(original);
            zu.setFlag(false);
            flag=zxUserUrlMapper.updateByExampleSelective(zu, example);
        }
        if(flag==0){
            return false;
        }
        ZxUserUrlExample exp = new ZxUserUrlExample();
        Criteria criteriac = exp.createCriteria();
        criteriac.andIdEqualTo(present);
        zu.setFlag(true);
        flag=zxUserUrlMapper.updateByExampleSelective(zu, exp);
        if(flag==0){
            return false;
        }
        return true;
    }
    @Override
    public void batchInsertUpdate(List<UserUrlMassage> dtos) {
        template.batchUpdate(BATCH_INSERT, dtos, 10, new ParameterizedPreparedStatementSetter<UserUrlMassage>() {
            @Override
            public void setValues(PreparedStatement ps, UserUrlMassage dto) throws SQLException {
                ps.setString(1, dto.getBoardId());
                ps.setString(2, dto.getUrlId());
                Date updateDate = new Date(dto.getDate().getTime());
                ps.setDate(3, updateDate);
                ps.setBoolean(4, dto.getIsDel());
                ps.setInt(5, dto.getSection());
                ps.setBoolean(6, dto.getFlag());
            }
        });
    }
}
