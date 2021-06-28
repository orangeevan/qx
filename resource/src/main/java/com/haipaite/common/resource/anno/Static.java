package com.haipaite.common.resource.anno;

import java.beans.PropertyEditor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Static {
  String value() default "";
  
  Class<? extends PropertyEditor> converter() default PropertyEditor.class;
  
  boolean required() default true;
  
  boolean unique() default false;
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\anno\Static.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */