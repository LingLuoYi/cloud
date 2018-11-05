package com.henglong.cloud.controller;

import com.henglong.cloud.service.ReflectService;
import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.PrInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/reflect")
@CrossOrigin(allowCredentials = "true")
public class ReflectController {

    @Autowired
    private ReflectService reflectService;


    @PostMapping("/reflect_add")
    public PrInfo Reflect(@RequestParam("assetsId") String assetsId, @RequestParam("num") String num, String s){
        return ExceptionUtil.Info(reflectService.Reflects(assetsId,new BigDecimal(num),s));
    }

    @RequestMapping("/reflect_all")
    public PrInfo ReflectAll(){
        return ExceptionUtil.Info(reflectService.reflectAll());
    }
}
