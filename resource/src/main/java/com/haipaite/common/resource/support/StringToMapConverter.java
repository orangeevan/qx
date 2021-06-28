 package com.haipaite.common.resource.support;
 
 import com.haipaite.common.utility.JsonUtils;
 import java.util.Map;
 import org.springframework.core.convert.converter.Converter;
 
 
 
 public class StringToMapConverter
   implements Converter<String, Map<String, Object>>
 {
   public Map<String, Object> convert(String source) {
     return JsonUtils.string2Map(source);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\support\StringToMapConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */