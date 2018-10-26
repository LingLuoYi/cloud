package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Assets;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetsJpa extends JpaRepository<Assets,Integer> {

    List<Assets> findByAssetsPhone(String phone);

    Assets findByAssetsPayId(String id);

    Assets findByAssetsPayIdAndAssetsPhone(String phone,String id);

}
