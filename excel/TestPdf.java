package com.bdi.sselab.excel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;

/**
 * @Author:Yankaikai
 * @Description:
 * @Date:Created in 2019/4/17
 */
public class TestPdf {
    PDDocument document = null;
    String path;

    public TestPdf(String path){
        try {
            document = PDDocument.load(new File(path));
            this.path=path;
            pdfTranWord();
           // parsePdf();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 直接解析pdf数据
    public void parsePdf() throws IOException {
        PDFTextStripper textStripper = new PDFTextStripper();
        System.out.println(textStripper.getText(document));
        document.close();
    }
    // 将pdf先转换word，然后再解析
    public void pdfTranWord(){
        try {
            int pagenumber = document.getNumberOfPages();
            path = path.substring(0, path.lastIndexOf("."));
            String fileName = path + ".doc";
            File file = new File(fileName);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(fileName);
            Writer writer = new OutputStreamWriter(fos, "UTF-8");
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);// 排序
            stripper.setStartPage(1);// 设置转换的开始页
            stripper.setEndPage(pagenumber);// 设置转换的结束页
            stripper.writeText(document, writer);
            writer.close();
            document.close();
            System.out.println("pdf转换word成功！");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
