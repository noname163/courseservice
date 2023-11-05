package com.example.courseservice.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.PaginationRequest;
import com.example.courseservice.exceptions.BadRequestException;



@Component
public class PageableUtil {

    public Pageable getPageable() throws BadRequestException {
        return PageRequest.of(0, 10);
    }

    public Pageable getPageable(PaginationRequest paginationRequest) {
        int page = paginationRequest != null ? paginationRequest.getPage() : 0;
        int size = paginationRequest != null ? paginationRequest.getSize() : 20;
        String sortField = paginationRequest != null ? paginationRequest.getSortField() : "";
        SortType sortType = paginationRequest != null ? paginationRequest.getSortType() : SortType.ASC;

        if (!sortField.isBlank()) {
            Sort sort = sortType == SortType.ASC ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
            return PageRequest.of(page, size, sort);
        }

        return PageRequest.of(page, size);
    }

    public Pageable getPageable(Integer page, Integer size, String field, SortType sortType) throws BadRequestException {
        page = page == null ? 0 : page;
        size = size == null ? 20 : size;
        sortType = sortType != null ? sortType : SortType.ASC;

        if (field != null && !field.isBlank()) {
            Sort sort = sortType == SortType.ASC ? Sort.by(field).ascending() : Sort.by(field).descending();
            return PageRequest.of(page, size, sort);
        }

        return PageRequest.of(page, size);
    }

}
