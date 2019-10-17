package com.bdi.sselab.utils;

import com.bdi.sselab.hbase.table.HBaseTable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.hbase.client.Put;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddDataUtils {
    /**
     * hbase入库代码
    * */
    public static ResponseEntity hbaseInData(String tableName, Object object) {
        try {
            if(object!=null) {
                ObjectMapper objectMapper = new ObjectMapper();
                // 将对象序列化为字符串
                String s = objectMapper.writeValueAsString(object);
                // 将字符串序列化为list对象。
                List<Map<String, String>> lists = objectMapper.readValue(s, new TypeReference<List<Map<String, String>>>() {
                });
                // 创建Hbase表名
                HBaseTable hBaseTable = new HBaseTable(tableName);
                int i=0;
                for (Map<String, String> map : lists) {
                    String id = map.get("id");
                    List<Put> puts = new ArrayList<>();
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        Put put = hBaseTable.toPut(entry.getKey() + "", id, entry.getValue() + "");
                        puts.add(put);
                    }
                   boolean flag=hBaseTable.insert(puts);
                    if(flag){
                        i++;
                    }
                }
                if(i==lists.size()){
                    return ResponseEntity.ok(ResultVOUtil.success(0));
                }else {
                    return ResponseEntity.ok(ResultVOUtil.error(2,"hbase入库失败!"));
                }
            }else {
                return ResponseEntity.ok(ResultVOUtil.error(1,"，mysql入库失败!"));
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok("异常!");
        }
    }
    /**
     * 得到前端响应请求的body体，并回去数据
     * */
    public static String getRequestBody(HttpServletRequest httpRequest)  {
        try {
            BufferedReader reader=new BufferedReader(new InputStreamReader(httpRequest.getInputStream()));
            String str = "";
            String wholeStr = "";
            while ((str = reader.readLine()) != null)
            {
                wholeStr+=str;
            }
            reader.close();
            return wholeStr;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
