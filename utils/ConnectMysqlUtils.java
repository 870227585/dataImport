package com.bdi.sselab.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * @Author:Yankaikai
 * @Description:数据库；连接配置类
 * @Date:Created in 2019/3/20
 */
@Component
public class ConnectMysqlUtils {
    private static ConnectMysqlUtils utils;
    @Autowired
    private Environment env;
    @PostConstruct
    public void init(){
        utils=this;
        utils.env=this.env;
        driver=utils.env.getProperty("spring.datasource.system.driver-class-name");
        username=utils.env.getProperty("spring.datasource.system.username");
        password=utils.env.getProperty("spring.datasource.system.password");
        url=utils.env.getProperty("spring.datasource.system.url");
    }

    private Connection connection =null;
    static PreparedStatement statement;
    static String driver = ReadConf.getProperty("sys.data.driver");
    static String url = ReadConf.getProperty("sys.data.url");
    static String username = ReadConf.getProperty("username");
    static String password = ReadConf.getProperty("password");

    static String driver_data = ReadConf.getProperty("driver");
    static String url_data = ReadConf.getProperty("url");
    static String username_data = ReadConf.getProperty("username");
    static String password_data = ReadConf.getProperty("password");
    public ConnectMysqlUtils(){
        connectMysqlSystem();
    }
    public ConnectMysqlUtils(String scheme){
        connectMysqlData();
    }
    /**
    * @Author：Yankaikai
    * @Description:数据库连接操作。
    * @Date: 2019/3/20
    * @param:表名
    */
     public void  connectMysqlSystem(){
        try {
            Class.forName(driver);
            connection= DriverManager.getConnection(url,username,password);
        }catch (Exception e){
            System.out.println("获取MySQL连接失败");
            e.printStackTrace();
        }
    }

    /***
     * 连接到指定的库
     * @param
     */

    public void  connectMysqlData(){
        try {
            Class.forName(driver_data);
//            String url2 =url.replace("test3",schema);
            connection= DriverManager.getConnection(url_data,username_data,password_data);
        }catch (Exception e){
            System.out.println("获取MySQL连接失败");
            e.printStackTrace();
        }
    }


    /**
    * @Author：Yankaikai
    * @Description:如果数据库中表不存在，则创建。
    * @Date: 2019/3/27
    * @param: 表名(tableName)，表中的属性(property)
    */
    public  boolean createDataTable(String tableName,List<String> property){
        if(tableName.isEmpty()){
            return false;
        }
        try {
            //int flag=0;
            connectMysqlData();
            StringBuilder p=new StringBuilder();
            for(String data:property){
                if (data.endsWith("/long")){
                    data=data.split("/long")[0];
                    p.append(data+" VARCHAR(500)"+",");
                }
                else if (data.endsWith("/int")){
                    data=data.split("/int")[0];
                    p.append(data+" int"+",");
                }
                else if (data.endsWith("/double")){
                    data=data.split("/double")[0];
                    p.append(data+" double"+",");
                }
                else if (data.endsWith("%")){
                    data="`"+data+"`";
                }
                else{
                    p.append(data+" VARCHAR(128)"+",");
                }
//                for(String a:PKs){
//                    if(a.equals(data))
//                    {
//                        p.append(data+" VARCHAR(256)"+",");
//                        flag = 1;
//                        break;
//                    }
//                }
//                if(flag==0)
//                {
//                    p.append(data+" LONGTEXT"+",");
//                }
            }
//            p.append("  PRIMARY KEY (");
//            for (String string:PKs){
//                p.append(string+",") ;
//            }
            //String data=p.substring(0,p.length()-1)+")";
            String data=p.substring(0,p.length()-1);
            String sql="Create table If Not Exists "+tableName+"("+"sid INT PRIMARY KEY AUTO_INCREMENT,"+data+")";
            //String sql="Create table If Not Exists "+tableName+"("+data+")";
            System.out.println(sql);
            statement=connection.prepareStatement(sql);
            statement.executeUpdate(sql);
            return true;
        }catch (Exception e){
            System.out.printf("表"+tableName+"创建失败!");
            e.printStackTrace();
            return false;

        }
    }

