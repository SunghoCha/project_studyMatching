package com.app.global.config.logging.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
//@Component
public class CustomAsyncRequestLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        // 비동기 요청인 경우엔 AsyncListener 추가
        if (httpRequest.isAsyncSupported()) {
            if (!httpRequest.isAsyncStarted()) {
                httpRequest.startAsync();
            }
            httpRequest.getAsyncContext().addListener(new AsyncListener() {
                @Override
                public void onComplete(AsyncEvent asyncEvent) throws IOException {
                    log.info("비동기 요청 완료: {}", httpRequest.getRequestURI());
                }

                @Override
                public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                    log.warn("비동기 요청 타임아웃: {}", httpRequest.getRequestURI());
                }

                @Override
                public void onError(AsyncEvent asyncEvent) throws IOException {
                    log.error("비동기 요청 에러", asyncEvent.getThrowable());
                }

                @Override
                public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                    log.info("비동기 요청 시작됨: {}", httpRequest.getRequestURI());
                }
            });
        }

        // 동기, 비동기 요청 처리
        log.info("요청 전: method={}, URI={}, clientIp={}",
                httpRequest.getMethod(), httpRequest.getRequestURI(), httpRequest.getRemoteAddr());

        // 요청 처리
        filterChain.doFilter(servletRequest, servletResponse);

        // 응답 후 로깅
        log.info("요청 후: status={}", httpResponse.getStatus());

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
