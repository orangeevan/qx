 package com.haipaite.common.utility;
 
 import com.haipaite.common.utility.thread.NamedThreadFactory;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.nio.charset.Charset;
 import java.util.Random;
 import java.util.concurrent.Callable;
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.Future;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.TimeoutException;
 import java.util.zip.DataFormatException;
 import java.util.zip.Deflater;
 import java.util.zip.Inflater;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.slf4j.helpers.FormattingTuple;
 import org.slf4j.helpers.MessageFormatter;
 
 
 
 
 
 
 public final class ZlibUtils
 {
   private static final Logger logger = LoggerFactory.getLogger(ZlibUtils.class);
 
 
 
   
   private static final int DEFUALT_BUFFER_SIZE = 1024;
 
 
   
   private static final int DEFAULT_LEVEL = 5;
 
 
 
   
   public static byte[] zip(String src) {
     byte[] bytes = src.getBytes(Charset.forName("UTF8"));
     return zip(bytes);
   }
 
 
 
 
 
 
   
   public static byte[] zip(byte[] src) {
     return zip(src, 5);
   }
 
   
   private static ExecutorService executorService = Executors.newCachedThreadPool((ThreadFactory)new NamedThreadFactory(new ThreadGroup("ZlibUtils"), "解压处理"));
 
 
 
 
 
 
 
   
   public static byte[] unzip(final byte[] src, long timeout, TimeUnit unit) {
     Future<byte[]> future = (Future)executorService.submit((Callable)new Callable<byte[]>()
         {
           public byte[] call() throws Exception {
             Inflater inflater = new Inflater();
             inflater.setInput(src);
             
             ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
             byte[] buff = new byte[1024];
             try {
               while (!inflater.finished()) {
                 int count = inflater.inflate(buff);
                 if (count == 0 && 
                   inflater.needsInput()) {
                   break;
                 }
                 
                 os.write(buff, 0, count);
               } 
               inflater.end();
             } catch (DataFormatException e) {
               String message = "解压时发生数据格式异常";
               ZlibUtils.logger.error(message, e);
               throw new IllegalArgumentException(message, e);
             } finally {
               try {
                 os.close();
               } catch (IOException iOException) {}
             } 
 
             
             return os.toByteArray();
           }
         });
     
     try {
       return future.get(timeout, unit);
     } catch (InterruptedException e) {
       throw new IllegalStateException("解压时被打断:" + e.getMessage());
     } catch (ExecutionException e) {
       throw new IllegalStateException("解压时发生错误:" + e.getMessage());
     } catch (TimeoutException e) {
       throw new IllegalStateException("解压处理超时");
     } finally {
       future.cancel(true);
     } 
   }
   
   public static byte[] zip(byte[] src, int level) {
     if (level < 0 || level > 9) {
       FormattingTuple message = MessageFormatter.format("不合法的压缩等级[{}]", Integer.valueOf(level));
       logger.error(message.getMessage());
       throw new IllegalArgumentException(message.getMessage());
     } 
     
     Deflater df = new Deflater(level);
     df.setInput(src);
     df.finish();
     
     ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
     byte[] buff = new byte[1024];
     while (!df.finished()) {
       int count = df.deflate(buff);
       baos.write(buff, 0, count);
     } 
     df.end();
     try {
       baos.close();
     } catch (IOException iOException) {}
 
     
     return baos.toByteArray();
   }
   
   public static void main(String[] args) throws Exception {
     int size = 50000;
     StringBuilder sb = new StringBuilder();
     Random random = new Random();
     for (int i = 0; i < size; i++) {
       sb.append(random.nextInt(size));
     }
     String beforeZip = sb.toString();
     byte[] src = zip(sb.toString().getBytes("UTF-8"));
     System.err.println("压缩前大小: " + sb.toString().length() + " 压缩后大小: " + src.length);
     byte[] unzip = unzip(src, System.currentTimeMillis() + 100L, TimeUnit.SECONDS);
     String afterUnzip = new String(unzip);
     System.err.println("压缩前和压缩后比较结果: " + beforeZip.equals(afterUnzip));
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\ZlibUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */