package com.ants.programmer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.googlecode.jsonplugin.annotations.JSON;

import net.sf.json.JSONObject;

public class UsersDao {

	// 判断用户是否注册过
	public static boolean isRegistered(String userid) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "select * from ants_users where AU_USER_ID=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, userid);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				if (resultset.getString("AU_USER_ID").equals(userid)) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(resultset, statement, null);
		}
		return false;
	}

	// 根据商品的id来选择对应的用户
	public static JSONObject selectUser(String id) {
		JSONObject product = new JSONObject();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ANTS_USERS where AU_USER_ID="
						+ "(select AP_MOBILE from ANTS_PRODUCT where AP_ID=?)";
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					product.put("userName", resultset.getString("AU_USER_NAME"));
					product.put("userImg", resultset.getString("AU_PHOTO"));
					product.put("userMobile", resultset.getString("AU_MOBILE"));
					product.put("userWechat", resultset.getString("WECHAT"));
					product.put("userQQ", resultset.getString("QQ"));
					product.put("userAddress", resultset.getString("AU_ADDRESS"));
					product.put("wcHide", resultset.getString("WECHATHIDDEN"));
					product.put("qqHide", resultset.getString("QQHIDDEN"));
					product.put("userid", resultset.getString("AU_USER_ID"));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}

	// 根据手机号码来获取对应用户的个人信息
	public static JSONObject selectMyself(String mobile) {
		JSONObject product = new JSONObject();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ANTS_USERS where AU_USER_ID=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, mobile);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					product.put("userName", resultset.getString("AU_USER_NAME"));
					product.put("userImg", resultset.getString("AU_PHOTO"));
					product.put("userMobile", resultset.getString("AU_MOBILE"));
					product.put("wcHide", resultset.getString("WECHATHIDDEN"));
					product.put("qqHide", resultset.getString("QQHIDDEN"));
					product.put("address", resultset.getString("AU_ADDRESS"));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}

	// 插入数据
	public static void insert(String userid, String password) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "insert into ANTS_USERS(AU_USER_ID, AU_USER_NAME,AU_PASSWORD,"
					+ "AU_MOBILE,AU_PHOTO,WECHAT,QQ,WECHATHIDDEN,QQHIDDEN,AU_ADDRESS) values(?,?,?,?,?,?,?,?,?,?)";
			statement = connection.prepareStatement(sql);
			statement.setString(1, userid);
			statement.setString(2, userid);
			statement.setString(3, password);
			statement.setString(4, userid);
			statement.setString(5, "images\\1.jpg");
			statement.setString(6, "这个人很懒，什么都没有留下！");
			statement.setString(7, "这个人很懒，什么都没有留下！");
			statement.setString(8, "false");
			statement.setString(9, "false");
			statement.setString(10, "这个人很懒，什么都没有留下！");

			statement.executeUpdate();
			System.out.println("注册成功！");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);

		}
	}
	
	
	// 插入学号对应的数据
	public static void insertstudent(String userid, String password) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "insert into ANTS_USERS(AU_USER_ID, AU_USER_NAME,AU_PASSWORD,"
					+ "AU_MOBILE,AU_PHOTO,WECHAT,QQ,WECHATHIDDEN,QQHIDDEN,AU_ADDRESS) values(?,?,?,?,?,?,?,?,?,?)";
			statement = connection.prepareStatement(sql);
			statement.setString(1, userid);
			statement.setString(2, userid);
			statement.setString(3, password);
			statement.setString(4, "");
			statement.setString(5, "img\\1.jpg");
			statement.setString(6, "这个人很懒，什么都没有留下！");
			statement.setString(7, "这个人很懒，什么都没有留下！");
			statement.setString(8, "false");
			statement.setString(9, "false");
			statement.setString(10, "这个人很懒，什么都没有留下！");

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);

		}
	}


	// 根据用户的手机号码来获取对应用户的信息
	public static JSONObject userMessage(String userid) {
		JSONObject user = new JSONObject();
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "select * from ants_users where AU_USER_ID=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, userid);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				user.put("name", resultset.getString("AU_USER_NAME"));
				user.put("mobile", resultset.getString("AU_MOBILE"));
				user.put("wechat", resultset.getString("WECHAT"));
				user.put("QQ", resultset.getString("QQ"));
				user.put("address", resultset.getString("AU_ADDRESS"));
				user.put("img", resultset.getString("AU_PHOTO"));
				user.put("wcHide", resultset.getString("WECHATHIDDEN"));
				user.put("qqHide", resultset.getString("QQHIDDEN"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(resultset, statement, null);
		}
		return user;
	}

	// 更新用户的个人信息
	public static void update(String userid, String name, String wechat, String qq, String address, String wechathidden,
			String qqhidden) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "update ANTS_USERS set AU_USER_NAME=?, WECHAT=?, QQ=?,AU_ADDRESS=?,WECHATHIDDEN=?,QQHIDDEN=? where AU_USER_ID=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, name);
			statement.setString(2, wechat);
			statement.setString(3, qq);
			statement.setString(4, address);
			statement.setString(5, wechathidden);
			statement.setString(6, qqhidden);
			statement.setString(7, userid);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);

		}
	}

	// 更改个人用户的密码
	public static void changePassword(String userid, String password) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "update ANTS_USERS set AU_PASSWORD=? where AU_USER_ID=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, password);
			statement.setString(2, userid);
			statement.executeUpdate();
			System.out.println("修改成功！");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);

		}
	}

	// 更改个人用户的头像
	public static void changePhoto(String userid, String photo) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "update ANTS_USERS set AU_PHOTO=? where AU_USER_ID=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, photo);
			statement.setString(2, userid);
			statement.executeUpdate();
			System.out.println("保存成功！");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);

		}
	}

	public static boolean Login(String userid, String password) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "select  * from ANTS_USERS where  AU_USER_ID=? and  AU_PASSWORD=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, userid);
			statement.setString(2, password);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				if (resultset.getString("AU_USER_ID").equals(userid)
						&& resultset.getString("AU_PASSWORD").equals(password)) {
					return true;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);

		}
		return false;
	}
	
}
