package com.mmorpg.qx.common.observer;

//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author: yuanchengyan
 * @description:
 * @since 19:08 2021/4/22
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations="classpath:applicationContext.xml")
public class OberverTest {
    public static void main(String[] args) {
        ObserverController controller = new ObserverController();
        controller.attachForEver(A.class, () -> System.out.println("1"));
        controller.attachOne(A.class, () -> System.out.println("2"));
        controller.fire(A.class);
        controller.fire(A.class);
    }

    public interface A {
    }
}

