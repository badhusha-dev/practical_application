package com.example.aopdemo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // @Before advice - logs method name before execution
    @Before("execution(* com.example.aopdemo.service.*.*(..))")
    public void logBeforeMethodExecution(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        System.out.println("Before advice: Executing method: " + className + "." + methodName);
    }

    // @After advice - logs method name after execution (whether success or failure)
    @After("execution(* com.example.aopdemo.service.*.*(..))")
    public void logAfterMethodExecution(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        System.out.println("After advice: Method completed: " + className + "." + methodName);
    }

    // @AfterReturning advice - logs method name and return value if successful
    @AfterReturning(
        pointcut = "execution(* com.example.aopdemo.service.*.*(..))",
        returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        System.out.println("AfterReturning advice: Method " + className + "." + methodName + 
                          " returned: " + result);
    }

    // @AfterThrowing advice - logs method name and exception if an error occurs
    @AfterThrowing(
        pointcut = "execution(* com.example.aopdemo.service.*.*(..))",
        throwing = "exception"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        System.out.println("AfterThrowing advice: Method " + className + "." + methodName + 
                          " threw exception: " + exception.getMessage());
    }

    // @Around advice - logs execution time of method
    @Around("execution(* com.example.aopdemo.service.*.*(..))")
    public Object logAroundMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        long startTime = System.currentTimeMillis();
        System.out.println("Around advice: Starting execution of " + className + "." + methodName);
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            System.out.println("Around advice: Completed " + className + "." + methodName + 
                              " in " + executionTime + "ms");
            return result;
        } catch (Throwable throwable) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            System.out.println("Around advice: Exception in " + className + "." + methodName + 
                              " after " + executionTime + "ms");
            throw throwable;
        }
    }
}
