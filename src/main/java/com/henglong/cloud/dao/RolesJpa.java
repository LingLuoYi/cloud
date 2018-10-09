package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolesJpa extends JpaRepository<Roles,Integer> {
    List<Roles> findByPhone(String phone);
}
