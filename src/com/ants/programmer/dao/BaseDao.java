package com.ants.programmer.dao;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import java.sql.DriverManager;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class BaseDao {
	private static Connection connection = null; // 声明一个连接对象
	private static DataSource ds = new ComboPooledDataSource("czd"); // 创建数据源c3p0

	public static Connection getConnection() {
		if (connection == null) { // 判断对象是否为空
			try {

				connection = ds.getConnection(); // 获取一个连接对象connection
				System.out.println("连接成功！");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return connection; // 返回对象connection
	}

	// 将sql语句与对象封装执行
	public static int exectuIUD(String sql, Object[] params) {
		int count = 0;
		Connection connection = getConnection();
		java.sql.PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					statement.setObject(i + 1, params[i]);
				}
			}
			count = statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);
		}
		return count;
	}

	// 关闭数据库的连接
	public static void closeResource(ResultSet resultset, java.sql.PreparedStatement statement, Connection connection) {
		try {
			if (resultset != null) {
				resultset.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
