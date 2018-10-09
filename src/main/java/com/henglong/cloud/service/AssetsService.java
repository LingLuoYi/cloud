package com.henglong.cloud.service;

import com.henglong.cloud.dao.AssetsJpa;
import com.henglong.cloud.dao.CommodityJpa;
import com.henglong.cloud.dao.OrderJpa;
import com.henglong.cloud.dao.PayJpa;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Commodity;
import com.henglong.cloud.entity.Order;
import com.henglong.cloud.entity.Pay;
import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.PrInfo;
import com.henglong.cloud.util.Regular;
import com.henglong.cloud.util.Time;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AssetsService {

    private static final Logger log = LoggerFactory.getLogger(AssetsService.class);

    @Autowired
    private AssetsJpa assetsJpa;

    @Autowired
    private PayJpa payJpa;

    @Autowired
    private OrderJpa orderJpa;

    @Autowired
    private CommodityJpa commodityJpa;

    @Autowired
    private Time time;

    /**
     * 个人资产订单查询
     * @return
     */
    public PrInfo AssetsOneAllInfo(){
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        return ExceptionUtil.Info(assetsJpa.findByAssetsPhone(phone));
    }

    /**
     * 个人资产更新
     */
    public void AssetsUpdate() throws ParseException {
        //获取登录用户
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        //获取用户资产信息
        List<Assets> assets = assetsJpa.findByAssetsPhone(phone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tim = sdf.format(new Date());
        //对每一条资产信息做处理
        for (Assets ass:assets){
            //判断当前资产是否有期限
            if(ass.getAssetsTerm().equals("0")){
                //判断是否到达开始时间
                if (time.belongDate(new Date(),new Date(sdf.parse(ass.getAssetsTime()).getTime()),0))
                    //计算时间
                    ass.setAssetsDay(""+time.differentDays(new Date(sdf.parse(ass.getAssetsTime()).getTime()),new Date()));
            }else {
                //判断是否到达开始时间
                if (time.belongDate(new Date(),new Date(sdf.parse(ass.getAssetsTime()).getTime()),0))
                //判断是否在时间段内
                   if(!time.belongDate(new Date(),new Date(sdf.parse(ass.getAssetsTime()).getTime()),Integer.valueOf(ass.getAssetsTerm())))
                       ass.setAssetsDay(""+time.differentDays(new Date(sdf.parse(ass.getAssetsTime()).getTime()),new Date()));
            }
            assetsJpa.save(ass);
        }

    }

    /**
     * 财务审核方法
     * @param id
     * @return
     */
    public Assets Examine(String id,Boolean o){
        log.info("审核订单开始");
        if (assetsJpa.findByAssetsPayId(id) != null) {
            log.warn("审核办法，ID【"+id+"】已经审核过了");
            return null;
        }
        if (id ==null && id == "")
            return null;
        //创建资产订单
        Assets assets = new Assets();
        //查询支付订单
        Pay pay = payJpa.findByPayId(id);
        if (pay == null) {
            log.warn("审核办法，支付订单【"+id+"】不存在");
            return null;
        }
        //查询商品订单
        Order order = orderJpa.findByOrderId(pay.getPayOrderId());
        if (order == null) {
            log.warn("审核办法，商品订单【"+pay.getPayOrderId()+"】不存在");
            return null;
        }
        //查询商品
        Commodity commodity = commodityJpa.findByCommodityId(order.getOrderCommodityId());
        if (commodity == null) {
            log.warn("审核办法，商品不存在【"+order.getOrderCommodityId()+"】");
            return null;
        }
        try {
            if (o) {
                //写入资产
                assets.setAssetsPayId(id);
                assets.setAssetsNum(order.getOrderNum());
                assets.setAssetsPhone(pay.getPayPhone());
                //收益计算公式暂未支持
                assets.setAssetsProfit(new BigDecimal("0"));

                assets.setAssetsTerm(order.getOrderTerm());
                assets.setAssetsType(order.getOrderCommodityType());
                //价值是收益加上当前商品单价*商品数量，暂不支持
                assets.setAssetsValue("");

                assets.setAssetsName(order.getOrderCommodityName());
                //计算用户持有当前资产的天数
                assets.setAssetsDay("");
                //时间
                if (commodity.getCommodityTime().equals("0")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String time = sdf.format(new Date());
                    assets.setAssetsTime(time);
                } else {
                    assets.setAssetsTime(commodity.getCommodityTime());
                }
                //改变资产状态
                assets.setAssetsState("0");
                //改变订单状态0
                order.setOrderState("0");
                //改变支付订单状态0
                pay.setPayState("0");
                //全部写入
                assetsJpa.save(assets);
                orderJpa.save(order);
                payJpa.save(pay);
                return assets;
            } else {
                //改变支付订单状态4
                pay.setPayState("4");
                payJpa.save(pay);
                return null;
            }
        }catch (Exception e){
            log.warn("审核方法，错误的布尔值！！");
            return null;
        }
    }

    /**
     * 全部资产查询
     * @return
     */
    public List<Assets> AssetsAll(){
        return assetsJpa.findAll();
    }


    /**
     * 单个查询
     * @param id
     * @return
     */
    public Assets AssetsOne(Integer id){
        if (id ==null)
            return null;
        Optional<Assets> assets = assetsJpa.findById(id);
        return assets.orElse(null);
    }


    /**
     * 修改资产
     * @param id
     * @param assets
     * @return
     */
    public Assets AssetsUpdate(Integer id,Assets assets){
        if (id == null )
            return null;
        if (Regular.isInteger(assets.getAssetsNum()))
            return null;
        Assets assets1 = assetsJpa.findById(id).orElse(null);
        assets1.setAssetsTime(assets.getAssetsTime());
        assets1.setAssetsState(assets.getAssetsState());
        assets1.setAssetsDay(assets.getAssetsDay());
        assets1.setAssetsName(assets.getAssetsName());
        assets1.setAssetsValue(assets.getAssetsValue());
        assets1.setAssetsType(assets.getAssetsType());
        assets1.setAssetsTerm(assets.getAssetsTerm());
        assets1.setAssetsProfit(assets.getAssetsProfit());
        assets1.setAssetsPhone(assets.getAssetsPhone());
        assets1.setAssetsNum(assets.getAssetsNum());
        assetsJpa.save(assets1);
        return assets1;
    }
}
