package com.zx.datamodels.goods.bean;

import java.io.Serializable;
import java.util.List;

import com.zx.datamodels.goods.bean.entity.GoodsOptionItem;
import com.zx.datamodels.store.entity.AttributeOption;

public class GoodsSubjection{
	private Long itemId;

	private List<AttributeOption> options;

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public List<AttributeOption> getOptions() {
		return options;
	}

	public void setOptions(List<AttributeOption> options) {
		this.options = options;
	}
}
