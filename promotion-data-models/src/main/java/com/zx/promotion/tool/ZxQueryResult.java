package com.zx.promotion.tool;
import java.io.Serializable;
import java.util.List;

import com.zx.promotion.datamodel.TradeTable;

/**
 * 共通数据返回model
 * 
 */
public class ZxQueryResult implements Serializable{
	private static final long serialVersionUID = 6654929295670413737L;
	private int totalPages;
	private List<TradeTable> pageList;
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public List<TradeTable> getPageList() {
		return pageList;
	}
	public void setPageList(List<TradeTable> pageList) {
		this.pageList = pageList;
	}
	
}
