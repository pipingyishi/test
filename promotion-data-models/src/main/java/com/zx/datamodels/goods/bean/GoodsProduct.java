package com.zx.datamodels.goods.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class GoodsProduct {
	private boolean sellBuyFlag;
	private int transactionType;
	private String name;
	private int quantity;
	private int quantityTotal;
	private double productPrice;
	private String remark;
	private long productId;
	private String picture;
	private List<ProductAttributes> productAttribute;
	private Date dateAvailable;

	public boolean isSellBuyFlag() {
		return sellBuyFlag;
	}

	public void setSellBuyFlag(boolean sellBuyFlag) {
		this.sellBuyFlag = sellBuyFlag;
	}

	public int getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public List<ProductAttributes> getProductAttribute() {
		return productAttribute;
	}

	public void setProductAttribute(List<ProductAttributes> productAttribute) {
		this.productAttribute = productAttribute;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getQuantityTotal() {
		return quantityTotal;
	}

	public void setQuantityTotal(int quantityTotal) {
		this.quantityTotal = quantityTotal;
	}

	public Date getDateAvailable() {
		return dateAvailable;
	}

	public void setDateAvailable(Date dateAvailable) {
		this.dateAvailable = dateAvailable;
	}
}
