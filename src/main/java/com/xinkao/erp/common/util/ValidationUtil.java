package com.xinkao.erp.common.util;

import cn.hutool.core.util.StrUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;


public class ValidationUtil {

    private static volatile Validator VALIDATOR;

    private ValidationUtil() {
    }

    
    @NonNull
    public static Validator getValidator() {
        if (VALIDATOR == null) {
            synchronized (ValidationUtil.class) {
                if (VALIDATOR == null) {

                    VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
                }
            }
        }

        return VALIDATOR;
    }

    
    public static void validate(Object obj, Class<?>... groups) {

        Validator validator = getValidator();

        if (obj instanceof Iterable) {

            validate((Iterable<?>) obj, groups);
        } else {

            Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj, groups);

            if (!CollectionUtils.isEmpty(constraintViolations)) {

                throw new ConstraintViolationException(constraintViolations);
            }
        }
    }

    
    public static void validate(@Nullable Iterable<?> objs, @Nullable Class<?>... groups) {
        if (objs == null) {
            return;
        }

        Validator validator = getValidator();

        AtomicInteger i = new AtomicInteger(0);
        final Set<ConstraintViolation<?>> allViolations = new LinkedHashSet<>();
        objs.forEach(obj -> {
            int index = i.getAndIncrement();
            Set<? extends ConstraintViolation<?>> violations = validator.validate(obj, groups);
            violations.forEach(violation -> {
                Path path = violation.getPropertyPath();
                if (path instanceof PathImpl) {
                    PathImpl pathImpl = (PathImpl) path;
                    pathImpl.makeLeafNodeIterableAndSetIndex(index);
                }
                allViolations.add(violation);
            });
        });
        if (!CollectionUtils.isEmpty(allViolations)) {
            throw new ConstraintViolationException(allViolations);
        }
    }

    
    @NonNull
    public static Map<String, String> mapWithValidError(
        Set<ConstraintViolation<?>> constraintViolations) {
        if (CollectionUtils.isEmpty(constraintViolations)) {
            return Collections.emptyMap();
        }

        Map<String, String> errMap = new HashMap<>(4);

        constraintViolations.forEach(constraintViolation ->
            errMap.put(constraintViolation.getPropertyPath().toString(),
                constraintViolation.getMessage()));
        return errMap;
    }

    @NonNull
    public static String strWithValidError(
        Set<ConstraintViolation<?>> constraintViolations) {
        if (CollectionUtils.isEmpty(constraintViolations)) {
            return "";
        }
        StringBuffer strErrBuffer = new StringBuffer();
        constraintViolations.forEach(constraintViolation ->
                strErrBuffer.append(constraintViolation.getMessage() + ";"));
        return StrUtil.removeSuffix(strErrBuffer.toString(), ";");
    }


    
    public static Map<String, String> mapWithFieldError(@Nullable List<FieldError> fieldErrors) {
        if (CollectionUtils.isEmpty(fieldErrors)) {
            return Collections.emptyMap();
        }

        Map<String, String> errMap = new HashMap<>(4);
        fieldErrors.forEach(
            filedError -> errMap.put(filedError.getField(), filedError.getDefaultMessage()));
        return errMap;
    }

    
    public static String strWithFieldError(@Nullable List<FieldError> fieldErrors) {
        StringBuffer strErrBuffer = new StringBuffer();
        if (CollectionUtils.isEmpty(fieldErrors)) {
            return "";
        }
        fieldErrors.forEach(
            filedError -> strErrBuffer.append(filedError.getDefaultMessage() + ";"));
        return StrUtil.removeSuffix(strErrBuffer.toString(), ";");
    }
}
