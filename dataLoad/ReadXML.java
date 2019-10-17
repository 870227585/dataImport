package com.bdi.sselab.dataLoad;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Author ChenXiang
 * @Date 2019/05/03,15:55
 * @Description
 */
public class ReadXML {
    /**
     * @Field 已经处理过的文件列表，文件名包含绝对路径
     */
    private static final List<File> completedXmlFiles = new ArrayList<>();
    private static Properties property = new Properties();
    private static InputStream file;
    private static String path;

    static {
        try {
            file = ReadXML.class.getClassLoader().getResourceAsStream("conf.properties");
            property.load(file);
            path = property.getProperty("export.xml.path");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("未找到配置文件");
        }
    }

    public ReadXML() {
    }

    /**
     * @param
     * @return void
     * @methodname getXMLs
     * @description 去路径下读取所有的xml文件
     * @author ChenXiang
     * @date 16:13,2019/5/3
     */
    private static List<File> getXMLs() {
        List<File> allFiles=new ArrayList<>();
        String rootPath = System.getProperty("user.dir") + File.separator + path;
        System.out.println(rootPath);
        File file = new File(rootPath);
        if (file.isDirectory()) {
            for (String string : file.list()) {
                if (string.endsWith(".xml")) {
                    allFiles.add(new File(rootPath + File.separator + string));
                }
            }
        }
//        allFiles.removeAll(completedXmlFiles);
//        completedXmlFiles.addAll(allFiles);
        return allFiles;
    }


    /**
     * @param
     * @return java.util.HashMap
     * @methodname readXML
     * @description 解析xml，得到业务所需的表名和相应列名，从export里面读到的是业务所需的表和列，表名和列名都用的是文档中的中文名，
     *              需要根据中文名去import中对应的xml读取英文列名
     * @author ChenXiang
     * @date 16:17,2019/5/3
     */
    @Test
    public static Map<String, List<String>> getTabelnameAndColName() {
//    public void getTabelnameAndColName() {
        Map<String, List<String>> tablenameToCols = new HashMap<>();
        List<File> xmlFiles= getXMLs(); //先获取目录下所有的文件
        List<Element> elements=null;
//        System.out.println(xmlFiles.size());
        if (!xmlFiles.isEmpty()) {
            int count=0;
            for (File file : xmlFiles) {
                SAXReader saxReader = new SAXReader();
                Document doc = null;
                try {
                    doc = saxReader.read(file);
                    elements= doc.getRootElement().elements();
                } catch (DocumentException e) {
                    System.out.println("读取xml文件出错");
                    e.printStackTrace();
                }

                //读取导入xml的位置
                String importPath=System.getProperty("user.dir")+File.separator+elements.get(0).getStringValue();
                System.out.println(importPath);
                File importXmlDir=new File(importPath);
                List<String> importXmls = null;
                if (importXmlDir.isDirectory()){
                    importXmls= Arrays.asList(importXmlDir.list());
                }
//                System.out.println("xmls: "+importXmls.get(0));
//                System.out.println("elements.size "+elements.size());

                //i=0表示的是importPath的路径，所以从1开始
                for (int i=1;i<elements.size();i++) {
                    /**
                     * 这里写的表名是文档名关键字，列名是中文列名
                     * 需要再根据入HBase时的xml做一次映射
                     */
                    //1、把exportXML中的一个dataset解析出来
                    String[] name=elements.get(i).elementText("name").split("/");
                    String excelName = name[0];
                    String sheetName=null;
                    if (2== name.length){
                        sheetName=name[1];
                    }
//                    System.out.println("excelName: "+excelName +" sheetName； "+sheetName);
                    List<String> cols = Arrays.asList(elements.get(i).element("cols").getStringValue().replaceAll("\\s*", "").split("-"));

                    //2、然后去找包含这个中文关键字的xml文档
                    String importXml=null;
                    for (String string:importXmls){
//                        System.out.println("excelname "+excelName);
//                        System.out.println("filename "+string);
                        if (string.contains(excelName)){
                            importXml=string;
                            break;
                        }
                    }
                    //3、再解析第二步中读到的xml
                    Map<String,Map<String,String>> tableToCol=new HashMap<>();
//                    System.out.println("importxml: "+importXml);
                    if (importXml!=null){
                        tableToCol= readImportXmls(importPath,importXml,sheetName);
                    }

                    if (null!= importXml){
                        String tableName=tableToCol.keySet().iterator().next();
                        Map<String,String> colToCol=tableToCol.get(tableName);

                        tablenameToCols.put(tableName,map(cols,colToCol));
//                        System.out.println("tablenameToCols: "+tablenameToCols);

                    }

                }
                System.out.println(file+" 数目为："+(tablenameToCols.size()-count));
                count=tablenameToCols.size();
            }
        }
        return tablenameToCols;
    }


