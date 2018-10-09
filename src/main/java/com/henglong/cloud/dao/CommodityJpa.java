package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommodityJpa extends JpaRepository<Commodity,Integer> {
   Commodity findByCommodityId (String id);
   List<Commodity> findByCommodityName (String name);
   List<Commodity> findByCommodityType (String type);
}
