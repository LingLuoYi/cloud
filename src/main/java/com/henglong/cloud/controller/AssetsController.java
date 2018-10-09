package com.henglong.cloud.controller;

import com.henglong.cloud.dao.PayJpa;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Pay;
import com.henglong.cloud.service.AssetsService;
import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/assets")
public class AssetsController {

    private final static Logger log = LoggerFactory.getLogger(AssetsController.class);

    @Autowired
    private AssetsService assetsService;


    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/assets_info")
    public Json AssetsOneAllInfo (){
        return ExceptionUtil.Success(200,"成功",assetsService.AssetsOneAllInfo());
    }

    /**
     * 审核接口，传入审核订单id
     * @param id
     * @return
     */
    @RequiresRoles("finance")
    @RequiresPermissions("finance:auditing")
    @PostMapping("/examine_finance")
    public Json Examine(@RequestParam("id") String id,@RequestParam("o") Boolean o){
        return ExceptionUtil.Success(200,"成功",assetsService.Examine(id,o));
    }

    @RequiresPermissions(value = {"finance:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/assets_admin_all")
    public Json AssetsAll(){
        return ExceptionUtil.Success(200,"成功",assetsService.AssetsAll());
    }

    @RequiresPermissions(value = {"finance:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/assets_admin_one")
    public Json AssetsOne(@RequestParam("id") Integer id){
        return ExceptionUtil.Success(200,"成功",assetsService.AssetsOne(id));
    }

    @RequiresPermissions(value = {"finance:update","admin:update"},logical = Logical.OR)
    @RequestMapping("/assets_admin_update")
    public Json AssetsUpdate(@RequestParam("id") Integer id ,@Valid Assets asstes){
        return ExceptionUtil.Success(200,"成功",assetsService.AssetsUpdate(id,asstes));
    }
}
