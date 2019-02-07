package com.ants.programmer.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ants.programmer.dao.ChatDao;
import com.ants.programmer.dao.ProductDao;
import com.ants.programmer.dao.UsersDao;
import com.ants.programmer.util.GetAddress;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {
	
	// 在线用户列表
	public ArrayList<String> online = new ArrayList<String>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		String method = request.getParameter("method");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html.charset=utf-8");
		String mobile = (String) request.getSession().getAttribute("mobile");
		String id = (String) request.getParameter("id");
		String user = request.getParameter("user");

		// 判断列表里面是否有用户，没有就往里卖弄添加
		if (!online.contains(mobile)) {
			online.add(mobile);
			return;
		}
		// 进入聊天室发送对应的商品信息，以及用户对应的信息
		if (method.equals("send")) {
			sendmessage(request, response, id);
			return;
		}
		//进入聊天室界面后发的消息
		if (method.equals("come")) {
			getusermessage(request, response,mobile,id);
			return;
		}
		if (method.equals("sendgoods")) {
			sendgoods(request, response, id);
			return ;
		}
		if (method.equals("mobile"))// 发送商品对应用户信息
		{
			sendproduct(request, response);
			return ;
		}
		if (method.equals("getmessage")) {
			getmessage(request, response);
			return ;
		}
		if (method.equals("offmessage")) {
			offmessage(request, response);
			return ;
		}

	}

	private void getusermessage(HttpServletRequest request, HttpServletResponse response,String mobile,String id) {
		JSONObject JUDGE = null;
		// 根据传入商品的id来获取用户名，用户头像，用户手机号码
		JSONObject User = ProductDao.selectProduct(id);
		String Mobile = User.getString("userid");
		String flag = "success";
		if (mobile == null) {
			flag = "notlogin";
		} else if (mobile.equals(Mobile)) {
			flag = "false";
		}
		JUDGE = new JSONObject();
		JUDGE.put("judge", flag);
		// 发送消息给前端判断用户的状态.
		try {
			response.getWriter().write(JSONObject.fromObject(JUDGE).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 发送商品信息
	private void sendgoods(HttpServletRequest request, HttpServletResponse response, String id) {

		JSONObject sendgoods = ProductDao.selectProduct(id);
		sendgoods.getString("goodsName");
		sendgoods.getString("goodsPrice");
		sendgoods.getString("goodsIntroduce");
		sendgoods.getString("goodsImg");
	}

	// 发送商品对应用户信息
	private void sendproduct(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String mobile1 = (String) request.getSession().getAttribute("mobile");
		JSONObject my = UsersDao.selectMyself(mobile1);
		if ((String) request.getSession().getAttribute("mobile") != null) {
			String myImg = my.getString("userImg");
			String myName = my.getString("userName");
			String myMobile = my.getString("userMobile");
			JSONObject User = new JSONObject();
			User.put("usermobile", myMobile);
			User.put("userimg", myImg);
			User.put("username", myName);
			response.getWriter().write(JSONObject.fromObject(User).toString());
		}
	}

	// 发送消息的来源，数量，以及商品对应id
	private void getmessage(HttpServletRequest request, HttpServletResponse response) throws IOException {

		
		JSONObject chat = new JSONObject();
		String Mobile = (String) request.getSession().getAttribute("mobile");
		String num = ChatDao.count(Mobile) + "";
		
		JSONObject OFchat = ChatDao.getMessage(Mobile);
		String from = OFchat.getString("myMobile");
		String goodid = OFchat.getString("goodid");
		chat.put("count", num);
		chat.put("from", from);
		chat.put("goodid", goodid);
		response.getWriter().write(JSONObject.fromObject(chat).toString());
	}

	// 发送离线消息
	private void offmessage(HttpServletRequest request, HttpServletResponse response) throws IOException {

		JSONObject chat = new JSONObject();
		String Mobile = (String) request.getSession().getAttribute("mobile");
		String num = ChatDao.count(Mobile) + "";
		JSONObject OFchat = ChatDao.getMessage(Mobile);
		String from = OFchat.getString("userName");
		String Mymobile = OFchat.getString("myMobile");
		String time = OFchat.getString("time");
		String offmessage = OFchat.getString("message");
		String to = OFchat.getString("to");
		
		chat.put("from", from);
		chat.put("time", time);
		chat.put("offmessage", offmessage);

		String MymobileArr[] = Mymobile.split(",");
		if (MymobileArr.length > 1) {
			String MOBILE = MymobileArr[MymobileArr.length - 1];
			Mymobile = MOBILE.substring(1, 12);
		} else if (!Mymobile.contains("[]")) {
			Mymobile = Mymobile.substring(2, 13);
		}

		if (!to.contains("[]")) {
			to = to.substring(to.indexOf("[") + 1, to.lastIndexOf("]") - 1);
		}
		if (Mymobile.length() > 5) {
			JSONObject users = UsersDao.selectMyself(Mymobile);
			String img = users.getString("userImg");
			chat.put("img", img);
		}
		response.getWriter().write(JSONObject.fromObject(chat).toString());

		ChatDao.delete(Mymobile, Mobile);

	}

	// 发送在线消息
	private void sendmessage(HttpServletRequest request, HttpServletResponse response, String id) throws IOException {

		JSONObject otherproduce = null;
		String mobile = (String) request.getSession().getAttribute("mobile");
		JSONObject produce = ProductDao.selectProduct(id);
		String Name = produce.getString("userName");
		String Img = produce.getString("userImg");
		String from = request.getParameter("from");
		
		
		
		String Mobile = produce.getString("userMobile");
		String wechat = produce.getString("userWechat");
		String Address = produce.getString("userAddress");
		String Userid = produce.getString("userid");
		JSONArray goodImg = produce.getJSONArray("goodsImg");// .getString("goodsImg");
		String goodimg = (String) goodImg.getJSONArray(0).get(1);
		String goodID = produce.getString("goodsID");
		String goodPrice = produce.getString("goodsPrice");
		String goodName = produce.getString("goodsName");
		String goodIntroduce = produce.getString("goodsIntroduce");
		String goodWays = produce.getString("goodsWays");
		String goodBargin = produce.getString("goodsBargin");
		JSONObject myself = UsersDao.selectMyself(mobile);
		String myname = myself.getString("userName");
		String myImg = myself.getString("userImg");
		String myid = mobile;
		String myaddress = myself.getString("address");
		String mymobile = myself.getString("userMobile");
		String ip = GetAddress.address();

		
		otherproduce = ProductDao.selectOthersProduct(Userid);
		if(from!=null)
		{
			JSONObject user =UsersDao.selectMyself(from);
			otherproduce.put("otherimg", user.getString("userImg"));
			otherproduce.put("othername", user.getString("userName"));
		}
		otherproduce.put("name", Name);
		otherproduce.put("img", Img);
		otherproduce.put("Mobile", Mobile);
		otherproduce.put("wechat", wechat);
		otherproduce.put("userid", Userid);
		otherproduce.put("Address", Address);
		otherproduce.put("goodImg", goodimg);
		otherproduce.put("goodPrice", goodPrice);
		otherproduce.put("goodid", goodID);
		otherproduce.put("goodName", goodName);
		otherproduce.put("goodIntroduce", goodIntroduce);
		otherproduce.put("goodWays", goodWays);
		otherproduce.put("goodBargin", goodBargin);
		otherproduce.put("myname", myname);
		otherproduce.put("myImg", myImg);
		otherproduce.put("myid", myid);
		otherproduce.put("myaddress", myaddress);
		otherproduce.put("mymobile", mymobile);
		otherproduce.put("ip", ip);
		response.getWriter().write(JSONObject.fromObject(otherproduce).toString());
	}

}
