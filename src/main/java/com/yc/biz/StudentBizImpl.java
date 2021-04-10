package com.yc.biz;

import com.yc.dao.StudentDao;
import com.yc.springframework.stereotype.MyResource;
import com.yc.springframework.stereotype.MyService;

/**
 * program:testspring
 * description:studentbiz
 * author:lsj
 * create:2021-04-04 14:46
 */
@MyService
public class StudentBizImpl implements StudentBiz {
    public StudentBizImpl() {
    }

    //    @Autowired()
//    @Qualifier("studentDaoimpl2")
//    @Inject
//    @Named("studentDaoimpl")
    @MyResource(name = "studentDaoimpl")
    StudentDao studentDao;

    @Override
    public int add(String name) {
        System.out.println("-------业务层-------");
        System.out.println("用户名是否重名");
        int result=studentDao.add(name);
        System.out.println("业务操作结束");
        return result;
    }

    @Override
    public void update(String name) {
        System.out.println("-------业务层-------");
        System.out.println("用户名是否重名");
        studentDao.update(name);
        System.out.println("业务操作结束");

    }

    @Override
    public void find(String name){
        System.out.println("-------业务层-------");
        System.out.println("查找用户名："+name);
        studentDao.find(name);
        System.out.println("业务操作结束");
    }
}
