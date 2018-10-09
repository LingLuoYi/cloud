package com.henglong.cloud.controller;

import com.henglong.cloud.service.OrderService;
import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    //单个用户订单信息
    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/order_info")
    public Json OrderInfo(){
        return ExceptionUtil.Success(200,"成功",orderService.OrderInfo());
    }

    //用户修改自己的订单
    @RequiresPermissions(value = {"user:update","admin:update"},logical = Logical.OR)
    @PostMapping("/order_user_update")
    public Json OrderUserUpdate(@RequestParam("id") String id,@RequestParam("num") String num) throws Exception {
        return  ExceptionUtil.Success(200,"成功",orderService.orderUserUpdate(id,num));
    }

    //用户删除自己的订单
    @RequiresPermissions(value = {"user:delete","admin:delete"},logical = Logical.OR)
    @PostMapping("/order_user_delete")
    public Json OrderUserDelete(@RequestParam("id") String id){
        return ExceptionUtil.Success(200,"成功",orderService.orderUserDelete(id));
    }

    //单个订单
    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/order_one_info")
    public Json OrderOneInfo(@RequestParam("order_id") String id){
        return ExceptionUtil.Success(200,"成功",orderService.OrderOneInfo(id));
    }

    //全部订单信息-管理员获取
    @RequiresPermissions(value = {"finance:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/order_all_info")
    public Json OrderAllInfo(){
        return ExceptionUtil.Success(200,"成功",orderService.OrderAllInfo());
    }

    //单个订单(管理员)
    @RequiresPermissions(value = {"finance:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/order_admin_one_info")
    public Json OrderAdminOneInfo(@RequestParam("id") String id){
        return ExceptionUtil.Success(200,"成功",orderService.OrderAdminOneInfo(id));
    }

    //管理员删除订单
    @RequiresPermissions(value = {"finance:delete","admin:delete"},logical = Logical.OR)
    @PostMapping("/order_admin_delete")
    public Json OrderDelete(@RequestParam("id") String id){
        return ExceptionUtil.Success(200,"成功",orderService.orderDelete(id));
    }

}
