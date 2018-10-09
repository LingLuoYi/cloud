package com.henglong.cloud.controller;

import com.henglong.cloud.entity.User;
import com.henglong.cloud.service.SignUpService;
import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.HttpUtil;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.ValidateCode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@CrossOrigin(allowCredentials="true")
@RestController
public class SignupController {

    private final static Logger log = LoggerFactory.getLogger(SignupController.class);

    @Autowired
    private SignUpService signUpService;

    //获取验证码普通图片
    @RequestMapping("/captcha/sig_up_code.jpg")
    public void validateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-cache");
        String SinUpCode = ValidateCode.generateTextCode(ValidateCode.TYPE_ALL_MIXED, 6, null);
        request.getSession().setAttribute("SigUpCode", SinUpCode);
        response.setContentType("image/jpeg");
        BufferedImage bim = ValidateCode.generateImageCode(SinUpCode, 135, 30, 10, true, Color.WHITE, Color.BLUE, null);
        ImageIO.write(bim, "JPEG", response.getOutputStream());
    }

    //获取短信验证码
    @RequestMapping("/captcha/{phone}")
    public Json PhoneCode(@PathVariable("phone")String phone,@RequestParam("code")String code,HttpServletRequest request, HttpServletResponse response)throws Exception {
        response.setHeader("Cache-Control", "no-cache");
        String PhoneCode = ValidateCode.generateTextCode(ValidateCode.TYPE_NUM_ONLY, 6, null);
        request.getSession().setAttribute("PhoneCode", PhoneCode);
        //验证图片验证码
        Session session = SecurityUtils.getSubject().getSession();
        String validateCode = (String) session.getAttribute("SigUpCode");
        if (code == null || code.equals("")) {
            return ExceptionUtil.error(500, "验证码不能是空");
        }
        if (validateCode == null)
            return ExceptionUtil.error(500,"请获取验证码");
        if (validateCode != null) {
            code = code.toLowerCase();
            validateCode = validateCode.toLowerCase();
            if (!code.equals(validateCode)) {
                session.removeAttribute("SigUpCode");
                return ExceptionUtil.error(500, "验证码错误");
            }
        }
        //发送短信
//        log.info("短信发送记录："+HttpUtil.doPost("http://smssh1.253.com/msg/send/json","{\"account\" : \"N8178610\",\"password\" : \"l2c7tWCnx\",\"msg\" : \"【蚂蚁区块链】您的注册验证码是："+PhoneCode+"，有效时间10分钟\", \"phone\" : \""+phone+"\"}"));
        log.info("用户【"+phone+"】短信验证码是："+PhoneCode);
        return ExceptionUtil.Success(200,"成功",PhoneCode);
    }

    @GetMapping("/sign_up")
    public Json GetSignup(){
        log.warn("有人试图用GET方法注册");
        return ExceptionUtil.error(200,"GETGETGETGETGETGETGETGETGETGETGETGET");
    }

    @PostMapping("/sign_up/{code}")
    public Json PostSignup(@Valid User user,@PathVariable(value = "code",required = false)String code,@RequestParam("phoneCode")String phoneCode){
        //验证短信验证码
        Session session = SecurityUtils.getSubject().getSession();
        String codes = (String) session.getAttribute("PhoneCode");
        if (phoneCode == null && phoneCode.equals("")){
            return ExceptionUtil.error(500, "短信验证码不能为空");
        }
        if (phoneCode == null)
            return ExceptionUtil.error(500,"请获取短信验证码");
        if (phoneCode != null) {
            codes = codes.toLowerCase();
            phoneCode = phoneCode.toLowerCase();
            if (!code.equals(phoneCode)) {
                session.removeAttribute("PhoneCode");
                return ExceptionUtil.error(500, "短信验证码错误");
            }
        }
        log.info("有人注册啦！【"+user.getPhone()+"】");
        return ExceptionUtil.Success(200,"请求成功",signUpService.SignUp(user.getPhone(),user.getPassword(),code));
    }

    @PostMapping("/sign_up")
    public Json PostSignup(@Valid User user,@RequestParam("phoneCode")String phoneCode,HttpServletRequest request, HttpServletResponse response){
        //验证短信验证码
        Session session = SecurityUtils.getSubject().getSession();
        String code = (String) session.getAttribute("PhoneCode");
        log.info("用户【"+user.getPhone()+"】，正确的短信验证码是【"+code+"】，获取到的短信验证码是【"+phoneCode+"】");
        if (phoneCode == null && phoneCode.equals("")){
            return ExceptionUtil.error(500, "短信验证码不能为空");
        }
        if (code == null)
            return ExceptionUtil.error(500,"请获取短信验证码");
        if (code != null) {
            code = code.toLowerCase();
            phoneCode = phoneCode.toLowerCase();
            if (!code.equals(phoneCode)) {
                session.removeAttribute("PhoneCode");
                return ExceptionUtil.error(500, "短信验证码错误");
            }
        }
        log.info("有人注册啦！【"+user.getPhone()+"】");
        return ExceptionUtil.Success(200,"请求成功",signUpService.SignUp(user.getPhone(),user.getPassword(),""));
    }

}
