package com.achernov.cryptoarb.config.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabasePropertiesConfig {

  @Bean
  @ConfigurationProperties(prefix = "app.datasource.primary")
  public DataSourceProperties primaryDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties(prefix = "app.datasource.replica")
  public DataSourceProperties replicaDataSourceProperties() {
    return new DataSourceProperties();
  }
}
