package com.henglong.cloud.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity(name = "Cloud_Assets")
public class Assets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "assets_pay_id")
    @NotBlank(message = "支付id不能为空")
    private String assetsPayId;

    @Column(name = "assets_name")
    private String assetsName;

    @Column(name = "assets_type")
    private String assetsType;

    @Column(name = "assets_num")
    private String assetsNum;

    @Column(name = "assets_term")
    private String assetsTerm;

    @Column(name = "assets_value")
    private String assetsValue;

    @Column(name = "assets_profit")
    private BigDecimal assetsProfit;

    @Column(name = "assets_time")
    private String assetsTime;

    @Column(name = "assets_day")
    private String assetsDay;

    @Column(name = "assets_state")
    private String assetsState;

    @Column(name = "assets_phone")
    @NotBlank(message = "手机号不能为空")
    private String assetsPhone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAssetsPayId() {
        return assetsPayId;
    }

    public void setAssetsPayId(String assetsPayId) {
        this.assetsPayId = assetsPayId;
    }

    public String getAssetsName() {
        return assetsName;
    }

    public void setAssetsName(String assetsName) {
        this.assetsName = assetsName;
    }

    public String getAssetsType() {
        return assetsType;
    }

    public void setAssetsType(String assetsType) {
        this.assetsType = assetsType;
    }

    public String getAssetsNum() {
        return assetsNum;
    }

    public void setAssetsNum(String assetsNum) {
        this.assetsNum = assetsNum;
    }

    public String getAssetsTerm() {
        return assetsTerm;
    }

    public void setAssetsTerm(String assetsTerm) {
        this.assetsTerm = assetsTerm;
    }

    public String getAssetsValue() {
        return assetsValue;
    }

    public void setAssetsValue(String assetsValue) {
        this.assetsValue = assetsValue;
    }

    public BigDecimal getAssetsProfit() {
        return assetsProfit;
    }

    public void setAssetsProfit(BigDecimal assetsProfit) {
        this.assetsProfit = assetsProfit;
    }

    public String getAssetsTime() {
        return assetsTime;
    }

    public void setAssetsTime(String assetsTime) {
        this.assetsTime = assetsTime;
    }

    public String getAssetsDay() {
        return assetsDay;
    }

    public void setAssetsDay(String assetsDay) {
        this.assetsDay = assetsDay;
    }

    public String getAssetsState() {
        return assetsState;
    }

    public void setAssetsState(String assetsState) {
        this.assetsState = assetsState;
    }

    public String getAssetsPhone() {
        return assetsPhone;
    }

    public void setAssetsPhone(String assetsPhone) {
        this.assetsPhone = assetsPhone;
    }
}
