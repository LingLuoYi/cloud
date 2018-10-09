package com.henglong.cloud.service;

import com.henglong.cloud.dao.RolesJpa;
import com.henglong.cloud.dao.UserJpa;
import com.henglong.cloud.entity.Roles;
import com.henglong.cloud.entity.Spread;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.util.FileConfig;
import com.henglong.cloud.util.MyMd5;
import com.henglong.cloud.util.MySalt;
import com.henglong.cloud.util.Regular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {

    private static Logger log = LoggerFactory.getLogger(SignUpService.class);

    @Autowired
    private MyMd5 myMd5;

    @Autowired
    private MySalt mySalt;

    @Autowired
    private UserJpa userJpa;

    @Autowired
    private RolesJpa rolesJpa;

    @Autowired
    private SpreadService spreadService;


    public Integer SignUp(String phone, String password, String code) {
        log.info("注册用户【" + phone + "】！");
        User user = new User();
        Roles roles = new Roles();
//        if (Regular.isPhone(phone))
//            return 4;
        if (phone == "")
            return 1;
        if (password == "")
            return 2;
        String salt = myMd5.Md5(mySalt.Salt(Integer.valueOf(FileConfig.OutputPath("encryption-salt", "8"))), Integer.valueOf(FileConfig.OutputPath("encryption", "1024")));
        //加密密码
        String pass = myMd5.Md5(password, salt, Integer.valueOf(FileConfig.OutputPath("encryption", "1024")));
        //查数据库，看是否有注册
        if (userJpa.findByPhone(phone) != null) {
            log.error("重复注册！！【" + phone + "】");
            return 3;
        }
        //写入登录信息
        log.info("用户【\"+phone+\"】，写入用户登录信息");
        user.setPhone(phone);
        user.setPassword(pass);
        user.setSalt(salt);
        //写入用户实名权限
        log.info("用户【"+phone+"】，写入实名权限");
        user.setUserStart(2);
        //写入角色
        log.info("用户【\"+phone+\"】，写入用户权限");
        roles.setPhone(phone);
        roles.setRoles("user");
        //判断是否有推广联系人
        if (code != "") {
            Spread spread = spreadService.SpreadInfo(code);
            if (spread != null) {
                log.info("用户【\"+phone+\"】，注册用户推荐人【" + spread.getSpreadPhone() + "】");
                spreadService.SpreadNum(spread.getSpreadNum() + 1, spread.getSpreadPromoCode());
                spreadService.Spreads(phone, code);
            }
        } else {
            spreadService.Spreads(phone, "");
            log.info("用户【\"+phone+\"】，没有填写推荐码");
        }
        //不管用户是不是被推荐都写入推荐码
        rolesJpa.save(roles);
        userJpa.save(user);
        return 0;
    }
}
