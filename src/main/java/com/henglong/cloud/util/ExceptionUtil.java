package com.henglong.cloud.util;

public class ExceptionUtil {
    public static Json Success(Integer state,String msg,Object o){
        Json json = new Json();
        json.setState(state);
        json.setMsg(msg);
        json.setDate(o);
        return json;
    }

    public static Json Success(Integer state,String msg){
        Json json = new Json();
        json.setState(state);
        json.setMsg(msg);
        return json;
    }

    public static Json error(Integer state,String msg){
        Json json = new Json();
        json.setState(state);
        json.setMsg(msg);
        return json;
    }

    public static Json error(Integer state,String msg, Object o){
        Json json = new Json();
        json.setState(state);
        json.setMsg(msg);
        json.setDate(o);
        return json;
    }

    public static  PrInfo Info(Object o){
        PrInfo info = new PrInfo();
        info.setCode(0);
        info.setPmsg("成功");
        info.setData(o);
        return info;
    }

    public static  PrInfo EInfo(Integer s,String msg){
        PrInfo info = new PrInfo();
        info.setCode(s);
        info.setPmsg(msg);
        info.setData("");
        return info;
    }

    public static PrInfo EInfos(Integer s ,String msg ,Object o){
        PrInfo info = new PrInfo();
        info.setCode(s);
        info.setPmsg(msg);
        info.setData(o);
        return info;
    }

}
