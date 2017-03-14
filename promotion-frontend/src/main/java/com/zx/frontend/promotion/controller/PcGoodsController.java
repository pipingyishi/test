package com.zx.frontend.promotion.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zx.datamodels.common.query.ZxQueryResult;
import com.zx.datamodels.common.request.PagingRequest;
import com.zx.datamodels.goods.bean.GoodsProduct;
import com.zx.datamodels.goods.bean.GoodsSubjection;
import com.zx.datamodels.goods.bean.entity.GoodsCategory;
import com.zx.datamodels.goods.bean.entity.GoodsOptionItem;
import com.zx.datamodels.store.entity.Product;
import com.zx.datamodels.store.entity.ProductMeta;
import com.zx.datamodels.user.bean.entity.User;
import com.zx.frontend.promotion.goods.util.GoodsUtil;
import com.zx.frontend.promotion.goods.util.PcGoodsUtil;
import com.zx.module.search.service.ProductMetaSearchService;
import com.zx.modules.goods.service.GoodsCategoryOptionService;
import com.zx.modules.goods.service.GoodsCategoryService;
import com.zx.modules.store.service.ProductAvailabilityService;
import com.zx.modules.store.service.ProductService;
import com.zx.modules.user.service.AccountService;

@Controller
@RequestMapping(value = "goods")
public class PcGoodsController {
	private static final String GOODS_LIST = "/pages/pc_goods/goods-list::part";

	private static final String GOODS_SEARCH = "/pages/pc_goods/goods-search::part";

	private static final String PRODUCT_SEARCH = "/pages/pc_goods/goods-list::product";

	private static final int GOODS_TYPE = 2;

	private static final int SEARCH_GOODS_COUNT = 100;

	@Autowired
	private GoodsCategoryService goodsCategoryService;

	@Autowired
	private GoodsCategoryOptionService goodsCategoryOptionService;

	@Autowired
	private ProductService productService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private ProductAvailabilityService productAvailabilityService;

	@Autowired
	private ProductMetaSearchService productMetaSearchService;

	@RequestMapping(value = "")
	public String goodsList(ModelMap model) {
		List<GoodsCategory> goodsCategories = goodsCategoryService.findCategoryByModule(1);// 所有类别的集合，邮票，钱币，邮资封票，其他
		List<List<GoodsOptionItem>> optionsForCategorys = new ArrayList<>();
		for (GoodsCategory goodsCategory : goodsCategories) {
			List<GoodsOptionItem> optionsForCategory = goodsCategoryOptionService
					.getOptionsByCategory(goodsCategory.getCateId());
			optionsForCategorys.add(optionsForCategory);
		}
		model.put("goodsCategories", goodsCategories);
		model.put("optionsForCategorys", optionsForCategorys);
		return "pages/pc_goods/goods";
	}

