package com.bdi.sselab.controller.StatisticBureau;
import com.alibaba.fastjson.JSONObject;
import com.bdi.sselab.domain.StatisticBureau.SocailContributionProportion;
import com.bdi.sselab.repository.StatisticBureau.SocailContributionProportionRepository;
import com.bdi.sselab.utils.AddDataUtils;
import com.bdi.sselab.utils.ResultVOUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author:Yankaikai
 * @Description:县职工社会保障缴费比例接口类
 * @Date:Created in 2019/5/27
 */
@Api(value = "县职工社会保障缴费比例接口类")
@RestController
@RequestMapping("api/socailProportion")
public class SocailContributionProportionController {
    @Autowired
    private SocailContributionProportionRepository repository;
    @ApiOperation(value = "向表中插入数据",notes = "向表中插入数据")
   // @ApiImplicitParam(name="socail")
    @PostMapping("/addMes")
    public ResponseEntity addMes() throws Exception {
        String tableName="socail_contribution_proportion";
        ObjectMapper objectMapper=new ObjectMapper();
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String result=AddDataUtils.getRequestBody(httpRequest);
        // 将字符串序列化为对象
        List<SocailContributionProportion> datas
                =objectMapper.readValue(result, new TypeReference<List<SocailContributionProportion>>() {});
        List<SocailContributionProportion> saveData=repository.saveAll(datas);
        // Hbase入库操作
        return AddDataUtils.hbaseInData(tableName,saveData);
    }

    @ApiOperation("根据日期返回表中数据,参数形式为?date=2019-5-4")
    @GetMapping("/getMesByDate")
    public ResponseEntity getMesByDate(@RequestParam("date")String date){
        HashMap result = new HashMap();
        List<SocailContributionProportion> lists=repository.findByDate(date);
        Collections.sort(lists);
        result.put("data", lists);
        result.put("code", 200);
        return ResponseEntity.ok(result);
    }

    @ApiOperation("根据日期删除整个表格数据,参数形式为?date=2019-5-4")
    @DeleteMapping("/deleteMesByDate")
    public ResponseEntity deleteMesByDate(@RequestParam("date")String date){
        List<SocailContributionProportion> lists=repository.findByDate(date);
        for(SocailContributionProportion s:lists){
            repository.deleteById(s.getId());
        }
        return ResponseEntity.ok(ResultVOUtil.success("删除成功!"));
    }

    @ApiOperation("修改数据,传递一个SocailContributionProportion对象")
    @PostMapping("/modifyMes")
    public ResponseEntity modifyMes(@RequestBody SocailContributionProportion socail){
        Long id=socail.getId();
        if(id!=null&&repository.findById(id)!=null) {
            repository.save(socail);
            return ResponseEntity.ok(ResultVOUtil.success("修改成功!"));
        }else {
            return ResponseEntity.ok(ResultVOUtil.success("修改失败!"));
        }
    }

    @ApiOperation("获取所有的日期")
    @GetMapping("/getAllDate")
    public ResponseEntity getAllDate(){
        List<String> dates=repository.getAllDate();
        List newList = new ArrayList(new HashSet(dates));
        return ResponseEntity.ok(newList);
    }


}
