package com.yc;

import com.yc.bean.HelloWorld;
import com.yc.springframework.MyAppConfig;
import com.yc.springframework.context.MyAnnotationConfigApplicationContext;
import com.yc.springframework.context.MyApplicationContext;

/**
 * @program: testspring
 * @description:
 * @author: ErFeng_V
 * @create: 2021-04-05 14:52
 */
public class Test {
    public static void main(String[] args) {
        MyApplicationContext ac=new MyAnnotationConfigApplicationContext(MyAppConfig.class);
        HelloWorld hw=(HelloWorld) ac.getBean("hw2");
        hw.show();
    }
}
