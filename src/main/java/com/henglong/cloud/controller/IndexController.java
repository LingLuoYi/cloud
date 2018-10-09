package com.henglong.cloud.controller;


import com.henglong.cloud.entity.User;
import com.henglong.cloud.service.UserService;
import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.Json;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(allowCredentials="true")
@RestController
public class IndexController {

    private static final Logger log = LogManager.getLogger(IndexController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/s")
    public Json GetIndex(){
        User user = userService.UserInfo();
        if (user != null)
        return ExceptionUtil.Success(200,"成功",user);
        return ExceptionUtil.Success(200,"成功");
    }

    @RequestMapping("/error/403")
    public Json unauthorizedRole(){
        log.info("------没有权限-------");
        return ExceptionUtil.error(500,"没有权限");
    }
}
