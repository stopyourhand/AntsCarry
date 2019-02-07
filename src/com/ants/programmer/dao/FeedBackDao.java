package com.ants.programmer.dao;



import com.ants.programmer.bean.FeedBackBean;

public class FeedBackDao {
	// 插入数据
	public static int insert(FeedBackBean feedback) {
		String sql = "insert into ANTS_FEEDBACK values(null,?,?,?,?,?)";
		Object[] params = {feedback.getSatisfaction(),feedback.getText(),feedback.getTime(),feedback.getMobile(),feedback.getUserName() };
		return BaseDao.exectuIUD(sql, params);
	}
	
}
