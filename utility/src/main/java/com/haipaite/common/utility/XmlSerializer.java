 package com.haipaite.common.utility;

 import java.beans.XMLDecoder;
 import java.beans.XMLEncoder;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.List;


 public class XmlSerializer
 {
   public static void write(Collection<?> list, OutputStream fos) throws IOException {
     XMLEncoder encoder = new XMLEncoder(fos);

     for (Object obj : list) {
       encoder.writeObject(obj);
     }
     encoder.flush();
     encoder.close();
     fos.close();
   }

   public static List<?> read(InputStream fis) throws IOException {
     List<Object> objList = new ArrayList();
     XMLDecoder decoder = new XMLDecoder(fis);
     Object obj = decoder.readObject();
     while (obj != null) {
       objList.add(obj);
       try {
         obj = decoder.readObject();
       } catch (ArrayIndexOutOfBoundsException e) {
         break;
       }
     }
     decoder.close();
     fis.close();
     return objList;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\XmlSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */