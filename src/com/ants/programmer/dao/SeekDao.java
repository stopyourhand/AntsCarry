package com.ants.programmer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SeekDao {

	// 获取卖家的个人信息，以及对应商品的发布类型（发布闲置，发布寻求）
	public static JSONObject selectSellByUser(String mobile, int status) {
		JSONObject sellProduct = new JSONObject();
		ArrayList<String> ID = new ArrayList<String>();
		ArrayList<String> Name = new ArrayList<String>();
		ArrayList<Double> Price = new ArrayList<Double>();
		ArrayList<String> Introduce = new ArrayList<String>();
		ArrayList<String> Ways = new ArrayList<String>();
		ArrayList<String> Bargin = new ArrayList<String>();
		JSONArray fileName=new JSONArray();
		ArrayList<String> Address = new ArrayList<String>();
		ArrayList<Integer> ParentID = new ArrayList<Integer>();
		ArrayList<Integer> ChildID = new ArrayList<Integer>();
		ArrayList<String> ParentName = new ArrayList<String>();
		ArrayList<String> ChildName = new ArrayList<String>();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ANTS_PRODUCT where AP_MOBILE=? and AP_STATUS=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, mobile);
				statement.setInt(2, status);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("AP_ID"));
					Name.add(resultset.getString("AP_NAME"));
					Price.add(resultset.getDouble("AP_PRICE"));
					Introduce.add(resultset.getString("AP_INTRODUCE"));
					Ways.add(resultset.getString("AP_WAYS"));
					Bargin.add(resultset.getString("AP_BARGIN"));
					ParentID.add(resultset.getInt("APC_ID"));
					ChildID.add(resultset.getInt("APC_CHILD_ID"));
					String filename=resultset.getString("AP_FILE_NAME");
					String fn[]=filename.split(";");
					for(String FileName:fn) {
						img.add(FileName);
					}
					fileName.add(img);
				}
				for (String id : ID) {
					JSONObject seller = UsersDao.selectUser(id);
					if(seller.getString("userAddress")!=null) {
						Address.add(seller.getString("userAddress"));
					}
					
				}
				for (int parentid : ParentID) {
					ParentName.add(ProductCategoryDao.selectNameByID(parentid));
				}
				for (int childid : ChildID) {
					ChildName.add(ProductCategoryDao.selectNameByID(childid));
				}
				sellProduct.put("address", Address);
				sellProduct.put("parentClassify", ParentName);
				sellProduct.put("childClassify", ChildName);
				sellProduct.put("goodsID", ID);
				sellProduct.put("goodsName", Name);
				sellProduct.put("goodsImg", fileName);
				sellProduct.put("goodsPrice", Price);
				sellProduct.put("goodsIntroduce", Introduce);
				sellProduct.put("goodsWays", Ways);
				sellProduct.put("goodsBargin", Bargin);

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(null, statement, null);
			}
		}
		return sellProduct;
	}

	// 根据用户id来判断商品
	public static JSONObject selectByUserID(String mobile) {
		JSONObject product = new JSONObject();
		ArrayList<String> Name = new ArrayList<String>();
		ArrayList<Double> Price = new ArrayList<Double>();
		ArrayList<String> Introduce = new ArrayList<String>();
		ArrayList<String> Ways = new ArrayList<String>();
		ArrayList<String> Bargin = new ArrayList<String>();
		JSONArray Img=new JSONArray();
		ArrayList<String> ID = new ArrayList<String>();
		
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ANTS_PRODUCT where AP_MOBILE=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, mobile);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("AP_ID"));
					Name.add(resultset.getString("AP_NAME"));
					Price.add(resultset.getDouble("AP_PRICE"));
					Introduce.add(resultset.getString("AP_INTRODUCE"));
					Ways.add(resultset.getString("AP_WAYS"));
					Bargin.add(resultset.getString("AP_BARGIN"));
					String fileName=resultset.getString("AP_FILE_NAME");
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
				}

				product.put("goodsID", ID);
				product.put("goodsName", Name);
				product.put("goodsPrice", Price);
				product.put("goodsIntroduce", Introduce);
				product.put("goodsWays", Ways);
				product.put("goodsBargin", Bargin);
				product.put("goodsImg", Img);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}
		return product;
	}

	
}
