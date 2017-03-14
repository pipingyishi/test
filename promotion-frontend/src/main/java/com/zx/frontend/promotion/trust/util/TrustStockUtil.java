package com.zx.frontend.promotion.trust.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sound.midi.VoiceStatus;

import com.zx.datamodels.store.entity.Product;
import com.zx.datamodels.store.entity.ProductAttribute;
import com.zx.datamodels.store.entity.ProductAvailability;
import com.zx.datamodels.store.entity.ProductDescription;
import com.zx.datamodels.store.vo.ProductAttributeVo;
import com.zx.datamodels.store.vo.ProductAttributeVo.ProductAttributeValue;
import com.zx.modules.store.service.ProductAvailabilityService;
import com.zx.modules.store.service.ProductService;
import com.zx.promotion.datamodel.HostStock;

public class TrustStockUtil {
    public static HostStock getProductDesc(Product product, HostStock hostStock) {
        boolean isSellBuyFlag = product.getSaleBuyFlag();// 求购还是出售
        int transactionType = product.getTransactionType();// 自行交易还是担保交易（第二个页面）
        ProductDescription productDescription = product.getProductDesc();
        String title = productDescription.getTitle();// 哪个所
        hostStock.setSellBuyFlag(isSellBuyFlag);
        hostStock.setTransactionType(transactionType);
        hostStock.setTitle(title);
        return hostStock;
    }

    public static HostStock getProductStock(Product product, HostStock hostStock, long productId,
            ProductAvailability productAvailability) {
        ProductDescription productDescription = product.getProductDesc();
        Date availableDate = productAvailability.getDateAvailable();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateAvailable = sdf.format(availableDate);
        int quantity = productAvailability.getQuantity();// 数量
        double productPrice = productAvailability.getProductPrice();// 出售价
        String remark = productAvailability.getRemark();// 备注
        String name = productDescription.getName();// 藏品名
        hostStock.setName(name);
        hostStock.setDateAvailable(dateAvailable);
        hostStock.setQuantity(quantity);
        hostStock.setProductPrice(productPrice);
        hostStock.setRemark(remark);
        hostStock.setProductId(productId);
        return hostStock;
    }

    public static List<HostStock> trustStock(List<Product> products,
            ProductAvailabilityService productAvailabilityService) {
        List<HostStock> hostStocks = new ArrayList<HostStock>();
        for (int i = 0; i < products.size(); i++) {
            HostStock hostStock = new HostStock();
            Product product = products.get(i);
            hostStock = getProductDesc(product, hostStock);
            ProductAvailability productAvailability = productAvailabilityService
                    .findByProductIdAndDefaultRegion(product.getProductId());
            hostStock = getProductStock(product, hostStock, product.getProductId(), productAvailability);
            hostStocks.add(hostStock);
        }
        return hostStocks;
    }
}
