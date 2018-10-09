package com.henglong.cloud.service;

import com.henglong.cloud.dao.AssetsJpa;
import com.henglong.cloud.dao.PayJpa;
import com.henglong.cloud.dao.SpreadJpa;
import com.henglong.cloud.dao.UserJpa;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Pay;
import com.henglong.cloud.entity.Spread;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.util.FileConfig;
import com.henglong.cloud.util.OnlyId;
import com.henglong.cloud.util.Regular;
import com.henglong.cloud.util.aop.aopName.Profit;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpreadService {

    private final static Logger log = LoggerFactory.getLogger(SpreadService.class);

    @Autowired
    private SpreadJpa spreadJpa;

    @Autowired
    private OnlyId onlyId;

    @Autowired
    private UserJpa userJpa;

    @Autowired
    private AssetsJpa assetsJpa;

    @Autowired
    private PayJpa payJpa;

    public List<Spread> SpreadAllInfo() {
        return spreadJpa.findAll();
    }


    //个人推广查看
    @Profit()
    public Spread SpreadOneInfo() {
        //获取登录用户
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        List<Spread> As = new ArrayList<>();
        List<Spread> Bs = new ArrayList<>();
        Map Am = new HashMap();
        Map Bm = new HashMap();
        //获取推广码
        Spread spread = spreadJpa.findBySpreadPhone(phone);
        //获取A类推荐
        As = spreadJpa.findBySpreadCode(spread.getSpreadPromoCode());
        //z转换数组
        Spread[] AS = new Spread[As.size()];
        As.toArray(AS);
        Spread[] spread2 = new Spread[As.size()];
        //获取B类推荐
        for (int i = 0; i < As.size(); i++) {
            Bs = spreadJpa.findBySpreadCode(As.get(i).getSpreadPromoCode());
            Spread[] BS = new Spread[Bs.size()];
            Bs.toArray(BS);
            //查询订单
            List<Assets> AAssets = assetsJpa.findByAssetsPhone(As.get(i).getSpreadPhone());
            for (Assets assets : AAssets) {
                Pay pay = payJpa.findByPayId(assets.getAssetsPayId());
                Am.put(pay.getPayId(), Double.valueOf(pay.getPayCommodityMoney()) * 0.03);
            }
            for (Spread Bsss : Bs) {
                List<Assets> BAssets = assetsJpa.findByAssetsPhone(Bsss.getSpreadPhone());
                for (Assets assets : BAssets) {
                    Pay pay = payJpa.findByPayId(assets.getAssetsPayId());
                    Bm.put(pay.getPayId(), Double.valueOf(pay.getPayCommodityMoney()) * 0.01);
                }
            }
            spread2[i] = new Spread();
            spread2[i].setId(As.get(i).getId());
            spread2[i].setSpreadPhone(As.get(i).getSpreadPhone());
            spread2[i].setSpreadPromoCode(As.get(i).getSpreadPromoCode());
            spread2[i].setSpreadUrl(As.get(i).getSpreadUrl());
            spread2[i].setSpreadNum(As.get(i).getSpreadNum());
            spread2[i].setSpreadMoney(As.get(i).getSpreadMoney());
            spread2[i].setSpreadCode(As.get(i).getSpreadCode());
            spread2[i].setProfit(Bm);
            spread2[i].setSpreadUser(BS);
        }

        Spread spread1 = new Spread();
        spread1.setId(spread.getId());
        spread1.setSpreadPhone(spread.getSpreadPhone());
        spread1.setSpreadPromoCode(spread.getSpreadPromoCode());
        spread1.setSpreadUrl(spread.getSpreadUrl());
        spread1.setSpreadNum(spread.getSpreadNum());
        spread1.setSpreadMoney(spread.getSpreadMoney());
        spread1.setSpreadCode(spread.getSpreadCode());
        spread1.setProfit(Am);
        spread1.setSpreadUser(spread2);
        return spread1;
    }

    //通过推荐码查看（管理）
    public Spread SpreadInfo(String code) {
        return spreadJpa.findBySpreadPromoCode(code);
    }


    /**
     * 更新输入，严格
     *
     * @param num
     * @return
     */
    public Spread SpreadNum(Integer num, String code) {
        Spread spread = spreadJpa.findBySpreadPromoCode(code);
        spread.setSpreadNum(num);
        spreadJpa.save(spread);
        return spread;
    }

    //推广生成
    public Spread Spreads(String phone, String code) {
        if (Regular.isPhone(phone))
            return null;
        Spread spread = new Spread();
        String s = "";
        while (true) {
            s = onlyId.RandomString(Integer.valueOf(FileConfig.OutputPath("spread", "6")));
            if (spreadJpa.findBySpreadPromoCode(s) == null) {
                break;
            }
        }
        spread.setSpreadPhone(phone);
        spread.setSpreadPromoCode(s);
        spread.setSpreadUrl("/sign_up/" + s);
        spread.setSpreadNum(0);
        spread.setSpreadMoney(0.0);
        spread.setSpreadCode(code);
        spreadJpa.save(spread);
        return spread;
    }

    //推广收益计算
    //收益计算重写
    //收益计算重写

}
