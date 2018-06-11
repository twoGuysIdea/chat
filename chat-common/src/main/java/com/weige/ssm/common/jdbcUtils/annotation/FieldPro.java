package com.weige.ssm.common.jdbcUtils.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface FieldPro {

    String name() default "";

    String JDBCName() default "";
}
