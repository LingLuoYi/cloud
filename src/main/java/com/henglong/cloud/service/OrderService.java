package com.henglong.cloud.service;

import com.henglong.cloud.dao.CommodityJpa;
import com.henglong.cloud.dao.OrderJpa;
import com.henglong.cloud.dao.PayJpa;
import com.henglong.cloud.entity.Commodity;
import com.henglong.cloud.entity.Order;
import com.henglong.cloud.entity.Pay;
import com.henglong.cloud.util.DES;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.List;


/**
 * 生成订单在商品类
 */
@Service
public class OrderService {

    @Autowired
    private OrderJpa orderJpa;

    @Autowired
    private PayJpa payJpa;

    @Autowired
    private CommodityJpa commodityJpa;

    @Autowired
    private DES des;

    //获取当前用户订单信息
    public List<Order> OrderInfo(){
        //获取登录手机号
        String phone=(String) SecurityUtils.getSubject().getPrincipal();
        return orderJpa.findByPhone(phone);
    }

    //获取单个订单（用户）
    public Order OrderOneInfo(String id){
        //获取登录手机号
        String phone=(String) SecurityUtils.getSubject().getPrincipal();
        return orderJpa.findByOrderIdAndPhone(id,phone);
    }

    //关闭订单（用户）
    public Order order(){
        return null;
    }


    //获取所有订单（管理员）
    public  List<Order> OrderAllInfo(){
        return orderJpa.findAll();
    }

    //根据ID查询单个订单（管理员）
    public Order OrderAdminOneInfo(String id){
        return orderJpa.findByOrderId(id);
    }

    //修改订单（用户）
    public Order orderUserUpdate(String id ,String num) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        //获取当前用户登录的用户
        String phone=(String) SecurityUtils.getSubject().getPrincipal();
        //查询定单是否存在
        Order order = orderJpa.findByOrderIdAndPhone(id,phone);
        if (order == null)
            return null;
        //查询支付订单是否生成支付订单,如果存在则不允许修改
        Pay pay = payJpa.findByPayOrderIdAndPayPhone(id,phone);
        if (pay != null)
            return null;
        //查询商品
        Commodity commodity = commodityJpa.findByCommodityId(order.getOrderCommodityId());
        if (commodity == null)
            return null;
        order.setOrderNum(num);
        int a = Integer.valueOf(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityMoney()), "szhl8888")));
        int b = Integer.valueOf(num);
        order.setOrderMoney(String.valueOf(a * b));
        orderJpa.save(order);
        return order;
    }

    //删除订单（用户）
    public Order orderUserDelete(String id){
        //获取当前用户登录的用户
        String phone=(String) SecurityUtils.getSubject().getPrincipal();
        //查询定单是否存在
        Order order = orderJpa.findByOrderIdAndPhone(id,phone);
        if (order == null)
            return null;
        //查询支付订单是否生成支付订单,如果存在则不允许删除
        Pay pay = payJpa.findByPayOrderIdAndPayPhone(id,phone);
        if (pay != null)
            return null;
        orderJpa.delete(order);
        return order;
    }

    //修改订单(管理员)
    //无规则修改
    public Order orderUpdate(Order order){
        //查询传入订单是否存在
        if (orderJpa.findByOrderId(order.getOrderId()) == null)
            return null;
        orderJpa.save(order);
        return order;
    }

    //删除订单(管理员)
    //无规则
    public Order orderDelete(String id){
        Order order = orderJpa.findByOrderId(id);
        if (order == null)
            return null;
        orderJpa.delete(order);
        return order;
    }
}
