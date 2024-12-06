package com.alibaba.csp.sentinel.dashboard.datasource.entity.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MetricJPARepository extends CrudRepository<MetricJPAEntity, Long> {
    List<MetricJPAEntity> findAllByAppAndResourceAndTimestampBetween(String app, String resource, Date timestampAfter, Date timestampBefore);

    @Query("SELECT DISTINCT resource FROM MetricJPAEntity WHERE app = :app ORDER BY blockQps DESC")
    List<String> findDistinctResourceByAppOrderByBlockQps(@Param("app") String app);
}
