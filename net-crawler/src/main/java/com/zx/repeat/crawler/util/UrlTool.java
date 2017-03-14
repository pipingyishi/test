package com.zx.repeat.crawler.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zx.modules.url.service.UrlService;
import com.zx.stock.datamodel.UserUrlInfo;

public class UrlTool {
    private static Logger LOG = LoggerFactory.getLogger(UrlTool.class);
    private static int idStart=6147263;
    private static final int totalCount=17054;
    private static final int avgCount=50;
    private static final String URL="http://shop.ybk001.com/trade_cancel_show.asp?id=";
    private static UrlService urlService = null;
    static {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        urlService = (UrlService) context.getBean("userUrlInfo");
    }
    
    public static void SaveUrl(){
        List<String> urList=null;
        for(int i=0;i<totalCount;i++){
            urList=getUrl(idStart,idStart+avgCount);
            List<UserUrlInfo> userUrlInfos=new ArrayList<UserUrlInfo>();
            for(int j=0;j<urList.size();j++){
                UserUrlInfo userUrlInfo=new UserUrlInfo();
                userUrlInfo.setUrl(urList.get(j));
                userUrlInfos.add(userUrlInfo);
            }
            LOG.info("cycle to "+i+" times");
            try{
                urlService.addUrl(userUrlInfos);
            }catch(Exception e){
                LOG.warn("An error occour when saving url into datebase"+e);
            }
            idStart+=avgCount;
        }
    }
    
    public static List<String> getUrl(int idStart,int idEnd){
        List<String> url=new ArrayList<String>();
        for(int i=idStart;i<idEnd;i++){
            url.add(URL+i);
        }
        return url;
     }
}
