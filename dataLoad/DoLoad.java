package com.bdi.sselab.dataLoad;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author ChenXiang
 * @Date 2019/05/04,20:54
 * @Description
 */
public class DoLoad{
    public static int status=1;

    public TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            System.out.println(df.format(new Date()).split(" ")[1]);
            if (status == 1||(df.format(new Date()).split(" ")[1].equals("23:59:59"))) {
                status=0;
                //1
                Map<String, List<String>> tablenameToCols = ReadXML.getTabelnameAndColName();
                System.out.println("tableSize: " + tablenameToCols.keySet().size());
                //2
//        GenerateEntities.generateEntities(tablenameToCols);
                //3
                if (CreateMysqlTable.createMysqlTable(tablenameToCols)) {
                    //4
//                    System.out.println(1111);
                    habaseToMysql.loadDataToMysql(tablenameToCols);
                }
            }
        }
    };

    /**
     * 1、读取xml文件，得到表名和列的map
     * 2、根据map生成类
     * 3、根据map生成MySQL表以及select all的分页访问方法
     * 4、从HBase向MySQL导入数据
     */

    public static void main(String[] args) {
        Timer timer = new Timer();
        DoLoad doLoad = new DoLoad();
        long delay = 0;
        long intervalPeriod = 1 * 1000 ;
        timer.scheduleAtFixedRate(doLoad.timerTask, delay, intervalPeriod);
    }



//    @Override
//    public void run() {
////        Timer timer = new Timer();
////        long delay = 0;
////        long intervalPeriod = 1 * 1000 ;
////        timer.scheduleAtFixedRate(timerTask, delay, intervalPeriod);
//        System.out.println("xinchengchengqidong-------------------------------");
//    }
}
