package com.ants.programmer.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ants.programmer.bean.ChatBean;

import net.sf.json.JSONObject;

public class ChatDao {
	private static Connection conn; //连接对象
	private ResultSet rSet; //结果集
	private static PreparedStatement pstm; //预编译的语句对象
	
	// 插入离线聊天信息
	public static int offlineInsert(ChatBean user) throws SQLException {
		String sql = "insert into ants_offline values(?,?,?,?,?,?)";
		Object[] params = { user.getMobile(),user.getUserName(),user.getToMobile(),user.getMessage(),user.getTime(),user.getId() };
		return BaseDao.exectuIUD(sql, params);
	
	}
	
	
	
	//统计由多少条离线消息
	public static int count(String mobile)
	{
		int Sum = 0;
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select count(*) from ants_offline where ants_username =?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, mobile);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					Sum = resultset.getInt(1);
				}
			}catch (SQLException e) {
					e.printStackTrace();
				} finally {
					BaseDao.closeResource(resultset, statement, null);
				}
		}
		return Sum;
	}

	// 删除mobile与对方买家/卖家的的聊天信息
	public static int delete(String mobile, String to) {
		String sql = "delete from ANTS_OFFLINE where ANTS_MOBILE=? and ANTS_USERNAME=?";
		Object[] params = { mobile, to };
		return BaseDao.exectuIUD(sql, params);

	}

	// 获取指定手机用户的信息
	public static JSONObject getMessage(String mobile) {
		JSONObject shop = new JSONObject();
		ArrayList<String> MyMobile = new ArrayList<String>(); // 获取我的手机号码
		ArrayList<String> UserName = new ArrayList<String>(); // 获取手机对应的用户名称
		ArrayList<String> To = new ArrayList<String>(); // 获取卖家/买家的手机号码
		ArrayList<String> Message = new ArrayList<String>(); // 获取聊天信息
		ArrayList<String> Time = new ArrayList<String>(); // 获取聊天时间
		ArrayList<String> goodid = new ArrayList<String>();//获取对应商品id

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ANTS_OFFLINE where ANTS_username=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, mobile);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					MyMobile.add(resultset.getString("ANTS_MOBILE"));
					UserName.add(resultset.getString("ANTS_USERNAME"));
					To.add(resultset.getString("ANTS_TO"));
					Message.add(resultset.getString("ANTS_MESSAGE"));
					Time.add(resultset.getString("ANTS_TIME"));
					goodid.add(resultset.getString("ANTS_GOODID"));
				}
				shop.put("myMobile", MyMobile);
				shop.put("userName", UserName);
				shop.put("to", To);
				shop.put("message", Message);
				shop.put("time", Time);
				shop.put("goodid", goodid);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return shop;
	}
	

}