    /**
     * @methodname map
     * @description 根据中文列名的列表从列名映射中获取对应英文列名返回
     * @author ChenXiang
     * @date 15:44,2019/5/11
     * @param chineseCols
     * @param CHStoENG
     * @return java.util.List<java.lang.String>
     */
    private static List<String> map(List<String> chineseCols,Map<String,String> CHStoENG){
        List<String> engCols=new ArrayList<>();
        for (String chsCol:chineseCols){
             if(chsCol.endsWith("/long"))
             {
                 //System.out.println("---------------------------------------"+chsCol);
                 engCols.add(CHStoENG.get(chsCol)+"/long");
             }
             else if(chsCol.endsWith("/int")){
                 engCols.add(CHStoENG.get(chsCol)+"/int");
             }
             else if(chsCol.endsWith("/double")){
                 engCols.add(CHStoENG.get(chsCol)+"/double");
             }
             else{
                 engCols.add(CHStoENG.get(chsCol));
             }

        }
        engCols.add("date");
        return engCols;
    }

    /**
     * @methodname readImportXmls
     * @description HBase中的一张表不一定就是一个文档，也可能只是excel文档中的一个sheet，如果sheetName为空，name就是一张表对应一个文档
     * @author ChenXiang
     * @date 16:39,2019/5/10
     * @param importPath
     * @param fileName
     * @param sheetName
     * @return java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.String>>>
     */
    private static Map<String,Map<String,String>> readImportXmls(String importPath,String fileName ,String sheetName){
        Map<String,Map<String,String>> tableToCol=new HashMap<>();
        System.out.println("importXmlPath: "+importPath+File.separator+fileName);
        try {
            SAXReader saxReader=new SAXReader();
            Document doc=saxReader.read(new File(importPath+File.separator+fileName));
            Map<String,String> colToCol=new HashMap<>();//<中文列名，英文列名>

            List<Node> dataList = doc.selectNodes("/DATASET/DATA");
            for (int i=0;i<dataList.size();i++){
//                System.out.println("begin read ");
                List<Node> itemList=dataList.get(i).selectNodes("ITEM");
                System.out.println("elementname "+((Element)(itemList.get(0))).attribute("sheetName").getStringValue());
                /**如果当前的data标签数据不是所需要的，就去读下一个标签，两种情况
                 * 1、传入的sheetName为null，说明这个文档内只有一个sheet，这种情况就直接去读item对应的值就可以
                 * 2、传入的sheetName不为null，说明该文档内有多个sheet，必须跟sheetName匹配上才可以进行后面的动作
                 */
                if (null!=sheetName && !((Element)itemList.get(0)).attribute("sheetName").getStringValue().equals(sheetName)){
                    continue;
                }
//                System.out.println("begin read 2");
                String[] chineseCols=((Element) itemList.get(9)).attribute("content").getStringValue().replaceAll("\\s*", "").split("-");
                String[] englishCols=((Element) itemList.get(7)).attribute("tableProperty").getStringValue().replaceAll("\\s*", "").split("-");
                String englishTableName=((Element) itemList.get(3)).attribute("hbaseName").getStringValue();
                if (chineseCols.length != englishCols.length){
                    System.out.println("importXml中英列名数量不等，请检查");
                    System.exit(0);
                }
                for (int m=0;m<chineseCols.length;m++){
                    colToCol.put(chineseCols[m],englishCols[m]);
                }
                tableToCol.put(englishTableName,colToCol);
                }
        } catch (DocumentException e) {
            System.out.println("读取importXML失败");
            e.printStackTrace();
        }
        return tableToCol;
    }
}