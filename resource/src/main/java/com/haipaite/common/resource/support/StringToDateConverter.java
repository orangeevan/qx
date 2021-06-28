 package com.haipaite.common.resource.support;

 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import org.springframework.core.convert.converter.Converter;







 public class StringToDateConverter
   implements Converter<String, Date>
 {
   public Date convert(String source) {
     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     try {
       return df.parse(source);
     } catch (ParseException e) {
       throw new IllegalArgumentException("字符串[" + source + "]不符合格式要求[yyyy-MM-dd HH:mm:ss]", e);
     }
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\support\StringToDateConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */