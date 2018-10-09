package com.henglong.cloud.service;

import com.henglong.cloud.dao.*;
import com.henglong.cloud.entity.*;
import com.henglong.cloud.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class PayService {

    private static final Logger log = LoggerFactory.getLogger(PayService.class);

    @Autowired
    private OrderJpa orderJpa;

    @Autowired
    private UserJpa userJpa;

    @Autowired
    private PayJpa payJpa;

    @Autowired
    private OnlyId onlyId;

    @Autowired
    private BankJpa bankJpa;

    @Autowired
    private Time time;

    @Autowired
    private CommodityJpa commodityJpa;

    @Autowired
    private DES des;

    //支付方法
    @Transactional(rollbackFor = Exception.class)
    public PrInfo PayFirst(String id) throws Exception {
        //获取当前购买人
        String phone=(String) SecurityUtils.getSubject().getPrincipal();
        //查询当前手机号是否有当前订单
        log.info("开始写入用户【"+phone+"】支付订单！");
        //验证购买订单是否存在
        Order order = orderJpa.findByOrderIdAndPhone(id,phone);
        if (order == null) {
            log.warn("用户【"+phone+"】，请求的订单【"+id+"】不存在！");
            return ExceptionUtil.EInfo(201,"订单【"+id+"】不存在");
        }
        //验证支付订单是否过期
        if (order.getOrderState().equals("3")) {
            log.warn("用户【"+phone+"】请求的商品订单已过期");
            return ExceptionUtil.EInfo(201,"请求的商品订单【"+id+"】已过期");
        }
        Commodity commodity = commodityJpa.findByCommodityId(order.getOrderCommodityId());
        if (commodity == null){
            log.warn("用户【"+phone+"】，在支付方法请求的商品【"+order.getOrderCommodityId()+"】，不存在");
            return ExceptionUtil.EInfo(201,"请求的商品【"+order.getOrderCommodityId()+"】不存在");
        }
        User user=userJpa.findByPhone(phone);
        //判断当前订单是否已经存在支付订单
        //查询产品库存
        BASE64Decoder decoder = new BASE64Decoder();
        int x = Integer.valueOf(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), "szhl8888")));
        Pay pay = null;
        pay = payJpa.findByPayOrderId(id);
        //如果有直接返回已有的
        if (pay !=null){
            log.warn("用户【"+phone+"】，订单【"+id+"】已存在支付订单！");
            return ExceptionUtil.EInfos(202,"订单已存在",pay);
        }else if (x > Integer.valueOf(order.getOrderNum()) && Integer.valueOf(order.getOrderNum()) > 0){
            log.info("用户【"+phone+"】，商品订单【"+id+"】开始生成支付订单");
            pay = new Pay();
            //支付订单中，扣除库存
            commodity.setCommodityStock(new BASE64Encoder().encode(des.encrypt(String.valueOf(x - Integer.valueOf(order.getOrderNum())).getBytes(), "szhl8888")));
            commodityJpa.save(commodity);
            //开始写入支付订单
            pay.setPayId(onlyId.PayId());
            pay.setPayOrderId(id);
            pay.setPayCommodityId(order.getOrderCommodityId());
            pay.setPayCommodityName(order.getOrderCommodityName());
            pay.setPayCommodityUnitPrice(""+(Integer.valueOf(order.getOrderMoney())/Integer.valueOf(order.getOrderNum())));
            pay.setPayCommodityMoney(order.getOrderMoney());
            pay.setPayNum(order.getOrderNum());
            pay.setPayMode("银行转账");
            pay.setPayName(user.getName());
            pay.setPayPhone(user.getPhone());
            pay.setPayEmail(user.getEmail());
            pay.setPayState("1");
            pay.setVoucherState("2");
            pay.setPayTime(new Date());
            pay.setVoucherUrl("");
            //改变订单状态为1
            order.setOrderState("1");
            //改变订单状态为1
            orderJpa.save(order);
            //写入支付订单
            payJpa.save(pay);
            //调用线程检查支付订单过期情况
            OrderTime(pay.getPayId());
            log.info("用户【"+phone+"】支付订单写入完成，支付id【"+pay.getPayId()+"】");
            return ExceptionUtil.Info(pay);
        }else {
            log.warn("用户【"+phone+"】，生成【"+order.getOrderId()+"】的支付订单失败，库存不足");
            return ExceptionUtil.EInfo(203,"库存不足！");
        }
    }

    //查询接受付款银行信息
    public List<Bank> BankAll(){
        return bankJpa.findAll();
    }

    //修改接受付款银行信息
    public Bank BankUpdate(Bank bank){
        Bank bank1 = bankJpa.findAll().get(0);
        bank1.setName(bank.getName());
        bank1.setBankAccount(bank.getBankAccount());
        bank1.setOpeningBank(bank.getOpeningBank());
        bankJpa.save(bank1);
        return bank1;
    }

    //查询支付定单凭证状态
    public List<Pay> PayvoucherState(String vs){
        return payJpa.findByVoucherState(vs);
    }

    //查询支付订单状态
    public List<Pay> PayState(String s){
        return payJpa.findByPayState(s);
    }

    //查询单个订单状态
    public Pay PayOne(String id){
        return payJpa.findByPayId(id);
    }

    //个人获取支付订单
    public List<Pay> PayUser(){
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        return payJpa.findByPayPhone(phone);
    }

    //管理员查看支付订单
    public List<Pay> PayAll(){
        return payJpa.findAll();
    }

    //管理员删除方法
    public Pay PayDelete(String id){
        //查询支付订单是否存在
        Pay pay = payJpa.findByPayId(id);
        if (pay != null)
            return null;
        payJpa.delete(pay);
        return pay;
    }

    //管理员修改方法
    public Pay PayUpdate(Pay pay){
        //查询支付订单是否存在
        if (payJpa.findByPayId(pay.getPayId()) == null)
            return null;
        payJpa.save(pay);
        return pay;
    }


    public void OrderTime(String payId) {
        Timer timer = new Timer();
        timer.schedule(new PayTask(timer,payId),new Date(),Integer.valueOf(FileConfig.OutputPath("scanning","5000")));
    }

    class PayTask extends TimerTask{

        private Timer timer;

        private String payId;

        public PayTask(Timer timer , String payId) {
            this.timer = timer;
            this.payId=payId;
        }

        @Override
        public void run() {
            BASE64Decoder decoder = new BASE64Decoder();
            //查询支付订单
            Pay pay = payJpa.findByPayId(payId);
            if (pay == null)
                this.timer.cancel();
            if (pay.getVoucherState().equals("1"))
                this.timer.cancel();
            if (time.belongDate(new Date(),pay.getPayTime(),Integer.valueOf(FileConfig.OutputPath("pay-overtime","30")))){
                //过期操作
                //查询订单
                Order order =  orderJpa.findByOrderId(pay.getPayOrderId());
                if (order == null)
                    this.timer.cancel();
                //查询商品
                Commodity commodity = commodityJpa.findByCommodityId(order.getOrderCommodityId());
                if (commodity == null)
                    this.timer.cancel();
                try {
                    //返回商品库存
                    //计算历史库存
                    Integer money = Integer.valueOf(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), "szhl8888")));
                    log.info("支付订单【"+payId+"】，查询到现有库存【"+money+"】");
                    Integer orderMoney = Integer.valueOf(order.getOrderNum());
                    log.info("支付订单【"+payId+"】拥有的商品数量【"+orderMoney+"】");
                    Integer s = money+orderMoney;
                    commodity.setCommodityStock(new BASE64Encoder().encode(des.encrypt(s.toString().getBytes("utf-8"), "szhl8888")));
                    log.info("订单【"+payId+"】返回后的库存【"+s+"】");
                    //改变订单状态
                    order.setOrderState("3");
                    //改变支付订单状态
                    pay.setPayState("3");
                    payJpa.save(pay);
                    orderJpa.save(order);
                    commodityJpa.save(commodity);
                } catch (Exception e) {
                    log.info("商品解密失败！");
                }

                log.info("支付订单【"+payId+"】超时");
                this.timer.cancel();
            }
        }
    }

}
