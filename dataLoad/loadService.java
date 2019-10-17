package com.bdi.sselab.dataLoad;

import java.util.List;
import java.util.Map;

public class loadService {
    static int status=0;
    public void execute(){
        status =0;
        Map<String, List<String>> tablenameToCols = ReadXML.getTabelnameAndColName();
        System.out.println("tableSize: " + tablenameToCols.keySet().size());
        if (CreateMysqlTable.createMysqlTable(tablenameToCols)) {
            habaseToMysql.loadDataToMysql(tablenameToCols);
        }
    }




}
