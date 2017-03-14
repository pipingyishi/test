package com.zx.frontend.promotion.goods.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zx.datamodels.goods.bean.GoodsProduct;
import com.zx.datamodels.goods.bean.ProductAttributes;
import com.zx.datamodels.store.entity.Product;
import com.zx.datamodels.store.entity.ProductAttribute;
import com.zx.datamodels.store.entity.ProductAvailability;
import com.zx.datamodels.store.entity.ProductDescription;
import com.zx.datamodels.store.vo.ProductAttributeVo;
import com.zx.datamodels.store.vo.ProductAttributeVo.ProductAttributeValue;
import com.zx.modules.store.service.ProductAvailabilityService;

public class GoodsUtil {
    public static GoodsProduct getProduct(Product product, GoodsProduct goodsProduct) {
        List<ProductAttributes> productAttributes = getProductAttribute(product);
        String picture = product.getPictures();// 藏品图片
        if (picture != null) {
            picture = picture.split(",")[0];
        } else {
            picture = "";
        }
        boolean isSellBuyFlag = product.getSaleBuyFlag();// 求购还是出售
        int transactionType = product.getTransactionType();// 自行交易还是担保交易（第二个页面）
        ProductDescription productDescription = product.getProductDesc();
        String name = productDescription.getName();// 藏品名
        goodsProduct.setSellBuyFlag(isSellBuyFlag);
        goodsProduct.setTransactionType(transactionType);
        goodsProduct.setPicture(picture);
        goodsProduct.setName(name);
        goodsProduct.setProductAttribute(productAttributes);
        return goodsProduct;
    }

    public static GoodsProduct getGoods(GoodsProduct goodsProduct, long productId,
            ProductAvailability productAvailability) {
        int quantity = productAvailability.getQuantity();// 数量
        int totalQuantity=productAvailability.getQuantityTotal();
        double productPrice = productAvailability.getProductPrice();// 出售价
        String remark = productAvailability.getRemark();// 备注
        Date dateAvailable=productAvailability.getDateAvailable();
        goodsProduct.setProductPrice(productPrice);
        goodsProduct.setQuantity(quantity);
        goodsProduct.setQuantityTotal(totalQuantity);
        goodsProduct.setRemark(remark);
        goodsProduct.setDateAvailable(dateAvailable);
        goodsProduct.setProductId(productId);
        return goodsProduct;
    }

    public static List<GoodsProduct> goodsProduct(List<Product> products,
            ProductAvailabilityService productAvailabilityService) {
        List<GoodsProduct> goodsProducts = new ArrayList<GoodsProduct>();
        for (int i = 0; i < products.size(); i++) {
            GoodsProduct goodsProduct = new GoodsProduct();
            Product product = products.get(i);
            goodsProduct = getProduct(product, goodsProduct);
            long productId = product.getProductId();
            ProductAvailability productAvailability = productAvailabilityService
                    .findByProductIdAndDefaultRegion(productId);
            goodsProduct = getGoods(goodsProduct, productId, productAvailability);
            goodsProducts.add(goodsProduct);
        }
        return goodsProducts;
    }

    public static List<ProductAttributes> getProductAttribute(Product product) {
        List<ProductAttribute> attributes=product.getProductAttributes();
        List<ProductAttributeVo> vos = ProductAttribute.toProductAttributeVo(attributes, true);
        product.setProductAttributes(null);
        product.setAttributeForDisp(vos);
        List<ProductAttributeVo> attributeForDisps = product.getAttributeForDisp();
        List<ProductAttributes> productAttributes = new ArrayList<ProductAttributes>();
        for (int k = 0; k < attributeForDisps.size(); k++) {
            ProductAttributes productAttribute = new ProductAttributes();
            ProductAttributeVo productAttributeVo=attributeForDisps.get(k);
            String attributeName=productAttributeVo.getAttributeName();
            productAttribute.setAttributeName(attributeName);
            List<ProductAttributeValue> productAttributeValues = productAttributeVo.getAttributeValues();
            List<String> attributeValue = new ArrayList<String>();
            ProductAttributeValue productAttributeValue=null;
            for (int n = 0; n < productAttributeValues.size(); n++) {
                productAttributeValue=productAttributeValues.get(n);
                attributeValue.add(productAttributeValue.getAttributeValue());
            }
            productAttribute.setAttributeValues(attributeValue);
            productAttributes.add(productAttribute);
        }
        return productAttributes;
    }
}