    /**
     * 向MYSQL数据库导入数据
     * @param tableName
     * @param colToData
     */
    public  void writeDataIntoMysql(String tableName, Map<String, String> colToData) {

        try {
            if(connection.isClosed()){
                connectMysqlData();
            }

            if (colToData.isEmpty() || tableName.isEmpty()) {
                return;
            }

            StringBuffer cols = new StringBuffer();
            StringBuffer data = new StringBuffer();
            for (String string : colToData.keySet()) {

                if (string.contains("(percent)")){
                    string="`"+string+"`";
                }
                else if (string.contains("%")){
                    string="`"+string+"`";
                }
                cols.append(string + ",");
                String string1;
                if(colToData.get(string).equals("—")||colToData.get(string).equals("_")||colToData.get(string).equals("/")||colToData.get(string).equals("-")||colToData.get(string).equals("-")||colToData.get(string).equals("")){
                    string1=null;
                }
                else if(colToData.get(string).contains("'")){
                    string1="'"+colToData.get(string).split("'")[0]+"''"+colToData.get(string).split("'")[1]+"'";
                }

                else{
                    string1="'"+colToData.get(string)+"'";
                }
                data.append(string1 + ",");
            }
            String sql = "replace into " + tableName + "(" + cols.substring(0, cols.length() - 1) + ")" + " values" + "(" + data.substring(0, data.length() - 1) + ")";
            System.out.println(sql);
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据data信息删除需要覆盖的数据
     * @param dataValue
     */
    public  void deletDataFromMysql(String tableName,String dataValue) {
        try {
            if(connection.isClosed()){
                connectMysqlData();
            }
            if (dataValue==null||tableName.isEmpty()) {
                return;
            }

            String sql = "DELETE FROM "+tableName+" WHERE date = "+dataValue;
            System.out.println(sql);
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看导入表的状态信息
     * @param scheme
     * @param table
     * @return
     */
    public  ResultSet getSchemeStatus(String scheme,String table){
        ResultSet resultSet =null;
        try {
            if(connection.isClosed()){
                connectMysqlSystem();
            }

            String sql = "select * from scheme_status where name = "+"\""+table+"\"";
           // System.out.println(sql);
            statement = connection.prepareStatement(sql);
            resultSet =statement.executeQuery();
            resultSet.next();
           // System.out.println("resultSet: "+resultSet.toString());
            if(resultSet.getRow()==0){
                System.out.println("insert");
                String sql2 ="insert into scheme_status(name,count,timer,status)"+" values("+"\""+table+"\""+",0,0.0,1)";
               // System.out.println(sql2);
                statement = connection.prepareStatement(sql2);
                statement.executeUpdate();
                statement = connection.prepareStatement(sql);
                resultSet =statement.executeQuery();
                resultSet.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultSet;
    }


    /**
     * 更改各个表状态：
     * @param scheme
     * @param table 数据库名称
     * @param
     */
    public  void updateStatus(String scheme,String table,Integer count,Double time,String status){
        try {
            if(connection.isClosed()){
                connectMysqlSystem();
            }
            String sql="update scheme_status set count="+count+",timer="+time+",status="+status+" where name ="+"\""+table+"\"";
            statement = connection.prepareStatement(sql);
            statement.execute();
//            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


//    /**
//    * @Author：Yankaikai
//    * @Description:向数据库中写入数据
//    * @Date: 2019/3/27
//    * @param: 表明，要写去的数据和表中的属性
//    */
//    public static void writeDataToBase(String tableName,List<String> data,List<String> property){
//        if(data.isEmpty()||tableName.isEmpty()){
//            return;
//        }
//        try {
//            //拼接要插入表中的属性。
//            StringBuilder s = new StringBuilder();
//            for(String d:property){
//                s.append(d+",");
//            }
////            System.out.println(property.size());
//            String finalData = s.substring(0, s.length()-1);
//            // 取出要插入表中的数值
//            StringBuilder realData=new StringBuilder();
//            for(String insertData:data){
//                realData.append("'"+insertData+"'"+",");
//            }
//            String finalRealData=realData.substring(0,realData.length()-1);
//            // 向数据库中插入数据
////            System.out.println(finalData + " :" + finalRealData);
//            String sql="replace into "+tableName+"("+finalData+")"+" values"+"("+finalRealData+")";
//            statement=connection.prepareStatement(sql);
//            statement.executeUpdate(sql);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }


    public static void main(String[] args){
        ConnectMysqlUtils connectMysqlUtils = new ConnectMysqlUtils();
//        ResultSet resultSet=connectMysqlUtils.getSchemeStatus("scheme_status","abc");
//        try {
//            //System.out.println(resultSet);
//            System.out.println(resultSet.getString(2));
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
        connectMysqlUtils.updateStatus("system_manager","a",0,0.0,"0");


    }

}
