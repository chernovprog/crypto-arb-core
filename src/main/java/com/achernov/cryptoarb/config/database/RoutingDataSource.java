package com.achernov.cryptoarb.config.database;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

  @Override
  protected @Nullable Object determineCurrentLookupKey() {
    return DbContextHolder.getDbType() != null
            ? DbContextHolder.getDbType()
            : DbContextHolder.DbType.PRIMARY;
  }
}
