package com.ants.programmer.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ants.programmer.bean.ChatBean;
import com.ants.programmer.bean.ChatRecordBean;
import com.ants.programmer.dao.ChatDao;
import com.ants.programmer.dao.ChatRecordsDao;
import com.ants.programmer.dao.ProductDao;
import com.ants.programmer.dao.UsersDao;
import com.ants.programmer.util.SensitiveWordsFilterUtils;

@ServerEndpoint("/webchat/{nickName}")
public class WebChatServer{
	
	private static Map<String, WebChatServer> users = new HashMap<>(); //用户集合
	private Session session; //客户端标识
	private String currentUser; //当前用户
	private static int onlineCount; //在线人数
	private List<ChatRecordBean> records; //聊天记录集合
	private List<ChatRecordBean> result; //从数据库中查询出来的聊天记录
	private ChatRecordsDao chatRecordsDao;//新建的一个消息记录，历史聊天记录
	
	private static final String TYPE_MESSAGE = "message"; //消息
	private static final String TYPE_RECORD = "record"; //聊天记录
	
	
	public WebChatServer() {
		chatRecordsDao = new ChatRecordsDao();
		records = new ArrayList<>();
	}
	
	@OnOpen
	public void onOpen(Session session,@PathParam(value = "nickName")String nickName){
		
		
		this.session = session;
		this.currentUser = nickName;
		//查询当前用户的聊天记录
		try {
			result = chatRecordsDao.queryChatRecords(currentUser);
			System.out.println("消息记录为:"+currentUser);
		} catch (SQLException e) {
			result = null;
		}
		//将当前用户添加到用户集合中
		users.put(currentUser, this);
		addCount();
		//broadcast("system",MessageFormat.format("{0} 上线了！", currentUser), "");//给每个用户发送上线的消息
		sendMsg(getJsonMsg("", "", TYPE_RECORD,""));
	}
	
	@OnError
	public void onError(Throwable throwable){//退出报错
		System.out.println(throwable.toString());
		saveRecords();
	}

