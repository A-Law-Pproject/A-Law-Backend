package com.service.alaw.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAspect {

    /// 서비스 계층
    @Pointcut("execution(* com.service.alaw.platform..application..*Usecase.*(..))")
    private void applicationLayer() {}

    /// 인증 계층
    @Pointcut("execution(* com.service.alaw.security..application..*Service*.*(..))")
    private void authLayer() {}


    /// 성능 시간 테스트
    @Around("applicationLayer() || authLayer()")
    public Object logProcessTime(ProceedingJoinPoint joinPoint) throws Throwable {

        /// 시작 시간
        long start = System.currentTimeMillis();

        /// 실제 메서드 실행
        Object proceed = joinPoint.proceed();

        /// 마무리 된 시간
        long executionTime = System.currentTimeMillis() - start;

        /// 서비스명 추출하기
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        String[] split = declaringTypeName.split("\\.");
        String serviceName = split[split.length - 1];

        log.info("[서비스 로깅] 실행 메서드: {}.{} 실행 시간 = {}ms",
                serviceName,
                joinPoint.getSignature().getName(),
                executionTime);

        return proceed;
    }


}
