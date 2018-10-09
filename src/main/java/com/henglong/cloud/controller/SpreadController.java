package com.henglong.cloud.controller;

import com.henglong.cloud.service.SpreadService;
import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/spread")
public class SpreadController {

    @Autowired
    private SpreadService spreadService;

    @RequiresPermissions("admin:select")
    @RequestMapping("/spread_admin_all_info")
    public Json SpreadAllInfo(){
        return ExceptionUtil.Success(200,"成功",spreadService.SpreadAllInfo());
    }

    @RequiresPermissions("user:select")
    @RequestMapping("/spread_one_info")
    public Json SpreadOneInfo(){
        return ExceptionUtil.Success(200,"成功",spreadService.SpreadOneInfo());
    }
}
