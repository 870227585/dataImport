package com.bdi.sselab.utils;

import com.bdi.sselab.domain.ResultVO;

/**
 * @Author:Yankaikai
 * @Description:
 * @Date:Created in 2019/5/14
 */
public class ResultVOUtil {
    public static ResultVO success(Object object){
        ResultVO resultVO = new ResultVO();
        resultVO.setData(object);
        resultVO.setCode(200);
        resultVO.setMsg("成功");
        return resultVO;
    }
    public static ResultVO success(){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(200);
        resultVO.setMsg("成功");
        return resultVO;
    }
    public static ResultVO success(String msg){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(200);
        resultVO.setMsg(msg);
        return resultVO;
    }
    public static ResultVO error(Integer code,String msg){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        return resultVO;
    }
}
