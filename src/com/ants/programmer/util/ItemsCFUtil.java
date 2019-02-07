package com.ants.programmer.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.ants.programmer.dao.ShopDao;


/**
 * 描述：对电影点评过用1表示，0代表没点评过。
 * 给目标用户推荐相似度最高用户喜欢的电影
 */
public class ItemsCFUtil {

    //系统用户
    private static String[] users=null;
    //和这些用户相关的电影
    private static String[] shopid=null;
    //用户点评电影情况
    private static Integer[][] allUserShopCommentList=null;
    
    //用户点评电影情况，行转列
    private static Integer[][] allMovieCommentList=null;
    //电影相似度
    private static HashMap<String,Double> shopABSimilaritys=null;
    //待推荐电影相似度列表
    private static HashMap<Integer,Object> shopSimilaritys=null;
    //用户所在的位置
    private static Integer targetUserIndex=null;
    //目标用户点评过的电影
    private static List<Integer> targetUserCommentedMovies=null;
    //推荐电影
    private static  List<Map.Entry<Integer, Object>> recommlist=null;
    
    public static Integer[][] CommentList(String [] Users,String [] ShopID){
    	Integer [][] commentList=new Integer[Users.length][ShopID.length];
    	for(int i=0;i<Users.length;i++) {
    		for(int j=0;j<ShopID.length;j++) {
    			if(ShopDao.judgeShopID(ShopID[j], Users[i])) {
    				commentList[i][j]=1;
    			}else {
    				commentList[i][j]=0;
    			}
    		}
    	}
    	
		return commentList;
    	
    }
    
    public static void init() {
    	HashSet<String> usersSet= ShopDao.selectShopMobile();
    	HashSet<String> shopSet= ShopDao.selectShopID();
    	Iterator<String> usersIntetor=usersSet.iterator();
    	Iterator<String> shopIntetor=shopSet.iterator();
    	int usersIndex=0,shopIndex=0;
    	users=new String[usersSet.size()];
    	shopid=new String[shopSet.size()];
    	while(usersIntetor.hasNext()) {
    		users[usersIndex]=usersIntetor.next();
    		usersIndex++;
    	}
    	while(shopIntetor.hasNext()) {
    		shopid[shopIndex]=shopIntetor.next();
    		shopIndex++;
    	}
    	
    	allUserShopCommentList=CommentList(users, shopid);
    	allMovieCommentList=new Integer[allUserShopCommentList[0].length][allUserShopCommentList.length];
    }

    public static ArrayList<String> getRecommendShop(String user) {
    	init();
    	ArrayList<String> recommendShop=new ArrayList<String>();
    	targetUserIndex=getUserIndex(user);
        if(targetUserIndex==null){
            System.out.println("没有搜索到此用户，请重新输入：");
        }else{
            //转换目标用户电影点评列表
            targetUserCommentedMovies=Arrays.asList(allUserShopCommentList[targetUserIndex]);
            //计算电影相似度
            calcAllMovieSimilaritys();
            //获取全部待推荐电影
            calcRecommendMovie();
            //输出推荐电影
            for (Map.Entry<Integer, Object> item:recommlist){
            	recommendShop.add(shopid[item.getKey()]);
            }
        }
		return recommendShop;
    }

    /**
     * 获取全部推荐电影
     */
    private static void calcRecommendMovie(){
        shopSimilaritys=new HashMap<>();
        for (int i=0;i<targetUserCommentedMovies.size()-1;i++){
            for (int j=i+1;j<targetUserCommentedMovies.size();j++){
                Object similarity=null;
                if(targetUserCommentedMovies.get(i)==1 &&  targetUserCommentedMovies.get(j)==0 && ( shopABSimilaritys.get(i+""+j)!=null || shopABSimilaritys.get(j+""+i)!=null)){
                    similarity=shopABSimilaritys.get(i+""+j)!=null?shopABSimilaritys.get(i+""+j):shopABSimilaritys.get(j+""+i);
                    shopSimilaritys.put(j,similarity);
                }else if(targetUserCommentedMovies.get(i)==0 &&  targetUserCommentedMovies.get(j)==1 && (shopABSimilaritys.get(i+""+j)!=null || shopABSimilaritys.get(j+""+i)!=null)){
                    similarity=shopABSimilaritys.get(i+""+j)!=null?shopABSimilaritys.get(i+""+j):shopABSimilaritys.get(j+""+i);
                    shopSimilaritys.put(i,similarity);
                }
            }
        }

        recommlist = new ArrayList<Map.Entry<Integer, Object>>(shopSimilaritys.entrySet());
        Collections.sort(recommlist, new Comparator<Map.Entry<Integer, Object>>() {
            @Override
            public int compare(Map.Entry<Integer, Object> o1, Map.Entry<Integer, Object> o2) {
                return o1.getValue().toString().compareTo(o2.getValue().toString());
            }
        });

    }

    /**
     * 计算全部物品间的相似度
     */
    private static void calcAllMovieSimilaritys(){
        converRow2Col();
        shopABSimilaritys=new HashMap<>();
        for (int i=0;i<allMovieCommentList.length-1;i++){
            for (int j=i+1;j<allMovieCommentList.length;j++){
                shopABSimilaritys.put(i+""+j,calcTwoMovieSimilarity(allMovieCommentList[i],allMovieCommentList[j]));
            }
        }

    }


    /**
     * 根据电影全部点评数据，计算两个电影相似度
     * @param movie1Stars
     * @param movie2Starts
     * @return
     */
    private static double calcTwoMovieSimilarity(Integer[] movie1Stars,Integer[] movie2Starts){
        float sum=0;
        for(int i=0;i<movie1Stars.length;i++){
            sum+=Math.pow(movie1Stars[i]-movie2Starts[i],2);
        }
        return Math.sqrt(sum);
    }

    /**
     * 数组行转列
     */
    private static void converRow2Col(){
        for (int i=0;i<allUserShopCommentList[0].length;i++){
            for(int j=0;j<allUserShopCommentList.length;j++){
                allMovieCommentList[i][j]=allUserShopCommentList[j][i];
            }
        }
    }

    /**
     * 查找用户所在的位置
     * @param user
     * @return
     */
    private static Integer getUserIndex(String user){
        if(user==null || "".contains(user)){
            return null;
        }

        for(int i=0;i<users.length;i++){
            if(user.equals(users[i])){
                return i;
            }
        }

        return null;
    }
}
