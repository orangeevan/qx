package com.test;

import java.io.File;
import java.io.FileFilter;

/**
 * @author: yuen.cy
 * @description:
 * @since 10:24 2021/6/25
 */
public class PropertiesFileFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        return pathname.getName().contains("properties");
    }
}

