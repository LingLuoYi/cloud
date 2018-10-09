package com.henglong.cloud.controller;

import com.henglong.cloud.config.redis.RedisSessionDao;
import com.henglong.cloud.config.shiro.NoPasswordToken;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.service.StaticService;
import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.ValidateCode;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;


@CrossOrigin(allowCredentials = "true")
@RestController
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StaticService staticService;

    @Autowired
    private RedisSessionDao redisSessionDao;

    //生成验证码图片
    @RequestMapping("/captcha/login_code.jpg")
    public void validateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-cache");
        String verifyCode = ValidateCode.generateTextCode(ValidateCode.TYPE_ALL_MIXED, 6, null);
        request.getSession().setAttribute("LoginCode", verifyCode);
        response.setContentType("image/jpeg");
        BufferedImage bim = ValidateCode.generateImageCode(verifyCode, 135, 30, 10, true, Color.WHITE, Color.BLUE, null);
        ImageIO.write(bim, "JPEG", response.getOutputStream());
    }

    @GetMapping("/login")
    public Json GetLogin() {
        return ExceptionUtil.Success(200, "GET登录？");
    }

    @PostMapping("/login")
    public Json PostLogin(User user, ServletRequest request, String code, BindingResult bindingResult) {
        Json json = new Json();
        if (bindingResult.hasErrors()) {
            return ExceptionUtil.Success(500, "登录失败");
        }
        //获取正确的验证码
        Session session = SecurityUtils.getSubject().getSession();
        String validateCode = (String) session.getAttribute("LoginCode");
        log.info("当前登录用户【" + user.getPhone() + "】，验证码为【" + validateCode + "】");
        //校验
        if (code == null || code.equals("")) {
            json.setState(500);
            json.setMsg("验证码不能为空");
            json.setDate(null);
            return json;
        }
        if (validateCode == null)
            return ExceptionUtil.error(500, "请获取验证码");
        if (validateCode != null) {
            code = code.toLowerCase();
            validateCode = validateCode.toLowerCase();
            if (!code.equals(validateCode)) {
                session.removeAttribute("LoginCode");
                json.setState(500);
                json.setMsg("验证码不正确");
                json.setDate(null);
                return json;
            }
        }

        //获取登录名
        String phone = user.getPhone();
        log.info("这里是Controller,接受到用户" + phone);
        NoPasswordToken token = new NoPasswordToken(user.getPhone(), user.getPassword());
        Subject currentUser = SecurityUtils.getSubject();
        try {
            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应
            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
            log.info("对用户[" + phone + "]进行登录验证..验证开始");
            //防止重复登录
            currentUser.login(token);
            staticService.Static();
            log.info("对用户[" + phone + "]进行登录验证..验证通过");
        } catch (UnknownAccountException uae) {
            log.info("对用户[" + phone + "]进行登录验证..验证未通过,未知账户");
            json.setState(500);
            json.setMsg("未知账户");
            json.setDate(null);
        } catch (IncorrectCredentialsException ice) {
            log.info("对用户[" + phone + "]进行登录验证..验证未通过,错误的凭证");
            json.setState(500);
            json.setMsg("密码不正确");
            json.setDate(null);
        } catch (LockedAccountException lae) {
            log.info("对用户[" + phone + "]进行登录验证..验证未通过,账户已锁定");
            json.setState(500);
            json.setMsg("账户已锁定");
            json.setDate(null);
        } catch (ExcessiveAttemptsException eae) {
            log.info("对用户[" + phone + "]进行登录验证..验证未通过,错误次数过多");
            json.setState(500);
            json.setMsg("用户名或密码错误次数过多");
            json.setDate(null);
        } catch (AuthenticationException ae) {
            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
            log.info("对用户[" + phone + "]进行登录验证..验证未通过,堆栈轨迹如下");
            ae.printStackTrace();
            json.setState(500);
            json.setMsg("用户名或密码不正确");
            json.setDate(null);
        }
        //验证是否登录成功
        if (currentUser.isAuthenticated()) {
            log.info("检查用户【"+phone+"】，是否重复登录");
            for (Session sessionsID : getLoginedSession(currentUser)) {
                sessionsID.stop();
                redisTemplate.delete("CLOUD_"+sessionsID.getId());
//                log.info("删除Session：CLOUD_"+sessionsID.getId());
                log.info("其他设备以踢下线！");
            }
            log.info("用户[" + phone + "]登录认证通过");
            json.setState(200);
            json.setMsg("登录成功");
            json.setDate(null);
            session.removeAttribute("LoginCode");
            return json;
        } else {
            token.clear();
            session.removeAttribute("LoginCode");
            return json;
        }
    }

    //获取登录验证码连接在此
    @RequestMapping("/get_login_code")
    public Json GetLoginCode(HttpServletRequest request, HttpServletResponse response, String code, String phone) {
        //生成短信验证码之前是不是要校验下图片验证码？
        Session session = SecurityUtils.getSubject().getSession();
        String validateCode = (String) session.getAttribute("LoginCode");
        log.info("当前请求用户【"+phone+"】，图片验证码【"+validateCode+"】，用户输入的验证码【"+code+"】");
        //校验
        if (code == null || code.equals("")) {
            return ExceptionUtil.Success(500, "验证码不能为空", "咋想的？");
        }
        if (validateCode == null)
            return ExceptionUtil.error(500, "请获取验证码");
        if (validateCode != null) {
            code = code.toLowerCase();
            validateCode = validateCode.toLowerCase();
            if (!code.equals(validateCode)) {
                session.removeAttribute("LoginCode");
                return ExceptionUtil.Success(500, "验证码错误", "你搞错啦");
            }
        }
        response.setHeader("Cache-Control", "no-cache");
        String verifyCode = ValidateCode.generateTextCode(ValidateCode.TYPE_ALL_MIXED, 8, null);
        request.getSession().setAttribute("LoginPhoneCode", verifyCode);
        //发送短信
        //                    log.info("短信发送记录："+HttpUtil.doPost(FileConfig.OutputPath("SMS-API","http://smssh1.253.com/msg/send/json"),
//                            FileConfig.getSubUtilSimple("SMS-content-code",
//                                    "{\"account\" : \"N8178610\",\"password\" : \"l2c7tWCnx\",\"msg\" : \"【蚂蚁区块链】您的登录验证码是：{code}，有效时间10分钟\", \"phone\" : \"{phone}\"}")
//                                    .replace("{code}",code)
//                                    .replace("{phone}",phone)));
        return ExceptionUtil.Success(200, "成功", verifyCode);
    }

    @PostMapping("/login_msg")
    public Json PostMsgLogin(User user, HttpServletRequest request, String phoneCode, BindingResult bindingResult) {
        Json json = new Json();
        if (bindingResult.hasErrors()) {
            return ExceptionUtil.Success(500, "登录失败");
        }
        //获取正确的验证码
        Session session = SecurityUtils.getSubject().getSession();
        String validateCode = (String) session.getAttribute("LoginPhoneCode");
        log.info("当前登录用户【" + user.getPhone() + "】，短信验证码为【" + validateCode + "】");
        //校验
        if (phoneCode == null || phoneCode.equals("")) {
            json.setState(500);
            json.setMsg("验证码不能为空");
            json.setDate("输入了可能会错，不输入铁定会错<(‵^′)>");
            return json;
        }
        if (validateCode == null)
            return ExceptionUtil.error(500, "你先获取个验证码要不要得");
        if (validateCode != null) {
            phoneCode = phoneCode.toLowerCase();
            validateCode = validateCode.toLowerCase();
            if (!phoneCode.equals(validateCode)) {
                session.setAttribute("LoginPhoneCode","alkdgjgdjfsgisdrioegjjdfjksgheiwoarhghsdklfuhgera");
                json.setState(500);
                json.setMsg("验证码不正确");
                json.setDate("在好好看看手机╯▽╰");
                return json;
            }else if (validateCode.equals("alkdgjgdjfsgisdrioegjjdfjksgheiwoarhghsdklfuhgera")){
                return ExceptionUtil.error(500,"验证码已过期，请重新获取验证码");
            }
        }
        //获取登录名
        String iphone = user.getPhone();
        log.info("这里是Controller,接受到用户" + iphone);
        NoPasswordToken token = new NoPasswordToken(iphone);
        Subject currentUser = SecurityUtils.getSubject();
        try {
            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应
            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
            log.info("对用户[" + iphone + "]进行登录验证..验证开始");
            currentUser.login(token);
            staticService.Static();
            log.info("对用户[" + iphone + "]进行登录验证..验证通过");
        } catch (UnknownAccountException uae) {
            log.info("对用户[" + iphone + "]进行登录验证..验证未通过,未知账户");
            json.setState(500);
            json.setMsg("不对，你还没有注册！");
            json.setDate(null);
        } catch (IncorrectCredentialsException ice) {
            log.info("对用户[" + iphone + "]进行登录验证..验证未通过,错误的凭证");
            json.setState(500);
            json.setMsg("密码不正确");
            json.setDate(null);
        } catch (LockedAccountException lae) {
            log.info("对用户[" + iphone + "]进行登录验证..验证未通过,账户已锁定");
            json.setState(500);
            json.setMsg("账户已锁定");
            json.setDate(null);
        } catch (ExcessiveAttemptsException eae) {
            log.info("对用户[" + iphone + "]进行登录验证..验证未通过,错误次数过多");
            json.setState(500);
            json.setMsg("用户名或密码错误次数过多");
            json.setDate(null);
        }
        //验证是否登录成功
        if (currentUser.isAuthenticated()) {
            log.info("用户[" + iphone + "]登录认证通过");
            json.setState(200);
            json.setMsg("恭喜你！成功了");
            json.setDate(null);
            session.removeAttribute("LoginPhoneCode");
            for (Session sessionsID : getLoginedSession(currentUser)) {
                sessionsID.stop();
                redisTemplate.delete("CLOUD_"+sessionsID.getId());
//                log.info("删除Session:CLOUD_"+sessionsID.getId());
                log.info("其他设备以踢下线！");
            }

            return json;
        } else {
            token.clear();
            session.removeAttribute("LoginPhoneCode");
            return json;
        }
    }

    private java.util.List<Session> getLoginedSession(Subject currentUser) {
//        Collection<Session> list = ((DefaultSessionManager) ((DefaultSecurityManager) SecurityUtils
//                .getSecurityManager()).getSessionManager()).getSessionDAO()
//                .getActiveSessions();
        Collection<String> list = redisTemplate.keys("CLOUD_*");
        java.util.List<Session> loginedList = new ArrayList<Session>();
        String loginUser = (String) currentUser.getPrincipal();
        log.info("当前key："+loginUser);
        for (String se : list) {
            Subject s = new Subject.Builder().session((Session) redisTemplate.opsForValue().get(se)).buildSubject();
            if (s.isAuthenticated()) {
                String user = (String) s.getPrincipal();
                if (user.equalsIgnoreCase(loginUser)) {
                    if (!((Session) redisTemplate.opsForValue().get(se)).getId().equals(
                            currentUser.getSession().getId())) {
                        loginedList.add((Session) redisTemplate.opsForValue().get(se));
                    }
                }
            }
        }
        return loginedList;
    }

}
