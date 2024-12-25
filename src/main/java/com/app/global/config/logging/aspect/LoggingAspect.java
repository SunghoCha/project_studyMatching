package com.app.global.config.logging.aspect;

import com.app.global.config.auth.dto.CurrentUser;
import com.app.global.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.app..controller.*.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String requestId = RequestContext.getRequestId();

        Object[] args = joinPoint.getArgs();
        String userId = extractUserId(args);

        log.info("[{}] [{}] 호출 됨. 사용자 ID: {}, 인자: {}",
                requestId, className + "." + methodName, userId, args);

        Object result;
        try {
            result = joinPoint.proceed();
            log.info("[{}] [{}] 응답 성공: {}", requestId, className + "." + methodName, result);
        } catch (Exception e) {
            log.error("[{}] [{}] 응답 실패: {}", requestId, className + "." + methodName, e.getMessage() , e);
            throw e;
        }
        return result;
    }

    private String extractUserId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof CurrentUser) {
                return String.valueOf(((CurrentUser) arg).getId());
            }
        }
        return "Anonymous";
    }
}
