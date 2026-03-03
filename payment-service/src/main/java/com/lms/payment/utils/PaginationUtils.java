package com.lms.payment.utils;

import com.lms.payment.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;

public class PaginationUtils {
    private PaginationUtils(){}

    public static Pageable createPageRequest(int page, int size){
        return PageRequest.of(page,size);
    }

    public static <T,R> PageResponse<R> toPageResponse(Page<T> page, Function<T,R> mapper){
        List<R> content = page.getContent().stream().map(mapper).toList();
        return new PageResponse<>(
                content,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize());
    }
    public static <T, R> PageResponse<R> toPageResponseFromList(
            Page<T> page,
            Function<List<T>, List<R>> listMapper
    ) {
        List<R> content = listMapper.apply(page.getContent());
        return new PageResponse<>(
                content,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

}
