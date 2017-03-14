package com.zx.frontend.promotion.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.zx.datamodels.store.entity.Merchant;
import com.zx.datamodels.store.entity.Product;
import com.zx.datamodels.store.entity.ProductAvailability;
import com.zx.datamodels.user.bean.entity.User;
import com.zx.frontend.promotion.trust.util.TrustStockUtil;
import com.zx.frontend.promotion.trust.util.UserInfoUtil;
import com.zx.frontend.promotion.util.DigitUtil;
import com.zx.modules.store.service.MerchantService;
import com.zx.modules.store.service.ProductAvailabilityService;
import com.zx.modules.store.service.ProductService;
import com.zx.modules.user.service.AccountService;
import com.zx.promotion.datamodel.HostStock;
import com.zx.promotion.datamodel.UserInfo;

@Controller
@RequestMapping(value = "trust/")
public class TrustController {
    private static final String PRODUCT = "pages/trust/product::paging";
    private static final String PRODUCT_DETAIL = "/pages/trust/product-detail";
    private static final String PC_PRODUCT_DETAIL = "/pages/trust/pc-product-detail";
    private static final int PRODUCT_ID = 1;
    private static final int PAGE_NO = 0;
    private static final int PAGE_SIZE = 10;
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductAvailabilityService productAvailabilityService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "merchant/{id}")
    public String trustStock(@PathVariable("id") String id, ModelMap model,Device device) {
        if (!DigitUtil.isAllDigit(id)) {
            return "/resources/error";
        }
        Long userId = Long.parseLong(id);
        Merchant merchant = merchantService.findByUserId(userId);
        User user = accountService.findByUserId(userId);
        if (user == null) {
            return "/resources/error";
        }
        UserInfo userInfo = UserInfoUtil.getUserMassage(merchant, user, PRODUCT_ID);
        userInfo.setUserId(userId);
        model.put("userInfo", userInfo);
        model.put("merchant", merchant);
        boolean isMobile=device.isMobile();
        if (isMobile) {
            return "pages/trust/host-stock";
        } else {
            return "pages/trust/pc-host-stock";
        }
    }

    @RequestMapping(value = "stock/{type}/{userId}", method = RequestMethod.GET)
    public String product(@PathVariable String type, @PathVariable long userId, ModelMap model) {
        boolean sellBuyFlag = type.equals("sell") ? true : false;
        Merchant merchant = merchantService.findByUserId(userId);
        List<HostStock> hostStocks = null;
        if (merchant != null) {
            long merchanId = merchant.getMerchantId();
            List<Product> products = productService.findAllProductsWithDescByMerchant(merchanId, PRODUCT_ID,
                    sellBuyFlag, PAGE_NO, PAGE_SIZE, null);
            hostStocks = TrustStockUtil.trustStock(products, productAvailabilityService);
        }
        model.put("hostStocks", hostStocks);
        return PRODUCT;
    }

    @RequestMapping(value = "product/{id}")
    public String productDetail(@PathVariable("id") String id, ModelMap model,Device device) {
        Long productId = Long.parseLong(id);
        Product product = productService.findByProductId(productId);
        Merchant merchant = product.getMerchant();
        Long userId = merchant.getUserId();
        User user = accountService.findByUserId(userId);
        UserInfo userMassage = UserInfoUtil.getUserMassage(merchant, user, PRODUCT_ID);
        userMassage.setUserId(userId);
        HostStock hostStock = new HostStock();
        hostStock = TrustStockUtil.getProductDesc(product, hostStock);
        ProductAvailability productAvailability = productAvailabilityService.findByProductIdAndDefaultRegion(productId);
        hostStock = TrustStockUtil.getProductStock(product, hostStock, productId, productAvailability);
        model.put("userMassage", userMassage);
        model.put("hostStocks", hostStock);
        boolean isMobile=device.isMobile();
        if (isMobile) {
            return PRODUCT_DETAIL;
        } else {
            return PC_PRODUCT_DETAIL;
        }
    }
}
