package com.henglong.cloud.service;

import com.henglong.cloud.dao.CommodityJpa;
import com.henglong.cloud.dao.PayJpa;
import com.henglong.cloud.dao.UserJpa;
import com.henglong.cloud.entity.Commodity;
import com.henglong.cloud.entity.Pay;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.util.LoadFile;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);


    private String[] types = {".jpg", ".bmp", ".jpeg", ".png"};

    @Autowired
    private PayJpa payJpa;

    @Autowired
    private CommodityJpa commodityJpa;

    @Autowired
    private UserJpa userJpa;


    public String PayFile(MultipartFile file, String id) throws Exception {
        //获取登录手机号
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        //查询支付订单是否存在
        log.info("上传方法，接受到：" + id);
        String name = "";
        Pay pay = payJpa.findByPayIdAndPayPhone(id, phone);
        if (pay == null || file == null) {
            return "支付订单不存在或者文件未选择";
        } else {
            if (pay.getVoucherState() != "1") {
                if (!file.isEmpty()) {
                    String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                    name = id + "" + type;
                    if (Arrays.asList(types).contains(type)) {
                        BufferedOutputStream out = null;
                        FileOutputStream outs = null;
                        log.info(LoadFile.Path());
                        File fileSourcePath = new File(LoadFile.Path() + "/img/pay");
                        if (!fileSourcePath.exists()) {
                            fileSourcePath.mkdirs();
                        }
                        log.info("上传的文件名为：" + name);
                        outs = new FileOutputStream(new File(fileSourcePath, name));
                        out = new BufferedOutputStream(outs);
                        out.write(file.getBytes());
                        out.flush();
                        outs.flush();
                        out.close();
                        outs.close();
                        log.info("将存储地址储存到数据库");
                        pay.setVoucherUrl("/pay/pay_img/" + name);
                        log.info("变更支付订单截图上传状态");
                        pay.setVoucherState("1");
                        payJpa.save(pay);
                        return "上传成功";
                    } else {
                        return "凭证已存在！";
                    }
                }
                return "此格式不支持";
            }
            return "文件不能为空";
        }
    }

    public String CommodityFile(MultipartFile file, String id) throws Exception {
        log.info("上传方法，接受到：" + id);
        String name = "";
        Commodity commodity = commodityJpa.findByCommodityId(id);
        if (commodity == null || file == null) {
            return "商品不存在或者文件未选择";
        } else {
            if (!file.isEmpty()) {
                String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                name = id + "" + type;
                if (Arrays.asList(types).contains(type)) {
                    BufferedOutputStream out = null;
                    FileOutputStream outs = null;
                    File fileSourcePath = new File(LoadFile.Path() + "/img/commodity");
                    if (!fileSourcePath.exists()) {
                        fileSourcePath.mkdirs();
                    }
                    log.info("上传的文件名为：" + name);
                    outs = new FileOutputStream(new File(fileSourcePath, name));
                    out = new BufferedOutputStream(outs);
                    out.write(file.getBytes());
                    out.flush();
                    outs.flush();
                    out.close();
                    outs.close();
                    log.info("将存储地址储存到数据库");
                    commodity.setCommodityUrl("/commodity/commodity_img/" + name);
                    commodityJpa.save(commodity);
                    return "上传成功";
                }
                return "此格式不支持";
            }
            return "文件不能为空";
        }
    }

    public String UserFile(MultipartFile file) throws Exception {
        //获取手机号
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        String name = "";
        User user = userJpa.findByPhone(phone);
        if (user == null || file == null) {
            return "用户不存在或者文件未选择";
        } else {
            if (!file.isEmpty()) {
                String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                name = phone + "" + type;
                if (Arrays.asList(types).contains(type)) {
                    BufferedOutputStream out = null;
                    FileOutputStream outs = null;
                    File fileSourcePath = new File(LoadFile.Path() + "/img/user");
                    if (!fileSourcePath.exists()) {
                        fileSourcePath.mkdirs();
                    }
                    log.info("上传的文件名为：" + name);
                    outs = new FileOutputStream(new File(fileSourcePath, name));
                    out = new BufferedOutputStream(outs);
                    out.write(file.getBytes());
                    out.flush();
                    outs.flush();
                    out.close();
                    outs.close();
                    log.info("将存储地址储存到数据库");
                    user.setImgUrl("/user/user_img/" + name);
                    userJpa.save(user);
                    return "上传成功";
                }
                return "此格式不支持";
            }
            return "文件不能为空";
        }
    }

    public String IDCardImg(MultipartFile cardImgA, MultipartFile cardImgB, String cardNo) throws Exception {
        Map<String,String> map = new HashMap();
        //获取手机号
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        //判断是否实名认证
        User user = userJpa.findByPhone(phone);
        if (user == null)
            return "用户不存在O__O";
        if (user.getUserStart() == 0)
            return "已经实名认证，如需改动，请联系客服";
        if (cardImgA == null || cardImgB == null || cardNo == "")
            return "请提供相关的资料";
        if (!cardImgA.isEmpty()) {
            String type = cardImgA.getOriginalFilename().substring(cardImgA.getOriginalFilename().lastIndexOf("."));
            String type2 = cardImgB.getOriginalFilename().substring(cardImgA.getOriginalFilename().lastIndexOf("."));
            if (Arrays.asList(types).contains(type)&&Arrays.asList(types).contains(type2)) {
                File fileSourcePath = new File(LoadFile.Path() + "/img/user/"+cardNo);
                if (!fileSourcePath.exists()) {
                    fileSourcePath.mkdirs();
                }
                //上传正面
                log.info("用户【" + phone + "】，上传的文件名为：" + cardNo + "_A" + type);
                FileOutputStream outs = new FileOutputStream(new File(fileSourcePath, cardNo + "_A" + type));
                BufferedOutputStream out = new BufferedOutputStream(outs);
                out.write(cardImgA.getBytes());
                map.put("card_A","/img/"+cardNo + "_A" + type);
                out.flush();
                outs.flush();
                out.close();
                outs.close();
                //上传反面
                log.info("用户【" + phone + "】，上传的文件名为：" + cardNo + "_B" + type);
                FileOutputStream outb = new FileOutputStream(new File(fileSourcePath, cardNo + "_B" + type));
                BufferedOutputStream outsb = new BufferedOutputStream(outb);
                outsb.write(cardImgB.getBytes());
                map.put("card_B","/img/"+cardNo + "_B" + type);
                outb.flush();
                outsb.flush();
                outb.close();
                outsb.close();
                //写入数据库
                user.setUserStart(1);
                user.setIDCardImg(map);
                userJpa.save(user);
                return "上传成功";
            } else {
                return "文件格式不支持！";
            }
        }
        return "文件不能为空";
    }
}


