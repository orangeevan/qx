 package com.haipaite.common.utility;

 import java.io.File;
 import java.io.FileFilter;
 import java.io.IOException;
 import java.net.JarURLConnection;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Stack;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;


 public class PackageScanner
 {
   private static final Logger logger = LoggerFactory.getLogger(PackageScanner.class);

   private final Collection<Class<?>> clazzCollection = new HashSet<>();

   public PackageScanner(String... packageNames) {
     for (String packageName : packageNames) {
       try {
         String packageDirectory = packageName.replace('.', '/');
         Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageDirectory);
         while (urls.hasMoreElements()) {
           URL url = urls.nextElement();
           if ("file".equals(url.getProtocol())) {
             File directory = new File(url.getPath());
             if (!directory.isDirectory()) {
               throw new RuntimeException("package:[" + packageName + "] is not directory");
             }
             this.clazzCollection.addAll(scanClassFromDirectory(packageName, directory)); continue;
           }  if ("jar".equals(url.getProtocol())) {
             JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
             this.clazzCollection.addAll(scanClassFromJar(packageName, jar));
           }
         }
       } catch (IOException exception) {
         throw new RuntimeException(exception);
       }
     }
   }

   public PackageScanner(Collection<String> packageNames) {
     this(packageNames.<String>toArray(new String[packageNames.size()]));
   }

   public Collection<Class<?>> getClazzCollection() {
     return this.clazzCollection;
   }

   public static Collection<Class<?>> scanClassFromJar(String packageName, JarFile jar) {
     Enumeration<JarEntry> jarEntries = jar.entries();
     Pattern pattern = Pattern.compile("(" + packageName.replace('.', '/') + ".*)\\.class");
     Collection<Class<?>> clazzCollection = new HashSet<>();

     while (jarEntries.hasMoreElements()) {
       JarEntry entry = jarEntries.nextElement();
       String name = entry.getName();
       Matcher matcher = pattern.matcher(name.replace(File.separatorChar, '/'));
       if (matcher.find()) {
         String className = matcher.group(1).replace('/', '.');
         try {
           Class<?> clazz = Class.forName(className);
           clazzCollection.add(clazz);
         } catch (ClassNotFoundException e) {
           logger.error("无法加载类[{}]", className, e);
         }
       }
     }
     return clazzCollection;
   }

   public static Collection<Class<?>> scanClassFromDirectory(String packageName, File directory) {
     final Stack<File> scanDirectories = new Stack<>();
     Collection<File> classFiles = new ArrayList<>();
     FileFilter fileFilter = new FileFilter()
       {
         public boolean accept(File file) {
           if (file.isDirectory()) {
             scanDirectories.push(file);
             return false;
           }
           return file.getName().matches(".*\\.class$");
         }
       };

     scanDirectories.push(directory);

     while (!scanDirectories.isEmpty()) {
       File scanDirectory = scanDirectories.pop();
       Collections.addAll(classFiles, scanDirectory.listFiles(fileFilter));
     }
     Pattern pattern = Pattern.compile("(" + packageName.replace('.', '/') + ".*)\\.class");
     Collection<Class<?>> clazzCollection = new HashSet<>();
     for (File file : classFiles) {
       Matcher matcher = pattern.matcher(file.getAbsolutePath().replace(File.separatorChar, '/'));
       if (matcher.find()) {
         String className = matcher.group(1).replace('/', '.');
         try {
           Class<?> clazz = Class.forName(className);
           clazzCollection.add(clazz);
         } catch (ClassNotFoundException e) {
           logger.error("无法加载类[{}]", className, e);
         }
       }
     }

     return clazzCollection;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\PackageScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */