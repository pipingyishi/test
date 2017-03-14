package com.zx.crawler.stock.trade.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zx.repeat.crawler.util.GrabTool;


public class ReStockTradeGrab {
    private static Logger LOG = LoggerFactory.getLogger(ReStockTradeGrab.class);
    public static void main(String[] args) {
        int threadNum = GrabTool.getThreadNum();
        try{
            GrabTool.grabUserInfo(threadNum);
        }catch(Exception e){
            LOG.warn("the reason for the end of the program is"+e);
        }
    }
}
