package com.test;
import org.apache.commons.io.monitor.*;

import java.io.File;

/**
 * @author: yuen.cy
 * @description:
 * @since 10:17 2021/6/25
 */
public class FileAlterationReload extends FileAlterationListenerAdaptor {
    @Override
    public void onStart(FileAlterationObserver observer) {
        File file=observer.getDirectory();
        if(file.isDirectory()){
            File[]  fs= file.listFiles();
            for(File f:fs){
                System.out.println(f.getName());
            }
        }
        System.out.println("start="+observer.getDirectory().getName());

    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("onDirectoryCreate="+directory.getName());
    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println("onDirectoryChange="+directory.getName());
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("onDirectoryDelete="+directory.getName());
    }

    @Override
    public void onFileCreate(File file) {
        System.out.println("onFileCreate="+file.getName());
    }

    @Override
    public void onFileChange(File file) {
        System.out.println("onFileChange="+file.getName());
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("onFileDelete="+file.getName());
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        System.out.println("onStop=");
    }
}

