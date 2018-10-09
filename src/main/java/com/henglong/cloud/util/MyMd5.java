package com.henglong.cloud.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Service;

@Service
public class MyMd5 {

    public String Md5(String password,String salt,Integer n){
        return new SimpleHash("MD5", password, salt, n).toHex();
    }

    public String Md5(String password,Integer n){
        return new SimpleHash("MD5", password, null, n).toHex();
    }
}