	@RequestMapping(value = "/option")
	public String pull(@RequestParam("dataItemId") Long dataItemId, @RequestParam("dataOptionId") Long dataOptionId,
			@RequestParam("dataOptionValueId") Long dataOptionValueId, @RequestParam("type") String type,
			@RequestParam("page") Integer page, ModelMap model) {
		GoodsSubjection goodsSubjection = PcGoodsUtil.reformItemAndOption(dataItemId, dataOptionId, dataOptionValueId);
		List<Product> products = productService.findProductsWithDescByMerchant(null, GOODS_TYPE, null, null,
				goodsSubjection.getItemId(), page, PagingRequest.PAGE_SIZE, null, goodsSubjection.getOptions());
		List<GoodsProduct> goodsProducts = GoodsUtil.goodsProduct(products, productAvailabilityService);
		Long itemId = goodsSubjection.getItemId();
		if ("category".equals(type)) {
			if (itemId == null) {
				List<GoodsCategory> goodsCategories = goodsCategoryService.findCategoryByModule(1);// 所有类别的集合，邮票，钱币，邮资封票，其他
				List<List<GoodsOptionItem>> optionsForCategorys = new ArrayList<>();
				for (GoodsCategory goodsCategory : goodsCategories) {
					List<GoodsOptionItem> optionsForCategory = goodsCategoryOptionService
							.getOptionsByCategory(goodsCategory.getCateId());
					optionsForCategorys.add(optionsForCategory);
				}
				model.put("optionsForCategorys", optionsForCategorys);
				model.put("type", type + "All");
			} else {
				List<GoodsOptionItem> optionsForCategory = goodsCategoryOptionService
						.getOptionsByCategory(itemId.intValue());
				model.put("optionsForCategory", optionsForCategory);
				model.put("type", type);
			}
		}
		List<Long> userIds = new ArrayList<>();
		for (int j = 0, size = products.size(); j < size; j++) {
			userIds.add(products.get(j).getMerchant().getUserId());
		}
		Map<Long, User> userMaps = accountService.findUserByIds(userIds);
		List<User> users = new ArrayList<User>();
		for (int k = 0, size = userIds.size(); k < size; k++) {
			for (Map.Entry<Long, User> entry : userMaps.entrySet()) {
				if (entry.getKey().equals(userIds.get(k))) {
					users.add(entry.getValue());
					break;
				}
			}
		}
		int count = productService.countProductsWithDescByMerchant(null, 2, null, null, itemId, null,
				goodsSubjection.getOptions());
		int totalPage = count % PagingRequest.PAGE_SIZE == 0 ? count / PagingRequest.PAGE_SIZE
				: count / PagingRequest.PAGE_SIZE + 1;
		if ("section".equals(type)) {
			model.put("type", type);
		}
		model.put("goodsProducts", goodsProducts);
		model.put("users", users);
		model.put("totalPage", totalPage);
		return GOODS_LIST;
	}

	@RequestMapping(value = "/goodsSearch")
	public String goodsSearch(@RequestParam("searchKey") String searchKey, ModelMap model) {
		ZxQueryResult<ProductMeta> productMetaResult = productMetaSearchService.searchProductMetas(0,
				SEARCH_GOODS_COUNT, searchKey, null, 0);
		if ((productMetaResult.getResults() != null) && (!productMetaResult.getResults().isEmpty())) {
			List<GoodsCategory> categories = goodsCategoryService.findRootCategoriesByModule(1);
			Map<Integer, String> categoriesMap = new HashMap<Integer, String>();
			if (!categories.isEmpty()) {
				for (GoodsCategory cate : categories) {
					categoriesMap.put(cate.getCateId(), cate.getCateName());
				}
			}
			for (ProductMeta meta : productMetaResult.getResults()) {
				if (meta == null) {
					continue;
				}
				if (meta.getCateLevel1() == null) {
					continue;
				}
				String name = categoriesMap.get(meta.getCateLevel1().intValue());
				meta.setCateLevel1Name(name);
			}
		}
		model.put("productMetaResult", productMetaResult);
		return GOODS_SEARCH;
	}

	@RequestMapping(value = "/productSearch")
	public String productSearch(@RequestParam("productId") Long productId, ModelMap model) {
		List<Product> products = productService.findProductByMeta(productId, GOODS_TYPE, null, null, null, 0,
				PagingRequest.PAGE_SIZE);
		List<GoodsProduct> goodsProducts = GoodsUtil.goodsProduct(products, productAvailabilityService);
		List<Long> userIds = new ArrayList<>();
		for (int j = 0, size = products.size(); j < size; j++) {
			userIds.add(products.get(j).getMerchant().getUserId());
		}
		Map<Long, User> userMaps = accountService.findUserByIds(userIds);
		List<User> users = new ArrayList<User>();
		for (int k = 0, size = userIds.size(); k < size; k++) {
			for (Map.Entry<Long, User> entry : userMaps.entrySet()) {
				if (entry.getKey().equals(userIds.get(k))) {
					users.add(entry.getValue());
					break;
				}
			}
		}
		model.put("goodsProducts", goodsProducts);
		model.put("users", users);
		return PRODUCT_SEARCH;
	}
}
