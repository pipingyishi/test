package com.zx.frontend.promotion.property.util;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.zx.datamodels.blog.bean.BlogModel;
import com.zx.datamodels.notice.bean.NoticeModel;

public class Property {
    public static String getUrl(String propertyName, String from, String to) {
        try {
            Resource resource = new ClassPathResource(propertyName);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            String fetchUrl = "";
            if (from.contains("blog")) {
                fetchUrl = props.getProperty("blog_url");
            } else {
                fetchUrl = props.getProperty("notice_url");
            }
            from = "{" + from + "}";
            return StringUtils.replace(fetchUrl, from, to);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getOpenAccount(String propertyName, Object obj, String openAccountStr,
            String openAccountUrlStr) {
        try {
            Resource resource = new ClassPathResource(propertyName);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            String openAccount = props.getProperty(openAccountStr);
            String openAccountUrl = props.getProperty(openAccountUrlStr);
            if (obj instanceof BlogModel) {
                BlogModel blogModel = (BlogModel) obj;
                blogModel.setOpenAccount(openAccount);
                blogModel.setOpenAccountUrl(openAccountUrl);
                return blogModel;
            } else {
                NoticeModel noticeModel = (NoticeModel) obj;
                noticeModel.setOpenAccount(openAccount);
                noticeModel.setOpenAccountUrl(openAccountUrl);
                return noticeModel;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
