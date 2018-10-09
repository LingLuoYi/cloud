package com.henglong.cloud.dao;

import com.henglong.cloud.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserJpa extends JpaRepository<User,Integer> {
    User findByPhone(String phone);

    User findByEmail(String email);

}
