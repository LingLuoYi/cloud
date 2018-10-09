package com.henglong.cloud.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity(name = "Cloud_Pay")
public class Pay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //序号

    @Column(name = "pay_id",unique =true)
    @NotBlank(message = "支付id不能为空")
    private String payId;
    //支付订单id

    @Column(name = "pay_order_id")
    @NotBlank(message = "订单id不能为空")
    private String payOrderId;
    //订单id

    @Column(name = "pay_commodity_id")
    @NotBlank(message = "商品id不能为空")
    private String payCommodityId;
    //商品id

    @Column(name = "pay_commodity_name")
    private String payCommodityName;
    //商品名称

    @Column(name = "pay_commodity_unit_price")
    private String payCommodityUnitPrice;
    //商品单价

    @Column(name = "pay_commodity_money")
    private String payCommodityMoney;
    //订单金额

    @Column(name = "pay_num")
    private String payNum;

    @Column(name = "pay_mode")
    private String payMode;
    //支付方式

    @Column(name = "pay_name")
    private String payName;
    //支付人姓名

    @Column(name = "pay_phone")
    @NotBlank(message = "手机号不能为空")
    private String payPhone;
    //支付人手机号

    @Column(name ="pay_email")
    @Email(message = "请输入正确的邮箱")
    private String payEmail;
    //支付人邮箱

    @Column(name = "pay_state")
    private String payState;
    //订单状态

    @Column(name = "pay_voucher_state")
    private String voucherState;
    //付款凭证状态11

    @Column(name = "pay_voucher_url")
    private String voucherUrl;

    @Column(name = "pay_time")
    private Date payTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }

    public String getPayCommodityId() {
        return payCommodityId;
    }

    public void setPayCommodityId(String payCommodityId) {
        this.payCommodityId = payCommodityId;
    }

    public String getPayCommodityName() {
        return payCommodityName;
    }

    public void setPayCommodityName(String payCommodityName) {
        this.payCommodityName = payCommodityName;
    }

    public String getPayCommodityUnitPrice() {
        return payCommodityUnitPrice;
    }

    public void setPayCommodityUnitPrice(String payCommodityUnitPrice) {
        this.payCommodityUnitPrice = payCommodityUnitPrice;
    }

    public String getPayCommodityMoney() {
        return payCommodityMoney;
    }

    public void setPayCommodityMoney(String payCommodityMoney) {
        this.payCommodityMoney = payCommodityMoney;
    }

    public String getPayNum() {
        return payNum;
    }

    public void setPayNum(String payNum) {
        this.payNum = payNum;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public String getPayPhone() {
        return payPhone;
    }

    public void setPayPhone(String payPhone) {
        this.payPhone = payPhone;
    }

    public String getPayEmail() {
        return payEmail;
    }

    public void setPayEmail(String payEmail) {
        this.payEmail = payEmail;
    }

    public String getPayState() {
        return payState;
    }

    public void setPayState(String payState) {
        this.payState = payState;
    }

    public String getVoucherState() {
        return voucherState;
    }

    public void setVoucherState(String voucherState) {
        this.voucherState = voucherState;
    }

    public String getVoucherUrl() {
        return voucherUrl;
    }

    public void setVoucherUrl(String voucherUrl) {
        this.voucherUrl = voucherUrl;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }
}
