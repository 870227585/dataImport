package com.bdi.sselab.utils;

import com.bdi.sselab.domain.DataReadList;
import com.bdi.sselab.domain.log.Log;
import com.bdi.sselab.excel.ReadDataInHbase;
import com.bdi.sselab.repository.departmentLog.SchemeStatusRepository;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
/**
 * @Author:Yankaikai
 * @Description:解析xml文件类
 * @Date:Created in 2019/3/20
 */
public class ParseXmlUtils {
    public ParseXmlUtils(){
    }
    /**
    * @Author：Yankaikai
    * @Description:xml文件解析函数
    * @Date: 2019/3/20
    * @param: xml文件路径
    */
    public static HashMap parseReadXml(String location, Path templatePath, String filename, String date, Log log, SchemeStatusRepository ssr) throws JSONException {
        try {
            HashMap res=new HashMap();
            List<DataReadList> dataReadLists=new ArrayList<>();
            SAXReader reader = new SAXReader();
            Document document = reader.read(location);//读取xml文件
            //获取所有的ITEM元素
            List<Node> itemList = document.selectNodes("/DATASET/DATA");
            int len = itemList.size();
            // 得到所有的DATA
            for(int j=0;j<len;j++) {
                // 存储结构化数据的表的属性
                List<String> dataProperty=new ArrayList<>();
                // 存储非结构化数据的表的属性
                List<String> headProperty=new ArrayList<>();
                // 存储表头数据中的行和列。
                List<Integer> rows=new ArrayList<>();
                List<Integer> cols=new ArrayList<>();
                // 获取第一个ITEM
                List<Node> node=itemList.get(j).selectNodes("ITEM");
                DataReadList list=new DataReadList();
                for (int i = 1; i < node.size()-1; i++) {
                    Element element = (Element) node.get(i);
                    // i=0非结构化数据检查内容，i=1结构化数据检查内容，i=2数据的起始行。i=3表数据数据库名。
                    // i=4表头数据库名,i=5表中的属性,i=6,表头表属性,其他的是零散数据。
                    if (i == 1) {
                        // 读取行
                        String[] rowList = element.attribute("Row").getValue().split("-");
                        List<Integer> rowLists = new ArrayList<>();
                        if (!Objects.equals(rowList[0], "")) {
                            for (String s : rowList) {
                                rowLists.add(Integer.parseInt(s));
                            }
                        }
                        list.setCheckHeadRows(rowLists);
                        // 读取列
                        String[] colsList = element.attribute("cols").getValue().split("-");
                        List<Integer> colsLists = new ArrayList<>();
                        if (!Objects.equals(colsList[0], "")) {
                            for (String s : colsList) {
                                colsLists.add(Integer.parseInt(s));
                            }
                        }
                        list.setCheckHeadCols(colsLists);
                        // 读取内容
                        String[] checkContent = element.attribute("content").getValue().split("-");
                        List<String> contentLists = new ArrayList<>();
                        if (!Objects.equals(checkContent[0], "")) {
                            for (String s : checkContent) {
                                contentLists.add(s);
                            }
                        }
                        list.setHeadCheckContent(contentLists);
                    } else if (i == 2) {
                        // 读取结构化数据检查内容的行
                        String[] rowList = element.attribute("Row").getValue().split("-");
                        List<Integer> contentLists = new ArrayList<>();
                        if (!Objects.equals(rowList[0], "")) {
                            for (String s : rowList) {
                                contentLists.add(Integer.parseInt(s));
                            }
                        }
                        list.setCheckDataRows(contentLists);

                        // 内容所在的列
                        String[] dataCols = element.attribute("cols").getValue().split("-");
                        List<Integer> colsLists = new ArrayList<>();
                        for (String s : dataCols) {
                            colsLists.add(Integer.parseInt(s));
                        }
                        list.setCheckDataCols(colsLists);
                        // 内容
                        String[] dataContent = element.attribute("content").getValue().split("-");
                        List<String> contentList = new ArrayList<>();
                        for (String array : dataContent) {
                            contentList.add(array);
                        }
                        list.setDataCheckContent(contentList);

                    } else if(i==3){
                        list.setHbaseName(element.attribute("hbaseName").getValue());
                    }else if(i==4){
                        list.setRowKey(Integer.parseInt(element.attribute("rowKey").getValue()));
                    }else if (i == 5) {
                        list.setBeginRow(Integer.parseInt(element.attribute("beginRow").getValue()));
                    } else if(i==6){
                        list.setEndCol(Integer.parseInt(element.attribute("endCols").getValue()));
                    } else if (i == 7) {
                        String[] temp = element.attribute("tableProperty").getValue().replaceAll("\\s*", "").split("-");
                        for (String s : temp) {
                            dataProperty.add(s);
                        }
                        list.setTableProperty(dataProperty);
                    }else if (i == 8 ) {
                        int num = Integer.parseInt(element.attribute("isHave").getValue());
                        if (num == 1) {
                            list.setHaveTableFinal(true);
                        } else {
                            list.setHaveTableFinal(false);
                        }
                    } else {
                        // 读取数据零散数据的行和列。
                       /* rows.add(Integer.parseInt(element.attribute("Row").getValue()));
                        cols.add(Integer.parseInt(element.attribute("cols").getValue()));*/
                    }
                }
                list.setDate(date);
                dataReadLists.add(list);
            }
            System.out.println(dataReadLists.toString() + ":" + templatePath);
            // 检验此文件是否和xml文件类型一致。
            boolean isContinue=CheckTypeMatchUtils.checkType(dataReadLists,templatePath);
            if(isContinue) {
                System.out.println("类型匹配，开始入库!");
                new ReadDataInHbase(templatePath,dataReadLists, log, ssr);
            }else {
                res.put("code",204);
                res.put("message","Excel和xml的类型不匹配!");
                System.out.println("Excel和xml的类型不匹配!");
                log.setStatus("Excel和xml的类型不匹配");
                log.setDataNums(0);
                // 删除当前上传的文件
                Path path=templatePath.getParent();
                File file=new File(String.valueOf(path));
                File[] fileLists=file.listFiles();
                for (File f:fileLists){
                    if(f.getName().equals(filename)){
                        f.delete();
                        break;
                    }
                }
                return res;
            }
        }catch (Exception e){
            e.printStackTrace();
            HashMap jsonObject=new HashMap();
            jsonObject.put("code",0);
            jsonObject.put("message","未知错误!");
            log.setStatus("未知错误");
            log.setDataNums(0);
            return jsonObject;
        }
        return null;
    }
}
