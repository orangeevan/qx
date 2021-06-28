package com.haipaite.common.resource.reader;

import java.io.InputStream;
import java.util.List;

public interface ResourceReader {
  String getFormat();
  
  <E> List<E> read(InputStream paramInputStream, Class<E> paramClass);
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\reader\ResourceReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */