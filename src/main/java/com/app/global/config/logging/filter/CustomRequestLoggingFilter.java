package com.app.global.config.logging.filter;

import com.app.global.context.RequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component // 비동기 처리 못하는 필터라 보류
public class CustomRequestLoggingFilter extends AbstractRequestLoggingFilter {

    public CustomRequestLoggingFilter() {
        setIncludeClientInfo(true); // 클라이언트 정보 포함
        setIncludeQueryString(true); // 쿼리 문자열 포함
        setIncludePayload(true); // 요청 본문 포함
        setAfterMessagePrefix("REQUEST DATA: "); // 로그 메시지 접두사
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        String requestId = UUID.randomUUID().toString();
        RequestContext.setRequestId(requestId);

        log.info("[{}] [Before request] {}", requestId, message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        String requestId = RequestContext.getRequestId();
        log.info("[{}] [After request] {}", requestId, message);

        RequestContext.clear();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            super.doFilterInternal(request, response, filterChain);
        } finally {
            RequestContext.clear();
        }
    }
}
