package com.app.global.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@Slf4j
@Component
public class CustomRequestLoggingFilter extends AbstractRequestLoggingFilter {

    public CustomRequestLoggingFilter() {
        setIncludeClientInfo(true); // 클라이언트 정보 포함
        setIncludeQueryString(true); // 쿼리 문자열 포함
        setIncludePayload(true); // 요청 본문 포함
        setAfterMessagePrefix("REQUEST DATA: "); // 로그 메시지 접두사
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        log.info("Before request: " + message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        log.info("After request: " + message);
    }
}
