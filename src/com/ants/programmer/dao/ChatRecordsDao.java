package com.ants.programmer.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ants.programmer.bean.ChatRecordBean;

/**
 * 聊天记录数据操作
 */
public class ChatRecordsDao {

	private static Connection conn; //连接对象
	private ResultSet rSet; //结果集
	private PreparedStatement pstm; //预编译的语句对象
	
	public ChatRecordsDao(){
		if(conn == null)
			conn = getConn();
	}
	
	/**
	 * 获取连接对象
	 * @return 连接对象
	 */
	private Connection getConn() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ants", "root", "980606");
			System.out.println("数据库连接成功");
		} catch (Exception e) {
			System.out.println("失败");
		}
		return conn;
	}
	
	/**
	 * 查询指定用户的聊天记录
	 * @param sender 消息发送者
	 * @param receiver 消息接收者
	 * @return 聊天记录列表
	 * @throws SQLException
	 */
	public List<ChatRecordBean> queryChatRecords(String user) throws SQLException{
		
		String sql = "select * from chat_record where sender=? or receiver=? order by time limit 0,50";
		pstm = conn.prepareStatement(sql);
		pstm.setString(1, user);
		pstm.setString(2, user);
		rSet = pstm.executeQuery();
		List<ChatRecordBean> records = new ArrayList<>();
		while (rSet.next()) {
			ChatRecordBean record = new ChatRecordBean();
			record.setSender(rSet.getString("sender"));
			record.setReceiver(rSet.getString("receiver"));
			record.setMessage(rSet.getString("message"));
			record.setTime(rSet.getTimestamp("time"));
			records.add(record);
		}
		return records;
	}
	
	/**
	 * 保存聊天记录
	 * @param record 聊天记录
	 * @return true:成功 false:失败
	 * @throws SQLException
	 */
	public boolean insertChatRecord(ChatRecordBean record) throws SQLException{
		String sql = "insert into chat_record (sender,receiver,message,time) values (?,?,?,?)";
		pstm = conn.prepareStatement(sql);
		pstm.setString(1, record.getSender());
		pstm.setString(2, record.getReceiver());
		pstm.setString(3, record.getMessage());
		pstm.setObject(4, record.getTime());
		return pstm.execute();
	}
	
}
