package com.app.global.config.logging.aspect;

import com.app.global.config.auth.dto.CurrentUser;
import com.app.global.context.RequestContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
//@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    @Around("execution(* com.app..service.*.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String requestId = RequestContext.getRequestId();

        Object[] args = joinPoint.getArgs();
        String userId = extractUserId(args);

        log.info("[{}] [{}] 호출 됨. 사용자 ID: {}, 인자: {}",
                requestId, className + "." + methodName, userId, formatArgs(args));

        Object result;
        try {
            result = joinPoint.proceed();
            log.info("[{}] [{}] 응답 성공: {}", requestId, className + "." + methodName, formatArgs(args));
        } catch (Exception e) {
            log.error("[{}] [{}] 응답 실패: {}", requestId, className + "." + methodName, e.getMessage() , e);
            throw e;
        }
        return result;
    }

    private String formatArgs(Object[] args) {
        return Arrays.stream(args)
                .map(arg -> arg == null ? "null" : arg.getClass().getSimpleName() + "(" + arg.toString() + ")")
                .collect(Collectors.joining(", "));
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
