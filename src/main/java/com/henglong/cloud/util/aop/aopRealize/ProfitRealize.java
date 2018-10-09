package com.henglong.cloud.util.aop.aopRealize;

import com.henglong.cloud.dao.AssetsJpa;
import com.henglong.cloud.dao.PayJpa;
import com.henglong.cloud.dao.SpreadJpa;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Pay;
import com.henglong.cloud.entity.Spread;
import com.henglong.cloud.util.FileConfig;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Aspect
public class ProfitRealize {

    private static final Logger log = LoggerFactory.getLogger(ProfitRealize.class);

    @Autowired
    private SpreadJpa spreadJpa;

    @Autowired
    private AssetsJpa assetsJpa;

    @Autowired
    private PayJpa payJpa;


    @Pointcut("@annotation(com.henglong.cloud.util.aop.aopName.Profit)")
    public void Profit() {
    }


    @After("Profit()")
    public void Pro() throws Throwable {
        Double AProfit = 0.0;
        Double BProfit = 0.0;
        //获取当前用户推荐码
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        Spread spread = spreadJpa.findBySpreadPhone(phone);
        String spreadCode = spread.getSpreadPromoCode();
        //获取当前用户推广的A类
        List<Spread> AList = spreadJpa.findBySpreadCode(spreadCode);
        //获取当前用户推广的B类
        for (Spread ASpread : AList) {
            List<Spread> BList = spreadJpa.findBySpreadCode(ASpread.getSpreadPromoCode());
            for (Spread BSpread : BList) {
                //获取B类推广用户资产
                List<Assets> BAssetsList = assetsJpa.findByAssetsPhone(BSpread.getSpreadPhone());
                if (BAssetsList == null)
                    break;
                //查询订单
                for (Assets assets : BAssetsList) {
                    Pay pay = payJpa.findByPayId(assets.getAssetsPayId());
                    log.info("当前计算B类订单ID为【"+assets.getAssetsPayId()+"】");
                    //计算提成
                    BProfit += Double.valueOf(pay.getPayCommodityMoney())*Double.valueOf(FileConfig.OutputPath("A-Proportion","0.01"));
                }
            }
            //获取推广A类用户资产
            List<Assets> AAssetsList = assetsJpa.findByAssetsPhone(ASpread.getSpreadPhone());
            if (AAssetsList == null)
                break;
            for (Assets AAssets : AAssetsList){
                Pay pay = payJpa.findByPayId(AAssets.getAssetsPayId());
                log.info("当前计算A类订单ID为【"+AAssets.getAssetsPayId()+"】");
                AProfit += Double.valueOf(pay.getPayCommodityMoney())*Double.valueOf(FileConfig.OutputPath("A-Proportion","0.03"));
            }
        }
        //将收益写入数据库
        log.info("用户【"+phone+"】，推广A类用户总收益为【"+AProfit+"】，推广B类用户总收益为【"+BProfit+"】");
        spread.setSpreadMoney(AProfit+BProfit);
        spreadJpa.save(spread);
    }
}
