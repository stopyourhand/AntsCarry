package com.ants.programmer.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.client.transport.TransportClient;

import com.ants.programmer.dao.ElasticSearchDao;
import com.ants.programmer.dao.ProductDao;

import net.sf.json.JSONObject;

@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {
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

		// 按下enter键发送请求
		if (method.equals("keyup")) {
			keyup(request,response);
			return;
		}
	}

	private void keyup(HttpServletRequest request, HttpServletResponse response) {

		String searchName = request.getParameter("text");
		String classify = request.getParameter("classify");
		TransportClient client=ElasticSearchDao.getTransportClient();
		switch(classify) {
		case "待售":classify="1";break;
		case "求卖":classify="2";break;
		case "赠送":classify="3";break;
		case "租赁":classify="4";break;
		}
		// 获取要查询的所有商品的信息并且限制商品个数
		JSONObject data = ElasticSearchDao.searchData(client, searchName, classify, 6);//ProductDao.searchData(searchName, "limit", classify);
		try {
			response.getWriter().write(JSONObject.fromObject(data).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
