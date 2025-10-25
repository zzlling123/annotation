package com.xinkao.erp.common.validation.validator;

import cn.hutool.core.util.StrUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.xinkao.erp.common.validation.constraint.Date;

public class DateValidatorForCharSequence implements ConstraintValidator<Date, CharSequence> {

    private String pattern;

    @Override
    public void initialize(Date parameters) {
         this.pattern = parameters.pattern();
    }

    @Override
    public boolean isValid(CharSequence charSequence,
        ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isEmpty(charSequence)) {
            return true;
        } else {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            format.setLenient(false);
            try {
                format.parse(charSequence.toString());
            } catch (ParseException e) {
                return false;
            }
            return true;
        }
    }

}
