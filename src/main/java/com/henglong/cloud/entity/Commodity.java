package com.henglong.cloud.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity(name = "Cloud_Commodity")
public class Commodity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "commodity_id")
    private String commodityId;

    @Column(name = "commodity_Name")
    private String commodityName;

    @Column(name = "commodity_Stock")
    private String commodityStock;

    @Column(name = "commodity_initial_Stock")
    private String commodityInitialStock;

    @Column(name = "commodity_Money")
    private String commodityMoney;

    @Column(name = "commodity_Type")
    private String commodityType;

    //交割时间，如果是0，则当天交割
    @Column(name = "commodity_time")
    private String commodityTime;

    //期限，如果为0，则是永久商品，期限商品则是交割之日起开始计算。
    @Column(name = "commodity_term")
    private String commodityTerm;

    @Column(name = "commodity_url")
    private String commodityUrl;

    @Transient
    private Integer HHHHH;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCommodityStock() {
        return commodityStock;
    }

    public void setCommodityStock(String commodityStock) {
        this.commodityStock = commodityStock;
    }

    public String getCommodityInitialStock() {
        return commodityInitialStock;
    }

    public void setCommodityInitialStock(String commodityInitialStock) {
        this.commodityInitialStock = commodityInitialStock;
    }

    public String getCommodityMoney() {
        return commodityMoney;
    }

    public void setCommodityMoney(String commodityMoney) {
        this.commodityMoney = commodityMoney;
    }

    public String getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(String commodityType) {
        this.commodityType = commodityType;
    }

    public String getCommodityTime() {
        return commodityTime;
    }

    public void setCommodityTime(String commodityTime) {
        this.commodityTime = commodityTime;
    }

    public String getCommodityUrl() {
        return commodityUrl;
    }

    public void setCommodityUrl(String commodityUrl) {
        this.commodityUrl = commodityUrl;
    }

    public String getCommodityTerm() {
        return commodityTerm;
    }

    public void setCommodityTerm(String commodityTerm) {
        this.commodityTerm = commodityTerm;
    }

    public Integer getHHHHH() {
        return HHHHH;
    }

    public void setHHHHH(Integer HHHHH) {
        this.HHHHH = HHHHH;
    }
}
