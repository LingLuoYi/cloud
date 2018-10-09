package com.henglong.cloud.controller;


import com.henglong.cloud.entity.Roles;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.service.UserService;
import com.henglong.cloud.util.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/user")
public class UserInfoController  {

    private static final Logger log = LoggerFactory.getLogger(UserInfoController.class);

    @Autowired
    private UserService userService;

    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/user_info")
    public Json UserInfo(){
        return ExceptionUtil.Success(200,"成功",userService.UserInfo());
    }

    @RequiresPermissions(value = {"user:update","admin:select"},logical = Logical.OR)
    @PostMapping("/user_update")
    public Json PostUser_Add(@Valid User user){
        return ExceptionUtil.Success(200,"成功",userService.UserAdd(user));
    }

    @RequiresPermissions("admin:update")
    @PostMapping("/roles_update")
    public Json PostRoles(@Valid Roles roles){
                return ExceptionUtil.Success(200,"成功",userService.RolesUpdate(roles));
    }

    /*邮件确认连接*/
    @RequestMapping("/email_user_confirm")
    public String EmailConfirm(@RequestParam("phone") String phone,@RequestParam("email") String email,@RequestParam("date") String date ,@RequestParam("code") String code) throws Exception {
        userService.MailConfirm(phone,email,date,code);
        return "redirect:/login";
    }

    @RequiresPermissions(value = {"user:install","admin:install"},logical = Logical.OR)
    @RequestMapping("/email")
    public Json mail(@RequestParam("email") String email) throws Exception {
        userService.mail(email);
        return ExceptionUtil.Success(200,"成功");
    }

    //图片验证码
    @RequestMapping("/captcha/password_code.jpg")
    public void PasswordCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-cache");
        String password_code = ValidateCode.generateTextCode(ValidateCode.TYPE_ALL_MIXED, 6, null);
        request.getSession().setAttribute("password_code", password_code);
        response.setContentType("image/jpeg");
        BufferedImage bim = ValidateCode.generateImageCode(password_code, 135, 30, 10, true, Color.WHITE, Color.BLUE, null);
        ImageIO.write(bim, "JPEG", response.getOutputStream());
    }

    //忘记密码
    @RequestMapping("/password_retrieve")
    public Json RetrievePassWord(@RequestParam("phone")String phone ,@RequestParam("password")String password,@RequestParam("code")String code) throws Exception {
        Session session = SecurityUtils.getSubject().getSession();
        String validateCode = (String) session.getAttribute("password_code");
        if (validateCode == null)
            return ExceptionUtil.error(500,"请获取验证码");
        if (code == null || code.equals("")) {
            return ExceptionUtil.error(500, "验证码不能是空");
        }
        if (!validateCode.equals(code))
            return ExceptionUtil.error(500,"错误的验证码");
        return ExceptionUtil.Success(200,"成功",userService.RetrievePassWord(phone,password));
    }

    //修改密码
    @RequiresPermissions(value = {"user:install","admin:install"},logical = Logical.OR)
    @RequestMapping("/password_update")
    public Json PasswordUpdate(@RequestParam("password")String password,@RequestParam("pass")String pass,@RequestParam("code")String code) throws Exception {
        Session session = SecurityUtils.getSubject().getSession();
        String validateCode = (String) session.getAttribute("password_code");
        if (validateCode == null)
            return ExceptionUtil.error(500,"请获取验证码");
        if (code == null || code.equals("")) {
            return ExceptionUtil.error(500, "验证码不能是空");
        }
        if (!validateCode.equals(code))
            return ExceptionUtil.error(500,"错误的验证码");
        return ExceptionUtil.Success(200,"成功",userService.PassWordUpdate(password,pass));
    }

    //接受连接
    @RequestMapping("/password_r")
    public Json PasswordR(@RequestParam("phone")String phone,@RequestParam("password")String password,@RequestParam("salt")String salt,@RequestParam("date")String date) throws Exception {
        return ExceptionUtil.Success(200,"成功",userService.Password(phone,salt,password,date));
    }

    //接受连接
    @RequestMapping("/password_u")
    public Json PassWordUpdate(@RequestParam("phone")String phone,@RequestParam("salt")String salt,@RequestParam("password")String password,@RequestParam("date")String date) throws Exception {
        return ExceptionUtil.Success(200,"成功",userService.Password(phone,salt,password,date));
    }

    //角色获取连接
    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/user_roles")
    public Json UserRoles(){
        return ExceptionUtil.Success(200,"成功",userService.UserRoles());
    }

    //用户头像上传
    @RequiresPermissions(value = {"user:install","admin:install"},logical = Logical.OR)
    @RequestMapping("/user_img")
    public Json UserImg(@RequestParam("file")MultipartFile file) throws Exception {
        return ExceptionUtil.Success(200,"成功",userService.UserImg(file));
    }

    //用户头像显示
    @RequiresPermissions(value = {"user:install","admin:install"},logical = Logical.OR)
    @RequestMapping("/user_img/{name}")
    public void UserImgInfo(HttpServletResponse response, @PathVariable("name")String name){
        FileInputStream fis = null;
        response.setContentType("image/gif");
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(LoadFile.Path()+"/img/user/"+name);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.error("显示图片发生了异常");
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
    //用户实名认证
    @PostMapping("/card_img")
    public Json cardImgs(MultipartFile fileA,MultipartFile fileB,String s) throws Exception {
        return ExceptionUtil.Success(200,"成功",userService.IDcardImg(fileA,fileB,s));
    }


    //用户身份证显示
    @RequestMapping("/card_img/{catd}/{name}")
    public void cardImg(HttpServletResponse response,@PathVariable("card") String card,@PathVariable("name") String name){
        FileInputStream fis = null;
        response.setContentType("image/gif");
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(LoadFile.Path()+"/img/user/"+card+"/"+name);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.error("显示图片发生了异常");
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

    //图片验证码
    @RequestMapping("/captcha/wallet_code.jpg")
    public void validateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-cache");
        String SinUpCode = ValidateCode.generateTextCode(ValidateCode.TYPE_ALL_MIXED, 6, null);
        request.getSession().setAttribute("wallet_code", SinUpCode);
        response.setContentType("image/jpeg");
        BufferedImage bim = ValidateCode.generateImageCode(SinUpCode, 135, 30, 10, true, Color.WHITE, Color.BLUE, null);
        ImageIO.write(bim, "JPEG", response.getOutputStream());
    }

    //发送短信验证码
    @RequestMapping("/captcha/wallet")
    public Json WalletCode(@RequestParam("code")String code, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Cache-Control", "no-cache");
        String walletCode = ValidateCode.generateTextCode(ValidateCode.TYPE_NUM_ONLY, 6, null);
        request.getSession().setAttribute("walletCode", walletCode);
        //验证图片验证码
        Session session = SecurityUtils.getSubject().getSession();
        String validateCode = (String) session.getAttribute("wallet_code");
        if (code == null || code.equals("")) {
            return ExceptionUtil.error(500, "验证码不能是空");
        }
        if (validateCode != null) {
            code = code.toLowerCase();
            validateCode = validateCode.toLowerCase();
            if (!code.equals(validateCode)) {
                return ExceptionUtil.error(500, "验证码错误");
            }
        }
        //短信发送
//        log.info("短信发送记录："+ HttpUtil.doPost("http://smssh1.253.com/msg/send/json","{\"account\" : \"N8178610\",\"password\" : \"l2c7tWCnx\",\"msg\" : \"【蚂蚁区块链】您的注册验证码是："+walletCode+"，有效时间10分钟\", \"phone\" : \""+(String) SecurityUtils.getSubject().getPrincipal()+"\"}"));
        log.info("【"+(String) SecurityUtils.getSubject().getPrincipal()+"】的短信验证码： ");
        return ExceptionUtil.Success(200,"成功");
    }

    //用户添加钱包修改钱包
    @RequestMapping("/wallet")
    public Json Wallet(@RequestParam("type")String type,@RequestParam("wallet")String wallet,@RequestParam("code")String code){
        //验证短信验证码
        if (code == null && code.equals(""))
            return ExceptionUtil.error(500,"短信验证码不能为空");
        //获取正确的验证码
        Session session = SecurityUtils.getSubject().getSession();
        String validateCode = (String) session.getAttribute("walletCode");
        if (validateCode != null) {
            code = code.toLowerCase();
            validateCode = validateCode.toLowerCase();
            if (!code.equals(validateCode)) {
                return ExceptionUtil.error(500, "短信验证码错误");
            }
        }
        return ExceptionUtil.Success(200,"成功",userService.Wallet(type,wallet));
    }

    @RequiresPermissions(value = {"admin:install","finance:install"})
    @PostMapping("/card_s")
    public Json CardS(String phone,Boolean b){
        return ExceptionUtil.Success(200,"成功",userService.CardImg(phone,b));
    }

    @RequiresPermissions("admin:select")
    @PostMapping("/roles_one")
    public Json PostrolesInfo(@RequestParam("phone") String phone){
        return ExceptionUtil.Success(200,"成功",userService.RolesOne(phone));
    }

    @RequiresPermissions("admin:select")
    @PostMapping("/realm_one")
    public Json PostRealmInfo(@Valid List roles){
        return ExceptionUtil.Success(200,"成功",userService.RealmOne(roles));
    }

    @RequiresPermissions("admin:select")
    @RequestMapping("/user_all_date")
    public Json UserAll(){
        return ExceptionUtil.Success(200,"成功",userService.UserAll());
    }

    @RequiresPermissions(value = {"admin:install","admin:update"},logical = Logical.AND)
    @PostMapping("/user_admin_add")
    public Json UserAdd(@Valid User user){
        return ExceptionUtil.Success(200,"成功",userService.UserAdd(user));
    }


}
