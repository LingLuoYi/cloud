package com.henglong.cloud.dao;

import com.henglong.cloud.entity.PutForward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PutForwardJpa extends JpaRepository<PutForward,Integer>{

    PutForward findByAssetsPayId(String id);
}
