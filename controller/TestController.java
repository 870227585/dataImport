package com.bdi.sselab.controller;

import com.bdi.sselab.utils.FileNameScanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author zjh
 * @Date 2019/03/27,18:13
 */
@Controller
@ResponseBody
public class TestController {

    @Autowired
    FileNameScanUtils fileNameScanUtils;

    @GetMapping("/test")
    public String test() {

        return "test";
    }
}
