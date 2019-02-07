package com.ants.programmer.dao;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import net.sf.json.JSONObject;

public class ElasticSearchDao {
	
	//创建client实例
	public static TransportClient getTransportClient() {
        TransportClient client=null;
		try {
			Settings settings = Settings.builder().put("cluster.name", "my-application").build();
			//获得client实例，连接9300接口
			client = new PreBuiltTransportClient(settings)
			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        return client;
	}
	
	//将数据打包成一个json对象
	public static JSONObject jsonProduct(String userimg,String username,String goodssource,
			String goodsid,String goodsname,String goodsprice,String goodsintroduce,String goodsways,
			String goodsbargin,String goodsimg,String status) {
		JSONObject json=new JSONObject();
		json.put("userImg", userimg);
        json.put("userName", username); 
        json.put("goodsSource", goodssource);
        json.put("goodsID",goodsid);
        json.put("goodsName", goodsname);
        json.put("goodsPrice", goodsprice);
        json.put("goodsIntroduce", goodsintroduce);
        json.put("goodsWays", goodsways);
        json.put("goodsBargin", goodsbargin);
        json.put("Status", status);
        ArrayList<String> img=new ArrayList<String>();
        //将以分号保存的图片路径分割开
        String fn[]=goodsimg.split(";");
		for(String filename:fn) {
			img.add(filename);
		}
		json.put("goodsImg", img);
		
		return json;
		
	}
	
	//将数据放入ES中
	public static void insertData(JSONObject product,TransportClient client) {
		Map<String, Object> json = new HashMap<String, Object>();
        String id=product.getString("goodsID");
        //将对应product的数据存放在json中
        json.put("userImg", product.getString("userImg"));
        json.put("userName", product.getString("userName")); 
        json.put("goodsSource", product.getString("goodsSource"));
        json.put("goodsID",id);
        json.put("goodsName", product.getString("goodsName"));
        json.put("goodsPrice", product.getString("goodsPrice"));
        json.put("goodsIntroduce", product.getString("goodsIntroduce"));
        json.put("goodsWays", product.getString("goodsWays"));
        json.put("goodsBargin", product.getString("goodsBargin"));
        json.put("goodsImg", product.getString("goodsImg"));
        json.put("Status", product.getString("Status"));
        
        //存放数据进ES文档
        client.prepareIndex("antscarry", "product",id ).setSource(json).execute().actionGet();
	}
	
	//删除ES中对应商品id的信息
	public static void deleteESData(TransportClient client,String shopid) {
		 client.prepareDelete("antscarry", "product", shopid).execute().actionGet();
		 System.out.println("ES已经删除对应订单信息");
	}
	
	
	//将ES中的数据更新
	public static void updateData(JSONObject product,TransportClient client) {
		Map<String, Object> json = new HashMap<String, Object>();
        String id=product.getString("goodsID");
        json.put("userImg", product.getString("userImg"));
        json.put("userName", product.getString("userName")); 
        json.put("goodsSource", product.getString("goodsSource"));
        json.put("goodsID",id);
        json.put("goodsName", product.getString("goodsName"));
        json.put("goodsPrice", product.getString("goodsPrice"));
        json.put("goodsIntroduce", product.getString("goodsIntroduce"));
        json.put("goodsWays", product.getString("goodsWays"));
        json.put("goodsBargin", product.getString("goodsBargin"));
        json.put("goodsImg", product.getString("goodsImg"));
        json.put("Status", product.getString("Status"));
        
        //更新文档
        client.prepareUpdate("antscarry", "product", id).setDoc(json,XContentType.JSON).get();
        System.out.println("更新成功");
	}
	
	//从ElasticSearch中查找数据
	public static JSONObject searchData(TransportClient client,String message,String status,int count) {
		JSONObject product=new JSONObject();
        ArrayList<String> GoodsImg=new ArrayList<String>();
        ArrayList<String> Status=new ArrayList<String>();
        ArrayList<String> UserImg=new ArrayList<String>();
        ArrayList<String> ID=new ArrayList<String>();
        ArrayList<String> Price=new ArrayList<String>();
        ArrayList<String> Introduce=new ArrayList<String>();
        ArrayList<String> Bargin=new ArrayList<String>();
        ArrayList<String> Ways=new ArrayList<String>();
        ArrayList<String> userName=new ArrayList<String>();
        ArrayList<String> goodsName=new ArrayList<String>();
        ArrayList<String> goodsSource=new ArrayList<String>();
        
        
		SearchRequestBuilder requestBuilder = client.prepareSearch("antscarry").setTypes("product");
        
		BoolQueryBuilder boolbuilder = QueryBuilders.boolQuery();
       
        //matchQuery会使用分词器，将我们输入的值进行分割
        MatchQueryBuilder builder1 = QueryBuilders.matchQuery("goodsName", message);
        MatchQueryBuilder builder2 = QueryBuilders.matchQuery("Status", status);
        BoolQueryBuilder bqb = QueryBuilders.boolQuery().must(builder1).must(builder2);
        boolbuilder.must(bqb);
        requestBuilder.setQuery(boolbuilder);
        SearchResponse responses = requestBuilder.setFrom(0).setSize(count).execute().actionGet();
        SearchHits hits= responses.getHits();
        
        
		for (SearchHit hit : hits){
			  int index=1;
	          Map<String, Object> map = hit.getSource();
	           for (String key : map.keySet()){
	        	   String value=map.get(key).toString();
	        	   //区分对应数组的信息
	        	   switch(index) {
		        	   case 1:GoodsImg.add(value);break;
		        	   case 2:Status.add(value);break;
		        	   case 3:UserImg.add(value);break;
		        	   case 4:ID.add(value);break;
		        	   case 5:Price.add(value);break;
		        	   case 6:Introduce.add(value);break;
		        	   case 7:Bargin.add(value);break;
		        	   case 8:Ways.add(value);break;
		        	   case 9:userName.add(value);break;
		        	   case 10:goodsName.add(value);break;
		        	   case 11:goodsSource.add(value);break;
	        	   }
	        	   index++;
	           }
	           index=1;
	      }
		
		//将获得数据的数组存入到json里面
		product.put("goodsImg", GoodsImg);
		product.put("Status", Status);
		product.put("userImg", UserImg);
		product.put("goodsID", ID);
		product.put("goodsPrice", Price);
		product.put("goodsIntroduce", Introduce);
		product.put("goodsBargin", Bargin);
		product.put("goodsWays", Ways);
		product.put("userName", userName);
		product.put("goodsName", goodsName);
		product.put("goodsSource", goodsSource);
		product.put("goodsImg", GoodsImg);
		return product;
	}
	
	
	
}
