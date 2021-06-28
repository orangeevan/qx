 package com.haipaite.common.utility;
 
 import com.alibaba.fastjson.JSON;
 import com.alibaba.fastjson.TypeReference;
 import java.lang.reflect.Type;
 import java.nio.charset.Charset;
 import java.util.List;
 import java.util.Map;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 
 
 
 
 
 public final class JsonUtils
 {
   private JsonUtils() {
     throw new IllegalAccessError("该类不允许实例化");
   }
   
   private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
 
   
   private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);
 
 
 
 
 
 
   
   public static String object2String(Object object) {
     return JSON.toJSONString(object, false);
   }
 
 
 
 
 
 
   
   public static String map2String(Map<?, ?> map) {
     return JSON.toJSONString(map, false);
   }
   
   private static TypeReference<Map<String, Object>> mapType = new TypeReference<Map<String, Object>>()
     {
     
     };
 
 
 
 
 
   
   public static Map<String, Object> string2Map(String content) {
     return (Map<String, Object>)JSON.parseObject(content, mapType, new com.alibaba.fastjson.parser.Feature[0]);
   }
 
 
 
 
 
 
 
 
   
   public static <T> List<T> string2List(String content, Class<T> clz) {
     return JSON.parseArray(content, clz);
   }
 
 
 
 
 
 
 
 
   
   public static <T> T string2Object(String content, Class<T> clz) {
     return (T)JSON.parseObject(content, clz);
   }
   
   public static <K, V> Map<K, V> string2Map(String content, Class<K> keyType, Class<V> valueType) {
     TypeReference<Map<K, V>> mapType = new TypeReference<Map<K, V>>(new Type[] { keyType, valueType }) {  }
       ;
     return (Map<K, V>)JSON.parseObject(content, mapType, new com.alibaba.fastjson.parser.Feature[0]);
   }
   
   public static byte[] encode(Object obj) {
     String json = toJson(obj, false);
     if (json != null) {
       return json.getBytes(CHARSET_UTF8);
     }
     return null;
   }
   
   public static String toJson(Object obj, boolean prettyFormat) {
     return JSON.toJSONString(obj, prettyFormat);
   }
   
   public static <T> T decode(byte[] data, Class<T> classOfT) {
     String json = new String(data, CHARSET_UTF8);
     return fromJson(json, classOfT);
   }
   
   public static <T> T fromJson(String json, Class<T> classOfT) {
     return (T)JSON.parseObject(json, classOfT);
   }
   
   public byte[] encode() {
     String json = toJson();
     if (json != null) {
       return json.getBytes(CHARSET_UTF8);
     }
     return null;
   }
   
   public String toJson() {
     return toJson(false);
   }
   
   public String toJson(boolean prettyFormat) {
     return toJson(this, prettyFormat);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\JsonUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */