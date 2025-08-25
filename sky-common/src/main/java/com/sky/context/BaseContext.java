package com.sky.context;

public class BaseContext {

    // 每次请求都是一个单独的线程，我们可以用ThreadLocal来获取当前线程登录时保存过的id
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
