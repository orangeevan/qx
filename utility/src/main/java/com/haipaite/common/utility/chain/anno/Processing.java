package com.haipaite.common.utility.chain.anno;

import com.haipaite.common.utility.chain.Way;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Processing {
  String name();
  
  int index() default 0;
  
  Way way() default Way.IN;
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\chain\anno\Processing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */