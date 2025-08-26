package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static com.sky.constant.AutoFillConstant.*;

/**
 * AOP自定义切面类，用于实现公共字段自动填充处理（冗余代码）
 */
@Slf4j
@Component
@Aspect
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在执行SQL语句之前，在通知中进行公共字段的赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段填充。。。");

        //  获取被拦截方法上的数据库操作类型(INSERT, UPDATE)
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); //获得方法上的注解对象
        OperationType operationType = autoFill.value(); // 获得数据库操作类型(INSERT, UPDATE)

        // 获取被拦截方法参数
        Object[] args = joinPoint.getArgs(); //获得所有参数
        if(args == null || args.length == 0){
            return; //不执行
        }
        Object entity = args[0];

        // 准备赋值的数据: createTime, updateTime, createUser, updateUser
        LocalDateTime now = LocalDateTime.now(); //updateTime, createTime
        Long currentId = BaseContext.getCurrentId(); // updateUser, createUser

        // 根据不同操作类型(INSERT, UPDATE)，为对应的属性赋值
        if(operationType == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(SET_CREATE_TIME, LocalDateTime.class);//LocalDateTime 类型, 所以LocalDateTime.class
                Method setUpdateTime = entity.getClass().getDeclaredMethod(SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(SET_CREATE_USER, Long.class); //Long 类型,所以Long.class
                Method setUpdateUser = entity.getClass().getDeclaredMethod(SET_UPDATE_USER, Long.class);

                //通过反射为对象赋值
                setCreateTime.invoke(entity, now); // 赋值为now
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId); //赋值为currentId
                setUpdateUser.invoke(entity, currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {

            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(SET_UPDATE_TIME, LocalDateTime.class);

                Method setUpdateUser = entity.getClass().getDeclaredMethod(SET_UPDATE_USER, Long.class);

                //通过反射为对象赋值
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
