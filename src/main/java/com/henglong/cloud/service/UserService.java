package com.henglong.cloud.service;

import com.henglong.cloud.dao.RealmJpa;
import com.henglong.cloud.dao.RolesJpa;
import com.henglong.cloud.dao.UserJpa;
import com.henglong.cloud.entity.Roles;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.util.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private static final String modeurl = LoadFile.Path()+"/mode/";

    @Autowired
    private UserJpa userJpa;

    @Autowired
    private RolesJpa rolesJpa;

    @Autowired
    private RealmJpa realmJpa;

    @Autowired
    private EmailService emailservice;

    @Autowired
    private DES des;

    @Autowired
    private OnlyId onlyId;

    @Autowired
    private MyMd5 myMd5;

    @Autowired
    private MySalt mySalt;

    @Autowired
    private FileService fileService;

    //获取登录用户信息
    public User UserInfo(){
        //获取登录手机号
        String phone=(String) SecurityUtils.getSubject().getPrincipal();
        User user= userJpa.findByPhone(phone);
        return user;
    }

    //更新用户信息
    public User UserAdd(User users){
        //获取手机号
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userJpa.findByPhone(phone);
        user.setName(users.getName());
        user.setEmail(users.getEmail());
        log.info("页面接受的数据"+users.getName()+";"+users.getEmail());
        return userJpa.save(user);
    }

    //发送邮件验证
    public void mail(String email) throws Exception {
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userJpa.findByPhone(phone);
        //生成确认连接
        String url=FileConfig.OutputPath("path","http://127.0.0.1:8080")+"/user/email_user_confirm?";
        /*准备加密*/
        BASE64Decoder decoder = new BASE64Decoder();
        //加密手机号
        url += "phone="+ java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(phone.getBytes("utf-8"),"szhl8888")), "UTF-8");
        //加密邮箱
        url += "&email="+ java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(email.getBytes("utf-8"),"szhl8888")), "UTF-8");
        //加密时间
        url += "&date="+ java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).getBytes("utf-8"),"szhl8888")), "UTF-8") ;
        //获取模板
        String mayi = new String(LoadFile.TemplateLoad(new File(modeurl+"mayi.html")));
        String s = mayi.replace("demo-title", "欢迎你！")
                .replace("demo-text.1", "麒麟算力为你提供不一样的挖矿服务体验！")
                .replace("demo-text.2", "高效率，精准服务一直是我们的标准，等等")
                .replace("demo-text.3", "那么我们开始吧")
                .replace("demo-text.url", url+"&code="+java.net.URLEncoder.encode(onlyId.RandomString(4),"utf-8"))
                .replace("demo-text.4","欢迎你！（请在10分钟内点击）");
        emailservice.mail(email,s,"麒麟算力-身份确认邀请函");
    }

    //接受受邮件验证
    public User MailConfirm(String phone,String email,String date,String code) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        if (phone == null || phone == "")
            return null;
        if (email == null || email == "")
            return null;
        if (date == null || date == "")
            return null;
        if (code == null || code == "")
            return null;
        User user = userJpa.findByPhone(new String(des.decrypt(decoder.decodeBuffer(phone),"szhl8888")));
        if (user == null)
            return null;
        //验证重复连接
        if (user.getRandomCode().equals(code))
            return null;
        //解密传入时间
        SimpleDateFormat simpleFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //
        String toDate = simpleFormat.format(new Date());
        long from = simpleFormat.parse(new String(des.decrypt(decoder.decodeBuffer(date),"szhl8888"))).getTime();
        long to = simpleFormat.parse(toDate).getTime();
        int minutes = (int) ((to - from)/(1000 * 60));
        //
        if (minutes >=30)
            return null;
        user.setEmail(new String(des.decrypt(decoder.decodeBuffer(email),"szhl8888")));
        user.setRandomCode(code);
        userJpa.save(user);
        return user;
    }

    //忘记密码发送短信
    public String RetrievePassWord(String phone,String password) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        //确认账户已经注册
        User user = userJpa.findByPhone(phone);
        if(user == null)
            return null;
        //生成重置连接
        String url = FileConfig.OutputPath("path","http://127.0.0.1:8080")+"/user/password_r?";
        //加密手机号
        url += "phone="+java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(phone.getBytes("utf-8"),"szhl8888")));
        //拼接随机码
        url += "&salt="+java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(user.getSalt().getBytes("utf-8"),"szhl8888")));
        //拼接密码
        url += "&password="+java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(password.getBytes("utf-8"),"szhl8888")));
        //拼接时间
        url += "&date="+java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).getBytes("utf-8"),"szhl8888")));
        //发送地址给用户
        //发送短信方法，测试版默认注销
