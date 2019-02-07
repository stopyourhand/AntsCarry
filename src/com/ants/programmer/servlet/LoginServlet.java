package com.ants.programmer.servlet;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ants.programmer.dao.UsersDao;
import com.ants.programmer.util.MD5Util;
import com.ants.programmer.util.Studentlogin;

import net.sf.json.JSONObject;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html.charset=utf-8");

		String method = request.getParameter("method");

		if (method.equals("remember")) {
			remember(request,response);
		}

		// 获取前端发送过来的用户名和密码
		String mobile = request.getParameter("mobile");
		String password = request.getParameter("password");
		if (password != null) {
			// 对密码进行加密，然后存入数据库
			password = MD5Util.MD5Encode(password);
		}
		response.setContentType("text/html;charset=UTF-8");

		if (method.equals("login")) {
			login(request,response,mobile,password);
			return ;
		}
		// 退出登录操作
		if (method.equals("loginout")) {
			loginout(request, response);
			return ;
		
		}
		if(method.equals("student"))
		{
			studentlogin(request,response);
			return ;
		}

	}
	
	
	
	private void login(HttpServletRequest request, HttpServletResponse response,String mobile,String password) throws IOException {

		String pw = request.getParameter("password");
		String remember = request.getParameter("remember");
		if (remember.equals("T")) {
			// 创建两个Cookie,用来保存账号和密码的信息
			Cookie mobilecookie = new Cookie("mobile", mobile);
			Cookie passwordcookie = new Cookie("password", pw);
			// 设置两个Cookie的生存期限是两个星期
			mobilecookie.setMaxAge(1209600);// 秒为单位
			passwordcookie.setMaxAge(1209600);
			// 保存Cookie
			response.addCookie(mobilecookie);
			response.addCookie(passwordcookie);
		} else {
			// 判断之前的Cookie的数组是否还保留账号和密码的信息
			Cookie Cookies[] = request.getCookies();
			// 如果保存到之前的账号和密码的信息
			if (Cookies != null && Cookies.length > 0) {
				// 遍历Cookie数组
				for (Cookie c : Cookies) {
					if (c.getName().equals("mobile") || c.getName().equals("password")) {
						c.setMaxAge(0); // 设置生存期限为0秒,也就是清除Cookie
						response.addCookie(c);// 重新保存Cookie
					}
				}
			}
		}

		// 如果登录成功给前端发送对应人的信息
		if (UsersDao.Login(mobile, password)) {
			HttpSession session = request.getSession();
			session.setAttribute("mobile", mobile);

			JSONObject user = UsersDao.userMessage(mobile);
			String photo = user.getString("img");
			String name = user.getString("name");
			JSONObject loginPhoto = new JSONObject();
			loginPhoto.put("judge", "success");
			loginPhoto.put("username", name);
			loginPhoto.put("img", photo);
			response.getWriter().write(JSONObject.fromObject(loginPhoto).toString());
		}
		// 登录失败给前端返回一个空的字符串
		else {
			response.getWriter().write("");
		}
	}

	private void remember(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		String MOBILE = "", PASSWORD = "";
		Cookie cookies[] = request.getCookies(); // 获取Cookie数组
		if (cookies != null && cookies.length > 0) {
			for (Cookie c : cookies) { // 遍历Cookie数组
				if (c.getName().equals("mobile")) { // 获取名称为username的Cookie
					MOBILE = c.getValue(); // 获取Cookie里的值
				}
				if (c.getName().equals("password")) { // 获取名称为password的Cookie
					PASSWORD = c.getValue(); // 获取Cookie里的值
				}

			}
		}

		JSONObject user = new JSONObject();
		user.put("mobile", MOBILE);
		user.put("password", PASSWORD);
		response.getWriter().write(JSONObject.fromObject(user).toString());
		return;
	}

	//进行学号登录的方法
	private void studentlogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		
		String str = request.getParameter("code");
		String encode = "encoded="+URLEncoder.encode(str);
		String code = Studentlogin.sendPost(encode)+"";
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		password = MD5Util.MD5Encode(password);
		//登录成功返回302，失败返回200
		
		
		JSONObject login = new JSONObject();
		
		if(code.equals("302"))
		{
			if(UsersDao.isRegistered(id))
			{
				request.getSession().setAttribute("mobile", id);
				JSONObject user = UsersDao.userMessage(id);
				String photo = user.getString("img");
				String name = user.getString("name");
				login.put("judge", "success");
				login.put("img", photo);
				login.put("username", name);
			}
			else
			{
				request.getSession().setAttribute("mobile", id);
				UsersDao.insertstudent(id, password);
				JSONObject user = UsersDao.userMessage(id);
				String photo = user.getString("img");
				String name = user.getString("name");
				login.put("judge", "success");
				login.put("img", photo);
				login.put("username", name);
			}
		}
		else
		{
			login.put("judge", "false");
		}
		//login.put("username", );
		//login.put("img", );
		//返回给前端是否登录成功
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.getWriter().write(JSONObject.fromObject(login).toString());
		
	}

	// 退出登录函数
	private void loginout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().removeAttribute("mobile");
		response.getWriter().write("loginout");
	}

}
