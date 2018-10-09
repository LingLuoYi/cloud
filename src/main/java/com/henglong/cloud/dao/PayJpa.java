package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayJpa extends JpaRepository<Pay,Integer> {
    List<Pay> findByPayPhone(String phone);

    Pay findByPayId(String id);

    Pay findByPayOrderId(String orderId);

    List<Pay> findByVoucherState(String vs);

    List<Pay> findByPayState(String s);

    Pay findByPayIdAndPayPhone(String id,String phone);

    Pay findByPayOrderIdAndPayPhone(String id ,String phone);
}
