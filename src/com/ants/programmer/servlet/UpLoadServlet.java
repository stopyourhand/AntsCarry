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

import org.elasticsearch.client.transport.TransportClient;

import com.ants.programmer.bean.ProductBean;
import com.ants.programmer.bean.RentBean;
import com.ants.programmer.dao.ElasticSearchDao;
import com.ants.programmer.dao.ProductCategoryDao;
import com.ants.programmer.dao.ProductDao;
import com.ants.programmer.dao.RentDao;
import com.ants.programmer.dao.UsersDao;
import com.ants.programmer.util.GetFileUtil;
import com.ants.programmer.util.PreventUtil;
import com.ants.programmer.util.ShopIdUtil;

import net.sf.json.JSONObject;
@WebServlet("/UpLoadServlet")

@MultipartConfig
public class UpLoadServlet extends HttpServlet {
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
		String fileName = "";
		// 上传图片
		if (method.equals("img")) {
			upimg(request,response,fileName);
			return ;
		}
		// 获取对应小分类的大分类
		if (method.equals("classify")) {
			String parentName = request.getParameter("parentClassify");
			if(parentName!=null) {
				JSONObject classify = ProductCategoryDao.selectChildName(parentName);
				response.getWriter().write(JSONObject.fromObject(classify).toString());
			}
			return ;
		}
		
		if (method.equals("text")) {
			getText(request,response);
			return ;
		}
		//上传视频
		if (method.equals("vedio")) {
			upvedio(request,response,fileName);
			return;
		}
		
	}

	
	
	//上传视频
	private void upvedio(HttpServletRequest request, HttpServletResponse response,String fileName) throws IOException, ServletException {
		String M=request.getServletContext().getRealPath("/");
		
		// 获取图片的绝对路径
		String Path = request.getServletContext().getRealPath("/") + "images";
		Collection<Part> parts = request.getParts();
		
		// 判断上传的图片是否为多张
		if (parts.size() == 1) {
			Part part = request.getPart("files");
			String header = part.getHeader("content-disposition");
			fileName = GetFileUtil.getFileName(header);
			part.write(Path + File.separator + fileName);
			
		}
	}

	private void getText(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		String User = session.getAttribute("mobile").toString();
		String goodsId = request.getParameter("id");

		// 防止JS注入
		String goodsName = PreventUtil.Prevent(request.getParameter("goodsName"));
		String goodsIntroduce = PreventUtil.Prevent(request.getParameter("goodsIntroduce"));
		// 发布的商品的详细信息
		String goodsPrice = request.getParameter("goodsPrice");
		String Ways = request.getParameter("ways");
		String Bargin = request.getParameter("isBargin");
		String Status = request.getParameter("status");
		String parentName = request.getParameter("parentClassify");
		String childName = request.getParameter("childClassify");
		String status = request.getParameter("status");
		int Parent=0,Child=0;
		if(parentName!=null) {
			Parent = ProductCategoryDao.selectIDByName(parentName);
		}
		if(childName!=null) {
			Child = ProductCategoryDao.selectIDByName(childName);
		}
		

		ProductBean product = null;
		RentBean rent=null;
		String haveImg = request.getParameter("haveImg");
		String path = ";";
		// 根据商品id获得商品图片的路径
		if (status.equals("3") || status.equals("4")) {
			path = ProductDao.selectPath(goodsId);
		}
			
		
		
		if (haveImg.equals("T")) {
			path=";";
			String goodsPath =request.getParameter("goodsPath");
			//判断是否存在视频文件
			if(goodsPath.contains("mp4")) {
				path="";
			}
			
			//遍历上传的文件的名字，进行拼接传入数据库
			String PATH[]=goodsPath.split(";");
			if(PATH.length>1) {
				for(String goodspath:PATH){
					if(goodspath!=null&&!(goodspath.equals(""))) {
						path += "images" + goodspath.substring(goodspath.lastIndexOf("\\"))+";";
					}
					
				}
			}else {
				path =path+ "images" + goodsPath.substring(goodsPath.lastIndexOf("\\"))+";";
			}
			
		}
		
		TransportClient client=ElasticSearchDao.getTransportClient();
		// 发布闲置
		if (Status.equals("1")) {
			String shopID=ShopIdUtil.getShopIdByUUID();
			if(goodsPrice.equals("0")) {
				//赠送
				product = new ProductBean(shopID, User, goodsName, Double.parseDouble(goodsPrice), goodsIntroduce, Ways,
						Bargin, Child, Parent, path, 3);
			}else {
				//闲置
				product = new ProductBean(shopID, User, goodsName, Double.parseDouble(goodsPrice), goodsIntroduce, Ways,
					Bargin, Child, Parent, path, 1);
			}
			
			
			//将新的数据存入到ES搜索引擎中
			JSONObject seller=UsersDao.selectMyself(User);
			String userImg=seller.getString("userImg");
			String userName=seller.getString("userName");
			String goodsSource=seller.getString("address");
			JSONObject json=ElasticSearchDao.jsonProduct(userImg, userName, goodsSource, shopID, 
					goodsName, goodsPrice, goodsIntroduce, Ways, Bargin, path, "1");
			ElasticSearchDao.insertData(json, client);
			
		} else if (Status.equals("2")) {// 发布寻求
			String shopID=ShopIdUtil.getShopIdByUUID();
			product = new ProductBean(shopID, User, goodsName, 6, goodsIntroduce, Ways, "1", Child, Parent, path, 2);

			
			JSONObject seller=UsersDao.selectMyself(User);
			String userImg=seller.getString("userImg");
			String userName=seller.getString("userName");
			String goodsSource=seller.getString("address");
			JSONObject json=ElasticSearchDao.jsonProduct(userImg, userName, goodsSource, shopID, 
					goodsName, "0", goodsIntroduce, Ways, Bargin, path, "2");
			ElasticSearchDao.insertData(json, client);
		}else if(Status.equals("3")) {//发布租赁
			String shopID=ShopIdUtil.getShopIdByUUID();
			String stock=request.getParameter("stock");
			product = new ProductBean(shopID, User, goodsName, Double.parseDouble(goodsPrice),
					goodsIntroduce, Ways, Bargin, Child,Parent, path, 4);
			if(stock!=null) {
				rent=new RentBean(shopID, Integer.parseInt(stock), 0);//0没有租出去，1租出去
			}
			
			JSONObject seller=UsersDao.selectMyself(User);
			String userImg=seller.getString("userImg");
			String userName=seller.getString("userName");
			String goodsSource=seller.getString("address");
			JSONObject json=ElasticSearchDao.jsonProduct(userImg, userName, goodsSource, shopID, 
					goodsName, goodsPrice, goodsIntroduce, Ways, Bargin, path, "3");
			ElasticSearchDao.insertData(json, client);
			
		}
		else if (Status.equals("4")) {// 我的闲置
			product = new ProductBean(goodsId, User, goodsName, Double.parseDouble(goodsPrice),
					goodsIntroduce, Ways, Bargin, Parent, Child, path, 1);
		} else if (Status.equals("5")) {// 我的寻求
			if(path.length()==1) {
				path=ProductDao.selectPath(goodsId);
			}
			product = new ProductBean(goodsId, User, goodsName, 6,
					goodsIntroduce, Ways, Bargin, Child, Parent, path, 2);
		}else if(Status.equals("6")) {//我的租赁
			if(path.length()==1) {
				path=ProductDao.selectPath(goodsId);
			}
			product = new ProductBean(goodsId, User, goodsName, Double.parseDouble(goodsPrice),
					goodsIntroduce, Ways, Bargin, Child, Parent, path, 4);
		}

		// 将获得的数据插入表中
		if (goodsId == null) {
			ProductDao.insert(product);
			//判断是否为租赁部分
			if(rent!=null) {
				RentDao.insert(rent);
			}
		} else {
			ProductDao.update(product);
			//将ES中的数据更新
//			JSONObject json=ElasticSearchDao.jsonProduct(product.getId(), product.getMobile(), product.getName(), shopID, 
//					goodsName, goodsPrice, goodsIntroduce, Ways, Bargin, path, "3");
//			ElasticSearchDao.updateData(product, client);
//			System.out.println("ES更新");
		}

		try {
			response.getWriter().write("true");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void upimg(HttpServletRequest request, HttpServletResponse response,String fileName) throws IOException, ServletException {

		// 获取图片的绝对路径
		String Path = request.getServletContext().getRealPath("/") + "images";
		Collection<Part> parts = request.getParts();

		// 判断上传的图片是否为多张
		if (parts.size() == 1) {
			Part part = request.getPart("files");
			String header = part.getHeader("content-disposition");
			fileName = GetFileUtil.getFileName(header);
			part.write(Path + File.separator + fileName);
			
			JSONObject judge = new JSONObject();
			judge.put("judge", "success");
			response.getWriter().write(JSONObject.fromObject(judge).toString());
		} else {

			// 上传多张图片
			for (Part part : parts) {
				String header = part.getHeader("content-disposition");
				fileName = GetFileUtil.getFileName(header);
				part.write(Path + File.separator + fileName);
			}
			
			JSONObject judge = new JSONObject();
			judge.put("judge", "success");
			response.getWriter().write(JSONObject.fromObject(judge).toString());
		}
	}

}
