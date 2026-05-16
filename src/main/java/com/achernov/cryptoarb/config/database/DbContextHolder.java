package com.achernov.cryptoarb.config.database;

public class DbContextHolder {
  public enum DbType {PRIMARY, REPLICA}

  private static final ThreadLocal<DbType> CONTEXT = new ThreadLocal<>();

  public static void setDbType(DbType dbType) {
    CONTEXT.set(dbType);
  }

  public static DbType getDbType() {
    return CONTEXT.get();
  }

  public static void clear() {
    CONTEXT.remove();
  }
}
