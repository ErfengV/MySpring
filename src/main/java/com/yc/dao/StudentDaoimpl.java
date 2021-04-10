package com.yc.dao;

import com.yc.springframework.stereotype.MyComponent;

import java.util.Random;

/**
 * program:testspring
 * description:studentDaoImpl
 * author:lsj
 * create:2021-04-04 14:48
 */
@MyComponent
public class StudentDaoimpl implements StudentDao {
    @Override
    public int add(String name) {
        System.out.println("mybatis添加学生" + name);
        Random r=new Random();
        return 1;
    }

    @Override
    public void update(String name) {
        System.out.println("更新学生" + name);
    }

    @Override
    public String find(String name) {
        System.out.println("mybatis查找:"+name);
        return name;
    }
}
