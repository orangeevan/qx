 package com.haipaite.common.ramcache.entity;
 
 import com.haipaite.common.ramcache.IEntity;
 import com.haipaite.common.ramcache.anno.Cached;
 import com.haipaite.common.ramcache.anno.Persister;
 import java.io.Serializable;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.GenerationType;
 import javax.persistence.Id;
 
 
 
 
 @Entity
 @Cached(persister = @Persister("10s"))
 public class User
   implements IEntity<Long>
 {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id")
   private long id;
   @Column(name = "user_name", length = 32)
   private String user_name;
   @Column(name = "age")
   private Integer age;
   @Column(name = "nice_name", length = 32)
   private String nice_name;
   
   public Long getId() {
     return Long.valueOf(this.id);
   }
   public void setId(long id) {
     this.id = id;
   }
   public String getUser_name() {
     return this.user_name;
   }
   public void setUser_name(String user_name) {
     this.user_name = user_name;
   }
   public Integer getAge() {
     return this.age;
   }
   public void setAge(Integer age) {
     this.age = age;
   }
   public String getNice_name() {
     return this.nice_name;
   }
   public void setNice_name(String nice_name) {
     this.nice_name = nice_name;
   }
 
   
   public boolean serialize() {
     return true;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\entity\User.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */