package com.alibaba.csp.sentinel.dashboard.repository.metric;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.jpa.MetricJPAEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.jpa.MetricJPARepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component("mysqlRepository")
public class MysqlMetricsRepository implements MetricsRepository<MetricEntity> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private MetricJPARepository jpaRepository;

    @Autowired
    public void setMetricJPARepository(MetricJPARepository jpaRepository) {
        log.info("+++++ Persist metric in mysql");
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(MetricEntity metric) {
        if (metric == null || StringUtils.isEmpty(metric.getApp())) {
            return;
        }
        MetricJPAEntity jpaEntity = new MetricJPAEntity();
        BeanUtils.copyProperties(metric, jpaEntity);
        this.jpaRepository.save(jpaEntity);
    }

    @Override
    public void saveAll(Iterable<MetricEntity> metrics) {
        if (metrics == null) {
            return;
        }

        metrics.forEach(this::save);
    }

    @Override
    public List<MetricEntity> queryByAppAndResourceBetween(String app, String resource, long startTime, long endTime) {
        List<MetricEntity> results = new ArrayList<>();
        if (StringUtils.isEmpty(app)) {
            return results;
        }

        if (StringUtils.isEmpty(resource)) {
            return results;
        }

        List<MetricJPAEntity> list = this.jpaRepository.findAllByAppAndResourceAndTimestampBetween(app, resource, Date.from(Instant.ofEpochMilli(startTime)), Date.from(Instant.ofEpochMilli(endTime)));
        return list.stream().map(jpa -> {
            MetricEntity metricEntity = new MetricEntity();
            BeanUtils.copyProperties(jpa, metricEntity);
            return metricEntity;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<String> listResourcesOfApp(String app) {
        return this.jpaRepository.findDistinctResourceByAppOrderByBlockQps(app);
    }
}
