package com.zx.modules.user.massage.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zx.modules.user.massage.service.CardNoInfoService;
import com.zx.stock.mybatis.dao.ZxCrawlerUserCardMapper;
import com.zx.stock.mybatis.entity.ZxCrawlerUserCard;
import com.zx.trade.datamodel.BankCardMassage;

@Service("cardNoInfo")
public class CardNoInfoServiceImpl implements CardNoInfoService {

    @Autowired
    private ZxCrawlerUserCardMapper zxCrawlerUserCardMapper;
    @Override
    public boolean addCardNoInfo(List<BankCardMassage> cardNumMassages) {
        for(int i=0;i<cardNumMassages.size();i++){
            ZxCrawlerUserCard zc=new ZxCrawlerUserCard();
            zc.setCardId(cardNumMassages.get(i).getCardId());
            zc.setUserId(cardNumMassages.get(i).getUserId());
            zc.setCardNo(cardNumMassages.get(i).getCardNo());
            zc.setCardType(cardNumMassages.get(i).getCardType());
            zc.setDepositBank(cardNumMassages.get(i).getDepositBank());
            zc.setIsDel(false);
            int flag=zxCrawlerUserCardMapper.insertSelective(zc);
            if(flag==0){
                return false;
            }
        }
        return true;
    }
}
