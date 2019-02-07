package com.ants.programmer.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ants.programmer.dao.AnnouncementDao;

import net.sf.json.JSONObject;
@WebServlet("/AnnouncementServlet")
public class AnnouncementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method=request.getParameter("method");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html.charset=utf-8");
		if(method.equals("more")) {
			JSONObject moreAnnouncement=AnnouncementDao.selectAnnouncement("all");
			response.getWriter().write(JSONObject.fromObject(moreAnnouncement).toString());
		}
		
		if(method.equals("title")) {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html.charset=utf-8");
			
			String id=request.getParameter("id");
			if(!id.equals("")) {
				JSONObject announcement=AnnouncementDao.AnnouncementById(id);
				response.getWriter().write(JSONObject.fromObject(announcement).toString());
			}
		}
	}

}