	//将消息转发到各个客户端
	@OnMessage
	public void onMessage(String msg){

		JSONObject jsonObj = new JSONObject(msg);
		String sender = (String)jsonObj.get("from");
		String receiver = (String) jsonObj.get("to");
		//String color = (String)jsonObj.get("color");
		String message = (String) jsonObj.get("content");
		String name = (String) jsonObj.get("name");//这个用户名还没传
		String id = (String) jsonObj.get("id");
		//message = "<font color=\""+color+"\">"+message+"</font>";
		
		//将当前的一条消息保存到消息集合中
		ChatRecordBean record = new ChatRecordBean();
		record.setReceiver(receiver);
		record.setSender(sender);
		record.setMessage(message);
		record.setTime(new Date());
		records.add(record);
		
		if (!"所有人".equals(receiver)) { //私聊 
			
			System.out.println("在线用户列表为:"+users);
			if(users.containsKey(receiver+"??"))
			{
				users.get(receiver).sendMsg(getJsonMsg(currentUser, message, TYPE_MESSAGE,id));
				this.sendMsg(getJsonMsg(currentUser," "+message, TYPE_MESSAGE,id)); //给自己发消息
			}
			else if (users.containsKey(receiver))
			{
				System.out.println("接收者是:"+receiver);
				users.get(receiver).sendMsg(getJsonMsg(currentUser, message, TYPE_MESSAGE,id));
				this.sendMsg(getJsonMsg(currentUser, " "+message, TYPE_MESSAGE,id)); //发送给主界面
			
				//将列表存入离线的数据库中
				ChatBean chat = new ChatBean(sender, name, receiver, message, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),id);
				try {
					ChatDao.offlineInsert(chat);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("到这里报错了!!!!");
					e.printStackTrace();
				}
			
			}
			else//离线用户
			{
				
				System.out.println(" 接收者是:"+receiver+" 发送者是:"+sender+" 消息是: "+message);
				this.sendMsg(getJsonMsg(currentUser, "(离线消息)"+" "+message, TYPE_MESSAGE,id)); //发送给主界面
				//将对应消息存入数据库
				ChatBean chat = new ChatBean(sender, name, receiver, message, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),id);
				try {
					ChatDao.offlineInsert(chat);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("到这里报错了!!!!");
					e.printStackTrace();
				}
				
			}
			return;
		}
		//如果不是私聊消广播给所有人
		//broadcast(sender, message,"");
	}

	@OnClose
	public void onClose(){
		//将当前对象从集合中移除
		users.remove(currentUser);
		subCount();
		//broadcast("system",MessageFormat.format("{0} 下线了！", currentUser), "");
		
		saveRecords();
	}

	/** 保存聊天记录到数据库 */
	private void saveRecords() {
		for(ChatRecordBean record : records){
			try {
				chatRecordsDao.insertChatRecord(record);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** 发送消息 */
	private void sendMsg(String msg) {

		//敏感词过滤
		msg = SensitiveWordsFilterUtils.filterWords(msg);
		if (this.session.isOpen()) {
			try {
				this.session.getBasicRemote().sendText(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** 给集合中的每个对象广播消息 */
	private void broadcast(String from, String msg,String id){

		for(WebChatServer server : users.values()){

			//判断是否是当前用户
			if(server.session != this.session)
				server.sendMsg(getJsonMsg(from, msg, TYPE_MESSAGE,id));
			else if("system".equals(from)) //系统消息（取消当前用户上线及下线提醒）
			{
				System.out.println("来到了系统消息这里了!!");
				this.sendMsg(getJsonMsg(currentUser, "", TYPE_MESSAGE,id));
			}
			else
				this.sendMsg(getJsonMsg(currentUser, msg, TYPE_MESSAGE,id));
			System.out.println("消息：======================\n"+msg);
		}
	}
	
	/** 获取在线人数 */
	private synchronized int getOnlineCount(){
		return onlineCount;
	}
	
	/** 在线用户数加一*/
	private synchronized void addCount(){
		onlineCount++;
	}
	
	/** 在线用户数减一 */
	private synchronized void subCount(){
		if (onlineCount > 0){			
			onlineCount--;
		}
	}
	
	/** 获取在线用户列表 */
	private synchronized String getOnlineUsers(){
		
		String result = "";
		for(String user : users.keySet()){ 
			result += user+"@";
		}
		if (result.length() > 0) {
			result = result.substring(0,result.length()-1);
		}
		return result;
	}
	
	/** 获取json格式的消息 */
	private String getJsonMsg(String from, String msg, String msgType,String id){
		JSONObject jsonObj = new JSONObject();
		JSONArray records = new JSONArray();
		
		if (msgType == TYPE_RECORD){
			for(ChatRecordBean record : result){
				JSONObject recordJson = new JSONObject();
				recordJson.append("sender", record.getSender());
				recordJson.append("receiver", record.getReceiver());
				recordJson.append("message", record.getMessage());
				recordJson.append("time", new SimpleDateFormat("MM-dd HH:mm:ss").format(record.getTime()));
				records.put(recordJson);
			}
			jsonObj.append("records", records);
		}
		jsonObj.append("count", getOnlineCount());
		jsonObj.append("userlist", getOnlineUsers());
		jsonObj.append("from", from);
		jsonObj.append("msg", msg);
		jsonObj.append("id", id);
		if(from.length()>5)
		{
			System.out.println("from:"+from);
			net.sf.json.JSONObject user = UsersDao.selectMyself(from);
			String Img = user.getString("userImg");
			jsonObj.append("img", Img);
		}
		jsonObj.append("time", new SimpleDateFormat("HH:mm:ss").format(new Date()));
		System.out.println("===================================\n"+jsonObj.toString());
		return jsonObj.toString();
	}
	
}
