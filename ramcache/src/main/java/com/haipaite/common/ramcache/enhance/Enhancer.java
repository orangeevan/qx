package com.haipaite.common.ramcache.enhance;

import com.haipaite.common.ramcache.exception.EnhanceException;

@Deprecated
public interface Enhancer {
  <T extends com.haipaite.common.ramcache.IEntity> T transform(T paramT) throws EnhanceException;
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\enhance\Enhancer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */