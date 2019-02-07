package com.ants.programmer.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.elasticsearch.client.transport.TransportClient;

import com.ants.programmer.bean.ProductBean;
import com.ants.programmer.bean.TransactionBean;
import com.ants.programmer.dao.ElasticSearchDao;
import com.ants.programmer.dao.ProductCategoryDao;
import com.ants.programmer.dao.ProductDao;
import com.ants.programmer.dao.RentDao;
import com.ants.programmer.dao.SeekDao;
import com.ants.programmer.dao.TransactionDao;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebServlet("/SeekServlet")
public class SeekServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html.charset=utf-8");

		HttpSession session = request.getSession();
		String mobile = session.getAttribute("mobile").toString();
		String method = request.getParameter("method");

		// 正在交易
		if (method.equals("trading")) {
			// 状态1,2都用1来填入即可
			JSONObject shop = TransactionDao.selectShopByMobile(mobile, 1);
			response.getWriter().write(JSONObject.fromObject(shop).toString());
			return ;
		}
		// 已经出售
		if (method.equals("sold")) {
			JSONObject shop = TransactionDao.selectShopByMobile(mobile, 3);
			response.getWriter().write(JSONObject.fromObject(shop).toString());
			return ;
		}
		// 删除
		if (method.equals("delete")) {
			delete(request,response,mobile);
			return ;
		}
		// 我的闲置
		if (method.equals("sell")) {
			
			JSONObject sellProduct = SeekDao.selectSellByUser(mobile, 1);
			response.getWriter().write(JSONObject.fromObject(sellProduct).toString());
			return ;
		}
		// 我的寻求
		if (method.equals("request")) {
			JSONObject sellProduct = SeekDao.selectSellByUser(mobile, 2);
			response.getWriter().write(JSONObject.fromObject(sellProduct).toString());
			return ;
		}
		// 我的租赁
		if (method.equals("rent")) {
			JSONObject rentProduct = RentDao.selectSellByUserRent(mobile, 4);
			response.getWriter().write(JSONObject.fromObject(rentProduct).toString());
			return ;
		}
		// 完成交易
		if (method.equals("finish")) {
			finish(request,response);
			return ;
		}

	}

	
	//完成交易
	private void finish(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String id = request.getParameter("id");
		TransactionBean t = new TransactionBean(id, "mobile", "name", 6, "introduce", "ways",
				"bargin", 1, 1, "", 3);
		if(id!=null) {
			//统计完成交易的类型的数量
			int ChildID=TransactionDao.selectChildIDByID(id);
			ProductCategoryDao.countHot(ChildID);
			
			ProductDao.delete(id);
			TransactionDao.changeStatus(t);
			
			//返回成功消息
			JSONObject judge = new JSONObject();
			judge.put("judge", "success");
			response.getWriter().write(JSONObject.fromObject(judge).toString());
			return;
		}
	}

	private void delete(HttpServletRequest request, HttpServletResponse response,String mobile) throws IOException {

		TransportClient client=ElasticSearchDao.getTransportClient();
		String type = request.getParameter("type");
		String id = request.getParameter("goodsID");

		// 删除我的闲置，寻求，租赁
		if (type.equals("sell") || type.equals("request")||type.equals("rent")) {
			ProductDao.delete(id);
			if(type.equals("rent")) {
				RentDao.delete(id);
			}
			
			//删除ES里面的商品信息
			if(id!=null) {
				ElasticSearchDao.deleteESData(client, id);
			}
			

			JSONObject judge = new JSONObject();
			judge.put("judge", "success");
			response.getWriter().write(JSONObject.fromObject(judge).toString());

		} else if (type.equals("trading")) {// 删除正在交易/取消交易
			String SellerMobile=TransactionDao.choseSellerMobile(id, mobile);
			JSONObject transaction=null;
			ProductBean product=null;
			if(SellerMobile!=null) {
				transaction = TransactionDao.selectTransaction(id,SellerMobile);
			}
			if(transaction!=null) {
				JSONArray Path=transaction.getJSONArray("goodsImg").getJSONArray(0);
				String goodsPath="";
				for(int i=0;i<Path.size();i++) {
					if(Path.get(0)==null||Path.get(0).equals("")) {
						goodsPath=";";
					}
					goodsPath+=Path.getString(i)+";";
				}
				 product = new ProductBean(transaction.getString("goodsID"), transaction.getString("goodsMobile"),
					transaction.getString("goodsName"), Double.parseDouble(transaction.getString("goodsPrice")),
					transaction.getString("goodsIntroduce"), transaction.getString("goodsWays"),
					transaction.getString("goodsBargin"), Integer.parseInt(transaction.getString("childId")),
					Integer.parseInt(transaction.getString("parentId")), goodsPath, 1);
			}
			
			
			//删除买家在交易栏中的信息
			TransactionDao.deleteByIdAndMobile(id, mobile);
			//将卖家的信息重新添加到porudct数据表中
			ProductDao.insert(product);
			//将卖家的信息从交易栏中删除
			TransactionDao.deleteByIdAndMobile(id, SellerMobile);

			JSONObject judge = new JSONObject();
			judge.put("judge", "success");
			response.getWriter().write(JSONObject.fromObject(judge).toString());
		}

	}

}
