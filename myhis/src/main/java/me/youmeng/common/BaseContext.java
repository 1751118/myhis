package me.youmeng.common;

/**
 * 基于ThreadLocal的工具类，浏览器向服务器发送一个请求的全部过程，是由同一个线程来完成的
 * 线程内部可以设置一个变量，在不同的地方获取
 */
public class BaseContext{
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();

    public static void setId(Long id){
        threadLocal.set(id);
    }
    public static Long getId(){
        return threadLocal.get();
    }
}
