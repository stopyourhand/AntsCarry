package com.ants.programmer.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.ants.programmer.dao.UsersDao;
import com.ants.programmer.util.GetFileUtil;
import com.ants.programmer.util.IsMobile;
import com.ants.programmer.util.MD5Util;
import com.ants.programmer.util.PreventUtil;
import com.ants.programmer.util.Rondom;
import com.ants.programmer.util.Sendmessage;

import net.sf.json.JSONObject;

@WebServlet("/UserServlet")
@MultipartConfig
public class UserServlet extends HttpServlet {
	private String code;

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
		String Mobile = request.getParameter("mobile");

		// 获得用户的信息
		if (method.equals("render") || method.equals("user")) {
			HttpSession session = request.getSession();
			String mobile = (String) session.getAttribute("mobile");
			JSONObject userMessage = UsersDao.userMessage(mobile);
			response.getWriter().write(JSONObject.fromObject(userMessage).toString());
			return ;
		}
		// 保存用户修改过后的信息
		if (method.equals("save")) {
			saveuser(request,response);
			return;
		}
		// 改变用户的头像
		if (method.equals("img")) {
			changeimg(request,response);
			return;
		}
		// 修改用户的密码
		if (method.equals("change")) {
			changepassword(request,response,Mobile);
			return;
		}
		// 发送修改密码的验证码
		if (method.equals("sendcode")) {

			code = Rondom.getnumber();
			Sendmessage.send(Mobile, "[蚂蚁置物]验证码为:" + code);
			return ;
		}
		// 时间到了刷新验证码
		if (method.equals("Timeout")) {
			code = Rondom.getnumber();
			return ;
		}

	}

	private void changepassword(HttpServletRequest request, HttpServletResponse response,String Mobile) throws IOException {


		String Code = request.getParameter("code");
		// 如果输入的验证码等于验证码,修改密码后返回success
		
		if (Code.equals(code)&&IsMobile.isMobile(Mobile)) {

			String password = request.getParameter("password");
			String encodePassword = MD5Util.MD5Encode(password);
			
			UsersDao.changePassword(Mobile, encodePassword);
			response.getWriter().write("success");
		} else {
			response.getWriter().write("false");
		}
	}

	private void changeimg(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String Path = request.getServletContext().getRealPath("/") + "images";
		Collection<Part> parts = request.getParts();
		String fileName = "";
		if (parts.size() == 1) {
			HttpSession session = request.getSession();
			String mobile = session.getAttribute("mobile").toString();
			Part part = request.getPart("files");
			String header = part.getHeader("content-disposition");
			fileName = GetFileUtil.getFileName(header);
			part.write(Path + File.separator + fileName);
			UsersDao.changePhoto(mobile, "images\\" + fileName);
		}
		
	}

	private void saveuser(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		String mobile = session.getAttribute("mobile").toString();
		String name = PreventUtil.Prevent(request.getParameter("name"));
		String wechat = PreventUtil.Prevent(request.getParameter("wechat"));
		String qq = PreventUtil.Prevent(request.getParameter("QQ"));
		String address = PreventUtil.Prevent(request.getParameter("address"));
		String qqhidden = request.getParameter("QQHide");
		String wechathidden = request.getParameter("wechatHide");
		UsersDao.update(mobile, name, wechat, qq, address, wechathidden, qqhidden);
		JSONObject flag = new JSONObject();
		flag.put("judge", "success");
		try {
			response.getWriter().write(JSONObject.fromObject(flag).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ;
	}

}
