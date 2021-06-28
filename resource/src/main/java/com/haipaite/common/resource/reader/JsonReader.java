 package com.haipaite.common.resource.reader;

 import com.alibaba.fastjson.JSON;
 import com.haipaite.common.resource.exception.DecodeException;
 import java.io.ByteArrayOutputStream;
 import java.io.InputStream;
 import java.util.List;







 public class JsonReader
   implements ResourceReader
 {
   public <E> List<E> read(InputStream input, Class<E> clz) {
     try {
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       byte[] buf = new byte[1024];
       int hasRead = 0;
       while ((hasRead = input.read(buf)) != -1) {
         baos.write(buf, 0, hasRead);
       }
       String msg = baos.toString();
       return JSON.parseArray(msg, clz);
     } catch (Exception e) {
       throw new DecodeException(e);
     }
   }


   public String getFormat() {
     return "json";
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\reader\JsonReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */