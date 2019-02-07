package com.ants.programmer.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ants.programmer.bean.ShopBean;
import com.ants.programmer.dao.ProductDao;
import com.ants.programmer.dao.ShopDao;

import net.sf.json.JSONObject;

@WebServlet("/ShopServlet")
public class ShopServlet extends HttpServlet {
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
		// 获取用户购物车的商品信息
		if (method.equals("data")) {
			getdata(request,response);
			return ;
		}

		// 删除购物车
		if (method.equals("delete")) {
			delete(request,response);
			return ;
		}
		// 添加购物车
		if (method.equals("add")) {
			add(request,response);
			return ;
		}
	}

	
	//获取用户购物车的商品信息
	private void getdata(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		String mobile = session.getAttribute("mobile").toString();
		JSONObject shop = ShopDao.selectShop(mobile);
		try {
			response.getWriter().write(JSONObject.fromObject(shop).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void delete(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		String mobile = session.getAttribute("mobile").toString();
		String id = request.getParameter("id");
		ShopDao.delete(id);
		JSONObject shop = ShopDao.selectShop(mobile);
		try {
			response.getWriter().write(JSONObject.fromObject(shop).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void add(HttpServletRequest request, HttpServletResponse response) throws IOException {

		JSONObject judge = new JSONObject();
		HttpSession session = request.getSession();
		String mobile = (String) session.getAttribute("mobile");
		String id = request.getParameter("id");
		// 获取系统的时间
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (mobile != null) {

			// 根据商品的id获取对应的商品信息
			JSONObject product = ProductDao.selectProduct(id);
			String goodsMobile = product.getString("goodsMobile");
			ShopBean shop = new ShopBean(id, mobile, format.format(date));
			ShopDao.insert(shop);
			judge.put("judge", "success");

			// 判断此商品是否为本人发布的
			if (mobile.equals(goodsMobile)) {
				judge.put("judge", "false");
			}

			response.getWriter().write(JSONObject.fromObject(judge).toString());
		} else {
			judge.put("judge", "notlogin");
			response.getWriter().write(JSONObject.fromObject(judge).toString());
		}

	}

}
