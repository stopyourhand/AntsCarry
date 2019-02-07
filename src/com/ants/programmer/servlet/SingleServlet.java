package com.ants.programmer.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ants.programmer.dao.ProductDao;
import com.ants.programmer.dao.ShopDao;

import net.sf.json.JSONObject;

@WebServlet("/SingleServlet")
public class SingleServlet extends HttpServlet {
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
		
		// 获取商品的数据，数据渲染
		if (method.equals("data")) {
			getsingle(request, response);
			return ;
		}
	}

	
	private void getsingle(HttpServletRequest request, HttpServletResponse response) {

		// 获取单个商品的信息
		String mobile = (String) request.getSession().getAttribute("mobile");
		String ID = request.getParameter("id");
		JSONObject product = ProductDao.selectProduct(ID);
		
		// 获取此商品发布的卖家的其他闲置商品
		JSONObject others = ProductDao.selectOthersProduct(ID);
		product.put("total", others.getString("total"));
		product.put("otherGoodsID", others.getString("otherGoodsID"));
		product.put("otherGoodsName", others.getString("otherGoodsName"));
		product.put("otherGoodsPrice", others.getString("OtherGoodsPrice"));
		product.put("otherGoodsImg", others.getString("otherGoodsImg"));

		// 判断此商品是否加入购物车
		boolean flag = ShopDao.isExist(ID);
		String judge = "";
		if (flag) {
			judge = "false";
		} else {
			judge = "success";
		}

		// 判断是否登录，来选择是否隐藏联系方式
		if (mobile != null) {
			product.put("login", "nothidden");
		} else {
			product.put("login", "hidden");
		}

		product.put("judge", judge);
		try {
			response.getWriter().write(JSONObject.fromObject(product).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