//        log.info("短信发送记录："+HttpUtil.doPost(FileConfig.OutputPath("SMS-API","http://smssh1.253.com/msg/send/json"),
//                FileConfig.getSubUtilSimple("SMS-content",
//                        "{\"account\" : \"N8178610\",\"password\" : \"l2c7tWCnx\",\"msg\" : \"【蚂蚁区块链】您的密码重置连接是：{url}，有效时间10分钟\", \"phone\" : \"{phone}\"}")
//                        .replace("{url}",url)
//                        .replace("{phone}",phone)));
        log.info("用户【"+phone+"】的密码重置连接："+url);
        return url;
    }

    //修改密码发送
    public String PassWordUpdate(String password,String pass) throws Exception {
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
            User user = userJpa.findByPhone(phone);
            if (user == null)
                return null;
            //使用非对称加密密码
            String pas=myMd5.Md5(password,user.getSalt(),Integer.valueOf(FileConfig.OutputPath("encryption","1024")));
            //验证密码是否正确
            if (!pas.equals(user.getPassword()))
                return null;
        //验证用户是否有邮箱
        if (!user.getEmail().equals("")) {
            //拼接url
            String url = FileConfig.OutputPath("path","http://127.0.0.1:8080")+"/user/password_u?"
                    + "phone=" + java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(user.getPhone().getBytes("utf-8"), "szhl8888")), "utf-8")
                    + "&salt=" + java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(user.getSalt().getBytes("utf-8"), "szhl8888")), "utf-8")
                    + "&password=" + java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(pass.getBytes("utf-8"), "szhl8888")), "utf-8")
                    + "&date=" + java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).getBytes("utf-8"), "szhl8888")), "UTF-8");
            String mayi = new String(LoadFile.TemplateLoad(new File(modeurl+"mayi.html")));
            String s = mayi.replace("demo-title", "麒麟算力-密码重置")
                    .replace("demo-text.1", "麒麟算力为你提供不一样的挖矿服务体验！")
                    .replace("demo-text.2", "高效率，精准服务一直是我们的标准，等等")
                    .replace("demo-text.3", "你的密码重置连接(10分钟内有效)")
                    .replace("demo-text.url", url)
                    .replace("demo-text.4", "重置！");
            log.info("邮箱发送"+url);
            emailservice.mail(user.getEmail(), s, "麒麟算力-修改你的密码");
            return "成功";
        }else {
            //拼接url
            String url = "http://127.0.0.1:8080/user/password_u?"
                    + "phone=" + java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(user.getPhone().getBytes("utf-8"), "szhl8888")), "utf-8")
                    + "&salt=" + java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(user.getSalt().getBytes("utf-8"), "szhl8888")), "utf-8")
                    + "&password=" + java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(pass.getBytes("utf-8"), "szhl8888")), "utf-8")
                    + "&date=" + java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).getBytes("utf-8"), "szhl8888")), "UTF-8");
