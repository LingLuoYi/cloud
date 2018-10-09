package com.henglong.cloud.service;

import com.henglong.cloud.dao.AssetsJpa;
import com.henglong.cloud.dao.PutForwardJpa;
import com.henglong.cloud.dao.UserJpa;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.PutForward;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.PrInfo;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class PutForwardService {

    @Autowired
    private PutForwardJpa putForwardJpa;

    @Autowired
    private AssetsJpa assetsJpa;

    @Autowired
    private UserJpa userJpa;

    public PrInfo Put(String assetsPayId, BigDecimal num){
        //获取当前操作人
        String phone=(String) SecurityUtils.getSubject().getPrincipal();
        //查询用户
        User user = userJpa.findByPhone(phone);
        if (user == null)
            return ExceptionUtil.EInfo(202,"是谁要提款呢");
        //查询资产
        Assets assets = assetsJpa.findByAssetsPayId(assetsPayId);
        if (assets == null)
            return ExceptionUtil.EInfo(202,"没有这笔财产哦！");
        //判断用户是否有这么多资产
        if (num.compareTo(assets.getAssetsProfit()) >= 0){
            //写入提现
            PutForward putForward = new PutForward();
            putForward.setAssetsId(assetsPayId);
            putForward.setType(assets.getAssetsType());
            putForward.setWallet((String) user.getWallet().get(assets.getAssetsType()));
            putForward.setNum(num);
            putForward.setName(user.getName());
            putForward.setPhone(user.getPhone());
            putForward.setEmail(user.getEmail());
            putForward.setSubmissionTime(new Date());
            putForward.setState(1);
            putForward.setAdoptTime(null);
            putForward.setHash("");
            putForwardJpa.save(putForward);
            return ExceptionUtil.Info(putForward);
        }else {
            return ExceptionUtil.EInfo(202,"没有这么多钱哦");
        }
    }

    public PrInfo PutS(String asstes,String hash,Boolean b){
            //判断是否有订单
        PutForward putForward = putForwardJpa.findByAssetsPayId(asstes);
        if (putForward == null)
            return ExceptionUtil.EInfo(202,"hehehe");
        if(putForward.getState() == 0)
            return ExceptionUtil.EInfo(202,"审核过了");
        if (b){
            putForward.setState(0);
            putForward.setHash(hash);
            putForwardJpa.save(putForward);
        }else {
            putForward.setState(4);
            putForward.setHash("提现被拒绝");
            putForwardJpa.save(putForward);
        }
        return ExceptionUtil.Info(putForward);
    }
}
