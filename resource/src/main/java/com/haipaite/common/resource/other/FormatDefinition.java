 package com.haipaite.common.resource.other;






 public class FormatDefinition
 {
   private final String location;
   private final String type;
   private final String suffix;

   public FormatDefinition(String location, String type, String suffix) {
     this.location = location;
     this.type = type;
     this.suffix = suffix;
   }



   public String getLocation() {
     return this.location;
   }

   public String getType() {
     return this.type;
   }

   public String getSuffix() {
     return this.suffix;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\other\FormatDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */