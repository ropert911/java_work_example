package com.quick.jdbc.repository;

import com.quick.jdbc.model.CDHHosts;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/10
 */
public interface CDHHostRepository extends Repository<CDHHosts, Long>{
    @Query(value = "select * from hosts", nativeQuery = true)
    List<CDHHosts> getAll();
}
