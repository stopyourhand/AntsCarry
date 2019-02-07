package com.ants.programmer.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ants.programmer.bean.FeedBackBean;
import com.ants.programmer.dao.FeedBackDao;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class FeedBackServlet
 */
@WebServlet("/FeedBackServlet")
public class FeedBackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html.charset=utf-8");
		
		String text=request.getParameter("text");
		String satis=request.getParameter("satisfy");
		String username=request.getParameter("username");
		String mobile=request.getParameter("mobile");
		
		if(text!=null&&satis!=null) {
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			FeedBackBean feedback=new FeedBackBean(text, satis, mobile,username,  format.format(new Date()));
			FeedBackDao.insert(feedback);
			
			JSONObject judge = new JSONObject();
			judge.put("judge", "success");
			response.getWriter().write(JSONObject.fromObject(judge).toString());
		}
	}

}
