package com.henglong.cloud.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import java.text.ParseException;

@Service
public class StaticService {

    private static final Logger log = LoggerFactory.getLogger(StaticService.class);

    @Autowired
    private AssetsService assetsService;

    @Autowired
    private SpreadService spreadService;

    public void Static(){
        try {
            assetsService.AssetsUpdate();
        } catch (ParseException e) {
            log.error("资产信息更新失败，堆栈追踪",e);
        }catch (Exception e1){
            log.error("未知错误",e1);
        }
    }
}
