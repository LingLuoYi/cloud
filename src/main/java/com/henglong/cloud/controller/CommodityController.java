package com.henglong.cloud.controller;

import com.henglong.cloud.dao.CommodityJpa;
import com.henglong.cloud.entity.Commodity;
import com.henglong.cloud.service.CommodityService;
import com.henglong.cloud.service.FileService;
import com.henglong.cloud.util.ExceptionUtil;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.LoadFile;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/commodity")
public class CommodityController {

    private static final Logger log= LoggerFactory.getLogger(CommodityController.class);

    @Autowired
    private CommodityJpa commodityJpa;

    @Autowired
    private CommodityService commodityService;

    @Autowired
    private FileService fileService;

    @RequestMapping("/commodity_info_all")
    public Json CommodityInfoAll(){
        try {
            return ExceptionUtil.Success(200,"成功",commodityService.CommodityAllRead());
        }catch (Exception e){
            log.info("出现未知错误！！"+e);
            return ExceptionUtil.error(500,e.getMessage());
        }
    }

    @RequestMapping("/commodity_info_id")
    public Json CommodityInfoId(@RequestParam("id") String id){
        return ExceptionUtil.Success(200,"成功",commodityJpa.findByCommodityId(id));
    }

    @RequestMapping("/commodity_info_name")
    public Json CommodityInfoName(@RequestParam("name") String name){
        return ExceptionUtil.Success(200,"成功",commodityJpa.findByCommodityName(name));
    }

    @RequestMapping("/commodity_info_type")
    public Json CommodityInfoType(@RequestParam("type") String type){
        return ExceptionUtil.Success(200,"成功",commodityJpa.findByCommodityType(type));
    }

    @RequiresPermissions("storehouse:install")
    @RequestMapping("/file_img")
    public Json File(@RequestParam(value = "commodity_img",required = false) MultipartFile file , @RequestParam("id") String id) throws Exception {
        return ExceptionUtil.Success(200,fileService.CommodityFile(file,id));
    }

    /*图片加载连接*/
    @RequestMapping("/commodity_img/{name}")
    public void PayImg(HttpServletResponse response,@PathVariable("name")String name){
        FileInputStream fis = null;
        response.setContentType("image/gif");
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(LoadFile.Path()+"/img/commodity/"+name);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.error("显示图片发生了异常",e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("图片显示类，关闭流出现了异常",e);
                }
            }
        }
    }


    /**
     * 添加商品方法
     * @param commodity
     * @return
     * @throws Exception
     */
    @RequiresPermissions(value = {"storehouse:install","admin:install"},logical = Logical.OR)
    @PostMapping("/commodity_add")
    public Json CommodityAdd(@Valid Commodity commodity) throws Exception {
        log.info("添加商品【"+commodity.getCommodityName()+"】");
        try {
            return ExceptionUtil.Success(200,"成功",commodityService.CommodityAdd(commodity));
        }catch (Exception e){
            log.info("添加商品出现了未知错误！！"+e);
            return ExceptionUtil.error(-1,e.getMessage());
        }
    }

    /**
     * 修改商品方法
     * @param commodity
     * @return
     * @throws Exception
     */
    @RequiresPermissions(value = {"storehouse:update","admin:update"},logical= Logical.OR)
    @PostMapping("/commodity_update")
    public Json CommodityUpdate(@Valid Commodity commodity) throws Exception {
        log.info("修改商品【"+commodity.getCommodityName()+"】");
        Commodity commodity1 = new Commodity();
        if (commodity.getCommodityId() != null) {
            commodity1 = commodityService.CommodityOneRead(commodity.getCommodityId());
            if (commodity1 == null)
                return ExceptionUtil.error(500,"商品id不予许被改动");
            else
               return ExceptionUtil.Success(200,"成功",commodityService.CommodityAdd(commodity)) ;
        }
        return ExceptionUtil.error(500,"未知错误");
    }



    /**
     * 购买信息
     * **重要**
     * @param id
     * @param num
     * @return
     */
    @RequiresPermissions(value = {"user:install","admin:install"},logical = Logical.OR)
    @PostMapping("/commodity_purchase")
    public Json CommodityPurchase(@RequestParam("id") String id, @RequestParam("num") String num) {
            log.info("这里是Controller,接受到id【"+id+"】数量【"+num+"】");
        try {
            return ExceptionUtil.Success(200,"成功",commodityService.CommodityPurchase(id, num));
        }catch (Exception e){
            return ExceptionUtil.error(-1,e.getMessage());
        }
    }
}
