package com.ants.programmer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AnnouncementDao {
	
	//将公告数据库的数据取出
	public static JSONObject selectAnnouncement(String type) {
		JSONObject product=new JSONObject();
		ArrayList<Integer> Id=new ArrayList<Integer>();
		ArrayList<String> Title=new ArrayList<String>();
		ArrayList<String> Content=new ArrayList<String>();
		ArrayList<String> Time=new ArrayList<String>();
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql="";
				if(type.equals("limit")) {
					sql = "select * from ants_announcement order by id desc limit 0,5 ";
				}else {
					sql = "select * from ants_announcement ";
				}
				
				statement=connection.prepareStatement(sql);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					Id.add(resultset.getInt("id"));
					Title.add(resultset.getString("title"));
					Content.add(resultset.getString("content"));
					Time.add(resultset.getString("time"));
				}
				product.put("id", Id);
				product.put("title", Title);
				product.put("content", Content);
				product.put("time", Time);

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}
		return product;
	}
	
	//根据公告id获取公告内容
	public static JSONObject AnnouncementById(String id) {
		JSONObject product=new JSONObject();
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql="select * from ants_announcement where id=?";
				statement=connection.prepareStatement(sql);
				statement.setString(1, id);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					product.put("id", resultset.getInt("id"));
					product.put("title", resultset.getString("title"));
					product.put("content", resultset.getString("content"));
					product.put("time", resultset.getString("time"));
				}
				

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}
		return product;
	}
	
}
