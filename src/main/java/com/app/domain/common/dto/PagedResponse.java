package com.app.domain.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagedResponse<T> {

    private List<T> content;       // 데이터 리스트
    private int currentPage;       // 현재 페이지
    private int totalPages;        // 총 페이지 수
    private long totalCount;    // 총 데이터 개수
    private int size;

    @Builder
    public PagedResponse(List<T> content, int currentPage, int totalPages, long totalCount, int size) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
        this.size = size;
    }
}
