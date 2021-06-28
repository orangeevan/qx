 package com.haipaite.common.resource.support;
 
 import com.alibaba.fastjson.JSONArray;
 import com.haipaite.common.utility.JsonUtils;
 import java.util.Collections;
 import java.util.List;
 import java.util.Set;
 import org.springframework.core.convert.TypeDescriptor;
 import org.springframework.core.convert.converter.ConditionalGenericConverter;
 import org.springframework.core.convert.converter.GenericConverter;
 
 
 
 
 
 
 public class StringToList
   implements ConditionalGenericConverter
 {
   public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
     if (sourceType.getType() != String.class) {
       return false;
     }
     if (!List.class.isAssignableFrom(targetType.getType())) {
       return false;
     }
     return true;
   }
 
   
   public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
     return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, List.class));
   }
 
   
   public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
     String content = (String)source;
     if (targetType.getElementTypeDescriptor().getType().isPrimitive()) {
       return JsonUtils.string2Object(content, targetType.getObjectType());
     }
     return JSONArray.parse(content);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\support\StringToList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */