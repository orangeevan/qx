package com.haipaite.common.utility.chain.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface InNotice {
  Type type() default Type.CONTENT;
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\chain\anno\InNotice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */