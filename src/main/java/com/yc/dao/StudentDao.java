package com.yc.dao;


/**
 * program:testspring
 * description:studentdao
 * author:lsj
 * create:2021-04-04 14:47
 */

public interface StudentDao {
    public int add(String name);

    public void update(String name);

    public String find(String name);
}
