package com.ants.programmer.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ants.programmer.dao.UsersDao;
import com.ants.programmer.util.IsMobile;
import com.ants.programmer.util.MD5Util;
import com.ants.programmer.util.Rondom;
import com.ants.programmer.util.Sendmessage;
import com.mysql.jdbc.StandardSocketFactory;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String code;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String method = request.getParameter("method");
		String mobile = request.getParameter("mobile");
		// 进行注册操作
		if (method.equals("registered")) {
			// 判断是否已经注册过
			registered(request, response, mobile);
			return;
		} 
		if (method.equals("sendmessage")) {
			sendmessage(request,response,mobile);
			return ;
		}
		if (method.equals("Timeout")) {
			// 时间到后，直接刷新存在servle里面的验证码
			code = Rondom.getnumber();
			request.getSession().setAttribute("code", code);
			return ;
		}
		// 给对应的用户发送验证码
		if (method.equals("sendcode")) {
			code = Rondom.getnumber();
			request.getSession().setAttribute("code", code);
			Sendmessage.send(mobile, "[蚂蚁置物] 验证码为:" + code);
			return;
		}
		// 判断注册时数据库是否存在用户
		if (method.equals("repeat")) {
			if (UsersDao.isRegistered(mobile)) {

				response.getWriter().write("registered");
				return;
			}
			return;
		}

	}
	
	private void sendmessage(HttpServletRequest request, HttpServletResponse response,String mobile) throws IOException {

		String mobileCode = request.getParameter("mobileCode");
		String password = request.getParameter("password");
		// 判断session中的手机验证码是否一致
		if (!mobileCode.equals(request.getSession().getAttribute("code"))) {
			response.getWriter().write("codeError");
			return;
		}

		// 判断用户手机号码是否符合格式，否则不让注册,不返回数据，直接提示注册失败
		if (IsMobile.isMobile(mobile)) {
			String encodePassword = MD5Util.MD5Encode(password);
			UsersDao.insert(mobile, encodePassword);
			response.getWriter().write("success");
		}
	}

	private void registered(HttpServletRequest request, HttpServletResponse response,String mobile) throws IOException {

		// 获得前端发送过来的验证码
		String vcode = request.getParameter("Vcode");
		Object obj = request.getSession().getAttribute("loginCapcha");
		HttpSession session = request.getSession();

		String loginCpacha = "";
		if (obj != null) {
			loginCpacha = obj.toString();
		}

		// 判断验证码是否正确
		if (!vcode.toUpperCase().equals(loginCpacha.toUpperCase())) {
			response.getWriter().write("VcodeError");
			return;
		}
		// 判断用户名是否已经被注册
		if (UsersDao.isRegistered(mobile)) {
			response.getWriter().write("registered");
			return;
		}
	}
}
