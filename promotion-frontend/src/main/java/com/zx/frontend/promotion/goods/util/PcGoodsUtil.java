package com.zx.frontend.promotion.goods.util;

import java.util.ArrayList;
import java.util.List;

import com.zx.datamodels.goods.bean.GoodsSubjection;
import com.zx.datamodels.store.entity.AttributeOption;

public class PcGoodsUtil {
	public static GoodsSubjection reformItemAndOption(Long dataItemId, Long dataOptionId, Long dataOptionValueId) {
		GoodsSubjection goodsSubjection = new GoodsSubjection();
		List<AttributeOption> options = new ArrayList<AttributeOption>();
		if (dataItemId == 0L) {
			dataItemId = null;
		}
		if (dataOptionId == 0L) {
			options = null;
		} else {
			AttributeOption attributeOption = new AttributeOption();
			attributeOption.setOptionId(dataOptionId);
			attributeOption.setOptionValueId(dataOptionValueId);
			options.add(attributeOption);
		}
		goodsSubjection.setItemId(dataItemId);
		goodsSubjection.setOptions(options);
		return goodsSubjection;
	}
}
