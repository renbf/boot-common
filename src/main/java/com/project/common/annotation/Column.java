package com.project.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库列注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /**标题-列中文名*/
    public String title();
    /**数据库字段名称*/
    public String colname();
    /**是否为主键*/
    public boolean isPk() default false;
}
