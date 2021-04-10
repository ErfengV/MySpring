package com.yc.bean;

import com.yc.springframework.stereotype.MyComponent;
import com.yc.springframework.stereotype.MyPostConstruct;
import com.yc.springframework.stereotype.MyPreDestroy;

/**
 * @program: testspring
 * @description:
 * @author: ErFeng_V
 * @create: 2021-04-05 14:48
 */
@MyComponent
public class HelloWorld {

    @MyPostConstruct
    public void setup(){
        System.out.println("MyPostConstruct");
    }

    @MyPreDestroy
    public void destroy(){
        System.out.println("MyDestroy");
    }

    public HelloWorld(){
        System.out.println("Hello,World 构造");
    }

    public void show(){
        System.out.println("show");
    }
}
