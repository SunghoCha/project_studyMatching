package com.app.global.config.logging.filter;

import com.app.global.context.RequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
public class CustomRequestLoggingFilter extends AbstractRequestLoggingFilter {

    public CustomRequestLoggingFilter() {
        setIncludeClientInfo(true); // 클라이언트 정보 포함
        setIncludeQueryString(true); // 쿼리 문자열 포함
        setIncludePayload(true); // 요청 본문 포함
        setAfterMessagePrefix("요청 데이터: "); // 로그 메시지 접두사
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        String requestId = UUID.randomUUID().toString();
        RequestContext.setRequestId(requestId);

        log.info("[{}] [요청 전] {}", requestId, message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        String requestId = RequestContext.getRequestId();

        Long startTime = (Long) request.getAttribute("startTime");
        Long elapsedTime = System.currentTimeMillis() - startTime;

        ContentCachingResponseWrapper response = (ContentCachingResponseWrapper) request.getAttribute("response");
        byte[] byteArray = response.getContentAsByteArray();

        String responseBody = new String(byteArray, StandardCharsets.UTF_8);
        System.out.println("responseBody :" + responseBody);
        log.info("[{}] [요청 후] {} | 처리 시간 {} ms | 응답 상태 : {} | 클라이언트 IP : {} | 요청 본문 : {}",
                requestId, message, elapsedTime, response.getStatus(), request.getRemoteAddr(), responseBody);

        try {
            response.copyBodyToResponse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RequestContext.clear();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        request.setAttribute("response", responseWrapper);
        super.doFilterInternal(request, responseWrapper, filterChain);
    }
}
