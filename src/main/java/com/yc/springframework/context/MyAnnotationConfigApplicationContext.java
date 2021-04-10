package com.yc.springframework.context;

import com.yc.springframework.stereotype.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @program: testspring
 * @description:
 * @author: ErFeng_V
 * @create: 2021-04-05 11:57
 */
public class MyAnnotationConfigApplicationContext implements MyApplicationContext {

    private Map<String,Object> beanMap=new HashMap<>();
    private Map<String,Class> lazyMap=new HashMap<>();

    private Set<Class> ManageBeanClasses=new HashSet<Class>();

    public MyAnnotationConfigApplicationContext(Class<?>... componentClasses)  {
        try {
            register(componentClasses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void register(Class<?>[] componentClasses) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException, ClassNotFoundException {
        if(componentClasses==null || componentClasses.length<=0){
            throw new RuntimeException("没有指定配置类");
        }
        for(Class cl:componentClasses){
            if(!cl.isAnnotationPresent(MyConfiguration.class)){
                continue;
            }

            String[] basePackages=getAppConfigBasePackages(cl);

            if(cl.isAnnotationPresent(MyComponentScan.class)){
                MyComponentScan mcs=(MyComponentScan)cl.getAnnotation(MyComponentScan.class);
                if(mcs.basePackages()!=null &&mcs.basePackages().length>0){
                    basePackages=mcs.basePackages();
                }
            }
            //method.invoke(MyAppConfig对象，obj就是当前解析的)
            Object obj=cl.newInstance();
            handleAtMyBean(cl,obj);
            
            for(String basePackage:basePackages){
                scanPackageAndSubPackageClasses(basePackage);
            }

            handleManagedBean();

            //循环beanMap中的每个bean ,找到他们每个类中的每个由@Autowired @Resource注解的方法以实现di
            handleDi(beanMap);
        }
    }

    private void handleDi(Map<String, Object> beanMap) throws InvocationTargetException, IllegalAccessException {
        Collection<Object> objectCollection=beanMap.values();
        for(Object obj:objectCollection){
            Class cls= obj.getClass();
            Method[] ms=cls.getDeclaredMethods();
            for(Method m:ms){
                if(m.isAnnotationPresent(MyAutowired.class)){

                    invokeAutowiredMethod(m,obj);
                }else if(m.isAnnotationPresent(MyResource.class)){
                    //1.取出  MyResource中name属性值
                    //2.如果没有，则取出m方法中参数的类型名，
                    //3.从beanMap取出
                    //4.invoke
                    invokeResourceMethod(m,obj);
                }
            }

            Field[] fs=cls.getDeclaredFields();
            for(Field field:fs){
                if(field.isAnnotationPresent(MyAutowired.class)){

                }else if(field.isAnnotationPresent(MyResource.class)){

                }
            }
        }
    }

    private void invokeResourceMethod(Method m, Object obj) throws InvocationTargetException, IllegalAccessException {
        MyResource mr=m.getAnnotation(MyResource.class);
        String beanId=mr.name();
        if(beanId==null || beanId.equalsIgnoreCase("")){
            String pname=m.getParameterTypes()[0].getSimpleName();
            beanId=pname.substring(0,1).toLowerCase()+pname.substring(1);
        }
        Object o=beanMap.get(beanId);
        m.invoke(obj,o);
    }

    /**

     * @param m
     * @param obj
     */
    private void invokeAutowiredMethod(Method m, Object obj) throws InvocationTargetException, IllegalAccessException {
//        1.取出m的参数类型
        Class typeClass=m.getParameterTypes()[0];
//        2.从beanMap中循环所有的object
        Set<String> keys=beanMap.keySet();
        for(String key :keys){
           // 4.如果是   则从beanMap取出
            Object o=beanMap.get(key);
            Class[] intefaces=o.getClass().getInterfaces();
            //        3.判断这些object 是否为 参数类型的实例 instanceof
           for(Class c:intefaces) {
               if(c==typeClass) {
                   m.invoke(obj, o);
                   break;
               }
                }
            }
        }

//
//        5.invoke



    /**
     * 处理managedBeanClasses 所有的class类，筛选出所有的@Component @Service @Repository的类
     */
    private void handleManagedBean() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        for(Class c:ManageBeanClasses){
            if(c.isAnnotationPresent(MyComponent.class)){
               saveManangedBean(c);
            }else if(c.isAnnotationPresent(MyService.class)){
                saveManangedBean(c);
            }else if(c.isAnnotationPresent(MyController.class)){
                saveManangedBean(c);
            }else if(c.isAnnotationPresent(MyRepository.class)){
                saveManangedBean(c);
            }else {
                continue;
            }
        }
    }

    private void saveManangedBean(Class c) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object o=c.newInstance();
        handlePostConstruct(o,c);
        String beanId= c.getSimpleName().substring(0,1).toLowerCase()+c.getSimpleName().substring(1);
        beanMap.put(beanId,o);
    }

    private void scanPackageAndSubPackageClasses(String basePackage) throws IOException, ClassNotFoundException {
        String packagePath=basePackage.replaceAll("\\.","/");
        System.out.println("扫描包路径："+basePackage+",替换后："+packagePath);
        Enumeration<URL> files=Thread.currentThread().getContextClassLoader().getResources(packagePath);
        while (files.hasMoreElements()){
            URL url=files.nextElement();
            System.out.println("配置的扫描的路径为："+url.getFile());
            //
            findClassesInPackages(url.getFile(),basePackage);
        }
    }


    /**
     * 查找file x下面及自爆所有的要托管的class 存到一个Set(managedBeanClasses)中..
     * @param file
     * @param basePackage
     */
    private void findClassesInPackages(String file, String basePackage) throws ClassNotFoundException {
        File f=new File(file);
        File[] classFiles=f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".class") || file.isDirectory();
            }
        });
        for(File cf:classFiles){
            if(cf.isDirectory()){
                basePackage+="."+cf.getName().substring(cf.getName().lastIndexOf("/"));
                findClassesInPackages(cf.getAbsolutePath(),basePackage);
            }else {
                URL[] urls=new URL[]{};
                URLClassLoader ucl=new URLClassLoader(urls);
                //com.yc.bean.Hello.class  ->com.yc.bean.Hello
                Class c = ucl.loadClass(basePackage+"."+cf.getName().replace(".class",""));
                ManageBeanClasses.add(c);
            }
        }
    }

    private void handleAtMyBean(Class cls,Object obj) throws InvocationTargetException, IllegalAccessException {
        //1.获取cls中所有的method
        Method[] ms=cls.getDeclaredMethods();
        //2.循环，判断，每个method上是否有 @MyBean注解
        for(Method m:ms){
            if(m.isAnnotationPresent(MyBean.class)){
                //3.有 则invo
                Object o=m.invoke(obj);
                //TODO: 加入处理@MyBean注解对应的方法所实例化的类中的@MyPostConstruct
                handlePostConstruct(o,o.getClass());   //o 在这里指HelloWorld对象  o.getClass反射对象
                beanMap.put(m.getName(),o);
            }
        }
    }

    /**
     * 处理一个Bean中的 @MyPostConstruct对应的方法
     * @param o
     * @param cls
     */
    private void handlePostConstruct(Object o, Class<?> cls) throws InvocationTargetException, IllegalAccessException {
        Method[] ms=cls.getDeclaredMethods();
        for(Method m:ms){
            if(m.isAnnotationPresent(MyPostConstruct.class)){
                m.invoke(o);
            }
        }
    }

    private String[] getAppConfigBasePackages(Class cl) {
        String[] path=new String[1];
        path[0]= cl.getPackage().getName();
        return path;
    }

    @Override
    public Object getBean(String id) {
        return beanMap.get(id);
    }
}
