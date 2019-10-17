package com.bdi.sselab.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author:Yankaikai
 * @Description:
 * @Date:Created in 2019/5/14
 */
@Data
@AllArgsConstructor
public class ResultVO<T> {
    /**
     * 返回错误代码
     */
    private Integer code;
    /**
     * 返回提示信息
     */
    private String msg;
    /**
     * 返回具体内容
     */
    private T data;
    public ResultVO(){
    }
}
