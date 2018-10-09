package com.henglong.cloud.controller;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(allowCredentials="true")
@Controller
public class UtilController {

    @GetMapping("/commodity")
    public String Commodity(){
        return "commodity/commodity";
    }

    @RequiresRoles("admin")
    @RequestMapping("/admin")
    public String Admin(){
        return "admin/admin";
    }

    @RequiresRoles("admin")
    @RequestMapping("/order_admin_all")
    public String OrderAll(){
        return "order/order_admin_all";
    }

    @RequiresRoles("admin")
    @RequestMapping("/order_one_admin_update")
    public String OrderUpdate(){
        return "order/order_one_admin_update";
    }

    @RequestMapping("/pay_a")
    public String Pay(){
        return "pay/pay";
    }

    @RequestMapping("/pay_confirm")
    public String PayConfirm(){
        return "pay/pay_confirm";
    }
}
