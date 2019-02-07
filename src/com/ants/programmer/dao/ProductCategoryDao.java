package com.ants.programmer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ProductCategoryDao {

	// 根据名字选择父类的名称和子类的名称
	public static JSONObject selectChildName(String name) {
		ArrayList<String> Name = new ArrayList<String>();
		JSONObject classify = new JSONObject();
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select APC_NAME from ANTS_PRODUCT_CATEGORY where "
						+ "APC_PARENT_ID =(select APC_ID from ANTS_PRODUCT_CATEGORY where APC_NAME=?" + ")";
				statement = connection.prepareStatement(sql);
				statement.setString(1, name);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					Name.add(resultset.getString("APC_NAME"));
				}
				classify.put("childClassify", Name);

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return classify;
	}

	// 根据分类名字获取childrenID来获取商品信息
	public static ArrayList<String> selectClassName(String name) {
		ArrayList<String> Name = new ArrayList<String>();
		String parentName = "";
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select APC_NAME from ANTS_PRODUCT_CATEGORY where APC_ID=(select"
						+ " APC_PARENT_ID from ANTS_PRODUCT_CATEGORY where APC_NAME= ?" + ")";
				statement = connection.prepareStatement(sql);
				statement.setString(1, name);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					parentName = resultset.getString("APC_NAME");
					Name.add(parentName);
				}
				if (!parentName.equals(name)) {
					Name.add(name);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return Name;
	}

	// 根据父类名字来获取对应分类的ID
	public static int selectIDByName(String name) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		int id = 0;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select APC_ID from ANTS_PRODUCT_CATEGORY where APC_NAME=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, name);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					id = resultset.getInt("APC_ID");
				}
				return id;

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return 0;
	}

	// 通过Id查找分类名字
	public static String selectNameByID(int id) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		String Name = "";
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select APC_NAME from ANTS_PRODUCT_CATEGORY where APC_ID=?";
				statement = connection.prepareStatement(sql);
				statement.setInt(1, id);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					Name = resultset.getString("APC_NAME");
				}
				return Name;

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return Name;
	}
	
	//计算子类的热度
	public static void countHot(int id) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		int resultset = 0;
		String Name = "";
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "update ANTS_PRODUCT_CATEGORY set APC_COUNT=APC_COUNT+1 where APC_ID=?";
				statement = connection.prepareStatement(sql);
				statement.setInt(1, id);
				resultset = statement.executeUpdate();
				if(resultset>0) {
					System.out.println("更新成功,热度加一!");
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(null, statement, null);
			}
		}

	}
	
	public static ArrayList<Integer> selectApcIDByCount() {
		ArrayList<Integer> ApcId=new ArrayList<Integer>();
		
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ants_product_category where apc_count=(select max(apc_count) from ants_product_category)";
				statement = connection.prepareStatement(sql);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ApcId.add(resultset.getInt("APC_ID"));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return ApcId;
	}
	
	//计算商品一共有多少件
	public static int countProduct() {
		int Sum=0;
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select count(*) from ANTS_PRODUCT ";
				statement = connection.prepareStatement(sql);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					Sum=resultset.getInt(1);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}
		return Sum;
	}
	
	
	//最新闲置
	public static JSONObject selectProductByNew() {
		JSONObject product = new JSONObject();
		ArrayList<String> ID=new ArrayList<String>();
		ArrayList<String> Name=new ArrayList<String>();
		JSONArray Img=new JSONArray();
		int Sum=countProduct();
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ANTS_PRODUCT  limit ?,?";
				statement = connection.prepareStatement(sql);
				statement.setInt(1, Sum-4);
				statement.setInt(2, Sum);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("AP_ID"));
					Name.add(resultset.getString("AP_NAME"));
					String fileName=resultset.getString("AP_FILE_NAME");
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
				}
				product.put("newId", ID);
				product.put("newName", Name);
				product.put("newImage", Img);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}
	
	//根据子类获取商品信息
	public static JSONObject selectProductByChildId(int childid,int count) {
		JSONObject product = new JSONObject();
		ArrayList<String> ID=new ArrayList<String>();
		ArrayList<String> Name=new ArrayList<String>();
		JSONArray Img=new JSONArray();
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ANTS_PRODUCT where APC_CHILD_ID=? limit ?,?";
				statement = connection.prepareStatement(sql);
				statement.setInt(1, childid);
				statement.setInt(2, 0);
				statement.setInt(3, count);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("AP_ID"));
					Name.add(resultset.getString("AP_NAME"));
					String fileName=resultset.getString("AP_FILE_NAME");
					
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
				}
				product.put("goodsID", ID);
				product.put("goodsName", Name);
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