//                    log.info("短信发送记录："+HttpUtil.doPost(FileConfig.OutputPath("SMS-API","http://smssh1.253.com/msg/send/json"),
//                            FileConfig.getSubUtilSimple("SMS-content",
//                                    "{\"account\" : \"N8178610\",\"password\" : \"l2c7tWCnx\",\"msg\" : \"【蚂蚁区块链】您的密码重置连接是：{url}，有效时间10分钟\", \"phone\" : \"{phone}\"}")
//                                    .replace("{url}",url)
//                                    .replace("{phone}",phone)));
            log.info("用户【"+phone+"】的密码重置连接："+url);
            return url;
        }
    }

    //修改密码和忘记密码连接验证
    public String Password(String phone,String salt, String password,String date) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        if (date == "")
            return "请不要修改链接！";
        SimpleDateFormat simpleFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String toDate = simpleFormat.format(new Date());
        long from = simpleFormat.parse(new String(des.decrypt(decoder.decodeBuffer(date),"szhl8888"))).getTime();
        long to = simpleFormat.parse(toDate).getTime();
        int minutes = (int) ((to - from)/(1000 * 60));
        //找回密码
        if (phone != "" && password != "" && salt != ""){
            //验证连接正确性
            //查询用户是否存在
            User user = userJpa.findByPhone(new String(des.decrypt(decoder.decodeBuffer(phone),"szhl8888")));
            if (user == null)
                return "用户不存在";
            //验证SALT是否正确
            if (!user.getSalt().equals(new String(des.decrypt(decoder.decodeBuffer(salt),"szhl8888"))))
                return "请不要修改链接！";
            //验证连接是否过期
            if (minutes >Integer.valueOf(FileConfig.OutputPath("minutes","10")))
                return "链接过期";
            //如果都通过，则开始加密密码，写入数据库
            //更新salt
            String salts = myMd5.Md5(mySalt.Salt(Integer.valueOf(FileConfig.OutputPath("encryption-salt","8"))),Integer.valueOf(FileConfig.OutputPath("encryption","1024")));
            //加密密码
            String pas=myMd5.Md5(new String(des.decrypt(decoder.decodeBuffer(password),"szhl8888")),salts,Integer.valueOf(FileConfig.OutputPath("encryption","1024")));
            //写入数据库
            user.setSalt(salts);
            user.setPassword(pas);
            userJpa.save(user);
            return "成功";
        }
        return "失败";
    }

    //用户修改钱包
    public User Wallet(String type,String wallet){
        //获取手机号
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        //查询用户
        User user = userJpa.findByPhone(phone);
        if (user == null)
            return null;
        //更新钱包
        Map map = new HashMap();
        map.put(type,wallet);
        user.setWallet(map);
        return userJpa.save(user);
    }


    //角色获取（用户）
    public List<Roles> UserRoles(){
        //获取手机号
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        //查询角色
       List<Roles> roles = rolesJpa.findByPhone(phone);
       if (roles == null)
        return null;
       return roles;
    }

    //用户头像上传
    public String UserImg(MultipartFile file) throws Exception {
        return fileService.UserFile(file);
    }

    //用户实名接口
    public String IDcardImg(MultipartFile fileA,MultipartFile fileB,String s) throws Exception {
        return fileService.IDCardImg(fileA,fileB,s);
    }


    //以下是管理员方法

    //获取全部用户信息
    @RequiresRoles("admin")
    public List<User> UserAll(){
        return userJpa.findAll();
    }

    //获取单个用户信息
    @RequiresRoles("admin")
    public User UserOne(Integer id){
        Optional<User> user = userJpa.findById(id);
        return user.orElse(null);
    }

    //获取角色信息
    @RequiresRoles("admin")
    public List<Roles> RolesOne(String phone){
        if (Regular.isPhone(phone))
            return rolesJpa.findByPhone(phone);
        return null;
    }

    //获取权限信息
    @RequiresRoles("admin")
    public List RealmOne(List<Roles> roles){
        List realm = new ArrayList();
        for (int i = 0;i < roles.size(); i++){
            realm.add(realmJpa.findByRoles(roles.get(i).getRoles()));
        }
        return realm;
    }

    //更新角色信息
    @RequiresRoles("admin")
    public Roles RolesUpdate(Roles roles){
        if (userJpa.findByPhone(roles.getPhone())!=null)
            return rolesJpa.save(roles);
        return null;
    }

    //添加角色信息
    @RequiresRoles("admin")
    public Roles RolesInstall(Roles roles){
        return rolesJpa.save(roles);
    }

    //用户审核实名的方法
    public String CardImg(String phone,Boolean b){
        User user = userJpa.findByPhone(phone);
        if (b){
            if (user == null)
                return "未查询到用户！";
            user.setUserStart(0);
            userJpa.save(user);
            return "成功，已通过";
        }else {
            user.setUserStart(4);
            userJpa.save(user);
            return "成功，已拒绝";
        }
    }

}
