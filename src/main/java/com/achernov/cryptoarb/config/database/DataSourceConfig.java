package com.achernov.cryptoarb.config.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

  private final DataSourceProperties primaryDataSourceProperties;
  private final DataSourceProperties replicaDataSourceProperties;

  @Bean
  public DataSource primaryDataSource() {
    HikariDataSource dataSource = new HikariDataSource();

    dataSource.setJdbcUrl(primaryDataSourceProperties.getUrl());
    dataSource.setUsername(primaryDataSourceProperties.getUsername());
    dataSource.setPassword(primaryDataSourceProperties.getPassword());
    dataSource.setDriverClassName(primaryDataSourceProperties.getDriverClassName());

    return dataSource;
  }

  @Bean
  public DataSource replicaDataSource() {
    HikariDataSource dataSource = new HikariDataSource();

    dataSource.setJdbcUrl(replicaDataSourceProperties.getUrl());
    dataSource.setUsername(replicaDataSourceProperties.getUsername());
    dataSource.setPassword(replicaDataSourceProperties.getPassword());
    dataSource.setDriverClassName(replicaDataSourceProperties.getDriverClassName());

    return dataSource;
  }

  @Bean
  public DataSource routingDataSource(
          @Qualifier("primaryDataSource") DataSource primaryDataSource,
          @Qualifier("replicaDataSource") DataSource replicaDataSource) {
    RoutingDataSource routingDataSource = new RoutingDataSource();

    Map<Object, Object> dataSourceMap = new HashMap<>();
    dataSourceMap.put(DbContextHolder.DbType.PRIMARY, primaryDataSource);
    dataSourceMap.put(DbContextHolder.DbType.REPLICA, replicaDataSource);

    routingDataSource.setTargetDataSources(dataSourceMap);
    routingDataSource.setDefaultTargetDataSource(primaryDataSource);

    return routingDataSource;
  }

  @Bean
  @Primary
  public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
    return new LazyConnectionDataSourceProxy(routingDataSource);
  }
}
