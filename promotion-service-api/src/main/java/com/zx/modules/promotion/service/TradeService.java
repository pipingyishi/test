package com.zx.modules.promotion.service;

import java.util.List;

import com.zx.promotion.datamodel.TestTable;
import com.zx.promotion.datamodel.TradeTable;
import com.zx.promotion.tool.ZxQueryResult;

//import com.zx.promotion.datamodel.TestTable;

public interface TradeService {
	
//	/**
//	 * 查找所有用户信息
//	 * 
//	 *   
//	 */
//	public List<TestTable> findAllData();
	
	
	
	
	/**
	 * 提交大宗出售
	 * 
	 * @param trade
	 *            提交的交易信息
	 * @return 保存是否成功
	 */
	public int addTradeData(TradeTable trade);

	
	/**
	 * 获取大宗出售列表
	 * 
	 * @param tradeType
	 *            交易类型
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            页大小
	 * @return 交易详细列表
	 */
	public List<TradeTable> selectTradeData(String tradeType, int pageNo, int pageSize);

	
	/**
	 *  查询交易类型的数量
	 * @param tradeType
	 *  交易类型
	 * @return
	 */
	
	/**
	 * 保存修改信息
	 * */
	public boolean  saveRecord(List<TradeTable> saveRecord); 


	/**
	 * 后台页面查询
	 */
	public ZxQueryResult backstagePage(String tradeType,String tradeVarieties, int pageNo, int pageSize);
	
	/**
	 * 删除被选中的记录
	 */
	public boolean deleteRecord(List<Integer> idList);
}
