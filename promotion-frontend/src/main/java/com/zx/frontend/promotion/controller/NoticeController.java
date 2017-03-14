package com.zx.frontend.promotion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zx.datamodels.common.request.PagingRequest;
import com.zx.datamodels.content.bean.BizFeed;
import com.zx.datamodels.content.bean.entity.Dict;
import com.zx.datamodels.content.bean.entity.Notice;
import com.zx.datamodels.content.constants.ContentTypeDef;
import com.zx.datamodels.market.bean.entity.Exchange;
import com.zx.datamodels.notice.bean.NoticeModel;
import com.zx.frontend.promotion.notice.util.NoticeUtil;
import com.zx.frontend.promotion.property.util.Property;
import com.zx.frontend.promotion.util.DigitUtil;
import com.zx.modules.content.service.DictService;
import com.zx.modules.content.service.FeedService;
import com.zx.modules.content.service.NoticeService;
import com.zx.modules.market.service.ExchangeService;

@Controller
@RequestMapping(value = "notice")
public class NoticeController {

    private static final int PAGE_NUM = 0;

    private static final int PAGE_SIZE = 10;

    private static final String PROPERTY_NAME = "url.properties";

    private static final String PROPERTY = "open-account.properties";

    private static final String OPEN_ACCOUNT = "notice_open_account";

    private static final String OPEN_ACCOUNT_URL = "notice_open_account_url";

    private static final String NOTICE_PAGE = "pages/notice/notice-page::notice";

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private DictService dictService;

    /**
     * 拉取公告信息
     * 
     * @param request
     * @param userId
     * @param noticeId
     */
    @RequestMapping(value = "/{noticeId}")
    public String pullNotice(@PathVariable("noticeId") String noticeId, ModelMap model, Device device) {
        boolean isMobile = device.isMobile();
        if (isMobile) {
            if (!DigitUtil.isAllDigit(noticeId)) {
                return "/resources/error";
            }
            NoticeModel noticeModel = new NoticeModel();
            Notice notice = noticeService.findById(Long.parseLong(noticeId));
            if (notice == null) {
                return "/resources/error";
            }
            noticeModel.setNotice(notice);
            int market = notice.getMarket();
            Exchange exchange = exchangeService.getExchangeById(market);
            noticeModel.setExchange(exchange);
            List<BizFeed> bizFeeds = feedService.findBizFeedsForObject(null, ContentTypeDef.NOTICE, noticeId, PAGE_NUM,
                    PAGE_SIZE);
            noticeModel.setBizFeeds(bizFeeds);
            noticeModel = (NoticeModel) Property.getOpenAccount(PROPERTY, noticeModel, OPEN_ACCOUNT, OPEN_ACCOUNT_URL);
            model.put("noticeModel", noticeModel);
            return "pages/notice/notice";
        } else {
            String url = Property.getUrl(PROPERTY_NAME, "noticeId", noticeId);
            if (url == null) {
                return "/resources/error";
            } else {
                return url;
            }
        }
    }

    @RequestMapping(value = "")
    public String noticeList(ModelMap model) {
        List<Dict> dicts = dictService.findDictByDictName("noticeType");
        List<Exchange> exchanges = exchangeService.findFrontUseExchanges();
        List<Notice> notices = noticeService.findNotices(0, PagingRequest.PAGE_SIZE, null, null);
        notices = NoticeUtil.fillNotices(notices);
        model.put("dicts", dicts);
        model.put("exchanges", exchanges);
        model.put("notices", notices);
        return "pages/notice/notice-list";
    }

    @RequestMapping(value = "/pull/{exchangeId}/{type}/{pageNum}")
    public String findNotice(@PathVariable("pageNum") int pageNum, @PathVariable("type") Integer type,
            @PathVariable("exchangeId") Integer exchangeId, ModelMap model) {
        type = type == 0 ? null : type;
        exchangeId = exchangeId == 0 ? null : exchangeId;
        List<Dict> dicts = dictService.findDictByDictName("noticeType");
        List<Notice> notices = noticeService.findNotices(pageNum, PagingRequest.PAGE_SIZE, type, exchangeId);
        notices = NoticeUtil.fillNotices(notices);
        model.put("dicts", dicts);
        model.put("notices", notices);
        return NOTICE_PAGE;
    }
}
