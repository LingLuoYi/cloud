package com.henglong.cloud.config.shiro;

import com.henglong.cloud.dao.RealmJpa;
import com.henglong.cloud.dao.RolesJpa;
import com.henglong.cloud.dao.UserJpa;
import com.henglong.cloud.entity.Realm;
import com.henglong.cloud.entity.Roles;
import com.henglong.cloud.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class RealmConfig extends AuthorizingRealm {

    private static final Logger log= LoggerFactory.getLogger(RealmConfig.class);

    @Autowired
    private UserJpa userJpa;

    @Autowired
    private RolesJpa rolesJpa;

    @Autowired
    private RealmJpa realmJpa;

    @Override
    //配置权限
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取登录用户信息
        String phone = (String)principalCollection.getPrimaryPrincipal();
        List<String> userRoles = new ArrayList<String>();
        List<String> userPermissions = new ArrayList<String>();
        //查询角色
        List<Roles> roles = rolesJpa.findByPhone(phone);
        if (roles != null){
            for(int i=0; i<roles.size();i++){
                List<Realm> realms = realmJpa.findByRoles(roles.get(i).getRoles());
                //添加角色
                userRoles.add(roles.get(i).getRoles());
                for (int j=0;j<realms.size();j++){
                    //添加权限
                    userPermissions.add(realms.get(j).getRealm());
                }
            }
        }else{
            throw new AuthorizationException("获取【"+phone+"】权限失败！！");
        }
        //为当前用户设置角色和权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(userRoles);
        authorizationInfo.addStringPermissions(userPermissions);
        return authorizationInfo;
    }

    @Override
    //验证登录
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String phone = (String)authenticationToken.getPrincipal();
        User user = userJpa.findByPhone(phone);
        if (user == null) {
            throw new UnknownAccountException();
        }else {
            log.info("手机号码【" + user.getPhone() + "】请求登录");
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                    user.getPhone(), //手机号
                    user.getPassword(), //数据库查询到的密码
                    ByteSource.Util.bytes(user.getSalt()),//salt=username+salt,计算密码
                    getName()  //realm name
            );
            return authenticationInfo;
        }
    }
}
