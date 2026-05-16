package com.achernov.cryptoarb.aspect;

import com.achernov.cryptoarb.config.database.DbContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

@Aspect
@Component
@Order(0)
public class DataSourceAspect {

  @Around("@annotation(org.springframework.transaction.annotation.Transactional) || " +
          "@within(org.springframework.transaction.annotation.Transactional)")
  public Object manageDataSourceContext(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    Transactional transactional = method.getAnnotation(Transactional.class);

    if (transactional == null) {
      transactional = joinPoint.getTarget().getClass().getAnnotation(Transactional.class);
    }

    if (transactional != null && transactional.readOnly()) {
      DbContextHolder.setDbType(DbContextHolder.DbType.REPLICA);
    } else {
      DbContextHolder.setDbType(DbContextHolder.DbType.PRIMARY);
    }

    try {
      return joinPoint.proceed();
    } finally {
      DbContextHolder.clear();
    }
  }
}
