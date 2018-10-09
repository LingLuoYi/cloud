package com.henglong.cloud.Exception;

import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.Json;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.authc.AccountException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class WordException extends RuntimeException {

    private final static Logger log = LoggerFactory.getLogger(WordException.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Json AntiEvil(Exception e){
        if (e.getMessage()!= null)
        if (e.getMessage().indexOf("Subject does not have permission")!=-1){
            log.warn("用户【"+(String) SecurityUtils.getSubject().getPrincipal()+"】请求了一个超越自己权限的连接");
            return ExceptionUtil.error(500,"没有权限");
        }else if (e.getMessage().indexOf("Required String parameter")!=-1){
            log.warn("错误的接口调用");
            return ExceptionUtil.error(500,"错误的接口调用：",e.getMessage());
        }else if (e.getMessage().indexOf("Request method")!=-1){
            log.warn("错误的请求方式");
            return ExceptionUtil.error(500,"错误的请求方式！",e.getMessage());
        }
        log.error("程序运行出现了一次逻辑异常",e);
        return ExceptionUtil.error(-1,e.getMessage());
    }

    @ExceptionHandler(value = AccountException.class)
    @ResponseBody
    public Json handleShiroException(Exception ex) {
        log.error("发生了一次异常，具体信息为如下",ex);
        return ExceptionUtil.error(-1,ex.getMessage());
    }
}
