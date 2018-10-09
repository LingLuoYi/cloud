package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankJpa extends JpaRepository<Bank,Integer> {
}
