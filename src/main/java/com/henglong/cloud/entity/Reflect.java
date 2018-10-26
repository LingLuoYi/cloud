package com.henglong.cloud.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "cloud_reflect")
public class Reflect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reflect_assetsId")
    private String assetsId;

    @Column(name = "reflect_name")
    private String name;

    @Column(name = "reflect_phone")
    private String phone;

    @Column(name = "reflect_email")
    private String email;

    @Column(name = "reflect_IDCard")
    private String IDCard;

    @Column(name = "reflect_assetsType")
    private String assetsType;

    @Column(name = "reflect_num")
    private BigDecimal num;

    @Column(name = "reflect_wallet")
    private String wallet;

    @Column(name = "reflect_status")
    private String status;

    @Column(name = "reflect_remarks")
    private String remarks;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAssetsId() {
        return assetsId;
    }

    public void setAssetsId(String assetsId) {
        this.assetsId = assetsId;
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

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public String getAssetsType() {
        return assetsType;
    }

    public void setAssetsType(String assetsType) {
        this.assetsType = assetsType;
    }

    public BigDecimal getNum() {
        return num;
    }

    public void setNum(BigDecimal num) {
        this.num = num;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
