package com.henglong.cloud.service;

import com.henglong.cloud.dao.AssetsJpa;
import com.henglong.cloud.dao.ReflectJpa;
import com.henglong.cloud.dao.UserJpa;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Reflect;
import com.henglong.cloud.entity.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ReflectService {

    @Autowired
    private UserJpa userJpa;

    @Autowired
    private AssetsJpa assetsJpa;

    @Autowired
    private ReflectJpa reflectJpa;

    public Object Reflects(String assetsId,BigDecimal num,String remarks){
        //创建提现订单
        Reflect reflect = new Reflect();
        String wallet = "";
        BigDecimal nums = null;
        //获取登录用户
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        //获取用户
        User user = userJpa.findByPhone(phone);
        if (user == null)
            return "似乎没有登录呢！";
        if (user.getIDCardNo() == null)
            return "没有实名认证呢";
        //查询资产
        Assets assets = assetsJpa.findByAssetsPayIdAndAssetsPhone(phone,assetsId);
        if (assets == null)
            return "这笔资产好像不是你的哦";
        //计算资产//////////////////////////////////////////////////////////////////////
        if (assets.getAssetsType() == "BTC") {
            if (assets.getAssetsProfit().compareTo(new BigDecimal(0.0001)) != 1)
                return "不够提现的哦！";
            reflect.setAssetsId(assetsId);
            reflect.setName(user.getName());
            reflect.setPhone(user.getPhone());
            reflect.setEmail(user.getEmail());
            reflect.setIDCard(user.getIDCardNo());
            reflect.setAssetsType(assets.getAssetsType());
            nums = assets.getAssetsProfit();
            if (num.compareTo(nums) != 1)
                return "还没有这么多可以提现呢";
            reflect.setNum(num);
            //扣除资产收益
            assets.setAssetsProfit(nums.subtract(num));
            wallet = (String) user.getWallet().get("BTC");
            if (wallet == "")
                return "没有钱包哦！";
            reflect.setWallet(wallet);
            reflect.setStatus("0");
            reflect.setRemarks(remarks);
        }else if (assets.getAssetsType() == "ETH"){
            if (assets.getAssetsProfit().compareTo(new BigDecimal(10.00)) != 1)
                return "不够提现的哦！";
            reflect.setAssetsId(assetsId);
            reflect.setName(user.getName());
            reflect.setPhone(user.getPhone());
            reflect.setEmail(user.getEmail());
            reflect.setIDCard(user.getIDCardNo());
            reflect.setAssetsType(assets.getAssetsType());
            nums = assets.getAssetsProfit();
            if (num.compareTo(nums) != 1)
                return "还没有这么多可以提现呢";
            reflect.setNum(num);
            //扣除资产收益
            assets.setAssetsProfit(nums.subtract(num));
            wallet = (String) user.getWallet().get("ETH");
            if (wallet == "")
                return "没有钱包哦！";
            reflect.setWallet(wallet);
            reflect.setStatus("0");
            reflect.setRemarks(remarks);
        }
        ////////////././././././././././././././././././././././././.

        reflectJpa.save(reflect);
        assetsJpa.save(assets);
        return reflect;
    }
}
