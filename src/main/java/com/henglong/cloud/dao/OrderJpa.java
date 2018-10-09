package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderJpa extends JpaRepository<Order,Integer>{

    List<Order> findByPhone(String phone);

    Order findByOrderIdAndPhone(String id , String phone);

    Order findByOrderId(String orderId);
}
