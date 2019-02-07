package com.ants.programmer.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.client.transport.TransportClient;

import com.ants.programmer.dao.ElasticSearchDao;
import com.ants.programmer.dao.ProductDao;
import com.ants.programmer.dao.RedisDao;

import net.sf.json.JSONObject;
import redis.clients.jedis.JedisCluster;

@WebServlet("/SaleServlet")
public class SaleServlet extends HttpServlet {

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
		// 获取当前页数
				
		JedisCluster jedis=RedisDao.getJedis();
		
		if (method.equals("goods")) {
			goods(request,response,jedis);
			return ;
		}
		// 请求为分类
		if (method.equals("classify")) {
			// 获取所有的分类
			getallclassify(request,response,jedis);
			return ;
		} 
		if (method.equals("search")) {
			// 查找对应分类的商品信息
			search(request,response);
			return;
		}

	}

	
	// 查找对应分类的商品信息
	private void search(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String search = java.net.URLDecoder.decode(request.getParameter("search"), "UTF-8");
		String classify = java.net.URLDecoder.decode(request.getParameter("classify"), "UTF-8");
		TransportClient client=ElasticSearchDao.getTransportClient();
		switch(classify) {
		case "待售":classify="1";break;
		case "求卖":classify="2";break;
		case "赠送":classify="3";break;
		case "租赁":classify="4";break;
		}
		JSONObject data = ElasticSearchDao.searchData(client, search, classify, 100);//ProductDao.searchData(search, "second", classify);
		response.getWriter().write(JSONObject.fromObject(data).toString());
	}

	private void getallclassify(HttpServletRequest request, HttpServletResponse response,JedisCluster jedis) throws IOException {

		String CLASSIFY=jedis.get("classify");
		JSONObject classify=new JSONObject();
		if(CLASSIFY==null||CLASSIFY.equals("")) {
			classify = ProductDao.selectClassify();
			jedis.set("classify", classify.toString());
		}else {
			classify=JSONObject.fromObject(CLASSIFY);
		}
		response.getWriter().write(JSONObject.fromObject(classify).toString());
	}

	private void goods(HttpServletRequest request, HttpServletResponse response,JedisCluster jedis) throws IOException {
		String currentPage = request.getParameter("currentPage");

		String name = request.getParameter("name");
		name = java.net.URLDecoder.decode(name, "UTF-8");
		String type=request.getParameter("type");
		if (name.equals("全部")) {
			
			String status = request.getParameter("status");
			int current = Integer.parseInt(currentPage);
			JSONObject allProduct = null;
			
			if(type.equals("0")) {
				//正常状态
				// 根据不同的商品状态获取不同类型的商品
				if (status.equals("0")) {// 出售
					String stat=jedis.hget("status", "0");
					if(stat==null||stat.equals("")) {
						allProduct = ProductDao.selectProductByStatus(Integer.parseInt(status) + 1, current, 32);
						jedis.hset("status", "0", allProduct.toString());
					}else {
						allProduct=JSONObject.fromObject(stat);
					}
					
				
				} else if (status.equals("1")) {// 求售
					String stat=jedis.hget("status", "1");
					if(stat==null||stat.equals("")) {
						allProduct = ProductDao.selectProductByStatus(Integer.parseInt(status) + 1, current, 32);
						jedis.hset("status", "1", allProduct.toString());
					}else {
						allProduct=JSONObject.fromObject(stat);
					}
				} else if (status.equals("2")) {// 赠送
					String stat=jedis.hget("status", "2");
					if(stat==null||stat.equals("")) {
						allProduct = ProductDao.selectProductByStatus(Integer.parseInt(status) + 1, current, 32);
						jedis.hset("status", "2", allProduct.toString());
					}else {
						allProduct=JSONObject.fromObject(stat);
					}
					
				} else if (status.equals("3")) {// 租赁
					String stat=jedis.hget("status", "3");
					if(stat==null||stat.equals("")) {
						allProduct = ProductDao.selectProductByStatus(Integer.parseInt(status) + 1, current, 32);
						jedis.hset("status", "3", allProduct.toString());
					}else {
						allProduct=JSONObject.fromObject(stat);
					}
				} 
//				else if (status.equals("4")) {// 代购
//					String stat=jedis.hget("status", "4");
//					if(stat==null||stat.equals("")) {
//						allProduct = ProductDao.selectProductByStatus(Integer.parseInt(status) + 1, current, 32);
//						jedis.hset("status", "4", allProduct.toString());
//					}else {
//						allProduct=JSONObject.fromObject(stat);
//					}
//				}
				response.getWriter().write(JSONObject.fromObject(allProduct).toString());
			}else if(type.equals("1")) {
				//价格升序
				if (status.equals("0")) {// 出售
					allProduct=ProductDao.selectProductByLowOrHigh("1", Integer.parseInt(status)+1);
				
				} else if (status.equals("1")) {// 求售
						allProduct=ProductDao.selectProductByLowOrHigh("1", Integer.parseInt(status)+1);
				} else if (status.equals("2")) {// 赠送
						allProduct=ProductDao.selectProductByLowOrHigh("1", Integer.parseInt(status)+1);
					
				} else if (status.equals("3")) {// 租赁
						allProduct=ProductDao.selectProductByLowOrHigh("1", Integer.parseInt(status)+1);
				} 
//				else if (status.equals("4")) {// 代购
//						allProduct=ProductDao.selectProductByLowOrHigh("1", Integer.parseInt(status)+1);
//				}
				response.getWriter().write(JSONObject.fromObject(allProduct).toString());
			}else if(type.equals("2")){
				
				//价格降序
				if (status.equals("0")) {// 出售
					allProduct=ProductDao.selectProductByLowOrHigh("2", Integer.parseInt(status)+1);
				
				} else if (status.equals("1")) {// 求售
						allProduct=ProductDao.selectProductByLowOrHigh("2", Integer.parseInt(status)+1);
				} else if (status.equals("2")) {// 赠送
						allProduct=ProductDao.selectProductByLowOrHigh("2", Integer.parseInt(status)+1);
					
				} else if (status.equals("3")) {// 租赁
						allProduct=ProductDao.selectProductByLowOrHigh("2", Integer.parseInt(status)+1);
				} 
//				else if (status.equals("4")) {// 代购
//						allProduct=ProductDao.selectProductByLowOrHigh("2", Integer.parseInt(status)+1);
//				}
				response.getWriter().write(JSONObject.fromObject(allProduct).toString());
			}else if(type.equals("3")) {
				String lowPrice=request.getParameter("lowPrice");
				String highPrice=request.getParameter("highPrice");
				
				if (status.equals("0")) {// 出售
					allProduct=ProductDao.selectProductByPrice(Double.parseDouble(lowPrice), Double.parseDouble(highPrice), Integer.parseInt(status)+1);
					
				} else if (status.equals("1")) {// 求售
					allProduct=ProductDao.selectProductByPrice(Double.parseDouble(lowPrice), Double.parseDouble(highPrice), Integer.parseInt(status)+1);
				} else if (status.equals("2")) {// 赠送
					allProduct=ProductDao.selectProductByPrice(Double.parseDouble(lowPrice), Double.parseDouble(highPrice), Integer.parseInt(status)+1);
					
				} else if (status.equals("3")) {// 租赁
					allProduct=ProductDao.selectProductByPrice(Double.parseDouble(lowPrice), Double.parseDouble(highPrice), Integer.parseInt(status)+1);
				}
//				else if (status.equals("4")) {// 代购
//					allProduct=ProductDao.selectProductByPrice(Double.parseDouble(lowPrice), Double.parseDouble(highPrice), Integer.parseInt(status)+1);
//				}
				response.getWriter().write(JSONObject.fromObject(allProduct).toString());
			}
			
		} else {
			// 根据当前页数获取商品信息
			int current = Integer.parseInt(currentPage);
			if(type.equals("0")) {
				JSONObject classProduct = ProductDao.selectProductByName(name, current, 20);
				response.getWriter().write(JSONObject.fromObject(classProduct).toString());
			}else if(type.equals("1")) {
				JSONObject classProduct = ProductDao.selectProductByClassNameLowOrHigh(name, current, 20, "1");
				response.getWriter().write(JSONObject.fromObject(classProduct).toString());
			}else if(type.equals("2")) {
				JSONObject classProduct = ProductDao.selectProductByClassNameLowOrHigh(name, current, 20, "2");
				response.getWriter().write(JSONObject.fromObject(classProduct).toString());
			}else if(type.equals("3")) {
				String lowPrice=request.getParameter("lowPrice");
				String highPrice=request.getParameter("highPrice");
				if(lowPrice!=null&&highPrice!=null) {
					JSONObject classProduct=ProductDao.selectProductByNameBetweenPrice(Double.parseDouble(lowPrice), Double.parseDouble(highPrice), name, current, 20);
					response.getWriter().write(JSONObject.fromObject(classProduct).toString());
				}
			}
			

		}
	}

}
