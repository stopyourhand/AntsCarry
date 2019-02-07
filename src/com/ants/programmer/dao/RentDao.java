package com.ants.programmer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ants.programmer.bean.RentBean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RentDao {
	// 插入数据
	public static int insert(RentBean rent) {
		String sql = "insert into ANTS_RENT values(?,?,?)";
		Object[] params = { rent.getId(),rent.getStock(),rent.getStatus() };
		return BaseDao.exectuIUD(sql, params);
	}
	
	// 删除某一行数据
	public static int delete(String id) {
		String sql = "delete from ANTS_RENT where AR_ID=?";
		Object[] params = { id };
		return BaseDao.exectuIUD(sql, params);

	}
	
	public static JSONObject selectStockByShopId(String id) {
		JSONObject product = new JSONObject();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select AR_STOCK from ANTS_RENT where AR_ID=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					product.put("goodsStock", resultset.getString("AR_STOCK"));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}
	
	
	//获取对应卖家手机号码的出租的商品
	public static JSONObject selectSellByUserRent(String mobile, int status) {
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
		ArrayList<String> Stock=new ArrayList<String>();

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
					if(seller.size()>0) {//seller.getString("userAddress")!=null
						Address.add(seller.getString("userAddress"));
					}
					///
					JSONObject stock = selectStockByShopId(id);
					if(stock.size()>0) {
						Stock.add(stock.getString("goodsStock"));
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
				sellProduct.put("Stock", Stock);

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(null, statement, null);
			}
		}
		return sellProduct;
	}
	
}
