package com.ants.programmer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.elasticsearch.client.transport.TransportClient;

import com.ants.programmer.bean.ProductBean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ProductDao {
	
	//升序或者降序
	public static JSONObject selectProductByLowOrHigh( String type,int status) {
		JSONObject product = new JSONObject();
		ArrayList<String> Name = new ArrayList<String>();
		ArrayList<Double> Price = new ArrayList<Double>();
		ArrayList<String> Introduce = new ArrayList<String>();
		JSONArray Img=new JSONArray();
		ArrayList<String> ID = new ArrayList<String>();
		ArrayList<String> userName = new ArrayList<String>();
		ArrayList<String> userAddress = new ArrayList<String>();
		ArrayList<String> userImg = new ArrayList<String>();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "";
				if (type.equals("0")) {
					sql = "select * from ANTS_PRODUCT where AP_STATUS=? ";
				} else if (type.equals("1")){
					sql = "select * from ANTS_PRODUCT where  AP_STATUS=? order by AP_PRICE ";
				}else if (type.equals("2")) {
					sql = "select * from ANTS_PRODUCT where  AP_STATUS=? order by AP_PRICE desc";
				}
				statement = connection.prepareStatement(sql);
				statement.setInt(1, status);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("AP_ID"));
					String fileName=resultset.getString("AP_FILE_NAME");
					Price.add(resultset.getDouble("AP_PRICE"));
					Name.add(resultset.getString("AP_NAME"));
					Introduce.add(resultset.getString("AP_INTRODUCE"));
					
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
				}

				for (String id : ID) {
					JSONObject user = UsersDao.selectUser(id);
					userName.add(user.getString("userName"));
					userAddress.add(user.getString("userAddress"));
					userImg.add(user.getString("userImg"));

				}
				product.put("userName", userName);
				product.put("goodsSource", userAddress);
				product.put("userImg", userImg);
				product.put("goodsID", ID);
				product.put("goodsImg", Img);
				product.put("goodsPrice", Price);
				product.put("goodsName", Name);
				product.put("goodsIntroduce", Introduce);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}
		return product;
	}
	
	
	// 根据商品价格来选择对应的商品
	public static JSONObject selectProductByPrice(double price1, double price2,int status) {
		JSONObject product = new JSONObject();
		ArrayList<String> Name = new ArrayList<String>();
		ArrayList<Double> Price = new ArrayList<Double>();
		ArrayList<String> Introduce = new ArrayList<String>();
		JSONArray Img=new JSONArray();
		ArrayList<String> ID = new ArrayList<String>();
		ArrayList<String> userName = new ArrayList<String>();
		ArrayList<String> userAddress = new ArrayList<String>();
		ArrayList<String> userImg = new ArrayList<String>();
		
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String	sql = "select * from ANTS_PRODUCT where AP_PRICE>=? and AP_PRICE<=? and AP_STATUS=? order by AP_PRICE";
				statement = connection.prepareStatement(sql);
				statement.setDouble(1, price1);
				statement.setDouble(2, price2);
				statement.setInt(3, status);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("AP_ID"));
					String fileName=resultset.getString("AP_FILE_NAME");
					Price.add(resultset.getDouble("AP_PRICE"));
					Name.add(resultset.getString("AP_NAME"));
					Introduce.add(resultset.getString("AP_INTRODUCE"));
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
				}

				for (String id : ID) {
					JSONObject user = UsersDao.selectUser(id);
					userName.add(user.getString("userName"));
					userAddress.add(user.getString("userAddress"));
					userImg.add(user.getString("userImg"));

				}
				product.put("userName", userName);
				product.put("goodsSource", userAddress);
				product.put("userImg", userImg);
				product.put("goodsID", ID);
				product.put("goodsImg", Img);
				product.put("goodsPrice", Price);
				product.put("goodsName", Name);
				product.put("goodsIntroduce", Introduce);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}
		return product;
	}

	// 获取对应mobile的用户的出售/寻求的商品的总数
	public static int totalUserProducts(String mobile) {
		int Sum = 0;
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select count(*)  from ANTS_PRODUCT where AP_MOBILE=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, mobile);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					Sum = resultset.getInt(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return Sum;
	}

	// 根据对应手机号码选择此用户的其他的发布的商品
	public static JSONObject selectOthersProduct(String id) {
		JSONObject others = new JSONObject();
		ArrayList<String> ID = new ArrayList<String>();
		ArrayList<String> Name = new ArrayList<String>();
		ArrayList<Double> Price = new ArrayList<Double>();
		JSONArray Img=new JSONArray();
		ArrayList<String> Mobile = new ArrayList<String>();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select *  from ANTS_PRODUCT where AP_MOBILE="
						+ "(select AP_MOBILE from ANTS_PRODUCT where AP_ID=?" + ")";
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("AP_ID"));
					Name.add(resultset.getString("AP_NAME"));
					Price.add(resultset.getDouble("AP_PRICE"));
					String fileName=resultset.getString("AP_FILE_NAME");
					Mobile.add(resultset.getString("AP_MOBILE"));
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
				}
				int total = 0;
				for (String m : Mobile) {
					total = totalUserProducts(m);
				}
				
				others.put("total", total);
				others.put("otherGoodsID", ID);
				others.put("otherGoodsName", Name);
				others.put("OtherGoodsPrice", Price);
				others.put("otherGoodsImg", Img);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return others;
	}

	// 获取子类所有商品的一共的页数
	public static int totalPageByStatus(int count, int status) {
		int totalPage = 1;
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select count(*) from ANTS_PRODUCT where AP_STATUS=? ";

				statement = connection.prepareStatement(sql);
				statement.setInt(1, status);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					
					int sum = resultset.getInt(1);      //获取数据的总数
					if (sum % count == 0) {				//判断是否能整除
						totalPage = sum / count;
					} else {
						totalPage = (sum / count) + 1;	//结果加一
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}
		return totalPage;
	}

	// 根据当前页数来选择不同状态(发布闲置，发布寻求)的商品
	public static JSONObject selectProductByStatus(int status, int currentPage, int count) {
		JSONObject product = new JSONObject();
		ArrayList<String> ID = new ArrayList<String>();
		ArrayList<String> Name = new ArrayList<String>();
		ArrayList<Double> Price = new ArrayList<Double>();
		ArrayList<String> Introduce = new ArrayList<String>();
		ArrayList<String> Ways = new ArrayList<String>();
		ArrayList<String> Bargin = new ArrayList<String>();
		ArrayList<String> Source = new ArrayList<String>();
		JSONArray Img=new JSONArray();
		ArrayList<String> UserImg = new ArrayList<String>();
		ArrayList<String> UserName = new ArrayList<String>();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ANTS_PRODUCT where AP_STATUS=? limit ?,?";

				statement = connection.prepareStatement(sql);
				statement.setInt(1, status);

				statement.setInt(2, currentPage * count);
				statement.setInt(3, count);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("AP_ID"));
					Name.add(resultset.getString("AP_NAME"));
					Price.add(resultset.getDouble("AP_PRICE"));
					Introduce.add(resultset.getString("AP_INTRODUCE"));
					Ways.add(resultset.getString("AP_WAYS"));
					Bargin.add(resultset.getString("AP_BARGIN"));
					String fileName=resultset.getString("AP_FILE_NAME");
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
				}

				for (String Id : ID) {
					JSONObject user = UsersDao.selectUser(Id);
					UserImg.add(user.getString("userImg"));
					UserName.add(user.getString("userName"));
					Source.add(user.getString("userAddress"));
				}

				product.put("userImg", UserImg);
				product.put("userName", UserName);
				product.put("totalPage", ProductDao.totalPageByStatus(32, status));
				product.put("goodsID", ID);
				product.put("goodsName", Name);
				product.put("goodsPrice", Price);
				product.put("goodsIntroduce", Introduce);
				product.put("goodsWays", Ways);
				product.put("goodsBargin", Bargin);
				product.put("goodsSource", Source);
				product.put("goodsImg", Img);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}
	
	
	// 根据分类名字获取childrenID来获取商品信息
	public static JSONObject selectProductByClassNameLowOrHigh(String name, int currentPage, int count,String type) {
		JSONObject product = new JSONObject();
		ArrayList<String> ID = new ArrayList<String>();
		ArrayList<String> Name = new ArrayList<String>();
		ArrayList<Double> Price = new ArrayList<Double>();
		ArrayList<String> Introduce = new ArrayList<String>();
		ArrayList<String> Ways = new ArrayList<String>();
		ArrayList<String> Bargin = new ArrayList<String>();

		JSONArray Img=new JSONArray();
		ArrayList<String> classify = new ArrayList<String>();
		
		ArrayList<String> UserImg = new ArrayList<String>();
		ArrayList<String> UserName = new ArrayList<String>();
		ArrayList<String> Source = new ArrayList<String>();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "";
				int id = ProductCategoryDao.selectIDByName(name);
				if (id < 9) {
					if(type.equals("1")) {
						sql = "select * from ANTS_PRODUCT where APC_ID=? order by AP_PRICE limit ?,?   ";
					}else {
						sql = "select * from ANTS_PRODUCT where APC_ID=? order by AP_PRICE desc limit ?,? ";
					}
					
				} else {
					if(type.equals("1")) {
						sql = "select * from ANTS_PRODUCT where APC_CHILD_ID=(select"
							+ " APC_ID from ANTS_PRODUCT_CATEGORY where APC_NAME= ?" + ") order by AP_PRICE limit ?,?  ";
					}else {
						sql = "select * from ANTS_PRODUCT where APC_CHILD_ID=(select"
								+ " APC_ID from ANTS_PRODUCT_CATEGORY where APC_NAME= ?" + ") order by AP_PRICE desc limit ?,? ";
					}
					
				}

				statement = connection.prepareStatement(sql);
				if (id < 7) {
					statement.setDouble(1, id);
				} else {
					statement.setString(1, name);
				}

				statement.setInt(2, currentPage * count);
				statement.setInt(3, count);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("AP_ID"));
					Name.add(resultset.getString("AP_NAME"));
					Price.add(resultset.getDouble("AP_PRICE"));
					Introduce.add(resultset.getString("AP_INTRODUCE"));
					Ways.add(resultset.getString("AP_WAYS"));
					Bargin.add(resultset.getString("AP_BARGIN"));
					String fileName=resultset.getString("AP_FILE_NAME");
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
				}

				for (String Id : ID) {
					JSONObject user = UsersDao.selectUser(Id);
					UserImg.add(user.getString("userImg"));
					UserName.add(user.getString("userName"));
					Source.add(user.getString("userAddress"));
				}

				classify = ProductCategoryDao.selectClassName(name);
				product.put("userImg", UserImg);
				product.put("userName", UserName);
				product.put("totalPage", ProductDao.totalPageByChildName(20, name));
				product.put("classify", classify);
				product.put("goodsID", ID);
				product.put("goodsName", Name);
				product.put("goodsPrice", Price);
				product.put("goodsIntroduce", Introduce);
				product.put("goodsWays", Ways);
				product.put("goodsSource", Source);
				product.put("goodsBargin", Bargin);
				product.put("goodsImg", Img);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}
	
		
		
	// 根据分类名字获取childrenID来获取商品信息
	public static JSONObject selectProductByNameBetweenPrice(double price1,double price2,String name, int currentPage, int count) {
		JSONObject product = new JSONObject();
		ArrayList<String> ID = new ArrayList<String>();
		ArrayList<String> Name = new ArrayList<String>();
		ArrayList<Double> Price = new ArrayList<Double>();
		ArrayList<String> Introduce = new ArrayList<String>();
		ArrayList<String> Ways = new ArrayList<String>();
		ArrayList<String> Bargin = new ArrayList<String>();

		JSONArray Img=new JSONArray();
		ArrayList<String> classify = new ArrayList<String>();
		
		ArrayList<String> UserImg = new ArrayList<String>();
		ArrayList<String> UserName = new ArrayList<String>();
		ArrayList<String> Source = new ArrayList<String>();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "";
				int id = ProductCategoryDao.selectIDByName(name);
				if (id < 9) {
					sql = "select * from ANTS_PRODUCT where APC_ID=? and AP_PRICE>=? and AP_PRICE<=?  limit ?,?";
				} else {
					sql = "select * from ANTS_PRODUCT where APC_CHILD_ID=(select"
							+ " APC_ID from ANTS_PRODUCT_CATEGORY where APC_NAME= ?" + ") and AP_PRICE>=? and AP_PRICE<=? limit ?,?";
				}

				statement = connection.prepareStatement(sql);
				if (id < 7) {
					statement.setDouble(1, id);
					statement.setDouble(2, price1);
					statement.setDouble(3, price2);
				} else {
					statement.setString(1, name);
					statement.setDouble(2, price1);
					statement.setDouble(3, price2);
				}

				statement.setInt(4, currentPage * count);
				statement.setInt(5, count);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("AP_ID"));
					Name.add(resultset.getString("AP_NAME"));
					Price.add(resultset.getDouble("AP_PRICE"));
					Introduce.add(resultset.getString("AP_INTRODUCE"));
					Ways.add(resultset.getString("AP_WAYS"));
					Bargin.add(resultset.getString("AP_BARGIN"));
					String fileName=resultset.getString("AP_FILE_NAME");
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
				}

				for (String Id : ID) {
					JSONObject user = UsersDao.selectUser(Id);
					UserImg.add(user.getString("userImg"));
					UserName.add(user.getString("userName"));
					Source.add(user.getString("userAddress"));
				}

				classify = ProductCategoryDao.selectClassName(name);
				product.put("userImg", UserImg);
				product.put("userName", UserName);
				product.put("totalPage", ProductDao.totalPageByChildName(20, name));//////////////////
				product.put("classify", classify);
				product.put("goodsID", ID);
				product.put("goodsName", Name);
				product.put("goodsPrice", Price);
				product.put("goodsIntroduce", Introduce);
				product.put("goodsWays", Ways);
				product.put("goodsSource", Source);
				product.put("goodsBargin", Bargin);
				product.put("goodsImg", Img);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}
	
	// 根据分类名字获取childrenID来获取商品信息
	public static JSONObject selectProductByName(String name, int currentPage, int count) {
		JSONObject product = new JSONObject();
		ArrayList<String> ID = new ArrayList<String>();
		ArrayList<String> Name = new ArrayList<String>();
		ArrayList<Double> Price = new ArrayList<Double>();
		ArrayList<String> Introduce = new ArrayList<String>();
		ArrayList<String> Ways = new ArrayList<String>();
		ArrayList<String> Bargin = new ArrayList<String>();

		JSONArray Img=new JSONArray();
		ArrayList<String> classify = new ArrayList<String>();
		
		ArrayList<String> UserImg = new ArrayList<String>();
		ArrayList<String> UserName = new ArrayList<String>();
		ArrayList<String> Source = new ArrayList<String>();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "";
				int id = ProductCategoryDao.selectIDByName(name);
				if (id < 9) {
					sql = "select * from ANTS_PRODUCT where APC_ID=? limit ?,?";
				} else {
					sql = "select * from ANTS_PRODUCT where APC_CHILD_ID=(select"
							+ " APC_ID from ANTS_PRODUCT_CATEGORY where APC_NAME= ?" + ") limit ?,?";
				}

				statement = connection.prepareStatement(sql);
				if (id < 7) {
					statement.setDouble(1, id);
				} else {
					statement.setString(1, name);
				}

				statement.setInt(2, currentPage * count);
				statement.setInt(3, count);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("AP_ID"));
					Name.add(resultset.getString("AP_NAME"));
					Price.add(resultset.getDouble("AP_PRICE"));
					Introduce.add(resultset.getString("AP_INTRODUCE"));
					Ways.add(resultset.getString("AP_WAYS"));
					Bargin.add(resultset.getString("AP_BARGIN"));
					String fileName=resultset.getString("AP_FILE_NAME");
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
				}

				for (String Id : ID) {
					JSONObject user = UsersDao.selectUser(Id);
					UserImg.add(user.getString("userImg"));
					UserName.add(user.getString("userName"));
					Source.add(user.getString("userAddress"));
				}

				classify = ProductCategoryDao.selectClassName(name);
				product.put("userImg", UserImg);
				product.put("userName", UserName);
				product.put("totalPage", ProductDao.totalPageByChildName(20, name));//////////////////
				product.put("classify", classify);
				product.put("goodsID", ID);
				product.put("goodsName", Name);
				product.put("goodsPrice", Price);
				product.put("goodsIntroduce", Introduce);
				product.put("goodsWays", Ways);
				product.put("goodsSource", Source);
				product.put("goodsBargin", Bargin);
				product.put("goodsImg", Img);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}

	// 获取次要分类名单
	public static JSONObject selectClassify() {
		JSONObject product = new JSONObject();
		ArrayList<String> classify = new ArrayList<String>();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select APC_NAME from ANTS_PRODUCT_CATEGORY where APC_ID>=?";
				statement = connection.prepareStatement(sql);
				statement.setInt(1, 9);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					classify.add(resultset.getString("APC_NAME"));
				}
				product.put("classify", classify);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}
	
	public static JSONObject selectShop(String id) {
		JSONObject product = new JSONObject();
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ANTS_PRODUCT where AP_ID=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					product.put("goodsID", resultset.getString("AP_ID"));
					product.put("goodsName", resultset.getString("AP_NAME"));
					String fileName=resultset.getString("AP_FILE_NAME");
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					product.put("goodsImg", img);
				}


			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(null, statement, null);
			}
		}

		return product;
	}
	
	
	public static JSONObject selectProduct(String id) {
		JSONObject product = new JSONObject();
		JSONArray Img=new JSONArray();
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ANTS_PRODUCT where AP_ID=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					product.put("goodsID", resultset.getString("AP_ID"));
					product.put("goodsMobile", resultset.getString("AP_MOBILE"));
					product.put("goodsName", resultset.getString("AP_NAME"));
					product.put("goodsPrice", resultset.getDouble("AP_PRICE"));
					product.put("goodsIntroduce", resultset.getString("AP_INTRODUCE"));
					product.put("goodsWays", resultset.getString("AP_WAYS"));
					product.put("goodsBargin", resultset.getString("AP_BARGIN"));
					product.put("parentId", resultset.getString("APC_ID"));
					product.put("childId", resultset.getString("APC_CHILD_ID"));
					String fileName=resultset.getString("AP_FILE_NAME");
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
					product.put("goodsImg", Img);
					product.put("status", resultset.getString("AP_STATUS"));
				}
				System.out.println("id="+id);
				JSONObject user = UsersDao.selectUser(id);
				product.put("userName", user.getString("userName"));
				product.put("userImg", user.getString("userImg"));
				product.put("userMobile", user.getString("userMobile"));
				product.put("userWechat", user.getString("userWechat"));
				product.put("userQQ", user.getString("userQQ"));
				product.put("userAddress", user.getString("userAddress"));
				product.put("wcHide", user.getString("wcHide"));
				product.put("qqHide", user.getString("qqHide"));
				product.put("userid", user.getString("userid"));

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}

	// 选择对应商品id的路径
	public static String selectPath(String id) {

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		String Path = "";
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select AP_FILE_NAME from ANTS_PRODUCT where AP_ID=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					Path = resultset.getString("AP_FILE_NAME");
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return Path;
	}

	// 插入数据
	public static int insert(ProductBean user) {
		String sql = "insert into ANTS_PRODUCT values(?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = {user.getId(), user.getMobile(), user.getName(), user.getPrice(), user.getIntroduce(), user.getWays(),
				user.getBargin(), user.getApcChildId(), user.getApcId(), user.getFileName(), user.getStatus() };
		return BaseDao.exectuIUD(sql, params);
	}

	// 更新数据
	public static int update(ProductBean user) {
		// 判断文件路径是否存在
		System.out.println("fileName"+user.getFileName());
		if (user.getFileName()!=null) {// 文件路径存在
			String sql = "update ANTS_PRODUCT set AP_NAME=?," + "AP_PRICE=?," + "AP_INTRODUCE=?," + "AP_WAYS=?,"
					+ "AP_BARGIN=?," + "AP_FILE_NAME=?," + "APC_ID=?," + "APC_CHILD_ID=?" + " where AP_ID=?";

			Object[] params = { user.getName(), user.getPrice(), user.getIntroduce(), user.getWays(), user.getBargin(),
					user.getFileName(), user.getApcId(), user.getApcChildId(), user.getId() };
			return BaseDao.exectuIUD(sql, params);
		} else {// 文件路径不存在
			String sql = "update ANTS_PRODUCT set AP_NAME=?," + "AP_PRICE=?," + "AP_INTRODUCE=?," + "AP_WAYS=?,"
					+ "AP_BARGIN=?," + "APC_ID=?," + "APC_CHILD_ID=?" + " where AP_ID=?";
			Object[] params = { user.getName(), user.getPrice(), user.getIntroduce(), user.getWays(), user.getBargin(),
					user.getApcId(), user.getApcChildId(), user.getId() };
			return BaseDao.exectuIUD(sql, params);
		}

	}

	// 删除某一行数据
	public static int delete(String id) {
		String sql = "delete from ANTS_PRODUCT where AP_ID=?";
		Object[] params = { id };
		return BaseDao.exectuIUD(sql, params);

	}

	// 获取子类所有商品的一共的页数
	public static int totalPageByChildName(int count, String name) {
		int totalPage = 1;
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "";
				int id = ProductCategoryDao.selectIDByName(name);
				if (id < 9) {
					sql = "select count(*) from ANTS_PRODUCT where APC_ID=?";
				} else {
					sql = "select count(*) from ANTS_PRODUCT where APC_CHILD_ID=(select"
							+ " APC_ID from ANTS_PRODUCT_CATEGORY where APC_NAME= ?" + ")";
				}
				statement = connection.prepareStatement(sql);
				if (id < 9) {
					statement.setDouble(1, id);
				} else {
					statement.setString(1, name);
				}
				resultset = statement.executeQuery();
				while (resultset.next()) {
					int sum = resultset.getInt(1);		//获取商品的总数量
					if (sum % count == 0) {
						totalPage = sum / count;
					} else {
						totalPage = (sum / count) + 1;
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}
		return totalPage;
	}


	public static JSONArray getData(int childid, int count,String type) {
		JSONArray message=new JSONArray();
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ANTS_PRODUCT where APC_CHILD_ID=? order by AP_ID  desc limit ?,? ";
				statement = connection.prepareStatement(sql);
				statement.setInt(1, childid);
				statement.setInt(2, 0);
				statement.setInt(3, count);// 浏览多少行下标0,1,2,3
				resultset = statement.executeQuery();
				while (resultset.next()) {
					if(type.equals("id")) {
						message.add(resultset.getString("AP_ID"));
					}else if(type.equals("name")) {
						message.add(resultset.getString("AP_NAME"));
					}else {
						ArrayList<String> img=new ArrayList<String>();
						String fileName=resultset.getString("AP_FILE_NAME");
						String fn[]=fileName.split(";");
						for(String filename:fn) {
							img.add(filename);
						}
						message.add(img);
					}
					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}
		return message;
	}
	
	//将数据库中的数据同步到ES中
	public static void insertAll() {
		int sum=0;
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		TransportClient client=ElasticSearchDao.getTransportClient();
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select *  from ANTS_PRODUCT ";
				statement = connection.prepareStatement(sql);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					sum++;
					JSONObject product=new JSONObject();
					ArrayList<String> img=new ArrayList<String>();
					String id=resultset.getString("AP_ID");
					product.put("goodsID",id );
					product.put("goodsName",resultset.getString("AP_NAME"));
					product.put("goodsPrice",resultset.getDouble("AP_PRICE"));
					product.put("goodsIntroduce", resultset.getString("AP_INTRODUCE"));
					product.put("goodsWays", resultset.getString("AP_STATUS"));
					product.put("goodsBargin", resultset.getString("AP_BARGIN"));
					product.put("Status", resultset.getString("AP_STATUS"));
					String fileName=resultset.getString("AP_FILE_NAME");
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					product.put("goodsImg", img);
					JSONObject user = UsersDao.selectUser(id);
					product.put("userImg",user.getString("userImg"));
					product.put("userName",user.getString("userName"));
					product.put("goodsSource",user.getString("userAddress"));
					
					ElasticSearchDao.insertData(product, client);
				}
				System.out.println("插入成功");
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

	}
	public static void main(String[] args) {
		insertAll();
	}
	
}
