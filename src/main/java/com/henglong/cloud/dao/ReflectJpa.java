package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Reflect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReflectJpa extends JpaRepository<Reflect,Integer> {

    List<Reflect> findByPhone(String phone);
}
