package com.yc.dao;


import com.yc.springframework.stereotype.MyComponent;

/**
 * program:testspring
 * description:studentDaoImpl
 * author:lsj
 * create:2021-04-04 14:48
 */
@MyComponent
public class StudentDaoimpl2 implements StudentDao {
    @Override
    public int add(String name) {
        System.out.println("-----业务层------");
        System.out.println("用户名是否重名");
      //  int result=stu
        System.out.println("添加学生" + name);
        return 1;
    }

    @Override
    public void update(String name) {
        System.out.println("更新学生" + name);
    }

    @Override
    public String find(String name) {
        return null;
    }
}
