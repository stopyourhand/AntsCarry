package com.ants.programmer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import com.ants.programmer.bean.ShopBean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ShopDao {
	
	//获取购物车中的所有订单号
	public static HashSet<String> selectShopID(){
		HashSet<String> shopID=new HashSet<String>();
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "select AP_ID from ants_shop ";
			statement = connection.prepareStatement(sql);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				shopID.add(resultset.getString("AP_ID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(resultset, statement, null);
		}
		return shopID;
	}
	
	//获取购物车中的所有买家信息
	public static HashSet<String> selectShopMobile(){
		HashSet<String> shopMobile=new HashSet<String>();
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "select AS_SELLER_MOBILE from ants_shop ";
			statement = connection.prepareStatement(sql);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				shopMobile.add(resultset.getString("AS_SELLER_MOBILE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(resultset, statement, null);
		}
		return shopMobile;
	}
	
	//判断买家是否存在此订单号
	public static boolean judgeShopID(String shopid,String mobile){
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "select AP_ID from ants_shop where AS_SELLER_MOBILE=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, mobile);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				if(resultset.getString("AP_ID").equals(shopid)) {
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
	
		
	
		
	// 判断添加到购物车
	public static boolean isExist(String id) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "select * from ants_shop where AP_ID=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				if (resultset.getString("AP_ID") .equals(id) ) {
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

	// 插入数据
	public static int insert(ShopBean shop) {
		String sql = "insert into ANTS_SHOP values(?,?,?)";
		Object[] params = { shop.getId(), shop.getSellerMobile(), shop.getTime() };
		return BaseDao.exectuIUD(sql, params);
	}

	// 删除某一行数据
	public static int delete(String id) {
		String sql = "delete from ANTS_SHOP where AP_ID=?";
		Object[] params = { id };
		return BaseDao.exectuIUD(sql, params);

	}

	// 根据商品的id来获取对应商品的信息
	public static JSONObject selectShopMessage(String id) {
		JSONObject shop = new JSONObject();
		JSONArray Img=new JSONArray();
		
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from  ANTS_PRODUCT where AP_ID=(select AP_ID from " + "ANTS_SHOP where AP_ID=?)";
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					shop.put("goodsID", resultset.getString("AP_ID"));
					shop.put("goodsMobile", resultset.getString("AP_MOBILE"));
					shop.put("goodsName", resultset.getString("AP_NAME"));
					shop.put("goodsPrice", resultset.getDouble("AP_PRICE"));
					shop.put("goodsIntroduce", resultset.getString("AP_INTRODUCE"));
					shop.put("goodsWays", resultset.getString("AP_WAYS"));
					shop.put("goodsBargin", resultset.getString("AP_BARGIN"));
					String fileName=resultset.getString("AP_FILE_NAME");
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
					shop.put("goodsImg", Img);
				}
				JSONObject user = UsersDao.selectUser(id);
				shop.put("userName", user.getString("userName"));
				shop.put("userImg", user.getString("userImg"));
				shop.put("userMobile", user.getString("userMobile"));
				shop.put("userWechat", user.getString("userWechat"));
				shop.put("userAddress", user.getString("userAddress"));

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return shop;
	}

	// 根据卖家/买家的手机号码来获取购物车的商品信息
	public static JSONObject selectShop(String mobile) {
		JSONObject shop = new JSONObject();
		ArrayList<String> ID = new ArrayList<String>();
		ArrayList<String> Name = new ArrayList<String>();
		JSONArray Img=new JSONArray();
		ArrayList<String> Introduce = new ArrayList<String>();
		ArrayList<Double> Price = new ArrayList<Double>();
		ArrayList<String> Address = new ArrayList<String>();
		
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ants_product where AP_ID in"
						+ "(select AP_ID from ants_shop where AS_SELLER_MOBILE=?" + ")";
				statement = connection.prepareStatement(sql);
				statement.setString(1, mobile);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("AP_ID"));
					Name.add(resultset.getString("AP_NAME"));
					String fileName=resultset.getString("AP_FILE_NAME");
					Introduce.add(resultset.getString("AP_INTRODUCE"));
					Price.add(resultset.getDouble("AP_PRICE"));
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
				}
				for (String id : ID) {
					JSONObject user = UsersDao.selectUser(id);
					Address.add(user.getString("userAddress"));
				}
				shop.put("goodsID", ID);
				shop.put("goodsName", Name);
				shop.put("goodsImg", Img);
				shop.put("goodsIntroduce", Introduce);
				shop.put("goodsPrice", Price);
				shop.put("goodsAddress", Address);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return shop;
	}
	
	
}
