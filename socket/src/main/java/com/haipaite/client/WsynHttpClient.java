 package com.haipaite.client;
 
 import java.io.IOException;
 import okhttp3.Headers;
 import okhttp3.OkHttpClient;
 import okhttp3.Request;
 import okhttp3.Response;
 
 
 
 
 
 
 
 
 
 
 public class WsynHttpClient
 {
   private static OkHttpClient client = new OkHttpClient();
   private Request.Builder builder;
   
   public WsynHttpClient(String url) {
     this.builder = new Request.Builder();
     this.builder.url(url);
   }
   
   public Response execute() throws IOException {
     return execute(false);
   }
   
   public Response execute(boolean print) throws IOException {
     Response response = client.newCall(this.builder.build()).execute();
     if (!response.isSuccessful()) {
       throw new IOException("服务器端错误: " + response);
     }
     
     if (print) {
       Headers responseHeaders = response.headers();
       for (int i = 0; i < responseHeaders.size(); i++) {
         System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
       }
       
       System.out.println(response.body().string());
     } 
     return response;
   }
   
   public Request.Builder getBuilder() {
     return this.builder;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\client\WsynHttpClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */