package com.ants.programmer.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ants.programmer.dao.AnnouncementDao;
import com.ants.programmer.dao.ProductCategoryDao;
import com.ants.programmer.dao.ProductDao;
import com.ants.programmer.dao.RedisDao;
import com.ants.programmer.util.ItemsCFUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.JedisCluster;

@WebServlet("/dataServlet")
public class GetDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JSONArray special=null;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html.charset=utf-8");
		HttpSession session=request.getSession();
		String mobile=(String)session.getAttribute("mobile");
		
		
		JedisCluster jedis=RedisDao.getJedis();
		
		
		JSONArray img=new JSONArray();
		
		JSONArray specialID=new JSONArray();
		JSONArray specialImage =new JSONArray();
		JSONArray specialName = new JSONArray();
		
		JSONArray hotID=new JSONArray();
		JSONArray hotImage = new JSONArray();
		JSONArray hotName = new JSONArray();
		
		JSONArray newID=new JSONArray();
		JSONArray newImage = new JSONArray();
		JSONArray newName = new JSONArray();
		
		if(mobile==null) {
			int specialindex=(int)(8+Math.random()*14);
			// 获取首页的不同分类的数据
			specialID = ProductDao.getData(specialindex, 4, "id");
			specialImage=ProductDao.getData(specialindex, 4, "filename");
			specialName=ProductDao.getData(specialindex, 4, "name");
			img.add(specialImage);
		}else {
			//猜你喜欢
			ArrayList<String>recommendList=ItemsCFUtil.getRecommendShop(mobile);
			ArrayList<String> Img=new ArrayList<String>();
			int sum=0;
			if(recommendList.size()>0) {
				for(String shopid:recommendList) {
					JSONObject shop = ProductDao.selectShop(shopid);
					if(sum>4) {
						break;
					}
					sum++;
					if(shop.size()>0) {
						specialID.add(shop.getString("goodsID"));
						Img.add(shop.getString("goodsImg"));
						specialName.add(shop.getString("goodsName"));
					}
				
				}
				img.add(Img);
			}else {
				int specialindex=(int)(8+Math.random()*14);
				// 获取首页的不同分类的数据
				specialID = ProductDao.getData(specialindex, 4, "id");
				specialImage=ProductDao.getData(specialindex, 4, "filename");
				specialName=ProductDao.getData(specialindex, 4, "name");
				img.add(specialImage);
			}
			
			
		}
		
		
		
		//最热闲置
		ArrayList<Integer> apcId=ProductCategoryDao.selectApcIDByCount();
		int hotindex=(int)(Math.random()*apcId.size());
		
		JSONObject Hot=ProductCategoryDao.selectProductByChildId(apcId.get(hotindex), 4);
		if(Hot.size()>0) {
			hotID=Hot.getJSONArray("goodsID");
			hotImage=Hot.getJSONArray("goodsImg");
			hotName=Hot.getJSONArray("goodsName");
		}
			
		
		//最新闲置
		JSONObject news=ProductCategoryDao.selectProductByNew();
		
		newID=news.getJSONArray("newId");
		newImage=news.getJSONArray("newImage");
		newName=news.getJSONArray("newName");
		
		
		//化妆数据渲染
		JSONArray firId=new JSONArray();
		JSONArray firImage=new JSONArray();
		JSONArray firName=new JSONArray();
		String fir=jedis.get("firId");
		String firImg=jedis.get("firImage");
		String firname=jedis.get("firName");
		if(fir==null||fir.equals("")) {
			firId = ProductDao.getData(10, 10, "id");
			firImage = ProductDao.getData(10, 10, "filename");
			firName = ProductDao.getData(10, 10, "name");
			jedis.set("firId", firId.toString());
			jedis.set("firImage", firImage.toString());
			jedis.set("firName", firName.toString());
		}else {
			firId=JSONArray.fromObject(fir);
			firImage=JSONArray.fromObject(firImg);
			firName=JSONArray.fromObject(firname);
		}
		
		
		//考试
		JSONArray secId=new JSONArray();
		JSONArray secImage=new JSONArray();
		JSONArray secName=new JSONArray();
		String sec=jedis.get("secId");
		String secImg=jedis.get("secImage");
		String secname=jedis.get("secName");
		if(sec==null||sec.equals("")) {
			secId = ProductDao.getData(8, 10, "id");
			secImage = ProductDao.getData(8, 10, "filename");
			secName = ProductDao.getData(8, 10, "name");
			jedis.set("secId", secId.toString());
			jedis.set("secImage", secImage.toString());
			jedis.set("secName", secName.toString());
		}else {
			secId=JSONArray.fromObject(sec);
			secImage=JSONArray.fromObject(secImg);
			secName=JSONArray.fromObject(secname);
		}
		
		
		//器具
		JSONArray thiId=new JSONArray();
		JSONArray thiImage=new JSONArray();
		JSONArray thiName=new JSONArray();
		String thi=jedis.get("thiId");
		String thiImg=jedis.get("thiImage");
		String thiname=jedis.get("thiName");
		if(thi==null||thi.equals("")) {
			thiId = ProductDao.getData(21, 10, "id");
			thiImage = ProductDao.getData(21, 10, "filename");
			thiName = ProductDao.getData(21, 10, "name");
			jedis.set("thiId", thiId.toString());
			jedis.set("thiImage", thiImage.toString());
			jedis.set("thiName", thiName.toString());
		}else {
			thiId=JSONArray.fromObject(thi);
			thiImage=JSONArray.fromObject(thiImg);
			thiName=JSONArray.fromObject(thiname);
		}
		
		
		//衣裤
		JSONArray forsId=new JSONArray();
		JSONArray forsImage=new JSONArray();
		JSONArray forsName=new JSONArray();	
		String fors=jedis.get("forsId");
		String forsImg=jedis.get("forsImage");
		String forsname=jedis.get("forsName");
		if(fors==null||fors.equals("")) {
			forsId = ProductDao.getData(14, 10, "id");
			forsImage = ProductDao.getData(14, 10, "filename");
			forsName = ProductDao.getData(14, 10, "name");
			jedis.set("forsId", forsId.toString());
			jedis.set("forsImage", forsImage.toString());
			jedis.set("forsName", forsName.toString());
		}else {
			forsId=JSONArray.fromObject(fors);
			forsImage=JSONArray.fromObject(forsImg);
			forsName=JSONArray.fromObject(forsname);
		}
		
		
		//桌椅
		JSONArray fifId=new JSONArray();
		JSONArray fifImage=new JSONArray();
		JSONArray fifName=new JSONArray();	
		String fif=jedis.get("fifId");
		String fifImg=jedis.get("fifImage");
		String fifname=jedis.get("fifName");
		if(fif==null||fif.equals("")) {
			fifId = ProductDao.getData(12, 10, "id");
			fifImage = ProductDao.getData(12, 10, "filename");
			fifName = ProductDao.getData(12, 10, "name");
			jedis.set("fifId", fifId.toString());
			jedis.set("fifImage", fifImage.toString());
			jedis.set("fifName", fifName.toString());
		}else {
			fifId=JSONArray.fromObject(fif);
			fifImage=JSONArray.fromObject(fifImg);
			fifName=JSONArray.fromObject(fifname);
		}
		
		
		//手机
		JSONArray sixId=new JSONArray();
		JSONArray sixImage=new JSONArray();
		JSONArray sixName=new JSONArray();
		String six=jedis.get("sixId");
		String sixImg=jedis.get("sixImage");
		String sixname=jedis.get("fifName");
		if(six==null||six.equals("")) {
			sixId = ProductDao.getData(16, 10, "id");//filename
			sixImage = ProductDao.getData(16, 10, "filename");
			sixName = ProductDao.getData(16, 10, "name");
			jedis.set("sixId", sixId.toString());
			jedis.set("sixImage", sixImage.toString());
			jedis.set("sixName", sixName.toString());
		}else {
			sixId=JSONArray.fromObject(six);
			sixImage=JSONArray.fromObject(sixImg);
			sixName=JSONArray.fromObject(sixname);
		}
		
		
		//组合
		JSONObject Message=new JSONObject();
		
		//将id赋值
		JSONArray id=new JSONArray();
		id.add(specialID);id.add(hotID);id.add(newID);
		id.add(firId);id.add(secId);id.add(thiId);
		id.add(forsId);id.add(fifId);id.add(sixId);
		
		//将img赋值
//		JSONArray img=new JSONArray();
		img.add(hotImage);img.add(newImage);
		img.add(firImage);img.add(secImage);img.add(thiImage);
		img.add(forsImage);img.add(fifImage);img.add(sixImage);
		
		//将name赋值
		JSONArray name=new JSONArray();
		name.add(specialName);name.add(hotName);name.add(newName);
		name.add(firName);name.add(secName);name.add(thiName);
		name.add(forsName);name.add(fifName);name.add(sixName);
		
		
		JSONObject Announcement=AnnouncementDao.selectAnnouncement("limit");
		
		//将数组压进json里面
		Message.put("id", id);
		Message.put("img", img);
		Message.put("name", name);
		Message.put("ID", Announcement.getJSONArray("id"));
		Message.put("title", Announcement.getJSONArray("title"));
		Message.put("content", Announcement.getJSONArray("content"));
		Message.put("time", Announcement.getJSONArray("time"));
		
		
		response.getWriter().write(JSONObject.fromObject(Message).toString());
	}

}
