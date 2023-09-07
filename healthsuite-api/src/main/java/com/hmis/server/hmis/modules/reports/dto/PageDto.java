package com.hmis.server.hmis.modules.reports.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class PageDto {
    private int pageNumber = 0;
    private int pageSize = 10;
    private int totalPages;
    private long totalElements;

    public PageDto(int pageNumber, int pageSize, int totalPages, long totalElements) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
