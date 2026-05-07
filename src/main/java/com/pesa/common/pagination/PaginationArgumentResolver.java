package com.pesa.common.pagination;

import com.pesa.common.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PaginationArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PaginationParam.class)
            && Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        PaginationParam options = parameter.getParameterAnnotation(PaginationParam.class);
        if (options == null) {
            throw new BadRequestException("Missing pagination configuration");
        }

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new BadRequestException("Invalid request context");
        }

        int page = parseIntOrDefault(request.getParameter("page"), options.defaultPage(), "page");
        int size = parseIntOrDefault(request.getParameter("size"), options.defaultSize(), "size");

        if (page < 0) {
            throw new BadRequestException("Parameter 'page' must be >= 0");
        }
        if (size <= 0) {
            throw new BadRequestException("Parameter 'size' must be > 0");
        }
        if (size > options.maxSize()) {
            throw new BadRequestException("Parameter 'size' must be <= " + options.maxSize());
        }

        String sortBy = trimOrDefault(request.getParameter("sortBy"), options.defaultSortBy());
        String sortDirRaw = trimOrDefault(request.getParameter("sortDir"), options.defaultSortDirection().name());

        if (sortBy.isBlank()) {
            return PageRequest.of(page, size);
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirRaw);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Parameter 'sortDir' must be 'asc' or 'desc'");
        }

        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    private int parseIntOrDefault(String raw, int defaultValue, String fieldName) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Parameter '" + fieldName + "' must be an integer");
        }
    }

    private String trimOrDefault(String raw, String defaultValue) {
        if (raw == null) {
            return defaultValue;
        }
        String value = raw.trim();
        return value.isEmpty() ? defaultValue : value;
    }
}
