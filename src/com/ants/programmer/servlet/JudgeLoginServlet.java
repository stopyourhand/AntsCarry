package com.ants.programmer.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ants.programmer.dao.UsersDao;
import com.ants.programmer.util.GetAddress;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebServlet("/JudgeLoginServlet")
public class JudgeLoginServlet extends HttpServlet {
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

		// 获取请求
		String method = request.getParameter("method");

		if (method.equals("judge")) {
			getjudge(request,response);
			return ;
		}

}
	//判断用户是否在线
	private void getjudge(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String MOBILE = "", PASSWORD = "", JUDGE = "", IMG = "", USER = "";
		String mobile = (String) request.getSession().getAttribute("mobile");

		if (mobile != null) {

			JUDGE = mobile;
			// 根据手机号码获得个人信息
			JSONObject judge = UsersDao.selectMyself(mobile);
			IMG = (String) judge.getString("userImg");
			USER = (String) judge.getString("userName");
		} else {

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
		}
		JSONObject user = new JSONObject();
		user.put("judge", JUDGE);
		user.put("img", IMG);
		user.put("username", USER);
		String id = (String)request.getSession().getAttribute("mobile");
		user.put("id", id);
		String ip = GetAddress.address();
		user.put("ip", ip);
		response.getWriter().write(JSONObject.fromObject(user).toString());
	}

	}
