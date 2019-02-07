package com.ants.programmer.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ants.programmer.bean.TransactionBean;
import com.ants.programmer.dao.ProductDao;
import com.ants.programmer.dao.TransactionDao;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebServlet("/TransactionServlet")
public class TransactionServlet extends HttpServlet {
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
		// 商品交易
		if (method.equals("transaction")) {
			transactionproduct(request,response);
			return;
		}

	}

	//商品交易函数
	private void transactionproduct(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		String Mobile = (String) session.getAttribute("mobile");
		String Id = request.getParameter("id");
		String SellerMobile="",Name="",Price="",Introduce="",Ways="",Bargin="",fileName="",parentId="",childId="",status="";
		// 获取对应id的商品信息
		if(Id!=null) {
			JSONObject Product = ProductDao.selectProduct(Id);
			SellerMobile = Product.getString("goodsMobile");
			Name = Product.getString("goodsName");
			Price = Product.getString("goodsPrice");
			Introduce = Product.getString("goodsIntroduce");
			Ways = Product.getString("goodsWays");
			Bargin = Product.getString("goodsBargin");
			//对数组进行分割，用;进行拼接
			JSONArray Path=Product.getJSONArray("goodsImg").getJSONArray(0);
			String goodsPath="";
			for(int i=0;i<Path.size();i++) {
				if(Path.get(0)==null||Path.get(0).equals("")) {
					goodsPath=";";
				}
				goodsPath+=Path.getString(i)+";";
			}
			fileName=goodsPath;
			parentId = Product.getString("parentId");
			childId = Product.getString("childId");
			status = Product.getString("status");
			
		}
		TransactionBean transaction = null;
		TransactionBean sellerTransaction = null;

		// 根据商品的状态（出售/寻求）来判断交易的类别，1为出售，2为寻求
		if (status.equals("1")) {
			transaction = new TransactionBean(Id, Mobile, Name, Double.parseDouble(Price), Introduce, Ways, Bargin,
					Double.parseDouble(parentId), Double.parseDouble(childId), fileName, 1);
			sellerTransaction = new TransactionBean(Id, SellerMobile, Name, Double.parseDouble(Price), Introduce,
					Ways, Bargin, Double.parseDouble(parentId), Double.parseDouble(childId), fileName, 1);

		} else if (status.equals("2") || status.equals("3")) {
			transaction = new TransactionBean(Id, Mobile, Name, Double.parseDouble(Price), Introduce, Ways, Bargin,
					Double.parseDouble(parentId), Double.parseDouble(childId), fileName, 2);
			sellerTransaction = new TransactionBean(Id, SellerMobile, Name, Double.parseDouble(Price), Introduce,
					Ways, Bargin, Double.parseDouble(parentId), Double.parseDouble(childId), fileName, 2);
		}

		// 插入数据，添加到用户正在交易栏中
		if (transaction != null) {
			TransactionDao.insert(transaction);
			TransactionDao.insert(sellerTransaction);
			
			//返回消息
			JSONObject judge = new JSONObject();
			judge.put("judge", "success");
			try {
				response.getWriter().write(JSONObject.fromObject(judge).toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
