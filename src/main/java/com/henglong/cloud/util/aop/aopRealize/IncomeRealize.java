package com.henglong.cloud.util.aop.aopRealize;

import com.henglong.cloud.dao.AssetsJpa;
import com.henglong.cloud.dao.CommodityJpa;
import com.henglong.cloud.dao.UserJpa;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.util.FileConfig;
import com.henglong.cloud.util.Result;
import com.henglong.cloud.util.Time;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Component
@Aspect
public class IncomeRealize {

    @Autowired
    private UserJpa userJpa;

    @Autowired
    private AssetsJpa assetsJpa;

    @Autowired
    private CommodityJpa commodityJpa;

    @Pointcut("@annotation(com.henglong.cloud.util.aop.aopName.Income)")
    public void Income(){}


    /**
     * 计算用户收益，注解在用户个人信息获取接口
     */
    @After("Income()")
    public void Inc() throws ScriptException {
        //获取用户信息
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userJpa.findByPhone(phone);
        if (user == null)
            return;
        //获取用户资产信息
        List<Assets> assetsList = assetsJpa.findByAssetsPhone(phone);
        for (Assets assets : assetsList){
            //判断币类型
            if (assets.getAssetsType() == "BTC"){
                //判断当前资产是否有期限
                if(assets.getAssetsTerm().equals("0")){
                    //非期限商品
                    //判断是否到达开始时间
                    if (Time.belongDate(new Date(),assets.getAssetsTime(),0)) {
                        //计算天数
                        assets.setAssetsDay("" + Time.differentDays(assets.getAssetsTime(), new Date()));
                        BigDecimal num = new BigDecimal(Integer.valueOf(assets.getAssetsNum()));
                        BigDecimal decimal = new BigDecimal((String) FileConfig.Variable("Theoretical coin"));
                        //查询商品
                        BigDecimal unit = ((num.multiply(decimal)).divide(new BigDecimal(24))).subtract(new BigDecimal(String.valueOf(Result.result(assets.getWatt()+"/100"))).multiply(num));
                        BigDecimal profit = (unit.multiply(new BigDecimal(Time.diffHours(new Date(),assets.getAssetsTime()))));
                        assets.setDeductions(""+(new BigDecimal(String.valueOf(Result.result(assets.getWatt()+"/100"))).multiply(num)).multiply(new BigDecimal(Time.diffHours(new Date(),assets.getAssetsTime()))));
                        assets.setAssetsProfit(num.add(profit));
                    }
                }else {
                    //期限商品
                    //判断是否到达开始时间
                    if (Time.belongDate(new Date(),assets.getAssetsTime(),0))
                        //判断是否在期限时间段内
                        if(!Time.belongDate(new Date(),assets.getAssetsTime(),Integer.valueOf(assets.getAssetsTerm()))){
                            //计算天数
                            assets.setAssetsDay(""+Time.differentDays(assets.getAssetsTime(),new Date()));
                            BigDecimal num = new BigDecimal(Integer.valueOf(assets.getAssetsNum()));
                            BigDecimal decimal = new BigDecimal((String) FileConfig.Variable("Theoretical coin"));
                            /* 查询商品 */
                            BigDecimal unit = ((num.multiply(decimal)).divide(new BigDecimal(24))).subtract(new BigDecimal(String.valueOf(Result.result(assets.getWatt()+"/100"))).multiply(num));
                            BigDecimal profit = (unit.multiply(new BigDecimal(Time.diffHours(new Date(),assets.getAssetsTime()))));
                            assets.setDeductions(""+(new BigDecimal(String.valueOf(Result.result(assets.getWatt()+"/100"))).multiply(num)).multiply(new BigDecimal(Time.diffHours(new Date(),assets.getAssetsTime()))));
                            assets.setAssetsProfit(num.add(profit));
                        }
                }
                assetsJpa.save(assets);
            }
            assets.getAssetsType();
        }
    }
}
