package com.henglong.cloud.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity(name = "Cloud_Order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_Id",unique =true)
    @NotBlank(message = "订单id不能为空")
    private String orderId;

    @Column(name = "order_commodity_id")
    @NotBlank(message = "商品id不能为空")
    private String orderCommodityId;

    @Column(name = "order_Commodity_Name")
    private String orderCommodityName;

    @Column(name = "order_Commodity_Type")
    private String orderCommodityType;

    @Column(name = "order_Name")
    private String orderName;

    @Column(name = "order_Money")
    private String orderMoney;

    @Column(name = "order_Num")
    private String orderNum;

    @Column(name = "order_Term")
    private String orderTerm;

    @Column(name = "order_Start_Time")
    private String orderStartTime;

    @Column(name = "order_Stop_Time")
    private String orderStopTime;

    @Column(name = "order_State")
    private String orderState;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Column(name = "email")
    @Email(message = "请正确输入邮箱")
    private String email;

    @Column()
    private Date orderTime;

    public Order(){
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(String orderMoney) {
        this.orderMoney = orderMoney;
    }

    public String getOrderTerm() {
        return orderTerm;
    }

    public void setOrderTerm(String orderTerm) {
        this.orderTerm = orderTerm;
    }

    public String getOrderStartTime() {
        return orderStartTime;
    }

    public void setOrderStartTime(String orderStartTime) {
        this.orderStartTime = orderStartTime;
    }

    public String getOrderStopTime() {
        return orderStopTime;
    }

    public void setOrderStopTime(String orderStopTime) {
        this.orderStopTime = orderStopTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrderCommodityId() {
        return orderCommodityId;
    }

    public void setOrderCommodityId(String orderCommodityId) {
        this.orderCommodityId = orderCommodityId;
    }

    public String getOrderCommodityName() {
        return orderCommodityName;
    }

    public void setOrderCommodityName(String orderCommodityName) {
        this.orderCommodityName = orderCommodityName;
    }

    public String getOrderCommodityType() {
        return orderCommodityType;
    }

    public void setOrderCommodityType(String orderCommodityType) {
        this.orderCommodityType = orderCommodityType;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }
}
