package com.example.courseservice.data.dto.request;


import com.example.courseservice.data.constants.SortType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PaginationRequest {
    private int page;
    private int size;
    private String sortField;
    private SortType sortType;
}
