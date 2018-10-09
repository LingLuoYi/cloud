package com.henglong.cloud.controller;

import com.henglong.cloud.service.PutForwardService;
import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/put")
public class PutForwardController {

    @Autowired
    private PutForwardService putForwardService;

    @RequiresPermissions(value = {"admin:install","user:install"})
    @PostMapping("/submission")
    public Json Put(String id,@RequestParam("num") String num){
        return ExceptionUtil.Success(200,"成功",putForwardService.Put(id,BigDecimal.valueOf(Long.valueOf(num))));
    }

    @RequiresPermissions(value = {"admin:update","finance:update"})
    @PostMapping("/submission_admin")
    public Json PutS(String id,String hash,String b){
        return ExceptionUtil.Success(200,"成功",putForwardService.PutS(id,hash,Boolean.getBoolean(b)));
    }
}
