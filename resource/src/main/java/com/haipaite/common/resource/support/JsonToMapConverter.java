 package com.haipaite.common.resource.support;
 
 import com.haipaite.common.utility.JsonUtils;
 import java.util.Collections;
 import java.util.Map;
 import java.util.Set;
 import org.springframework.core.convert.TypeDescriptor;
 import org.springframework.core.convert.converter.ConditionalGenericConverter;
 import org.springframework.core.convert.converter.GenericConverter;
 
 
 
 
 
 
 
 public class JsonToMapConverter
   implements ConditionalGenericConverter
 {
   public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
     if (sourceType.getType() != String.class) {
       return false;
     }
     if (!Map.class.isAssignableFrom(targetType.getType())) {
       return false;
     }
     return true;
   }
 
   
   public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
     return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Map.class));
   }
 
   
   public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
     String string = (String)source;
     return JsonUtils.string2Map(string, targetType.getMapKeyTypeDescriptor().getType(), targetType
         .getMapValueTypeDescriptor().getType());
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\support\JsonToMapConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */