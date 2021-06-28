package com.haipaite.common.ramcache;

import com.haipaite.common.ramcache.anno.CachedEntityConfig;
import java.util.Map;

public interface ServiceManagerMBean {
  Map<String, Map<String, String>> getAllPersisterInfo();
  
  Map<String, String> getPersisterInfo(String paramString);
  
  Map<String, CachedEntityConfig> getAllCachedEntityConfig();
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\ServiceManagerMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */