package com.ants.programmer.servlet;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ants.programmer.dao.ProductDao;
import com.ants.programmer.util.CpachaUtil;
import com.ants.programmer.util.Sendmessage;

import net.sf.json.JSONObject;

/**
 * 
 * @author llq 验证码servlet
 */
@WebServlet("/CpachaServlet")
public class CpachaServlet extends HttpServlet {

	private static final long serialVersionUID = 4919529414762301338L;

	public void doGet(HttpServletRequest request, HttpServletResponse reponse) throws IOException {
		doPost(request, reponse);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse reponse) throws IOException {
		String method = request.getParameter("method");
		String productid = request.getParameter("id");
		String letter = "";
		String SellerMobile = null;
		// 图片验证码method，判断图片验证码
		if ("loginCapcha".equals(method)) {
			generateLoginCpacha(request, reponse);
			return;
		}

		// 发送联系卖家消息的方法
		if ("sendmessage".equals(method)) {
			// 叠加发送的信息
			if (productid != null) {
				JSONObject product = ProductDao.selectProduct(productid);
				SellerMobile = product.getString("goodsMobile");
				String SellerName = product.getString("goodsName");
				// String SellerIntroduce = product.getString("goodsIntroduce");
				if ((SellerName.toString()).length() > 8) {
					SellerName = (String) SellerName.substring(0, 8);
				}
				letter += "您的商品:" + SellerName + "...";
			}
			String QQ = request.getParameter("QQ");
			String Wechat = request.getParameter("wechat");
			String Mobile = request.getParameter("mobile");
			letter += "有买家联系你,ta";

			if (Mobile != null && Mobile != "") {
				letter += "的手机号:" + Mobile;
			} else if (Wechat != null && Wechat != "") {
				letter += ("微信:" + Wechat + " ");
			} else if (QQ != null && QQ != "") {
				letter += ("QQ:" + QQ + " ");
			}

			Sendmessage.send(SellerMobile, letter);
			reponse.getWriter().write("success");
			return;
		}

		reponse.getWriter().write("error");// 返回验证码发送错误
	}

	private void generateLoginCpacha(HttpServletRequest request, HttpServletResponse reponse) throws IOException {// 用流向前端发送图片验证码
		CpachaUtil cpachaUtil = new CpachaUtil();
		String generatorVCode = cpachaUtil.generatorVCode();
		request.getSession().setAttribute("loginCapcha", generatorVCode);
		BufferedImage generatorRotateVCodeImage = cpachaUtil.generatorRotateVCodeImage(generatorVCode, true);
		ImageIO.write(generatorRotateVCodeImage, "gif", reponse.getOutputStream());
	}
}
