package com.zx.frontend.promotion.notice.util;

import java.util.List;

import com.zx.datamodels.content.bean.entity.Notice;
import com.zx.datamodels.market.bean.entity.Exchange;

public class NoticeUtil {

    private static final int YOUBIQUAN_MARKET_ID = 7;

    public static List<Notice> fillNotices(List<Notice> notices) {
        if (!notices.isEmpty()) {
            for (int i = 0, len = notices.size(); i < len; i++) {
                Notice notice = notices.get(i);
                if (notice.getMarket() == YOUBIQUAN_MARKET_ID) {
                    notice.setExchange(Exchange.getYoubiquanExchange());
                    notices.add(i, notice);
                }
            }
            return notices;
        }
        return notices;
    }
}
