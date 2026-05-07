package com.pesa.common.pagination;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.domain.Sort;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PaginationParam {

    int defaultPage() default 0;

    int defaultSize() default 20;

    int maxSize() default 100;

    String defaultSortBy() default "";

    Sort.Direction defaultSortDirection() default Sort.Direction.DESC;
}
