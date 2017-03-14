package com.zx.frontend.promotion.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.zx.datamodels.goods.bean.GoodsProduct;
import com.zx.datamodels.store.entity.Merchant;
import com.zx.datamodels.store.entity.Product;
import com.zx.datamodels.store.entity.ProductAvailability;
import com.zx.datamodels.user.bean.entity.User;
import com.zx.frontend.promotion.goods.util.GoodsUtil;
import com.zx.frontend.promotion.trust.util.UserInfoUtil;
import com.zx.frontend.promotion.util.DigitUtil;
import com.zx.modules.store.service.MerchantService;
import com.zx.modules.store.service.ProductAvailabilityService;
import com.zx.modules.store.service.ProductService;
import com.zx.modules.user.service.AccountService;
import com.zx.promotion.datamodel.UserInfo;


@Controller
@RequestMapping(value = "goods/")
public class GoodsController {
    private static final String PRODUCT = "pages/goods/product::part";
    private static final String PC_PRODUCT = "pages/goods/pc-product::part";
    private static final String MERCHANT_PRICE = "/pages/goods/merchant-price";
    private static final String PC_MERCHANT_PRICE = "/pages/goods/pc-merchant-price";
    private static final int PAGE_NO = 0;
    private static final int PAGE_SIZE = 10;
    private static final int PRODUCT_ID = 2;
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductAvailabilityService productAvailabilityService;

    @Autowired
    private AccountService accountService;
    
    @RequestMapping(value = "merchant/{id}")
    public String goodsPrice(@PathVariable("id") String id, ModelMap model,Device device) {
        if (!DigitUtil.isAllDigit(id)) {
            return "/resources/error";
        }
        Long userId = Long.parseLong(id);
        Merchant merchant=merchantService.findByUserId(userId);
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
            return "pages/goods/goods-price";
        } else {
            return "pages/goods/pc-goods-price";
        }
    }

    @RequestMapping(value = "product/{type}/{userId}", method = RequestMethod.GET)
    public String product(@PathVariable String type, @PathVariable long userId,
            ModelMap model,Device device) {
        boolean sellBuyFlag = type.equals("sell") ? true : false;
        Merchant merchant = merchantService.findByUserId(userId);
        List<GoodsProduct> goodsProducts = null;
        if (merchant != null) {
            long merchanId = merchant.getMerchantId();
            List<Product> products = productService.findProductsWithDescByMerchant(merchanId, PRODUCT_ID, sellBuyFlag,
                    null, null, PAGE_NO, PAGE_SIZE, null, null);
            goodsProducts = GoodsUtil.goodsProduct(products, productAvailabilityService);
        }
        model.put("goodsProducts", goodsProducts);
        boolean isMobile=device.isMobile();
        if (isMobile) {
            return PRODUCT;
        } else {
            return PC_PRODUCT;
        }
    }

    @RequestMapping(value = "product/{id}")
    public String merchantPrice(@PathVariable("id") String id, ModelMap model,Device device) {
        long productId = Long.parseLong(id);
        Product product = productService.findByProductId(productId);
        Merchant merchant = product.getMerchant();
        Long userId = merchant.getUserId();
        User user = accountService.findByUserId(userId);
        UserInfo userInfo = UserInfoUtil.getUserMassage(merchant, user, PRODUCT_ID);
        GoodsProduct goodsProduct = new GoodsProduct();
        goodsProduct = GoodsUtil.getProduct(product, goodsProduct);
        ProductAvailability productAvailability = productAvailabilityService.findByProductIdAndDefaultRegion(productId);
        goodsProduct = GoodsUtil.getGoods(goodsProduct, productId, productAvailability);
        model.put("userInfo", userInfo);
        model.put("goodsProducts", goodsProduct);
        boolean isMobile=device.isMobile();
        if (isMobile) {
            return MERCHANT_PRICE;
        } else {
            return PC_MERCHANT_PRICE;
        }
    }
}
