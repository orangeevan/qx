package com.mmorpg.qx;

import com.haipaite.common.resource.StorageManager;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author: yuen.cy
 * @description:
 * @since 11:06 2021/6/25
 */
@Component
public class TestServer {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static void main(String[] args) {

    }
}

