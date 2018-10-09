package com.henglong.cloud.controller;

import com.henglong.cloud.entity.Bank;
import com.henglong.cloud.entity.Pay;
import com.henglong.cloud.service.FileService;
import com.henglong.cloud.service.PayService;
import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.LoadFile;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/pay")
public class PayController {

    private static final Logger log = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private PayService payService;

    @Autowired
    private FileService fileService;

    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @PostMapping("/pay")
    public Json PayFirst(@RequestParam("id") String id) throws Exception {
        log.info("成功接收到数据【"+id+"】");
        return ExceptionUtil.Success(200,"成功",payService.PayFirst(id));
    }

    @RequestMapping("/bank")
    public Json Bank(){
        return ExceptionUtil.Success(200,"成功",payService.BankAll());
    }

    /*上传文件*/
    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/file_img")
    public Json File(@RequestParam(value = "pay_img",required = false) MultipartFile file ,@RequestParam("id") String id) throws Exception {
        return ExceptionUtil.Success(200,fileService.PayFile(file,id));
    }

    /*图片加载连接*/
    @RequiresPermissions(value = {"user:select","user:select"},logical = Logical.OR)
    @RequestMapping("/pay_img/{name}")
    public void PayImg(HttpServletResponse response, HttpSession session,@PathVariable("name")String name){
        FileInputStream fis = null;
        response.setContentType("image/gif");
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(LoadFile.Path()+"/img/pay/"+name);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.error("显示图片发生了异常",e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("图片显示类，关闭流出现了异常",e);
                }
            }
        }
    }


    @RequiresPermissions(value = {"finance:update","admin:update"},logical = Logical.OR)
    @PostMapping("/bank_update")
    public Json BankUpdate(@Valid Bank bank){
        return ExceptionUtil.Success(200,"成功",payService.BankUpdate(bank));
    }

    @RequiresPermissions(value = {"finance:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/pay_voucher_state")
    public Json PayvoucherState(@RequestParam("sv") String sv){
        return ExceptionUtil.Success(200,"成功",payService.PayvoucherState(sv));
    }

    @RequiresPermissions(value = {"finance:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/pay_state")
    public Json PayState(@RequestParam("s") String s){
        return ExceptionUtil.Success(200,"成功",payService.PayState(s));
    }

    @RequiresPermissions(value = {"finance:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/pay_one")
    public Json PayOne(@RequestParam("id") String id){
        return ExceptionUtil.Success(200,"成功",payService.PayOne(id));
    }

    @RequiresPermissions(value = {"finance:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/pay_admin_all")
    public Json PayAll(){
        return ExceptionUtil.Success(200,"成功",payService.PayAll());
    }

    @RequiresPermissions(value = {"finance:delete","admin:delete"},logical = Logical.OR)
    @PostMapping("/pay_delete")
    public Json PayDelete(@RequestParam("id") String id){
        return ExceptionUtil.Success(200,"成功",payService.PayDelete(id));
    }

    @RequiresPermissions(value = {"finance:update","admin:update"},logical = Logical.OR)
    @PostMapping("/pay_update")
    public Json PayUpdate(@Valid Pay pay){
        return ExceptionUtil.Success(200,"成功",payService.PayUpdate(pay));
    }

    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/pay_user_info")
    public Json PayUser(){
        return ExceptionUtil.Success(200,"成功",payService.PayUser());
    }

}
