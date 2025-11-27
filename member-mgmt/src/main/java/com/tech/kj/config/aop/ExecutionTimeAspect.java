package com.tech.kj.config.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

    @Around("@annotation(com.tech.kj.config.aop.LogTime)")
    public Object logExecutionTIme(ProceedingJoinPoint joinPoint) throws Throwable {
        long start  = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
        }finally{
            long end  = System.currentTimeMillis();

            String methodName = joinPoint.getSignature().getName();
            log.info("Execution time of {} :: {} ms", methodName, (end - start));
        }
        return result;

    }
}
