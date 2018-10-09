package com.henglong.cloud.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileConfig {

    private static final String path = LoadFile.Path();

    private static final Logger log = LoggerFactory.getLogger(FileConfig.class);

    //读取配置变量
    public static String OutputPath(String name,String value){
        //读取文件
        String source = LoadFile.TemplateLoad(new File(path+"/config/cloud.config"));
        if (source.equals("")){
//            log.warn("配置文件异常！！");
        }
        //正则匹配
        String rgex = "<"+name+">(.*?)</"+name+">";
        if (!getSubUtilSimple(source,rgex).equals("")) {
            log.warn("配置参数【" + name + "】，异常！将使用默认值！");
            value = getSubUtilSimple(source, rgex);
        }
        return value;
    }


    /**
     * 返回单个字符串，若匹配到多个的话就返回第一个
     * @param soap
     * @param rgex
     * @return
     */
    public static String getSubUtilSimple(String soap,String rgex) {
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            return m.group(1);
        }
        return "";
    }
}
