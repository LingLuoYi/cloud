package com.henglong.cloud.service;

import com.henglong.cloud.dao.CommodityJpa;
import com.henglong.cloud.dao.OrderJpa;
import com.henglong.cloud.dao.UserJpa;
import com.henglong.cloud.entity.Commodity;
import com.henglong.cloud.entity.Order;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CommodityService {

    private static final Logger log = LoggerFactory.getLogger(CommodityService.class);

    @Autowired
    private ChangeToPinYin changeToPinYin;

    @Autowired
    private OnlyId onlyId;

    @Autowired
    private DES des;

    @Autowired
    private CommodityJpa commodityJpa;

    @Autowired
    private OrderJpa orderJpa;

    @Autowired
    private UserJpa userJpa;

    @Autowired
    private Time time;


    /**
     * 商品写入加密
     *
     * @param commodity
     * @return
     * @throws Exception
     */
    public Commodity CommodityAdd(Commodity commodity) throws Exception {
        log.info("写入商品开始");
        BASE64Decoder decoder = new BASE64Decoder();
        //数据验证区
        if (commodity.getCommodityMoney() == null || Regular.isNumeric(commodity.getCommodityMoney()))
            return null;
        if (commodity.getCommodityStock() == null || Regular.isPhone(commodity.getCommodityStock()))
            return null;
        if (commodity.getCommodityName() == null || Regular.isSpecialChar(commodity.getCommodityName()))
            return null;
        if (commodity.getCommodityType() == null || Regular.isString(commodity.getCommodityType()))
            return null;
        //验证结束
        Commodity commodity1 = new Commodity();
        if (commodity.getCommodityId() == null || commodity.getCommodityId().equals("")) {
            String id = onlyId.generateRefID(changeToPinYin.getStringPinYin(commodity.getCommodityName()));
            commodity1.setCommodityId(id);
            log.info("商品ID【" + id + "】");
        } else {
            //验证商品id是否合法
            if (commodityJpa.findByCommodityId(commodity.getCommodityId()) == null)
                return null;
            commodity1.setId(commodity.getId());
            commodity1.setCommodityId(commodity.getCommodityId());
            log.info("ID【" + commodity.getId() + "】");
            log.info("商品ID【" + commodity.getCommodityId() + "】");
        }
        commodity1.setCommodityName(commodity.getCommodityName());
        commodity1.setCommodityMoney(new BASE64Encoder().encode(des.encrypt(commodity.getCommodityMoney().getBytes("utf-8"), "szhl8888")));
//        byte[] buf = decoder.decodeBuffer(new BASE64Encoder().encode(des.encrypt(commodity.getCommodityMoney().getBytes("utf-8"),"szhl8888")));
//        log.info("这是解密后的文本："+new String(des.decrypt(buf, "szhl8888")));
        commodity1.setCommodityStock(new BASE64Encoder().encode(des.encrypt(commodity.getCommodityStock().getBytes("utf-8"), "szhl8888")));
        commodity1.setCommodityInitialStock(new BASE64Encoder().encode(des.encrypt(commodity.getCommodityInitialStock().getBytes("utf-8"), "szhl8888")));
        commodity1.setCommodityType(commodity.getCommodityType());
        commodity1.setCommodityTerm(commodity.getCommodityTerm());
        //图片上传
        commodityJpa.save(commodity1);
        log.info("写入商品完成");
        return commodity;
    }

    /**
     * 查询全部商品(解密)
     *
     * @param
     * @return
     * @throws Exception
     */
    public List<Commodity> CommodityAllRead() throws Exception {
        List<Commodity> listAll = commodityJpa.findAll();
        List<Commodity> listAll2 = new ArrayList<Commodity>();
        BASE64Decoder decoder = new BASE64Decoder();
        for (int i = 0; i < listAll.size(); i++) {
            Commodity commodity1 = new Commodity();
            commodity1.setId(listAll.get(i).getId());
            commodity1.setCommodityId(listAll.get(i).getCommodityId());
            commodity1.setCommodityName(listAll.get(i).getCommodityName());
            commodity1.setCommodityMoney(new String(des.decrypt(decoder.decodeBuffer(listAll.get(i).getCommodityMoney()), "szhl8888")));
            commodity1.setCommodityStock(new String(des.decrypt(decoder.decodeBuffer(listAll.get(i).getCommodityStock()), "szhl8888")));
            commodity1.setCommodityInitialStock(new String(des.decrypt(decoder.decodeBuffer(listAll.get(i).getCommodityInitialStock()), "szhl8888")));
            Double d=Double.valueOf(new String(des.decrypt(decoder.decodeBuffer(listAll.get(i).getCommodityStock()), "szhl8888")))/Double.valueOf(new String(des.decrypt(decoder.decodeBuffer(listAll.get(i).getCommodityInitialStock()), "szhl8888")))*100;
            commodity1.setHHHHH(Math.round(Float.valueOf(""+d)));
            commodity1.setCommodityTime(listAll.get(i).getCommodityTime());
            commodity1.setCommodityType(listAll.get(i).getCommodityType());
            commodity1.setCommodityTerm(listAll.get(i).getCommodityTerm());
            listAll2.add(commodity1);
        }
        return listAll2;
    }

    /**
     * 单个查询ID查询
     *
     * @param s
     * @return
     */
    public Commodity CommodityOneRead(String s) throws Exception {
        Commodity commodity = commodityJpa.findByCommodityId(s);
        if (commodity == null)
            return null;
        Commodity commodity1 = new Commodity();
        BASE64Decoder decoder = new BASE64Decoder();
        commodity1.setId(commodity.getId());
        commodity1.setCommodityName(commodity.getCommodityName());
        commodity1.setCommodityId(commodity.getCommodityId());
        commodity1.setCommodityMoney(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityMoney()), "szhl8888")));
        commodity1.setCommodityStock(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), "szhl8888")));
        commodity1.setCommodityType(commodity.getCommodityType());
        return commodity1;
    }


    @Transactional(rollbackFor = Exception.class)
    public Order CommodityPurchase(String id, String num) throws Exception {
        //获取登录手机号
        String phone = (String) SecurityUtils.getSubject().getPrincipal();
        //创建用户实体类
        User user = new User();
        //获取当前用户信息
        user = userJpa.findByPhone(phone);
        if (user == null) {
            log.warn("购买方法，购买用户【" + phone + "】不存在");
            return null;
        }
        log.info("开始生成用户【"+phone+"】的订单信息");
        BASE64Decoder decoder = new BASE64Decoder();
        //创建订单实体
        Order order = new Order();
        //获取商品信息
        Commodity commodity = commodityJpa.findByCommodityId(id);

        if (commodity == null) {
            log.warn("用户【"+phone+"】，未查询到商品【" + id + "】信息！");
            return null;
        }
        //查询产品库存
        int x = Integer.valueOf(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), "szhl8888")));
        log.info("用户【"+phone+"】请求生成订单，查询到库存信息" + x);
        if (x > Integer.valueOf(num) && Integer.valueOf(num) > 0) {
            //订单方法不对产品库存做出影响
//            commodity.setCommodityStock(new BASE64Encoder().encode(des.encrypt(String.valueOf(x - Integer.valueOf(num)).getBytes(), "szhl8888")));
//            commodityJpa.save(commodity);
            //写入订单信息
            order.setName(user.getName());
            order.setEmail(user.getEmail());
            order.setPhone(user.getPhone());
            order.setOrderCommodityId(id);
            order.setOrderCommodityName(commodity.getCommodityName());
            order.setOrderCommodityType(commodity.getCommodityType());
            order.setOrderId(onlyId.OrderId());
            //订单价格计算
            int a = Integer.valueOf(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityMoney()), "szhl8888")));
            int b = Integer.valueOf(num);
            order.setOrderMoney(String.valueOf(a * b));
            order.setOrderNum(num);
            if (user.getName() != null)
                order.setOrderName(commodity.getCommodityType() + "_" + user.getName());
            else
                order.setOrderName(commodity.getCommodityType() + "_" + user.getPhone());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tim = sdf.format(new Date());
            //判断交货时间，如果为零写入当前时间
            if (commodity.getCommodityTime().equals("0"))
                order.setOrderStartTime(tim);
            else
                order.setOrderStartTime(commodity.getCommodityTime());
            //判断是否是永久商品，如果不是则返回期限
            if (commodity.getCommodityTerm().equals("0")) {
                order.setOrderTerm("0");
                order.setOrderStopTime("--");
            } else {
                if (commodity.getCommodityTime().equals("0"))
                    order.setOrderStopTime(time.TimePuls(tim, (Integer.valueOf(commodity.getCommodityTerm()))));
                else
                    order.setOrderStopTime(time.TimePuls(commodity.getCommodityTime(), Integer.valueOf(commodity.getCommodityTerm())));
                order.setOrderTerm(commodity.getCommodityTerm());
            }
            order.setOrderTime(new Date());
            order.setOrderState("1");
            log.info("订单生成完毕");
            orderJpa.save(order);
            //调用线程检查订单过期情况
            OrderTime(order.getOrderId());
            return order;
        } else {
            log.warn("购买方法，用户使用错误的商品数量");
            return null;
        }
    }

    //判断订单是否过期，如果过期则回退库存
    public void OrderTime(String orderId) {
        Timer timer = new Timer();
        timer.schedule(new Task(timer,orderId),new Date(),Integer.valueOf(FileConfig.OutputPath("scanning","5000")));
    }

    class Task extends TimerTask{

        private Timer timer;

        private String orderId;

        public Task(Timer timer , String orderId) {
            this.timer = timer;
            this.orderId=orderId;
        }

        @Override
        public void run() {
            BASE64Decoder decoder = new BASE64Decoder();
//            log.info("当前时间【"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"】");
            Order order = orderJpa.findByOrderId(orderId);
            if (order == null) {
                log.warn("不存在的订单【"+orderId+"】");
                this.timer.cancel();
            }
            //判断订单状态
            if (order.getOrderState().equals(1)) {
                log.info("订单【"+orderId+"】已生成支付订单");
                this.timer.cancel();
            }
            //判断订单是否过期
            if (time.belongDate(new Date(), order.getOrderTime(), Integer.valueOf(FileConfig.OutputPath("overtime","30")))) {
                //过期操作
                    //改变订单状态为过期
                    order.setOrderState("3");
                    orderJpa.save(order);
                log.info("订单【"+orderId+"】已过期");
                this.timer.cancel();
            }
        }
    }
//    @Scheduled(cron = "0/2 * * * * *")
//    public void OrderTime2(){
//        log.info("循环输出");
//    }

}
