package com.henglong.cloud.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Entity(name = "Cloud_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phone" ,unique =true)
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "salt")
    private String salt;

    @Column(name = "name")
    private String name;

    @Email(message = "请正确输入邮箱")
    @Column(name = "email")
    private String email;

    /*头像链接*/
    @Column(name = "img_url")
    private String imgUrl;

    /*身份证号码*/
    @Column(name = "ID_card_no")
    private String IDCardNo;

    /*身份证图片链接*/
    @ElementCollection
    @Column(name = "IDCardImg")
    private Map<String,String> IDCardImg;

    @Column(name = "user_start")
    private Integer userStart;

    @Column(name = "random_code")
    private String RandomCode;

    @Column(name = "roles")
    private String Roles;

    @ElementCollection
    @Column(name = "wallet")
    private Map<String,String> Wallet;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getRandomCode() {
        return RandomCode;
    }

    public void setRandomCode(String randomCode) {
        RandomCode = randomCode;
    }

    public String getRoles() {
        return Roles;
    }

    public void setRoles(String roles) {
        Roles = roles;
    }

    public Map getWallet() {
        return Wallet;
    }

    public void setWallet(Map wallet) {
        Wallet = wallet;
    }

    public Map getIDCardImg() {
        return IDCardImg;
    }

    public void setIDCardImg(Map IDCardImg) {
        this.IDCardImg = IDCardImg;
    }

    public String getIDCardNo() {
        return IDCardNo;
    }

    public void setIDCardNo(String IDCardNo) {
        this.IDCardNo = IDCardNo;
    }

    public Integer getUserStart() {
        return userStart;
    }

    public void setUserStart(Integer userStart) {
        this.userStart = userStart;
    }
}
